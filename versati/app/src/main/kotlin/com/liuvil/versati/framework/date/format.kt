package com.liuvil.versati.framework.date

import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private const val LONG_DATE_PATTERN = "MMM MM, y"
private const val LONG_DATE_TIME_PATTERN = "MMM MM, y 'at' H:m a"

fun LocalDate.formatHumanReadableLong(): String =
    DateTimeFormatter.ofPattern(LONG_DATE_PATTERN).format(this)

fun OffsetDateTime.formatHumanReadableLong(): String =
    DateTimeFormatter.ofPattern(LONG_DATE_TIME_PATTERN).format(this)

fun LocalDate.formatHumanReadable(): String {
    val daysDifference = ChronoUnit.DAYS.between(this, LocalDate.now())
    return when (daysDifference) {
        0L -> "Today"
        1L -> "Yesterday"
        else -> formatHumanReadableLong()
    }
}