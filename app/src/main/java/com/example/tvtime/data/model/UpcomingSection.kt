package com.example.tvtime.data.model

import java.time.LocalDate

enum class UpcomingSection(val displayName: String, val order: Int) {
    TODAY("Today", 0),
    TOMORROW("Tomorrow", 1),
    THIS_WEEK("This Week", 2),
    LATER("Coming Up Later", 3),
    TBA("Date TBA", 4)
}

typealias UpcomingCategory = UpcomingSection

data class UpcomingEpisodeItem(
    val episode: Episode,
    val show: Show,
    val isWatched: Boolean = false,
    val category: UpcomingSection = UpcomingSection.LATER
)

fun categorizeEpisode(episode: Episode?, today: LocalDate = LocalDate.now()): UpcomingSection {
    if (episode == null || episode.airDate.isNullOrBlank()) {
        return UpcomingSection.TBA
    }
    val days = episode.daysUntil(today) ?: return UpcomingSection.TBA
    return when {
        days <= 0L -> UpcomingSection.TODAY
        days == 1L -> UpcomingSection.TOMORROW
        days in 2L..7L -> UpcomingSection.THIS_WEEK
        else -> UpcomingSection.LATER
    }
}
