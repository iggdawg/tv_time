package com.example.tvtime.ui.discover

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.tvtime.theme.TvTimeWatchedGreen
import com.example.tvtime.theme.TvTimeYellow
import com.example.tvtime.ui.components.NetworkBadge
import com.example.tvtime.ui.components.StatusBadge

@Composable
fun DiscoverScreen(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    shows: List<Show>,
    isLoading: Boolean,
    trackedShowIds: Set<Int>,
    onToggleTrack: (Show) -> Unit,
    onSelectShow: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TvTimeBackground)
    ) {
        // Search Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Discover Shows",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = TvTimeTextPrimary
            )
            Text(
                text = "Search TMDB database or explore trending titles",
                fontSize = 13.sp,
                color = TvTimeTextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search TV shows (e.g., Severance, Fallout)...", color = TvTimeTextMuted) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = TvTimeTextMuted
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
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

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = TvTimeYellow)
            }
        } else if (shows.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = TvTimeTextMuted,
                        modifier = Modifier.size(56.dp)
                    )
                    Text(
                        text = "No TV shows found",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TvTimeTextPrimary
                    )
                    Text(
                        text = "Try another search title or configure a custom TMDB API key in Settings.",
                        fontSize = 14.sp,
                        color = TvTimeTextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(shows, key = { it.id }) { show ->
                    val isTracked = trackedShowIds.contains(show.id)
                    DiscoverShowCard(
                        show = show,
                        isTracked = isTracked,
                        onCardClick = { onSelectShow(show.id) },
                        onToggleTrack = { onToggleTrack(show) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DiscoverShowCard(
    show: Show,
    isTracked: Boolean,
    onCardClick: () -> Unit,
    onToggleTrack: () -> Unit,
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
                    text = "${show.numberOfSeasons} Seasons • ★ ${String.format("%.1f", show.voteAverage)}",
                    color = TvTimeTextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = show.overview,
                    color = TvTimeTextMuted,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Track / Tracked button
            OutlinedButton(
                onClick = onToggleTrack,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isTracked) Color(0xFF14532D) else TvTimeSurfaceVariant,
                    contentColor = if (isTracked) TvTimeWatchedGreen else TvTimeYellow
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isTracked) TvTimeWatchedGreen else TvTimeYellow
                ),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = if (isTracked) Icons.Default.Check else Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isTracked) "Tracked" else "Track",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
