package com.liuvil.versati.repository.data

import java.net.URL

interface Enclosure {
    val id: Int
    val url: URL
    val mimeType: String
    val size: Int
    val entryId: Int
}
