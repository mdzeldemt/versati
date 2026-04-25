package com.mdzeldemt.versati.repository.data

import java.net.URL
import java.time.OffsetDateTime

interface Feed {
    val id: Int
    val feedUrl: URL
    val siteUrl: URL
    val title: String
    val description: String
    val checkedAt: OffsetDateTime
    val nextCheckAt: OffsetDateTime
    val categoryId: Int
    val hideGlobally: Boolean
    val iconId: Int
    val parsingErrorCount: Int
    val parsingErrorMessage: String
}
