package com.example.better_edt

import com.example.better_edt.data.CourseConfig
import com.example.better_edt.network.EdtFilterEngine
import com.example.better_edt.network.ICalParser
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

class EdtEngineTest {

    @Test
    fun testICalParsingAndUnfolding() {
        val sampleIcs = """
            BEGIN:VCALENDAR
            VERSION:2.0
            PRODID:-//Test//
            BEGIN:VEVENT
            UID:test-uid-123
            SUMMARY:IN422 Architecture 
             Systemes
            DESCRIPTION:4AM\n_CM_ Course\n* Prof Dupont\n(Exporté le...)
            LOCATION:Room A101
            DTSTART:20260907T080000Z
            DTEND:20260907T100000Z
            END:VEVENT
            END:VCALENDAR
        """.trimIndent()

        val rawEvents = ICalParser.parseVEvents(sampleIcs)
        assertEquals(1, rawEvents.size)

        val event = rawEvents[0]
        assertEquals("test-uid-123", event.uid)
        assertEquals("IN422 Architecture Systemes", event.summary)
        assertTrue(event.description.contains("Prof Dupont"))
        assertEquals("Room A101", event.location)

        val startTime = ICalParser.parseDateTime(event.dtStartRaw)
        assertNotNull(startTime)
        assertEquals(ZoneId.of("Europe/Paris"), startTime.zone)
    }

    @Test
    fun testCourseConfigMatrix() {
        var config = CourseConfig()

        assertTrue(config.isCourseInGroup("IN422", "G1"))
        assertTrue(config.isCourseInGroup("AU424", "G3"))

        config = config.addCourse("TEST101", setOf("G2", "G4"))
        assertTrue(config.isCourseInGroup("TEST101", "G2"))
        assertTrue(config.isCourseInGroup("TEST101", "G4"))

        config = config.toggleCourseGroup("TEST101", "G1")
        assertTrue(config.isCourseInGroup("TEST101", "G1"))

        config = config.removeCourse("TEST101")
        assertTrue(!config.isCourseInGroup("TEST101", "G1"))
        assertTrue(!config.isCourseInGroup("TEST101", "G2"))
    }

    @Test
    fun testFetchAndMergeG1AndG3() = runBlocking {
        val filterEngine = EdtFilterEngine()
        val config = CourseConfig()

        val monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val sunday = monday.plusDays(6)

        val events = filterEngine.fetchAndFilterSchedule(
            config = config,
            startDate = monday,
            endDate = sunday
        )

        println("=== MERGED SCHEDULE (${events.size} events) for week $monday to $sunday ===")
        events.forEach { e ->
            println("[${e.groupTag}] ${e.displayTitle} | ${e.startTime.dayOfWeek} ${e.formattedTimeSpan} | Salle: ${e.location}")
        }
    }
}
