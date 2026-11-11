package com.example.tvtime.data.model

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@Serializable
data class Episode(
    val id: Int,
    val showId: Int = 0,
    val showName: String = "",
    val showPosterPath: String? = null,
    val showBackdropPath: String? = null,
    val name: String = "",
    val overview: String = "",
    val airDate: String? = null, // Format: YYYY-MM-DD
    val episodeNumber: Int = 1,
    val seasonNumber: Int = 1,
    val runtime: Int? = null,
    val stillPath: String? = null,
    val network: String = "",
    val isWatched: Boolean = false,
    val section: UpcomingSection = UpcomingSection.LATER,
) {
    val episodeCode: String
        get() = "S%02dE%02d".format(Locale.US, seasonNumber, episodeNumber)

    val formattedEpisodeCode: String
        get() = episodeCode

    val fullStillUrl: String?
        get() = stillPath?.let {
            if (it.startsWith("http")) it else "https://image.tmdb.org/t/p/w500$it"
        }

    val stillUrl: String?
        get() = fullStillUrl

    val fullPosterUrl: String?
        get() = showPosterPath?.let {
            if (it.startsWith("http")) it else "https://image.tmdb.org/t/p/w500$it"
        }

    val showPosterUrl: String?
        get() = fullPosterUrl

    val fullBackdropUrl: String?
        get() = showBackdropPath?.let {
            if (it.startsWith("http")) it else "https://image.tmdb.org/t/p/w780$it"
        }

    val parsedLocalDate: LocalDate?
        get() = try {
            airDate?.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it) }
        } catch (_: Exception) {
            null
        }

    fun daysUntil(today: LocalDate = LocalDate.now()): Long? {
        val date = parsedLocalDate ?: return null
        return ChronoUnit.DAYS.between(today, date)
    }

    val daysUntil: Long?
        get() = daysUntil()

    val isSeasonPremiere: Boolean
        get() = episodeNumber == 1

    fun countdownDisplay(today: LocalDate = LocalDate.now()): String {
        val days = daysUntil(today) ?: return "TBA"
        return when {
            days < 0 -> "${-days}d ago"
            days == 0L -> "Today"
            days == 1L -> "Tomorrow"
            days in 2L..6L -> "In $days days"
            days == 7L -> "In 1 week"
            days in 8L..13L -> "In ${(days / 7)} wk, ${(days % 7)}d"
            else -> {
                val date = parsedLocalDate
                date?.format(DateTimeFormatter.ofPattern("MMM d")) ?: "In $days days"
            }
        }
    }

    fun formattedAirDate(): String {
        val date = parsedLocalDate ?: return airDate ?: "Date TBA"
        return date.format(DateTimeFormatter.ofPattern("EEE, MMM d, yyyy"))
    }

    val formattedAirDate: String
        get() = formattedAirDate()
}
