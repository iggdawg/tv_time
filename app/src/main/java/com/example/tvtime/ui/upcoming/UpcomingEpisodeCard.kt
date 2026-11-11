package com.example.tvtime.ui.upcoming

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tvtime.data.model.Episode
import com.example.tvtime.theme.TvTimeAiringTodayRed
import com.example.tvtime.theme.TvTimeBorder
import com.example.tvtime.theme.TvTimeSurface
import com.example.tvtime.theme.TvTimeTextMuted
import com.example.tvtime.theme.TvTimeTextPrimary
import com.example.tvtime.theme.TvTimeTextSecondary
import com.example.tvtime.theme.TvTimeWatchedGreen
import com.example.tvtime.theme.TvTimeYellow
import com.example.tvtime.ui.components.CountdownBadge
import com.example.tvtime.ui.components.NetworkBadge

@Composable
fun UpcomingEpisodeCard(
    episode: Episode,
    onCardClick: () -> Unit,
    onToggleWatched: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = TvTimeSurface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, TvTimeBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Poster thumbnail
            Box(
                modifier = Modifier
                    .size(width = 65.dp, height = 95.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1F2432))
            ) {
                val imageUrl = episode.stillUrl ?: episode.showPosterUrl
                if (!imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = episode.showName,
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            ) {
                // Network and Countdown badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NetworkBadge(network = episode.network)
                    episode.daysUntil?.let { days ->
                        CountdownBadge(daysUntil = days)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Show Name
                Text(
                    text = episode.showName,
                    color = TvTimeTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Episode Code & Title
                val epCodeAndTitle = "${episode.formattedEpisodeCode} • ${episode.name}"
                Text(
                    text = epCodeAndTitle,
                    color = TvTimeTextSecondary,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Air date & Season Premiere pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = episode.formattedAirDate,
                        color = if (episode.daysUntil == 0L) TvTimeAiringTodayRed else TvTimeTextMuted,
                        fontSize = 12.sp,
                        fontWeight = if (episode.daysUntil == 0L) FontWeight.SemiBold else FontWeight.Normal
                    )

                    if (episode.isSeasonPremiere) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF332900))
                                .border(0.5.dp, TvTimeYellow, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "SEASON PREMIERE",
                                color = TvTimeYellow,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Watched Checkmark Toggle
            IconButton(
                onClick = onToggleWatched,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (episode.isWatched) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                    contentDescription = if (episode.isWatched) "Mark unwatched" else "Mark watched",
                    tint = if (episode.isWatched) TvTimeWatchedGreen else TvTimeTextMuted,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
