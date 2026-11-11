package com.example.tvtime

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.tvtime.notifications.NotificationHelper
import com.example.tvtime.notifications.NotificationScheduler
import com.example.tvtime.theme.TVTimeTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            NotificationScheduler.scheduleDailyEpisodeCheck(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install Material Design splash screen before super.onCreate
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // Setup notification channels and daily morning scheduler
        NotificationHelper.createNotificationChannels(this)
        NotificationScheduler.scheduleDailyEpisodeCheck(this)

        // Prompt for notification permission on Android 13+ (API 33+)
        checkNotificationPermission()

        enableEdgeToEdge()
        setContent {
            TVTimeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation()
                }
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
