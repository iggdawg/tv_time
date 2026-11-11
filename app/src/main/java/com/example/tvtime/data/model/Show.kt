package com.example.tvtime.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Show(
    val id: Int,
    val name: String,
    val overview: String = "",
    val posterPath: String? = null,
    val backdropPath: String? = null,
    val firstAirDate: String? = null,
    val status: String = "Returning Series",
    val voteAverage: Double = 0.0,
    val networks: List<String> = emptyList(),
    val genres: List<String> = emptyList(),
    val numberOfSeasons: Int = 1,
    val numberOfEpisodes: Int = 10,
    val nextEpisodeToAir: Episode? = null,
    val lastEpisodeToAir: Episode? = null,
) {
    val fullPosterUrl: String?
        get() = posterPath?.let {
            if (it.startsWith("http")) it else "https://image.tmdb.org/t/p/w500$it"
        }

    val fullBackdropUrl: String?
        get() = backdropPath?.let {
            if (it.startsWith("http")) it else "https://image.tmdb.org/t/p/w780$it"
        }

    val posterUrl: String?
        get() = fullPosterUrl

    val backdropUrl: String?
        get() = fullBackdropUrl

    val primaryNetwork: String
        get() = networks.firstOrNull() ?: "TV"

    val network: String
        get() = primaryNetwork
}
