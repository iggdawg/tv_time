package com.example.tvtime.ui.shows

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tvtime.data.model.Show
import com.example.tvtime.theme.TvTimeBackground
import com.example.tvtime.theme.TvTimeBorder
import com.example.tvtime.theme.TvTimeOnYellow
import com.example.tvtime.theme.TvTimeSurface
import com.example.tvtime.theme.TvTimeSurfaceVariant
import com.example.tvtime.theme.TvTimeTextMuted
import com.example.tvtime.theme.TvTimeTextPrimary
import com.example.tvtime.theme.TvTimeTextSecondary
import com.example.tvtime.theme.TvTimeYellow
import com.example.tvtime.ui.components.NetworkBadge
import com.example.tvtime.ui.components.StatusBadge

@Composable
fun ShowsScreen(
    trackedShows: List<Show>,
    onSelectShow: (Int) -> Unit,
    onUntrackShow: (Show) -> Unit,
    onNavigateToDiscover: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredShows = remember(trackedShows, searchQuery) {
        if (searchQuery.isBlank()) {
            trackedShows
        } else {
            trackedShows.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                    it.network.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TvTimeBackground)
    ) {
        // Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "My Watchlist",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = TvTimeTextPrimary
            )
            Text(
                text = "${trackedShows.size} shows tracked",
                fontSize = 13.sp,
                color = TvTimeTextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search in tracked shows
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Filter your tracked shows...", color = TvTimeTextMuted) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = TvTimeTextMuted
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = TvTimeTextMuted
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TvTimeYellow,
                    unfocusedBorderColor = TvTimeBorder,
                    focusedContainerColor = TvTimeSurfaceVariant,
                    unfocusedContainerColor = TvTimeSurfaceVariant,
                    focusedTextColor = TvTimeTextPrimary,
                    unfocusedTextColor = TvTimeTextPrimary
                )
            )
        }

        if (filteredShows.isEmpty()) {
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
                        imageVector = Icons.Default.Tv,
                        contentDescription = null,
                        tint = TvTimeTextMuted,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = if (trackedShows.isEmpty()) "No Tracked Shows" else "No matching shows",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TvTimeTextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (trackedShows.isEmpty())
                            "You haven't tracked any series yet. Add your favorite shows to follow upcoming episodes."
                        else "Try a different search keyword.",
                        fontSize = 14.sp,
                        color = TvTimeTextSecondary,
                        textAlign = TextAlign.Center
                    )
                    if (trackedShows.isEmpty()) {
                        Button(
                            onClick = onNavigateToDiscover,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TvTimeYellow,
                                contentColor = TvTimeOnYellow
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "Find Shows", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredShows, key = { it.id }) { show ->
                    TrackedShowCard(
                        show = show,
                        onCardClick = { onSelectShow(show.id) },
                        onUntrackClick = { onUntrackShow(show) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TrackedShowCard(
    show: Show,
    onCardClick: () -> Unit,
    onUntrackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = TvTimeSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, TvTimeBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Poster
            Box(
                modifier = Modifier
                    .size(width = 65.dp, height = 95.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1F2432))
            ) {
                if (!show.posterUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = show.posterUrl,
                        contentDescription = show.name,
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NetworkBadge(network = show.network)
                    StatusBadge(status = show.status)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = show.name,
                    color = TvTimeTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${show.numberOfSeasons} Seasons • ${show.numberOfEpisodes} Episodes",
                    color = TvTimeTextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                val nextEp = show.nextEpisodeToAir
                if (nextEp != null) {
                    Text(
                        text = "Next: ${nextEp.formattedEpisodeCode} (${nextEp.formattedAirDate})",
                        color = TvTimeYellow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Text(
                        text = "Rating: ★ ${String.format("%.1f", show.voteAverage)}/10",
                        color = TvTimeTextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(
                onClick = onUntrackClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Untrack show",
                    tint = TvTimeTextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
