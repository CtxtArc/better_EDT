package com.example.better_edt

import com.example.better_edt.data.DownloadedVersion
import com.example.better_edt.data.EventDto
import com.example.better_edt.data.toEdtEvent
import com.example.better_edt.model.EdtEvent
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZonedDateTime

class VersionRepositoryTest {

    @Test
    fun testVersionRotationMaxThree() {
        val versions = mutableListOf<DownloadedVersion>()

        fun addVersion(id: String, date: String): List<DownloadedVersion> {
            val newVer = DownloadedVersion(id = id, timestamp = System.currentTimeMillis(), formattedDate = date, events = emptyList())
            val updated = (listOf(newVer) + versions).take(3)
            versions.clear()
            versions.addAll(updated)
            return versions
        }

        addVersion("v1", "01/01/2026 10:00")
        assertEquals(1, versions.size)
        assertEquals("v1", versions.first().id)

        addVersion("v2", "01/01/2026 11:00")
        assertEquals(2, versions.size)
        assertEquals("v2", versions[0].id)
        assertEquals("v1", versions[1].id)

        addVersion("v3", "01/01/2026 12:00")
        assertEquals(3, versions.size)
        assertEquals("v3", versions[0].id)
        assertEquals("v2", versions[1].id)
        assertEquals("v1", versions[2].id)

        // Adding 4th version: v1 (oldest) should be replaced
        addVersion("v4", "01/01/2026 13:00")
        assertEquals(3, versions.size)
        assertEquals("v4", versions[0].id)
        assertEquals("v3", versions[1].id)
        assertEquals("v2", versions[2].id)
    }

    @Test
    fun testEdtEventDtoConversion() {
        val now = ZonedDateTime.now()
        val event = EdtEvent(
            uid = "uid123",
            originalTitle = "IN422 CM",
            displayTitle = "[CM G1] IN422 CM",
            subject = "IN422",
            courseType = "CM",
            groupTag = "G1",
            teacher = "Prof Dupont",
            location = "A101",
            startTime = now,
            endTime = now.plusHours(2),
            rawDescription = "Desc test"
        )

        val dto = EventDto(
            uid = event.uid,
            originalTitle = event.originalTitle,
            displayTitle = event.displayTitle,
            subject = event.subject,
            courseType = event.courseType,
            groupTag = event.groupTag,
            teacher = event.teacher,
            location = event.location,
            startTimeIso = event.startTime.toString(),
            endTimeIso = event.endTime.toString(),
            rawDescription = event.rawDescription
        )

        val converted = dto.toEdtEvent()
        assertEquals(event.uid, converted.uid)
        assertEquals(event.displayTitle, converted.displayTitle)
        assertEquals(event.startTime.toInstant(), converted.startTime.toInstant())
        assertEquals(event.endTime.toInstant(), converted.endTime.toInstant())
    }
}
