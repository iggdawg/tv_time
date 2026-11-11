package com.example.tvtime.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.tvtime.data.api.TmdbApi
import com.example.tvtime.data.model.TrackedShow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.time.LocalDate

class EpisodeNotificationWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val prefs = appContext.getSharedPreferences("tv_time_prefs", Context.MODE_PRIVATE)

        val airingTodayEnabled = prefs.getBoolean("notifications_airing_today_enabled", true)
        val seasonPremiereEnabled = prefs.getBoolean("notifications_season_premiere_enabled", true)

        if (!airingTodayEnabled && !seasonPremiereEnabled) {
            return@withContext Result.success()
        }

        NotificationHelper.createNotificationChannels(appContext)

        val savedJson = prefs.getString("tracked_shows_json", null) ?: return@withContext Result.success()
        val trackedShows = try {
            json.decodeFromString<List<TrackedShow>>(savedJson)
        } catch (_: Exception) {
            return@withContext Result.failure()
        }

        val today = LocalDate.now()
        val apiKey = prefs.getString("tmdb_api_key", "") ?: ""
        val api = TmdbApi()

        for (tracked in trackedShows) {
            var show = tracked.show

            // If API key is present, attempt to pull latest episode details
            if (apiKey.isNotBlank()) {
                val freshShow = api.getTvDetails(show.id, apiKey)
                if (freshShow != null) {
                    show = freshShow
                }
            }

            val nextEp = show.nextEpisodeToAir ?: continue
            val daysUntil = nextEp.daysUntil(today) ?: continue

            // 1. Episode airing today notification (Morning alert)
            if (airingTodayEnabled && daysUntil == 0L) {
                val notifiedKey = "notified_today_${today}_${show.id}_${nextEp.id}"
                val alreadyNotified = prefs.getBoolean(notifiedKey, false)
                if (!alreadyNotified) {
                    NotificationHelper.notifyEpisodeAiringToday(appContext, show, nextEp)
                    prefs.edit().putBoolean(notifiedKey, true).apply()
                }
            }

            // 2. New season premiere coming in 1 week (7 days in advance)
            if (seasonPremiereEnabled && daysUntil == 7L && nextEp.episodeNumber == 1) {
                val notifiedKey = "notified_season_prem_${show.id}_s${nextEp.seasonNumber}"
                val alreadyNotified = prefs.getBoolean(notifiedKey, false)
                if (!alreadyNotified) {
                    NotificationHelper.notifySeasonPremiereInAdvance(appContext, show, nextEp, daysInAdvance = 7)
                    prefs.edit().putBoolean(notifiedKey, true).apply()
                }
            }
        }

        Result.success()
    }
}
