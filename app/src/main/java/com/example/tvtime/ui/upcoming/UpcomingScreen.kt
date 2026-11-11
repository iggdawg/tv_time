package com.example.tvtime.ui.upcoming

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tvtime.data.model.Episode
import com.example.tvtime.data.model.UpcomingSection
import com.example.tvtime.theme.TvTimeAiringTodayRed
import com.example.tvtime.theme.TvTimeBackground
import com.example.tvtime.theme.TvTimeBorder
import com.example.tvtime.theme.TvTimeOnYellow
import com.example.tvtime.theme.TvTimeSurfaceVariant
import com.example.tvtime.theme.TvTimeTextMuted
import com.example.tvtime.theme.TvTimeTextPrimary
import com.example.tvtime.theme.TvTimeTextSecondary
import com.example.tvtime.theme.TvTimeYellow

enum class UpcomingFilter(val label: String) {
    ALL("All"),
    THIS_WEEK("This Week"),
    LATER("Later"),
    TBA("TBA")
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UpcomingScreen(
    episodes: List<Episode>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onSelectShow: (Int) -> Unit,
    onToggleWatched: (Episode) -> Unit,
    onNavigateToDiscover: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(UpcomingFilter.ALL) }

    val filteredEpisodes = remember(episodes, selectedFilter) {
        when (selectedFilter) {
            UpcomingFilter.ALL -> episodes
            UpcomingFilter.THIS_WEEK -> episodes.filter {
                val days = it.daysUntil
                days != null && days in 0..7
            }
            UpcomingFilter.LATER -> episodes.filter {
                val days = it.daysUntil
                days != null && days > 7
            }
            UpcomingFilter.TBA -> episodes.filter { it.daysUntil == null }
        }
    }

    val groupedEpisodes = remember(filteredEpisodes) {
        filteredEpisodes.groupBy { it.section }
            .toSortedMap(compareBy { it.ordinal })
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TvTimeBackground)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Upcoming Schedule",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = TvTimeTextPrimary
                )
                Text(
                    text = "${filteredEpisodes.size} episodes tracked",
                    fontSize = 13.sp,
                    color = TvTimeTextSecondary
                )
            }

            if (isRefreshing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = TvTimeYellow,
                    strokeWidth = 2.5.dp
                )
            } else {
                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh schedule",
                        tint = TvTimeYellow
                    )
                }
            }
        }

        // Filter chips row
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(UpcomingFilter.values()) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TvTimeYellow,
                        selectedLabelColor = TvTimeOnYellow,
                        containerColor = TvTimeSurfaceVariant,
                        labelColor = TvTimeTextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = TvTimeBorder,
                        selectedBorderColor = TvTimeYellow,
                        enabled = true,
                        selected = selectedFilter == filter
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredEpisodes.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = TvTimeTextMuted,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = if (episodes.isEmpty()) "No Upcoming Episodes" else "No episodes in this filter",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TvTimeTextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (episodes.isEmpty())
                            "Add shows from the Discover tab to build your automatic calendar."
                        else "Check other tabs or refresh the schedule.",
                        fontSize = 14.sp,
                        color = TvTimeTextSecondary,
                        textAlign = TextAlign.Center
                    )
                    if (episodes.isEmpty()) {
                        Button(
                            onClick = onNavigateToDiscover,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TvTimeYellow,
                                contentColor = TvTimeOnYellow
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "Discover TV Shows", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Episode list with sticky section headers
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                groupedEpisodes.forEach { (section, sectionEpisodes) ->
                    stickyHeader(key = "header_${section.name}") {
                        SectionHeader(section = section, count = sectionEpisodes.size)
                    }

                    items(sectionEpisodes, key = { "ep_${it.id}_${it.seasonNumber}_${it.episodeNumber}" }) { episode ->
                        UpcomingEpisodeCard(
                            episode = episode,
                            onCardClick = { onSelectShow(episode.showId) },
                            onToggleWatched = { onToggleWatched(episode) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    section: UpcomingSection,
    count: Int
) {
    val headerColor = when (section) {
        UpcomingSection.TODAY -> TvTimeAiringTodayRed
        UpcomingSection.TOMORROW -> TvTimeYellow
        UpcomingSection.THIS_WEEK -> Color(0xFF60A5FA)
        UpcomingSection.LATER -> Color(0xFFCBD5E1)
        UpcomingSection.TBA -> TvTimeTextMuted
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(TvTimeBackground)
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(width = 4.dp, height = 16.dp)
                    .background(headerColor, RoundedCornerShape(2.dp))
            )
            Text(
                text = section.displayName.uppercase(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = headerColor,
                letterSpacing = 1.sp
            )
            Text(
                text = "($count)",
                fontSize = 12.sp,
                color = TvTimeTextMuted
            )
        }
    }
}
