package com.example.tvtime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tvtime.theme.TvTimeAiringTodayRed
import com.example.tvtime.theme.TvTimeEndedGray
import com.example.tvtime.theme.TvTimeReturningBlue
import com.example.tvtime.theme.TvTimeSurfaceVariant
import com.example.tvtime.theme.TvTimeTextPrimary
import com.example.tvtime.theme.TvTimeYellow
import com.example.tvtime.theme.TvTimeYellowContainer

@Composable
fun CountdownBadge(
    daysUntil: Long,
    modifier: Modifier = Modifier
) {
    val (text, bgColor, textColor) = when {
        daysUntil < 0 -> Triple("Aired", TvTimeSurfaceVariant, Color(0xFF94A3B8))
        daysUntil == 0L -> Triple("TODAY", TvTimeAiringTodayRed, Color.White)
        daysUntil == 1L -> Triple("TOMORROW", TvTimeYellow, Color(0xFF1A1500))
        daysUntil in 2..6 -> Triple("in $daysUntil days", TvTimeYellowContainer, TvTimeYellow)
        else -> Triple("in ${(daysUntil + 6) / 7} wks", TvTimeSurfaceVariant, Color(0xFFCBD5E1))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun NetworkBadge(
    network: String,
    modifier: Modifier = Modifier
) {
    if (network.isBlank()) return
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF1E2433))
            .border(0.5.dp, Color(0xFF333B50), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = network.uppercase(),
            color = Color(0xFF94A3B8),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status.lowercase()) {
        "returning series" -> Color(0xFF0C4A6E) to TvTimeReturningBlue
        "ended" -> Color(0xFF262626) to TvTimeEndedGray
        "canceled", "cancelled" -> Color(0xFF450A0A) to Color(0xFFF87171)
        "in production", "planned" -> Color(0xFF3B2500) to TvTimeYellow
        else -> Color(0xFF1E2433) to Color(0xFF94A3B8)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = status,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
