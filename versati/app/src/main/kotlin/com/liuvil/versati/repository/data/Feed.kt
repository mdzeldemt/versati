package com.liuvil.versati.repository.data

import java.net.URL

interface Feed {
    val id: Int
    val feedUrl: URL
    val siteUrl: URL
    val title: String
    val description: String
    val categoryId: Int
    val hideGlobally: Boolean
    val iconId: Int
}
