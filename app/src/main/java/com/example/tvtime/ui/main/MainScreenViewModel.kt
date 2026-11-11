package com.example.tvtime.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvtime.data.model.Episode
import com.example.tvtime.data.model.Show
import com.example.tvtime.data.repository.TvRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AppTab(val title: String) {
    UPCOMING("Upcoming"),
    SHOWS("Watchlist"),
    DISCOVER("Discover")
}

class MainScreenViewModel(
    private val repository: TvRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(AppTab.UPCOMING)
    val selectedTab: StateFlow<AppTab> = _selectedTab.asStateFlow()

    private val _upcomingEpisodes = MutableStateFlow<List<Episode>>(emptyList())
    val upcomingEpisodes: StateFlow<List<Episode>> = _upcomingEpisodes.asStateFlow()

    private val _trackedShows = MutableStateFlow<List<Show>>(emptyList())
    val trackedShows: StateFlow<List<Show>> = _trackedShows.asStateFlow()

    private val _discoverShows = MutableStateFlow<List<Show>>(emptyList())
    val discoverShows: StateFlow<List<Show>> = _discoverShows.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _isLoadingDiscover = MutableStateFlow(false)
    val isLoadingDiscover: StateFlow<Boolean> = _isLoadingDiscover.asStateFlow()

    private val _selectedShow = MutableStateFlow<Show?>(null)
    val selectedShow: StateFlow<Show?> = _selectedShow.asStateFlow()

    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    private val _apiKey = MutableStateFlow(repository.getApiKey())
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private var searchJob: Job? = null
    private var lastRefreshTimestamp: Long = 0

    init {
        refreshAll(force = true)
        loadTrending()
    }

    fun selectTab(tab: AppTab) {
        _selectedTab.value = tab
        if (tab == AppTab.UPCOMING) {
            refreshUpcomingIfStale()
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        if (query.isBlank()) {
            loadTrending()
        } else {
            searchJob = viewModelScope.launch {
                delay(300) // Debounce
                _isLoadingDiscover.value = true
                val results = repository.searchShows(query)
                _discoverShows.value = results
                _isLoadingDiscover.value = false
            }
        }
    }

    private fun loadTrending() {
        viewModelScope.launch {
            _isLoadingDiscover.value = true
            val trending = repository.getTrendingShows()
            _discoverShows.value = trending
            _isLoadingDiscover.value = false
        }
    }

    fun refreshUpcomingIfStale() {
        val now = System.currentTimeMillis()
        if (now - lastRefreshTimestamp > 15_000) {
            refreshAll()
        }
    }

    fun refreshAll(force: Boolean = false) {
        val now = System.currentTimeMillis()
        if (!force && (now - lastRefreshTimestamp < 3_000)) return
        lastRefreshTimestamp = now

        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val shows = repository.getTrackedShows()
                _trackedShows.value = shows
                val episodes = repository.getUpcomingEpisodes()
                _upcomingEpisodes.value = episodes

                // Refresh selected show if open
                _selectedShow.value?.let { current ->
                    val updated = repository.getShowDetails(current.id)
                    _selectedShow.value = updated
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun toggleTrackShow(show: Show) {
        viewModelScope.launch {
            val isTracked = repository.isTracked(show.id)
            if (isTracked) {
                repository.untrackShow(show.id)
            } else {
                repository.trackShow(show)
            }
            // Update state
            _trackedShows.value = repository.getTrackedShows()
            _upcomingEpisodes.value = repository.getUpcomingEpisodes()
            _selectedShow.value?.let { current ->
                if (current.id == show.id) {
                    _selectedShow.value = repository.getShowDetails(current.id)
                }
            }
        }
    }

    fun untrackShow(show: Show) {
        viewModelScope.launch {
            repository.untrackShow(show.id)
            _trackedShows.value = repository.getTrackedShows()
            _upcomingEpisodes.value = repository.getUpcomingEpisodes()
            if (_selectedShow.value?.id == show.id) {
                _selectedShow.value = null
            }
        }
    }

    fun toggleWatched(episode: Episode) {
        viewModelScope.launch {
            repository.setEpisodeWatched(episode.id, !episode.isWatched)
            _upcomingEpisodes.value = repository.getUpcomingEpisodes()
        }
    }

    fun selectShow(showId: Int) {
        viewModelScope.launch {
            val show = repository.getShowDetails(showId)
            _selectedShow.value = show
        }
    }

    fun clearSelectedShow() {
        _selectedShow.value = null
    }

    fun openSettings(open: Boolean) {
        _isSettingsOpen.value = open
    }

    fun saveApiKey(key: String) {
        repository.saveApiKey(key)
        _apiKey.value = key
        refreshAll(force = true)
        if (_searchQuery.value.isNotBlank()) {
            onSearchQueryChange(_searchQuery.value)
        } else {
            loadTrending()
        }
    }
}
