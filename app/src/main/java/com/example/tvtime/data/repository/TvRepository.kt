package com.example.tvtime.data.repository

import android.content.Context
import com.example.tvtime.data.api.SampleData
import com.example.tvtime.data.api.TmdbApi
import com.example.tvtime.data.model.Episode
import com.example.tvtime.data.model.Show
import com.example.tvtime.data.model.TrackedShow
import com.example.tvtime.data.model.UpcomingCategory
import com.example.tvtime.data.model.UpcomingEpisodeItem
import com.example.tvtime.data.model.UpcomingSection
import com.example.tvtime.data.model.categorizeEpisode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate

class TvRepository(
    private val context: Context,
    private val api: TmdbApi = TmdbApi(),
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private val prefs = context.getSharedPreferences("tv_time_prefs", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    private val _apiKey = MutableStateFlow(prefs.getString("tmdb_api_key", "") ?: "")
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private val _trackedShows = MutableStateFlow<List<TrackedShow>>(emptyList())
    val trackedShows: StateFlow<List<TrackedShow>> = _trackedShows.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val upcomingEpisodes: StateFlow<List<UpcomingEpisodeItem>> = _trackedShows.combine(_apiKey) { shows, _ ->
        computeUpcoming(shows)
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    init {
        loadTrackedShows()
    }

    fun getApiKey(): String = _apiKey.value

    private fun loadTrackedShows() {
        val savedJson = prefs.getString("tracked_shows_json", null)
        if (!savedJson.isNullOrBlank()) {
            try {
                val list = json.decodeFromString<List<TrackedShow>>(savedJson)
                _trackedShows.value = list
                return
            } catch (_: Exception) {}
        }

        // First launch: Preload top sample shows
        val initialList = SampleData.sampleShows.take(5).map { show ->
            TrackedShow(show = show)
        }
        _trackedShows.value = initialList
        persistTrackedShows(initialList)
    }

    private fun persistTrackedShows(list: List<TrackedShow>) {
        try {
            val encoded = json.encodeToString(list)
            prefs.edit().putString("tracked_shows_json", encoded).apply()
        } catch (_: Exception) {}
    }

    fun saveApiKey(newKey: String) {
        val trimmed = newKey.trim()
        prefs.edit().putString("tmdb_api_key", trimmed).apply()
        _apiKey.value = trimmed
        if (trimmed.isNotBlank()) {
            scope.launch { refreshTrackedShows() }
        }
    }

    fun isTracked(showId: Int): Boolean {
        return _trackedShows.value.any { it.show.id == showId }
    }

    fun getTrackedShows(): List<Show> {
        return _trackedShows.value.map { it.show }
    }

    fun getUpcomingEpisodes(): List<Episode> {
        val items = computeUpcoming(_trackedShows.value)
        return items.map { item ->
            item.episode.copy(
                isWatched = item.isWatched,
                section = item.category
            )
        }
    }

    fun toggleTrackShow(show: Show) {
        val current = _trackedShows.value
        val existing = current.find { it.show.id == show.id }
        if (existing != null) {
            untrackShow(show.id)
        } else {
            trackShow(show)
        }
    }

    fun trackShow(show: Show) {
        val current = _trackedShows.value.toMutableList()
        if (current.none { it.show.id == show.id }) {
            current.add(0, TrackedShow(show = show))
            _trackedShows.value = current
            persistTrackedShows(current)

            // Fetch latest metadata from TMDB if key exists
            val key = _apiKey.value
            if (key.isNotBlank()) {
                scope.launch {
                    val fullShow = api.getTvDetails(show.id, key)
                    if (fullShow != null) {
                        updateShowDetails(fullShow)
                    }
                }
            }
        }
    }

    fun untrackShow(showId: Int) {
        val updated = _trackedShows.value.filterNot { it.show.id == showId }
        _trackedShows.value = updated
        persistTrackedShows(updated)
    }

    fun setEpisodeWatched(episodeId: Int, isWatched: Boolean) {
        val current = _trackedShows.value.map { tracked ->
            val watched = tracked.watchedEpisodeIds.toMutableSet()
            if (isWatched) {
                watched.add(episodeId)
            } else {
                watched.remove(episodeId)
            }
            tracked.copy(watchedEpisodeIds = watched)
        }
        _trackedShows.value = current
        persistTrackedShows(current)
    }

    fun toggleWatched(showId: Int, episodeId: Int) {
        val current = _trackedShows.value.map { tracked ->
            if (tracked.show.id == showId) {
                val watched = tracked.watchedEpisodeIds.toMutableSet()
                if (watched.contains(episodeId)) {
                    watched.remove(episodeId)
                } else {
                    watched.add(episodeId)
                }
                tracked.copy(watchedEpisodeIds = watched)
            } else {
                tracked
            }
        }
        _trackedShows.value = current
        persistTrackedShows(current)
    }

    private fun updateShowDetails(fullShow: Show) {
        val current = _trackedShows.value.map { tracked ->
            if (tracked.show.id == fullShow.id) {
                tracked.copy(show = fullShow)
            } else {
                tracked
            }
        }
        _trackedShows.value = current
        persistTrackedShows(current)
    }

    suspend fun refreshTrackedShows() = withContext(Dispatchers.IO) {
        val key = _apiKey.value
        _isRefreshing.value = true
        try {
            if (key.isNotBlank()) {
                val updated = coroutineScope {
                    _trackedShows.value.map { tracked ->
                        async {
                            try {
                                val full = api.getTvDetails(tracked.show.id, key)
                                if (full != null) {
                                    tracked.copy(show = full)
                                } else {
                                    tracked
                                }
                            } catch (_: Exception) {
                                tracked
                            }
                        }
                    }.awaitAll()
                }
                _trackedShows.value = updated
                persistTrackedShows(updated)
            } else {
                delay(300)
                _trackedShows.value = _trackedShows.value.map { it.copy() }
            }
        } finally {
            _isRefreshing.value = false
        }
    }

    suspend fun searchShows(query: String): List<Show> {
        return api.searchTv(query, _apiKey.value)
    }

    suspend fun getTrendingShows(): List<Show> {
        return api.getTrendingTv(_apiKey.value)
    }

    suspend fun getShowDetails(showId: Int): Show? {
        val existing = _trackedShows.value.find { it.show.id == showId }?.show
        if (existing?.nextEpisodeToAir != null && _apiKey.value.isBlank()) {
            return existing
        }
        return api.getTvDetails(showId, _apiKey.value) ?: existing
    }

    suspend fun getSeasonEpisodes(showId: Int, seasonNumber: Int): List<Episode> {
        return api.getSeasonEpisodes(showId, seasonNumber, _apiKey.value)
    }

    private fun computeUpcoming(tracked: List<TrackedShow>): List<UpcomingEpisodeItem> {
        val today = LocalDate.now()
        val items = mutableListOf<UpcomingEpisodeItem>()

        for (item in tracked) {
            val nextEp = item.show.nextEpisodeToAir
            if (nextEp != null) {
                val category = categorizeEpisode(nextEp, today)
                val isWatched = item.isEpisodeWatched(nextEp.id)
                items.add(
                    UpcomingEpisodeItem(
                        episode = nextEp.copy(
                            showId = item.show.id,
                            showName = item.show.name,
                            showPosterPath = item.show.posterPath,
                            showBackdropPath = item.show.backdropPath,
                            network = item.show.primaryNetwork,
                            isWatched = isWatched,
                            section = category
                        ),
                        show = item.show,
                        isWatched = isWatched,
                        category = category
                    )
                )
            } else {
                val dummyEpisode = Episode(
                    id = -item.show.id,
                    showId = item.show.id,
                    showName = item.show.name,
                    showPosterPath = item.show.posterPath,
                    showBackdropPath = item.show.backdropPath,
                    name = "Next Season (${item.show.status})",
                    overview = "Air date not yet announced by network.",
                    airDate = null,
                    episodeNumber = 1,
                    seasonNumber = (item.show.numberOfSeasons + 1).coerceAtLeast(1),
                    network = item.show.primaryNetwork,
                    isWatched = false,
                    section = UpcomingSection.TBA
                )
                items.add(
                    UpcomingEpisodeItem(
                        episode = dummyEpisode,
                        show = item.show,
                        isWatched = false,
                        category = UpcomingSection.TBA
                    )
                )
            }
        }

        return items.sortedWith(
            compareBy<UpcomingEpisodeItem> { it.category.order }
                .thenBy { it.episode.daysUntil(today) ?: Long.MAX_VALUE }
                .thenBy { it.show.name }
        )
    }
}
