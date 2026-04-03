package com.liuvil.versati.repository.api.data

import java.net.URL

data class Enclosure(
    val id: Int,
    val userId: Int,
    val entryId: Int,
    val url: URL,
    val mimeType: String,
    val size: Int,
    val mediaProgression: Int
)
