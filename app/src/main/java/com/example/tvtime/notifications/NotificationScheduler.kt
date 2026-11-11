package com.example.tvtime.notifications

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object NotificationScheduler {
    private const val WORK_NAME_DAILY = "daily_episode_check"
    private const val WORK_NAME_IMMEDIATE = "immediate_episode_check"
    private const val PREFS_NAME = "tv_time_notification_prefs"
    private const val KEY_MORNING_ENABLED = "morning_episodes_enabled"
    private const val KEY_SEASON_ENABLED = "season_premiere_enabled"

    fun isMorningAlertsEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_MORNING_ENABLED, true)
    }

    fun setMorningAlertsEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_MORNING_ENABLED, enabled).apply()
        updateWorkSchedule(context)
    }

    fun isSeasonAlertsEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_SEASON_ENABLED, true)
    }

    fun setSeasonAlertsEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SEASON_ENABLED, enabled).apply()
        updateWorkSchedule(context)
    }

    fun scheduleDailyEpisodeCheck(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val initialDelay = calculateInitialDelayTo8AM()

        val dailyWorkRequest = PeriodicWorkRequestBuilder<EpisodeNotificationWorker>(
            24, TimeUnit.HOURS,
            15, TimeUnit.MINUTES // Flex interval
        )
            .setConstraints(constraints)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME_DAILY,
            ExistingPeriodicWorkPolicy.UPDATE,
            dailyWorkRequest
        )
    }

    private fun updateWorkSchedule(context: Context) {
        val anyEnabled = isMorningAlertsEnabled(context) || isSeasonAlertsEnabled(context)
        if (anyEnabled) {
            scheduleDailyEpisodeCheck(context)
        } else {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME_DAILY)
        }
    }

    fun triggerImmediateCheck(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val immediateWork = OneTimeWorkRequestBuilder<EpisodeNotificationWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME_IMMEDIATE,
            ExistingWorkPolicy.REPLACE,
            immediateWork
        )
    }

    private fun calculateInitialDelayTo8AM(): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        return target.timeInMillis - now.timeInMillis
    }
}
