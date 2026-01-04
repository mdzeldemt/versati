package com.liuvil.versati.framework.string

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

fun isValidURL(value: String): Boolean {
    return value.toHttpUrlOrNull() != null
}