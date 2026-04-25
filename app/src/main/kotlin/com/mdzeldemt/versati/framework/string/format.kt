package com.mdzeldemt.versati.framework.string

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

fun isValidUrl(value: String): Boolean {
    return value.toHttpUrlOrNull() != null
}