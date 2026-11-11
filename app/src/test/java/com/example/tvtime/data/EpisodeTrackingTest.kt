package com.example.tvtime.data

import com.example.tvtime.data.model.Episode
import com.example.tvtime.data.model.UpcomingSection
import com.example.tvtime.data.model.categorizeEpisode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class EpisodeTrackingTest {

    private val referenceDate = LocalDate.of(2026, 11, 11)

    @Test
    fun testEpisodeCodeFormatting() {
        val ep1 = Episode(id = 1, seasonNumber = 1, episodeNumber = 5)
        assertEquals("S01E05", ep1.episodeCode)

        val ep2 = Episode(id = 2, seasonNumber = 12, episodeNumber = 24)
        assertEquals("S12E24", ep2.episodeCode)
    }

    @Test
    fun testCountdownCalculation() {
        // Today
        val todayEp = Episode(
            id = 1,
            airDate = referenceDate.toString()
        )
        assertEquals(0L, todayEp.daysUntil(referenceDate))
        assertEquals("Today", todayEp.countdownDisplay(referenceDate))

        // Tomorrow
        val tomorrowEp = Episode(
            id = 2,
            airDate = referenceDate.plusDays(1).toString()
        )
        assertEquals(1L, tomorrowEp.daysUntil(referenceDate))
        assertEquals("Tomorrow", tomorrowEp.countdownDisplay(referenceDate))

        // In 4 days
        val fourDaysEp = Episode(
            id = 3,
            airDate = referenceDate.plusDays(4).toString()
        )
        assertEquals(4L, fourDaysEp.daysUntil(referenceDate))
        assertEquals("In 4 days", fourDaysEp.countdownDisplay(referenceDate))

        // In 1 week (7 days)
        val oneWeekEp = Episode(
            id = 4,
            airDate = referenceDate.plusDays(7).toString()
        )
        assertEquals(7L, oneWeekEp.daysUntil(referenceDate))
        assertEquals("In 1 week", oneWeekEp.countdownDisplay(referenceDate))

        // Past episode (2 days ago)
        val pastEp = Episode(
            id = 5,
            airDate = referenceDate.minusDays(2).toString()
        )
        assertEquals(-2L, pastEp.daysUntil(referenceDate))
        assertEquals("2d ago", pastEp.countdownDisplay(referenceDate))
    }

    @Test
    fun testCategorizeEpisode() {
        val todayEp = Episode(id = 1, airDate = referenceDate.toString())
        assertEquals(UpcomingSection.TODAY, categorizeEpisode(todayEp, referenceDate))

        val tomorrowEp = Episode(id = 2, airDate = referenceDate.plusDays(1).toString())
        assertEquals(UpcomingSection.TOMORROW, categorizeEpisode(tomorrowEp, referenceDate))

        val thisWeekEp = Episode(id = 3, airDate = referenceDate.plusDays(5).toString())
        assertEquals(UpcomingSection.THIS_WEEK, categorizeEpisode(thisWeekEp, referenceDate))

        val laterEp = Episode(id = 4, airDate = referenceDate.plusDays(15).toString())
        assertEquals(UpcomingSection.LATER, categorizeEpisode(laterEp, referenceDate))

        val tbaEp = Episode(id = 5, airDate = null)
        assertEquals(UpcomingSection.TBA, categorizeEpisode(tbaEp, referenceDate))
    }

    @Test
    fun testSeasonPremiereIdentification() {
        val premiere = Episode(id = 1, seasonNumber = 2, episodeNumber = 1)
        assertTrue(premiere.isSeasonPremiere)

        val midSeason = Episode(id = 2, seasonNumber = 2, episodeNumber = 2)
        assertFalse(midSeason.isSeasonPremiere)
    }

    @Test
    fun testSeasonPremiereOneWeekAdvanceNotificationQualification() {
        val seasonPremiereNextWeek = Episode(
            id = 10,
            seasonNumber = 3,
            episodeNumber = 1,
            airDate = referenceDate.plusDays(7).toString()
        )

        val daysUntil = seasonPremiereNextWeek.daysUntil(referenceDate)
        assertEquals(7L, daysUntil)
        assertTrue(seasonPremiereNextWeek.isSeasonPremiere)

        // Episode 2 in 7 days should NOT qualify as season premiere
        val regularEpNextWeek = Episode(
            id = 11,
            seasonNumber = 3,
            episodeNumber = 2,
            airDate = referenceDate.plusDays(7).toString()
        )
        assertFalse(regularEpNextWeek.isSeasonPremiere)
    }
}
