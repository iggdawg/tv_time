package com.example.tvtime.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.tvtime.R
import com.example.tvtime.theme.TvTimeAiringTodayRed
import com.example.tvtime.theme.TvTimeBackground
import com.example.tvtime.theme.TvTimeBorder
import com.example.tvtime.theme.TvTimeOnYellow
import com.example.tvtime.theme.TvTimeSurface
import com.example.tvtime.theme.TvTimeTextMuted
import com.example.tvtime.theme.TvTimeTextPrimary
import com.example.tvtime.theme.TvTimeTextSecondary
import com.example.tvtime.theme.TvTimeYellow
import com.example.tvtime.ui.detail.ShowDetailSheet
import com.example.tvtime.ui.discover.DiscoverScreen
import com.example.tvtime.ui.settings.SettingsDialog
import com.example.tvtime.ui.shows.ShowsScreen
import com.example.tvtime.ui.upcoming.UpcomingScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainScreenViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val upcomingEpisodes by viewModel.upcomingEpisodes.collectAsState()
    val trackedShows by viewModel.trackedShows.collectAsState()
    val discoverShows by viewModel.discoverShows.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isLoadingDiscover by viewModel.isLoadingDiscover.collectAsState()
    val selectedShow by viewModel.selectedShow.collectAsState()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()
    val apiKey by viewModel.apiKey.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Automatically refresh "Upcoming" when app is opened or resumed into the foreground
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshUpcomingIfStale()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val trackedShowIds = remember(trackedShows) {
        trackedShows.map { it.id }.toSet()
    }

    val todayAiringCount = remember(upcomingEpisodes) {
        upcomingEpisodes.count { it.daysUntil == 0L }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = TvTimeBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_splash_logo),
                            contentDescription = "TV Time Logo",
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "TV TIME",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = TvTimeYellow
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.openSettings(true) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = TvTimeTextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TvTimeSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = TvTimeSurface,
                tonalElevation = 8.dp
            ) {
                // Tab 1: Upcoming
                NavigationBarItem(
                    selected = selectedTab == AppTab.UPCOMING,
                    onClick = { viewModel.selectTab(AppTab.UPCOMING) },
                    icon = {
                        if (todayAiringCount > 0) {
                            BadgedBox(badge = {
                                Badge(containerColor = TvTimeAiringTodayRed) {
                                    Text(
                                        text = todayAiringCount.toString(),
                                        color = Color.White,
                                        fontSize = 10.sp
                                    )
                                }
                            }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Upcoming")
                            }
                        } else {
                            Icon(Icons.Default.DateRange, contentDescription = "Upcoming")
                        }
                    },
                    label = { Text("Upcoming", fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TvTimeOnYellow,
                        selectedTextColor = TvTimeYellow,
                        indicatorColor = TvTimeYellow,
                        unselectedIconColor = TvTimeTextMuted,
                        unselectedTextColor = TvTimeTextMuted
                    )
                )

                // Tab 2: Watchlist
                NavigationBarItem(
                    selected = selectedTab == AppTab.SHOWS,
                    onClick = { viewModel.selectTab(AppTab.SHOWS) },
                    icon = {
                        Icon(Icons.Default.List, contentDescription = "Watchlist")
                    },
                    label = { Text("Watchlist", fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TvTimeOnYellow,
                        selectedTextColor = TvTimeYellow,
                        indicatorColor = TvTimeYellow,
                        unselectedIconColor = TvTimeTextMuted,
                        unselectedTextColor = TvTimeTextMuted
                    )
                )

                // Tab 3: Discover
                NavigationBarItem(
                    selected = selectedTab == AppTab.DISCOVER,
                    onClick = { viewModel.selectTab(AppTab.DISCOVER) },
                    icon = {
                        Icon(Icons.Default.Search, contentDescription = "Discover")
                    },
                    label = { Text("Discover", fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TvTimeOnYellow,
                        selectedTextColor = TvTimeYellow,
                        indicatorColor = TvTimeYellow,
                        unselectedIconColor = TvTimeTextMuted,
                        unselectedTextColor = TvTimeTextMuted
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                AppTab.UPCOMING -> UpcomingScreen(
                    episodes = upcomingEpisodes,
                    isRefreshing = isRefreshing,
                    onRefresh = { viewModel.refreshAll(force = true) },
                    onSelectShow = { viewModel.selectShow(it) },
                    onToggleWatched = { viewModel.toggleWatched(it) },
                    onNavigateToDiscover = { viewModel.selectTab(AppTab.DISCOVER) }
                )
                AppTab.SHOWS -> ShowsScreen(
                    trackedShows = trackedShows,
                    onSelectShow = { viewModel.selectShow(it) },
                    onUntrackShow = { viewModel.untrackShow(it) },
                    onNavigateToDiscover = { viewModel.selectTab(AppTab.DISCOVER) }
                )
                AppTab.DISCOVER -> DiscoverScreen(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                    shows = discoverShows,
                    isLoading = isLoadingDiscover,
                    trackedShowIds = trackedShowIds,
                    onToggleTrack = { viewModel.toggleTrackShow(it) },
                    onSelectShow = { viewModel.selectShow(it) }
                )
            }
        }
    }

    // Show Detail ModalBottomSheet
    selectedShow?.let { show ->
        val isTracked = trackedShowIds.contains(show.id)
        ShowDetailSheet(
            show = show,
            isTracked = isTracked,
            onToggleTrack = { viewModel.toggleTrackShow(show) },
            sheetState = sheetState,
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                    viewModel.clearSelectedShow()
                }
            }
        )
    }

    // Settings Dialog
    if (isSettingsOpen) {
        SettingsDialog(
            currentApiKey = apiKey,
            onSaveApiKey = {
                viewModel.saveApiKey(it)
                viewModel.openSettings(false)
            },
            onDismissRequest = { viewModel.openSettings(false) }
        )
    }
}
