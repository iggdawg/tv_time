package com.example.tvtime.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.tvtime.MainActivity
import com.example.tvtime.R
import com.example.tvtime.data.model.Episode
import com.example.tvtime.data.model.Show

object NotificationHelper {

    const val CHANNEL_ID_AIRING_TODAY = "tv_time_airing_today"
    const val CHANNEL_ID_NEW_SEASONS = "tv_time_new_seasons"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // 1. Channel for episodes airing today
            val todayChannel = NotificationChannel(
                CHANNEL_ID_AIRING_TODAY,
                "Episodes Airing Today",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Morning alerts for new episodes of your tracked TV shows airing today."
                enableLights(true)
                lightColor = 0xFFFFCC00.toInt()
                enableVibration(true)
            }

            // 2. Channel for new season premieres approaching
            val seasonsChannel = NotificationChannel(
                CHANNEL_ID_NEW_SEASONS,
                "New Season Premieres",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts 1 week in advance when a new season premiere of a tracked show is coming soon."
                enableLights(true)
                lightColor = 0xFFFFCC00.toInt()
                enableVibration(true)
            }

            manager.createNotificationChannel(todayChannel)
            manager.createNotificationChannel(seasonsChannel)
        }
    }

    private fun createContentIntent(context: Context, showId: Int? = null): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            showId?.let { putExtra("EXTRA_SHOW_ID", it) }
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getActivity(context, showId ?: 0, intent, flags)
    }

    fun notifyEpisodeAiringToday(context: Context, show: Show, episode: Episode) {
        val notificationId = 100000 + (show.id % 10000) * 100 + (episode.id % 100)

        val title = "📺 Airing Today: ${show.name}"
        val text = "${episode.episodeCode} • \"${episode.name}\" airs tonight on ${show.primaryNetwork}!"
        val bigText = buildString {
            append("Episode: ${episode.episodeCode} - \"${episode.name}\"\n")
            append("Network: ${show.primaryNetwork}\n")
            if (episode.overview.isNotBlank()) {
                append("\nSynopsis:\n${episode.overview}")
            }
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_AIRING_TODAY)
            .setSmallIcon(R.drawable.ic_launcher_monochrome)
            .setColor(ContextCompat.getColor(context, R.color.tv_time_yellow))
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setContentIntent(createContentIntent(context, show.id))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Permission not granted
        }
    }

    fun notifySeasonPremiereInAdvance(context: Context, show: Show, episode: Episode, daysInAdvance: Int = 7) {
        val notificationId = 200000 + (show.id % 10000) * 10 + (episode.seasonNumber % 10)

        val timePhrase = if (daysInAdvance == 7) "1 week" else "$daysInAdvance days"
        val title = "🎉 Season ${episode.seasonNumber} Premiere in $timePhrase!"
        val text = "${show.name} returns ${episode.formattedAirDate} on ${show.primaryNetwork}."
        val bigText = buildString {
            append("${show.name} Season ${episode.seasonNumber} premieres in $timePhrase!\n\n")
            append("Premiere Date: ${episode.formattedAirDate}\n")
            append("Episode 1: \"${episode.name}\"\n")
            append("Broadcast on: ${show.primaryNetwork}\n")
            if (episode.overview.isNotBlank()) {
                append("\nOverview:\n${episode.overview}")
            }
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_NEW_SEASONS)
            .setSmallIcon(R.drawable.ic_launcher_monochrome)
            .setColor(ContextCompat.getColor(context, R.color.tv_time_yellow))
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setContentIntent(createContentIntent(context, show.id))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_EVENT)

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Permission not granted
        }
    }

    fun sendMorningEpisodeAlert(
        context: Context,
        showName: String,
        episodeCode: String,
        episodeTitle: String,
        showId: Int
    ) {
        val fakeShow = Show(id = showId, name = showName, networks = listOf("TV Network"))
        val fakeEpisode = Episode(
            id = showId * 100 + 1,
            showId = showId,
            showName = showName,
            name = episodeTitle,
            airDate = java.time.LocalDate.now().toString(),
            episodeNumber = 1,
            seasonNumber = 1,
            network = "TV Network"
        )
        notifyEpisodeAiringToday(context, fakeShow, fakeEpisode)
    }

    fun sendSeasonPremiereAlert(
        context: Context,
        showName: String,
        seasonNumber: Int,
        daysInAdvance: Int,
        showId: Int
    ) {
        val fakeShow = Show(id = showId, name = showName, networks = listOf("TV Network"))
        val fakeEpisode = Episode(
            id = showId * 100 + seasonNumber,
            showId = showId,
            showName = showName,
            name = "Season $seasonNumber Premiere",
            airDate = java.time.LocalDate.now().plusDays(daysInAdvance.toLong()).toString(),
            episodeNumber = 1,
            seasonNumber = seasonNumber,
            network = "TV Network"
        )
        notifySeasonPremiereInAdvance(context, fakeShow, fakeEpisode, daysInAdvance = daysInAdvance)
    }

    fun sendTestNotification(context: Context, isSeasonPremiere: Boolean) {
        if (isSeasonPremiere) {
            sendSeasonPremiereAlert(context, "Severance", 2, 7, 95396)
        } else {
            sendMorningEpisodeAlert(context, "The Bear", "S04E01", "Every Second Counts", 125910)
        }
    }
}
