package com.example.tvtime.data.api

import com.example.tvtime.data.model.Episode
import com.example.tvtime.data.model.Show
import java.time.LocalDate

object SampleData {
    private val today: LocalDate = LocalDate.now()

    val sampleShows: List<Show> by lazy {
        listOf(
            Show(
                id = 95396,
                name = "Severance",
                overview = "Mark leads a team of office workers whose memories have been surgically divided between their work and personal lives.",
                posterPath = "https://image.tmdb.org/t/p/w500/u3bZgnGQ9T01sWNhyveQz0wH0Hl.jpg",
                backdropPath = "https://image.tmdb.org/t/p/w780/roYyPiQDzKmIKUEyO91569tqRIz.jpg",
                firstAirDate = "2022-02-17",
                status = "Returning Series",
                voteAverage = 8.4,
                networks = listOf("Apple TV+"),
                genres = listOf("Sci-Fi & Fantasy", "Drama", "Mystery"),
                numberOfSeasons = 2,
                nextEpisodeToAir = Episode(
                    id = 101,
                    showId = 95396,
                    showName = "Severance",
                    showPosterPath = "https://image.tmdb.org/t/p/w500/u3bZgnGQ9T01sWNhyveQz0wH0Hl.jpg",
                    name = "Goodbye Mrs. Cobel",
                    overview = "The Macrodata Refinement team executes the overtime contingency protocol with high stakes.",
                    airDate = today.plusDays(1).toString(),
                    episodeNumber = 1,
                    seasonNumber = 2,
                    runtime = 55,
                    stillPath = "https://image.tmdb.org/t/p/w500/roYyPiQDzKmIKUEyO91569tqRIz.jpg",
                    network = "Apple TV+"
                )
            ),
            Show(
                id = 1396,
                name = "Breaking Bad",
                overview = "A chemistry teacher diagnosed with inoperable lung cancer turns to manufacturing and selling methamphetamine.",
                posterPath = "https://image.tmdb.org/t/p/w500/ztkUQFLlC19CCMYHW9o1zWhJRNq.jpg",
                backdropPath = "https://image.tmdb.org/t/p/w780/tsRy63Mu5cu8etL1X7ZLyf7UP1M.jpg",
                firstAirDate = "2008-01-20",
                status = "Ended",
                voteAverage = 8.9,
                networks = listOf("AMC"),
                genres = listOf("Drama", "Crime"),
                numberOfSeasons = 5
            ),
            Show(
                id = 125910,
                name = "The Bear",
                overview = "A young fine-dining chef comes home to Chicago to run his family Italian beef sandwich shop.",
                posterPath = "https://image.tmdb.org/t/p/w500/sHFlw56fR5A1SVOeZ0aK2bI7dJq.jpg",
                backdropPath = "https://image.tmdb.org/t/p/w780/sHFlw56fR5A1SVOeZ0aK2bI7dJq.jpg",
                firstAirDate = "2022-06-23",
                status = "Returning Series",
                voteAverage = 8.3,
                networks = listOf("FX", "Hulu"),
                genres = listOf("Drama", "Comedy"),
                numberOfSeasons = 4,
                nextEpisodeToAir = Episode(
                    id = 102,
                    showId = 125910,
                    showName = "The Bear",
                    showPosterPath = "https://image.tmdb.org/t/p/w500/sHFlw56fR5A1SVOeZ0aK2bI7dJq.jpg",
                    name = "Every Second Counts",
                    overview = "Carmy and Sydney overhaul the kitchen line in preparation for review night.",
                    airDate = today.toString(),
                    episodeNumber = 1,
                    seasonNumber = 4,
                    runtime = 38,
                    stillPath = "https://image.tmdb.org/t/p/w500/sHFlw56fR5A1SVOeZ0aK2bI7dJq.jpg",
                    network = "FX"
                )
            ),
            Show(
                id = 94997,
                name = "House of the Dragon",
                overview = "The Targaryen dynasty is at the absolute apex of its power, with more than 15 dragons under their yoke.",
                posterPath = "https://image.tmdb.org/t/p/w500/1X4hFsXpjjY00j4h97oP6e7lqX7.jpg",
                backdropPath = "https://image.tmdb.org/t/p/w780/etjA2oF1A0gT1V7U8oUfXoBq6lJ.jpg",
                firstAirDate = "2022-08-21",
                status = "Returning Series",
                voteAverage = 8.4,
                networks = listOf("HBO", "Max"),
                genres = listOf("Sci-Fi & Fantasy", "Action & Adventure", "Drama"),
                numberOfSeasons = 3,
                nextEpisodeToAir = Episode(
                    id = 103,
                    showId = 94997,
                    showName = "House of the Dragon",
                    showPosterPath = "https://image.tmdb.org/t/p/w500/1X4hFsXpjjY00j4h97oP6e7lqX7.jpg",
                    name = "The Battle of the Gullet",
                    overview = "The Greens and Blacks clash as dragonfire engulfs the Narrow Sea.",
                    airDate = today.plusDays(4).toString(),
                    episodeNumber = 1,
                    seasonNumber = 3,
                    runtime = 62,
                    stillPath = "https://image.tmdb.org/t/p/w500/etjA2oF1A0gT1V7U8oUfXoBq6lJ.jpg",
                    network = "HBO"
                )
            ),
            Show(
                id = 66732,
                name = "Stranger Things",
                overview = "When a young boy vanishes, a small town uncovers a mystery involving secret experiments, terrifying supernatural forces and one strange little girl.",
                posterPath = "https://image.tmdb.org/t/p/w500/49WJfeN0moxb9IPfGn8AIqMGskD.jpg",
                backdropPath = "https://image.tmdb.org/t/p/w780/56v2ox9hpvYiLQI9goJ9q0UqM5v.jpg",
                firstAirDate = "2016-07-15",
                status = "Returning Series",
                voteAverage = 8.6,
                networks = listOf("Netflix"),
                genres = listOf("Sci-Fi & Fantasy", "Drama", "Mystery"),
                numberOfSeasons = 5,
                nextEpisodeToAir = Episode(
                    id = 104,
                    showId = 66732,
                    showName = "Stranger Things",
                    showPosterPath = "https://image.tmdb.org/t/p/w500/49WJfeN0moxb9IPfGn8AIqMGskD.jpg",
                    name = "The Crawl",
                    overview = "The final battle for Hawkins begins as the Upside Down breaches reality.",
                    airDate = today.plusDays(18).toString(),
                    episodeNumber = 1,
                    seasonNumber = 5,
                    runtime = 70,
                    stillPath = "https://image.tmdb.org/t/p/w500/56v2ox9hpvYiLQI9goJ9q0UqM5v.jpg",
                    network = "Netflix"
                )
            ),
            Show(
                id = 100088,
                name = "The Last of Us",
                overview = "Twenty years after modern civilization has been destroyed, Joel is hired to smuggle Ellie out of an oppressive quarantine zone.",
                posterPath = "https://image.tmdb.org/t/p/w500/uKvVjHNqB5VmOrdxqmi2vt702m8.jpg",
                backdropPath = "https://image.tmdb.org/t/p/w780/uDgy6hyPd82kOHh6I95FLtLuv67.jpg",
                firstAirDate = "2023-01-15",
                status = "Returning Series",
                voteAverage = 8.6,
                networks = listOf("HBO", "Max"),
                genres = listOf("Drama", "Sci-Fi & Fantasy"),
                numberOfSeasons = 2,
                nextEpisodeToAir = Episode(
                    id = 105,
                    showId = 100088,
                    showName = "The Last of Us",
                    showPosterPath = "https://image.tmdb.org/t/p/w500/uKvVjHNqB5VmOrdxqmi2vt702m8.jpg",
                    name = "Seattle Day One",
                    overview = "Joel and Ellie face new factions as winter approaches Jackson.",
                    airDate = null, // TBA
                    episodeNumber = 1,
                    seasonNumber = 2,
                    runtime = 60,
                    stillPath = "https://image.tmdb.org/t/p/w500/uDgy6hyPd82kOHh6I95FLtLuv67.jpg",
                    network = "HBO"
                )
            ),
            Show(
                id = 119051,
                name = "Wednesday",
                overview = "Wednesday Addams' misadventures as a student at Nevermore Academy.",
                posterPath = "https://image.tmdb.org/t/p/w500/9PFonBhy4cQy7Jz20NpMygczOkv.jpg",
                backdropPath = "https://image.tmdb.org/t/p/w780/iHSwvFe7FdFFRmEN09577UuvDkh.jpg",
                firstAirDate = "2022-11-23",
                status = "Returning Series",
                voteAverage = 8.4,
                networks = listOf("Netflix"),
                genres = listOf("Sci-Fi & Fantasy", "Mystery", "Comedy"),
                numberOfSeasons = 2,
                nextEpisodeToAir = Episode(
                    id = 106,
                    showId = 119051,
                    showName = "Wednesday",
                    showPosterPath = "https://image.tmdb.org/t/p/w500/9PFonBhy4cQy7Jz20NpMygczOkv.jpg",
                    name = "Here We Woe Again",
                    overview = "Wednesday returns to Nevermore to investigate a cryptic new stalker.",
                    airDate = today.plusDays(6).toString(),
                    episodeNumber = 1,
                    seasonNumber = 2,
                    runtime = 50,
                    stillPath = "https://image.tmdb.org/t/p/w500/iHSwvFe7FdFFRmEN09577UuvDkh.jpg",
                    network = "Netflix"
                )
            ),
            Show(
                id = 124364,
                name = "From",
                overview = "Unravel the mystery of a city in middle America that traps everyone who enters.",
                posterPath = "https://image.tmdb.org/t/p/w500/cjxlL1t25o2i2o60Z2u26l1yvV.jpg",
                backdropPath = "https://image.tmdb.org/t/p/w780/nQh2Q9Z5rKx4b4rL2s5b3xV3e5m.jpg",
                firstAirDate = "2022-02-20",
                status = "Returning Series",
                voteAverage = 8.2,
                networks = listOf("MGM+"),
                genres = listOf("Mystery", "Sci-Fi & Fantasy", "Drama"),
                numberOfSeasons = 3,
                nextEpisodeToAir = Episode(
                    id = 107,
                    showId = 124364,
                    showName = "From",
                    showPosterPath = "https://image.tmdb.org/t/p/w500/cjxlL1t25o2i2o60Z2u26l1yvV.jpg",
                    name = "Passage",
                    overview = "Boyd and Kenny investigate the forest bells as nightfall nears.",
                    airDate = today.plusDays(2).toString(),
                    episodeNumber = 4,
                    seasonNumber = 3,
                    runtime = 52,
                    stillPath = "https://image.tmdb.org/t/p/w500/nQh2Q9Z5rKx4b4rL2s5b3xV3e5m.jpg",
                    network = "MGM+"
                )
            )
        )
    }
}
