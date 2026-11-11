# TV Time Clone for Android

A modern Android clone of the TV Time app built with Jetpack Compose, Kotlin, and Material Design 3. The application is designed to track upcoming episodes of user-specified TV shows, providing automated calendar schedules, rich show discovery via The Movie Database (TMDB) API, and background morning and season premiere notifications.

---

## Features

### 1. Upcoming Episodes Schedule
- **Automated Tracking Calendar**: Aggregates the next upcoming episode for all tracked shows.
- **Dynamic Chronological Categorization**:
  - `Today` (highlighted in red)
  - `Tomorrow` (highlighted in TV Time gold)
  - `This Week`
  - `Coming Up Later`
  - `Date TBA` (announcement pending)
- **Automatic Refresh on Open**: The Upcoming tab automatically refreshes whenever the app is opened or brought back to the foreground.
- **Filter Chips**: Quickly filter episodes by *All*, *This Week*, *Later*, and *TBA*.
- **Watched Checkmark**: Mark episodes as watched directly from the card.

### 2. Watchlist Management
- Full catalogue of tracked TV series.
- Shows total seasons, episodes, network badges (HBO, Apple TV+, FX, Netflix, etc.), and status pills (*Returning Series*, *Ended*, *In Production*).
- Instant search filter across tracked series.
- One-tap untrack / delete from watchlist.

### 3. Discover & TMDB Integration
- Search across TMDB's television library.
- Trending TV shows list populated on launch.
- 1-tap `+ Track` / `✓ Tracked` toggle button right from the list.
- Fallback offline sample catalog preloaded with popular shows (*Severance*, *The Bear*, *House of the Dragon*, *Stranger Things*, *From*, *The Last of Us*, etc.).
- Optional custom TMDB API v3 key configuration in Settings.

### 4. Background Notifications (WorkManager)
- **Morning Episode Airing Alert**: At 8:00 AM daily, checks tracked shows and notifies you if a new episode airs that day (`📺 Airing Today: <Show Name>`).
- **1-Week Season Premiere Alert**: Alerts 7 days in advance when a new season premiere is approaching (`🎉 Season <N> Premiere in 1 week!`).
- **Notification Settings**: Toggle morning alerts and season premiere alerts independently, with built-in test buttons to preview notification formatting immediately.

### 5. Material 3 Design & Adaptive Icons
- Iconic TV Time yellow & deep dark theme palette (`#FFCC00` accent on `#0D0F17` background).
- Material Design adaptive icons with full vector layers:
  - Vector foreground (`ic_launcher_foreground.xml`)
  - Background (`ic_launcher_background.xml`)
  - Themed monochrome vector (`ic_launcher_monochrome.xml`)
  - Full raster mipmaps (`mdpi`, `hdpi`, `xhdpi`, `xxhdpi`, `xxxhdpi`)
- Android 12+ SplashScreen API integration (`androidx.core.splashscreen`).

---

## Project Structure

```
tv_time/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/example/tvtime/
│       │   │   ├── MainActivity.kt
│       │   │   ├── Navigation.kt
│       │   │   ├── NavigationKeys.kt
│       │   │   ├── data/
│       │   │   │   ├── api/
│       │   │   │   │   ├── TmdbApi.kt
│       │   │   │   │   └── SampleData.kt
│       │   │   │   ├── model/
│       │   │   │   │   ├── Show.kt
│       │   │   │   │   ├── Episode.kt
│       │   │   │   │   ├── TrackedShow.kt
│       │   │   │   │   └── UpcomingSection.kt
│       │   │   │   └── repository/
│       │   │   │       └── TvRepository.kt
│       │   │   ├── notifications/
│       │   │   │   ├── NotificationHelper.kt
│       │   │   │   ├── NotificationScheduler.kt
│       │   │   │   └── EpisodeNotificationWorker.kt
│       │   │   ├── theme/
│       │   │   │   ├── Color.kt
│       │   │   │   ├── Theme.kt
│       │   │   │   └── Type.kt
│       │   │   └── ui/
│       │   │       ├── components/
│       │   │       │   └── Badges.kt
│       │   │       ├── detail/
│       │   │       │   └── ShowDetailSheet.kt
│       │   │       ├── discover/
│       │   │       │   └── DiscoverScreen.kt
│       │   │       ├── main/
│       │   │       │   ├── MainScreen.kt
│       │   │       │   └── MainScreenViewModel.kt
│       │   │       ├── settings/
│       │   │       │   └── SettingsDialog.kt
│       │   │       ├── shows/
│       │   │       │   └── ShowsScreen.kt
│       │   │       └── upcoming/
│       │   │           ├── UpcomingEpisodeCard.kt
│       │   │           └── UpcomingScreen.kt
│       │   └── res/
│       │       ├── drawable/
│       │       ├── mipmap-*/
│       │       ├── values/
│       │       └── xml/
│       └── test/java/com/example/tvtime/data/
│           └── EpisodeTrackingTest.kt
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
├── build.gradle.kts
├── settings.gradle.kts
├── tv_time.apk
└── README.md
```

---

## Building and Running

### Prerequisites
- JDK 17
- Android SDK (API 35, Build-tools 35.0.0 or 36.0.0)

### Run Unit Tests
```bash
./gradlew test
```

### Build Debug APK
```bash
./gradlew assembleDebug
```
The compiled debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk` and also provided directly at `tv_time.apk` in the root folder.

### Install on Device or Emulator
```bash
adb install -r tv_time.apk
```

---

## Configuration

### TMDB API Key
By default, the application runs out-of-the-box with rich offline sample data. To connect to live TMDB data:
1. Obtain a free API key from [The Movie Database (TMDB)](https://www.themoviedb.org/settings/api).
2. In the app, tap the **Settings** gear icon in the top right.
3. Paste your TMDB v3 API Key and tap **Save Key**.

---

## License
MIT License.
