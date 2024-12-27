package com.liuvil.versati.framework.date

import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal

private const val LONG_DATE_PATTERN = "MMM MM, y 'at' H:m a"

fun Temporal.formatHumanReadableLong(): String =
    DateTimeFormatter.ofPattern(LONG_DATE_PATTERN).format(this)
}