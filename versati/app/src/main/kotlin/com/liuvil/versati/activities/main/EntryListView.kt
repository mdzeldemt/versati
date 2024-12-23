package com.liuvil.versati.activities.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import com.liuvil.versati.components.EntryTile
import java.net.URL
import java.time.Duration
import java.time.OffsetDateTime

data class Entry(
    val title: String,
    val feedTitle: String,
    val publishedAt: OffsetDateTime,
    val content: EntryContent,
    val enclosures: List<Enclosure>
)

data class EntryContent(
    val text: String,
    val imageURLs: List<URL>
)

data class Enclosure(
    val url: URL
)

@Composable
fun EntryListView(
    entries: List<Entry>,
    contentSpacing: Dp
) {
    Column (
        verticalArrangement = Arrangement.spacedBy(contentSpacing),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        entries.forEach {
            EntryTile(
                title = it.title,
                feedTitle = it.feedTitle,
                timeSincePublished = Duration.between(it.publishedAt, OffsetDateTime.now()),
                content = it.content.text,
                imageUrl = it.enclosures.firstOrNull()?.url ?: it.content.imageURLs.firstOrNull()
            )
        }
    }
}