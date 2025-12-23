package com.liuvil.versati.framework.string

import java.net.URL

fun isValidURL(value: String): Boolean {
    return try {
        URL(value)
        true
    } catch (e: Exception) {
        false
    }
}