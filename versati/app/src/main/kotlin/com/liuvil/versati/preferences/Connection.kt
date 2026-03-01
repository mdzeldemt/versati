package com.liuvil.versati.preferences

import java.net.URL

data class Connection (
    val id: Long,
    val name: String,
    val baseUrl: URL
)