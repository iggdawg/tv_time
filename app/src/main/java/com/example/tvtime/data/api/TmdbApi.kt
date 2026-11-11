package com.example.tvtime.data.api

import com.example.tvtime.data.model.Episode
import com.example.tvtime.data.model.Show
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

class TmdbApi(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()
) {
    private val baseUrl = "https://api.themoviedb.org/3"

    suspend fun validateApiKey(apiKey: String): Boolean = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) return@withContext false
        try {
            val url = "$baseUrl/authentication?api_key=$apiKey"
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (_: Exception) {
            false
        }
    }

    suspend fun searchTv(query: String, apiKey: String): List<Show> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext SampleData.sampleShows.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.overview.contains(query, ignoreCase = true)
            }
        }

        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val url = "$baseUrl/search/tv?query=$encodedQuery&api_key=$apiKey&include_adult=false"
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext emptyList()
                val body = response.body?.string() ?: return@withContext emptyList()
                val json = JSONObject(body)
                val results = json.optJSONArray("results") ?: JSONArray()
                val shows = mutableListOf<Show>()
                for (i in 0 until results.length()) {
                    val item = results.getJSONObject(i)
                    shows.add(parseShowOverview(item))
                }
                shows
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun getTrendingTv(apiKey: String): List<Show> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext SampleData.sampleShows
        }

        try {
            val url = "$baseUrl/trending/tv/week?api_key=$apiKey"
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext SampleData.sampleShows
                val body = response.body?.string() ?: return@withContext SampleData.sampleShows
                val json = JSONObject(body)
                val results = json.optJSONArray("results") ?: JSONArray()
                val shows = mutableListOf<Show>()
                for (i in 0 until results.length()) {
                    val item = results.getJSONObject(i)
                    shows.add(parseShowOverview(item))
                }
                shows
            }
        } catch (_: Exception) {
            SampleData.sampleShows
        }
    }

    suspend fun getTvDetails(showId: Int, apiKey: String): Show? = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext SampleData.sampleShows.find { it.id == showId }
        }

        try {
            val url = "$baseUrl/tv/$showId?api_key=$apiKey&append_to_response=next_episode_to_air,last_episode_to_air"
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val body = response.body?.string() ?: return@withContext null
                val json = JSONObject(body)
                parseFullShow(json)
            }
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getSeasonEpisodes(showId: Int, seasonNumber: Int, apiKey: String): List<Episode> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            val show = SampleData.sampleShows.find { it.id == showId }
            return@withContext listOfNotNull(show?.nextEpisodeToAir)
        }

        try {
            val url = "$baseUrl/tv/$showId/season/$seasonNumber?api_key=$apiKey"
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext emptyList()
                val body = response.body?.string() ?: return@withContext emptyList()
                val json = JSONObject(body)
                val episodesArray = json.optJSONArray("episodes") ?: JSONArray()
                val episodes = mutableListOf<Episode>()
                for (i in 0 until episodesArray.length()) {
                    val epJson = episodesArray.getJSONObject(i)
                    episodes.add(parseEpisode(epJson, showId = showId))
                }
                episodes
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun parseShowOverview(item: JSONObject): Show {
        val posterPath = item.optString("poster_path").takeIf { it.isNotBlank() && it != "null" }
        val backdropPath = item.optString("backdrop_path").takeIf { it.isNotBlank() && it != "null" }
        return Show(
            id = item.getInt("id"),
            name = item.optString("name", "Unknown"),
            overview = item.optString("overview", ""),
            posterPath = posterPath,
            backdropPath = backdropPath,
            firstAirDate = item.optString("first_air_date").takeIf { it.isNotBlank() && it != "null" },
            voteAverage = item.optDouble("vote_average", 0.0)
        )
    }

    private fun parseFullShow(json: JSONObject): Show {
        val id = json.getInt("id")
        val name = json.optString("name", "Unknown")
        val overview = json.optString("overview", "")
        val posterPath = json.optString("poster_path").takeIf { it.isNotBlank() && it != "null" }
        val backdropPath = json.optString("backdrop_path").takeIf { it.isNotBlank() && it != "null" }
        val firstAirDate = json.optString("first_air_date").takeIf { it.isNotBlank() && it != "null" }
        val status = json.optString("status").takeIf { it.isNotBlank() && it != "null" }
        val voteAverage = json.optDouble("vote_average", 0.0)
        val numberOfSeasons = json.optInt("number_of_seasons", 1)

        val networksList = mutableListOf<String>()
        val networksArray = json.optJSONArray("networks")
        if (networksArray != null) {
            for (i in 0 until networksArray.length()) {
                val net = networksArray.getJSONObject(i)
                val netName = net.optString("name")
                if (netName.isNotBlank()) networksList.add(netName)
            }
        }

        val genresList = mutableListOf<String>()
        val genresArray = json.optJSONArray("genres")
        if (genresArray != null) {
            for (i in 0 until genresArray.length()) {
                val genre = genresArray.getJSONObject(i)
                val genreName = genre.optString("name")
                if (genreName.isNotBlank()) genresList.add(genreName)
            }
        }

        val primaryNet = networksList.firstOrNull()

        val nextEpJson = json.optJSONObject("next_episode_to_air")
        val nextEpisode = nextEpJson?.let {
            parseEpisode(it, showId = id, showName = name, showPoster = posterPath, showBackdrop = backdropPath, defaultNetwork = primaryNet)
        }

        val lastEpJson = json.optJSONObject("last_episode_to_air")
        val lastEpisode = lastEpJson?.let {
            parseEpisode(it, showId = id, showName = name, showPoster = posterPath, showBackdrop = backdropPath, defaultNetwork = primaryNet)
        }

        return Show(
            id = id,
            name = name,
            overview = overview,
            posterPath = posterPath,
            backdropPath = backdropPath,
            firstAirDate = firstAirDate,
            status = status ?: "Returning Series",
            voteAverage = voteAverage,
            networks = networksList,
            genres = genresList,
            numberOfSeasons = numberOfSeasons,
            nextEpisodeToAir = nextEpisode,
            lastEpisodeToAir = lastEpisode
        )
    }

    private fun parseEpisode(
        epJson: JSONObject,
        showId: Int = 0,
        showName: String = "",
        showPoster: String? = null,
        showBackdrop: String? = null,
        defaultNetwork: String? = null
    ): Episode {
        val still = epJson.optString("still_path").takeIf { it.isNotBlank() && it != "null" }
        val air = epJson.optString("air_date").takeIf { it.isNotBlank() && it != "null" }
        val runtimeVal = if (epJson.has("runtime") && !epJson.isNull("runtime")) epJson.optInt("runtime") else null

        return Episode(
            id = epJson.optInt("id", 0),
            showId = showId,
            showName = showName,
            showPosterPath = showPoster,
            showBackdropPath = showBackdrop,
            name = epJson.optString("name", "Episode"),
            overview = epJson.optString("overview", ""),
            airDate = air,
            episodeNumber = epJson.optInt("episode_number", 1),
            seasonNumber = epJson.optInt("season_number", 1),
            runtime = runtimeVal,
            stillPath = still,
            network = defaultNetwork ?: ""
        )
    }
}
