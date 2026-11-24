package com.example.better_edt.data

import android.content.Context
import com.example.better_edt.model.EdtEvent
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

data class EventDto(
    val uid: String,
    val originalTitle: String,
    val displayTitle: String,
    val subject: String,
    val courseType: String,
    val groupTag: String,
    val teacher: String,
    val location: String,
    val startTimeIso: String,
    val endTimeIso: String,
    val rawDescription: String
)

data class DownloadedVersion(
    val id: String,
    val timestamp: Long,
    val formattedDate: String,
    val events: List<EventDto>
)

fun EdtEvent.toDto(): EventDto = EventDto(
    uid = uid,
    originalTitle = originalTitle,
    displayTitle = displayTitle,
    subject = subject,
    courseType = courseType,
    groupTag = groupTag,
    teacher = teacher,
    location = location,
    startTimeIso = startTime.toString(),
    endTimeIso = endTime.toString(),
    rawDescription = rawDescription
)

fun EventDto.toEdtEvent(): EdtEvent = EdtEvent(
    uid = uid,
    originalTitle = originalTitle,
    displayTitle = displayTitle,
    subject = subject,
    courseType = courseType,
    groupTag = groupTag,
    teacher = teacher,
    location = location,
    startTime = ZonedDateTime.parse(startTimeIso),
    endTime = ZonedDateTime.parse(endTimeIso),
    rawDescription = rawDescription
)

class VersionRepository(private val context: Context) {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val versionsFile: File
        get() = File(context.filesDir, "downloaded_versions.json")

    fun loadVersions(): List<DownloadedVersion> {
        return try {
            if (versionsFile.exists()) {
                val jsonText = versionsFile.readText(Charsets.UTF_8)
                val type = object : TypeToken<List<DownloadedVersion>>() {}.type
                val loaded: List<DownloadedVersion>? = gson.fromJson(jsonText, type)
                loaded ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun saveVersion(events: List<EdtEvent>): List<DownloadedVersion> {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm:ss", Locale.FRENCH)
            val dateStr = LocalDateTime.now().format(formatter)

            val newVersion = DownloadedVersion(
                id = UUID.randomUUID().toString(),
                timestamp = System.currentTimeMillis(),
                formattedDate = dateStr,
                events = events.map { it.toDto() }
            )

            val existing = loadVersions()
            val updated = (listOf(newVersion) + existing).take(MAX_VERSIONS)

            val jsonText = gson.toJson(updated)
            versionsFile.writeText(jsonText, Charsets.UTF_8)
            updated
        } catch (e: Exception) {
            e.printStackTrace()
            loadVersions()
        }
    }

    companion object {
        const val MAX_VERSIONS = 3
    }
}
