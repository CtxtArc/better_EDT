package com.example.better_edt.network

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class RawVEvent(
    val uid: String,
    val summary: String,
    val description: String,
    val location: String,
    val dtStartRaw: String,
    val dtEndRaw: String
)

object ICalParser {
    private val PARIS_ZONE = ZoneId.of("Europe/Paris")
    private val UTC_ZONE = ZoneId.of("UTC")

    /**
     * Unfolds RFC 5545 multiline parameters (lines starting with space/tab).
     */
    fun unfoldLines(icsContent: String): List<String> {
        val rawLines = icsContent.lines()
        val unfolded = mutableListOf<String>()

        for (line in rawLines) {
            if ((line.startsWith(" ") || line.startsWith("\t")) && unfolded.isNotEmpty()) {
                val lastIdx = unfolded.size - 1
                unfolded[lastIdx] = unfolded[lastIdx] + line.substring(1)
            } else {
                unfolded.add(line)
            }
        }
        return unfolded
    }

    /**
     * Parses all VEVENT components from raw iCal content.
     */
    fun parseVEvents(icsContent: String): List<RawVEvent> {
        val lines = unfoldLines(icsContent)
        val events = mutableListOf<RawVEvent>()

        var inEvent = false
        var uid = ""
        var summary = ""
        var description = ""
        var location = ""
        var dtStartRaw = ""
        var dtEndRaw = ""
        var lastKey = ""

        for (line in lines) {
            val trimmed = line.trim()
            when {
                trimmed.equals("BEGIN:VEVENT", ignoreCase = true) -> {
                    inEvent = true
                    uid = ""
                    summary = ""
                    description = ""
                    location = ""
                    dtStartRaw = ""
                    dtEndRaw = ""
                    lastKey = ""
                }
                trimmed.equals("END:VEVENT", ignoreCase = true) -> {
                    if (inEvent) {
                        events.add(
                            RawVEvent(
                                uid = uid,
                                summary = decodeText(summary),
                                description = decodeText(description),
                                location = decodeText(location),
                                dtStartRaw = dtStartRaw,
                                dtEndRaw = dtEndRaw
                            )
                        )
                        inEvent = false
                        lastKey = ""
                    }
                }
                inEvent -> {
                    val colonIndex = trimmed.indexOf(':')
                    if (colonIndex != -1) {
                        val keyPart = trimmed.substring(0, colonIndex).uppercase()
                        val valuePart = trimmed.substring(colonIndex + 1)

                        when {
                            keyPart == "UID" || keyPart.startsWith("UID;") -> {
                                uid = valuePart
                                lastKey = "UID"
                            }
                            keyPart == "SUMMARY" || keyPart.startsWith("SUMMARY;") -> {
                                summary = valuePart
                                lastKey = "SUMMARY"
                            }
                            keyPart == "DESCRIPTION" || keyPart.startsWith("DESCRIPTION;") -> {
                                description = valuePart
                                lastKey = "DESCRIPTION"
                            }
                            keyPart == "LOCATION" || keyPart.startsWith("LOCATION;") -> {
                                location = valuePart
                                lastKey = "LOCATION"
                            }
                            keyPart == "DTSTART" || keyPart.startsWith("DTSTART;") -> {
                                dtStartRaw = valuePart
                                lastKey = "DTSTART"
                            }
                            keyPart == "DTEND" || keyPart.startsWith("DTEND;") -> {
                                dtEndRaw = valuePart
                                lastKey = "DTEND"
                            }
                            else -> lastKey = ""
                        }
                    } else if (lastKey.isNotEmpty()) {
                        when (lastKey) {
                            "SUMMARY" -> summary += "\n$trimmed"
                            "DESCRIPTION" -> description += "\n$trimmed"
                            "LOCATION" -> location += "\n$trimmed"
                        }
                    }
                }
            }
        }
        return events
    }

    /**
     * Decodes iCal text escape sequences (\n, \, , \;, \\).
     */
    fun decodeText(text: String): String {
        return text
            .replace("\\n", "\n")
            .replace("\\N", "\n")
            .replace("\\,", ",")
            .replace("\\;", ";")
            .replace("\\\\", "\\")
            .trim()
    }

    /**
     * Converts iCal date string (e.g. "20260905T080000Z", "TZID=Europe/Paris:20260905T080000" or "20260905T080000")
     * to ZonedDateTime in Europe/Paris zone.
     */
    fun parseDateTime(rawDateTime: String): ZonedDateTime {
        val valueAfterColon = if (rawDateTime.contains(":")) {
            rawDateTime.substringAfterLast(":")
        } else {
            rawDateTime
        }
        val cleanValue = valueAfterColon.trim()
        if (cleanValue.isEmpty()) {
            return ZonedDateTime.now(PARIS_ZONE)
        }

        return try {
            if (cleanValue.endsWith("Z", ignoreCase = true)) {
                val dateStr = cleanValue.substring(0, cleanValue.length - 1)
                val formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
                val localDt = LocalDateTime.parse(dateStr, formatter)
                localDt.atZone(UTC_ZONE).withZoneSameInstant(PARIS_ZONE)
            } else if (cleanValue.length >= 15 && cleanValue.contains("T")) {
                val dateStr = cleanValue.take(15)
                val formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
                val localDt = LocalDateTime.parse(dateStr, formatter)
                localDt.atZone(PARIS_ZONE)
            } else if (cleanValue.length >= 8) {
                val dateStr = cleanValue.take(8) + "T000000"
                val formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
                val localDt = LocalDateTime.parse(dateStr, formatter)
                localDt.atZone(PARIS_ZONE)
            } else {
                ZonedDateTime.now(PARIS_ZONE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ZonedDateTime.now(PARIS_ZONE)
        }
    }
}
