package com.example.tvtime.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tvtime.data.model.Show
import com.example.tvtime.theme.TvTimeAiringTodayRed
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
import com.example.tvtime.ui.components.CountdownBadge
import com.example.tvtime.ui.components.NetworkBadge
import com.example.tvtime.ui.components.StatusBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDetailSheet(
    show: Show,
    isTracked: Boolean,
    onToggleTrack: () -> Unit,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = TvTimeBackground,
        dragHandle = null,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // Backdrop Header with gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                val bannerUrl = show.backdropUrl ?: show.posterUrl
                if (!bannerUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = bannerUrl,
                        contentDescription = show.name,
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Dark gradient overlay
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    TvTimeBackground.copy(alpha = 0.6f),
                                    TvTimeBackground
                                )
                            )
                        )
                )

                // Close button
                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close sheet",
                        tint = Color.White
                    )
                }
            }

            // Info Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = show.name,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = TvTimeTextPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            NetworkBadge(network = show.network)
                            StatusBadge(status = show.status)

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = TvTimeYellow,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = String.format("%.1f", show.voteAverage),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TvTimeYellow
                                )
                            }
                        }
                    }

                    // Poster thumbnail
                    if (!show.posterUrl.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .size(width = 65.dp, height = 95.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, TvTimeBorder, RoundedCornerShape(8.dp))
                        ) {
                            AsyncImage(
                                model = show.posterUrl,
                                contentDescription = show.name,
                                modifier = Modifier.matchParentSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Track Toggle Button
                Button(
                    onClick = onToggleTrack,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isTracked) Color(0xFF14532D) else TvTimeYellow,
                        contentColor = if (isTracked) TvTimeWatchedGreen else TvTimeOnYellow
                    )
                ) {
                    Icon(
                        imageVector = if (isTracked) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isTracked) "Tracking Show (Tap to Untrack)" else "+ Add to Tracked Shows",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                // Next Episode Card (if any)
                show.nextEpisodeToAir?.let { nextEp ->
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Next Upcoming Episode",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TvTimeTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = TvTimeSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TvTimeBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${nextEp.formattedEpisodeCode} • ${nextEp.name}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TvTimeTextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                nextEp.daysUntil?.let { days ->
                                    CountdownBadge(daysUntil = days)
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Airs: ${nextEp.formattedAirDate}",
                                fontSize = 13.sp,
                                color = if (nextEp.daysUntil == 0L) TvTimeAiringTodayRed else TvTimeTextSecondary
                            )

                            if (nextEp.overview.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = nextEp.overview,
                                    fontSize = 12.sp,
                                    color = TvTimeTextMuted,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                // Overview / Synopsis
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Overview",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TvTimeTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = show.overview.ifBlank { "No description available for this series." },
                    fontSize = 14.sp,
                    color = TvTimeTextSecondary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
