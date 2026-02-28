package com.liuvil.versati.repository.api.data

import java.time.OffsetDateTime

data class Version(
    val version: String,
    val commit: String,
    val buildDate: OffsetDateTime,
    val goVersion: String,
    val compiler: String,
    val arch: String,
    val os: String
)
