package com.example.better_edt.model

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class EdtEvent(
    val uid: String,
    val originalTitle: String,
    val displayTitle: String,
    val subject: String,
    val courseType: String, // "CM", "TD", "TP", "REUNION", or ""
    val groupTag: String, // "G1", "G2", "G3", "G4" or ""
    val teacher: String,
    val location: String,
    val startTime: ZonedDateTime,
    val endTime: ZonedDateTime,
    val rawDescription: String
) {
    val formattedTimeSpan: String
        get() {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            return "${startTime.format(formatter)} - ${endTime.format(formatter)}"
        }

    val dayOfWeek: String
        get() = startTime.dayOfWeek.name
}
