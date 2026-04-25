package com.mdzeldemt.versati.framework.random

import java.util.Base64
import java.util.Random

fun Random.nextString(length: Int): String {
    val byteArray = ByteArray(length)
    nextBytes(byteArray)
    return Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(byteArray)
        .take(length)
}