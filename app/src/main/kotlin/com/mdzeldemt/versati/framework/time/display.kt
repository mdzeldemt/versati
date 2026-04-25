package com.mdzeldemt.versati.framework.time

import java.time.Duration

fun toHumanReadable(
    duration: Duration
): String {
    return if (duration < Duration.ofMinutes(60)) {
        "${duration.toMinutes()}min"
    } else if (duration < Duration.ofHours(24)) {
        "${duration.toHours()}h"
    } else if (duration < Duration.ofDays(365)) {
        "${duration.toDays()}d"
    } else {
        "${duration.toDays() / 365}y"
    }
}