package com.example.better_edt.network

import com.example.better_edt.data.CourseConfig
import com.example.better_edt.model.EdtEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class EdtFilterEngine(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()
) {

    suspend fun fetchAndFilterSchedule(
        config: CourseConfig,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null
    ): List<EdtEvent> = withContext(Dispatchers.IO) {
        val filteredEvents = mutableListOf<EdtEvent>()
        val addedUids = mutableSetOf<String>()
        val addedFingerprints = mutableSetOf<String>()

        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dateParams = if (startDate != null && endDate != null) {
            "&firstDate=${startDate.format(dateFormatter)}&lastDate=${endDate.format(dateFormatter)}"
        } else {
            ""
        }

        val academicYear = config.academicYear.ifBlank { "2026-2027" }

        // Process each group in order (G1, G2, G3, G4)
        for (groupTag in CourseConfig.ALL_GROUPS) {
            val resourceId = config.groupResources[groupTag] ?: continue
            if (resourceId.isBlank()) continue

            val activeCoursesForGroup = config.groupCourses[groupTag] ?: emptyList()

            val baseUrl = "https://edt.grenoble-inp.fr/directCal/$academicYear/etudiant/esisar?resources=$resourceId"
            val targetUrl = baseUrl + dateParams

            val requestBuilder = Request.Builder().url(targetUrl)

            if (config.username.isNotBlank() && config.password.isNotBlank()) {
                val credential = Credentials.basic(config.username, config.password)
                requestBuilder.header("Authorization", credential)
            }

            try {
                val response = client.newCall(requestBuilder.build()).execute()
                if (response.isSuccessful) {
                    val icsBody = response.body?.string().orEmpty()
                    val rawEvents = ICalParser.parseVEvents(icsBody)

                    for (rawEvent in rawEvents) {
                        val uid = rawEvent.uid.ifBlank { "${rawEvent.summary}_${rawEvent.dtStartRaw}" }
                        val originalTitle = rawEvent.summary
                        val titleUpper = originalTitle.uppercase()
                        val descOriginal = rawEvent.description
                        val descUpper = descOriginal.uppercase()

                        val fingerprint = "${titleUpper}_${rawEvent.dtStartRaw}_${rawEvent.dtEndRaw}"

                        var keep = false
                        var isMeeting = false

                        // 1. Global events & meetings
                        if (titleUpper.contains("RÉUNION") || titleUpper.contains("REUNION") ||
                            titleUpper.contains("INTERNATIONAL") || descUpper.contains("2A")
                        ) {
                            keep = true
                            isMeeting = true
                        } else {
                            // 2. Course filtering (CM, TD, TP) against active courses in table
                            for (course in activeCoursesForGroup) {
                                val cleanCourse = course.uppercase().trim()
                                if (cleanCourse.isNotBlank() &&
                                    (titleUpper.contains(cleanCourse) || descUpper.contains(cleanCourse))
                                ) {
                                    keep = true
                                    break
                                }
                            }
                        }

                        if (keep) {
                            if (addedUids.contains(uid) || addedFingerprints.contains(fingerprint)) {
                                continue
                            }
                            addedUids.add(uid)
                            addedFingerprints.add(fingerprint)

                            // Extract professor from description
                            val descLines = descOriginal.split('\n')
                                .map { it.trim() }
                                .filter { it.isNotEmpty() }

                            var prof = ""
                            for (line in descLines) {
                                if (line.contains("4AM") || line.contains("2A") || line.contains("_S7_")) {
                                    continue
                                }
                                if (line.startsWith("(Exporté")) {
                                    continue
                                }
                                prof = line.replace("* ", "").trim()
                                break
                            }

                            // Detect course type
                            var courseType = ""
                            if (!isMeeting) {
                                when {
                                    descUpper.contains("_CM_") -> courseType = "CM"
                                    descUpper.contains("_TD_") -> courseType = "TD"
                                    descUpper.contains("_TP_") -> courseType = "TP"
                                }
                            } else {
                                courseType = "REUNION"
                            }

                            // Matched subject extraction
                            val matchedSubject = activeCoursesForGroup.firstOrNull {
                                val code = it.uppercase().trim()
                                code.isNotBlank() && (titleUpper.contains(code) || descUpper.contains(code))
                            } ?: originalTitle.split(" ").firstOrNull().orEmpty()

                            // Group tag
                            val group = if (courseType.isNotBlank() && courseType != "REUNION") groupTag else ""

                            // Tags formatting
                            val tags = mutableListOf<String>()
                            if (courseType.isNotBlank() && courseType != "REUNION") tags.add(courseType)
                            if (group.isNotBlank()) tags.add(group)

                            val displayTitle = if (tags.isNotEmpty()) {
                                val tagFinal = tags.joinToString(" ")
                                "[$tagFinal] $originalTitle"
                            } else {
                                originalTitle
                            }

                            val startTime = ICalParser.parseDateTime(rawEvent.dtStartRaw)
                            val endTime = ICalParser.parseDateTime(rawEvent.dtEndRaw)

                            filteredEvents.add(
                                EdtEvent(
                                    uid = uid,
                                    originalTitle = originalTitle,
                                    displayTitle = displayTitle,
                                    subject = matchedSubject,
                                    courseType = courseType,
                                    groupTag = groupTag,
                                    teacher = prof,
                                    location = rawEvent.location,
                                    startTime = startTime,
                                    endTime = endTime,
                                    rawDescription = descOriginal
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Sort events chronologically by start time
        return@withContext filteredEvents.sortedBy { it.startTime }
    }
}
