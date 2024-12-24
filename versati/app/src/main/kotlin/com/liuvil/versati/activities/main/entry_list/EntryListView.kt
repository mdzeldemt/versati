package com.liuvil.versati.activities.main.entry_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.liuvil.versati.activities.main.EntryTile
import java.net.URL
import java.time.Duration
import java.time.OffsetDateTime

data class Entry(
    val id: Int,
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
    entryPadding: Dp = 10.dp,
    contentSpacing: Dp = 8.dp,
    onEntryTileTapped: (Int) -> Unit
) {
    Column (
        verticalArrangement = Arrangement.spacedBy(contentSpacing),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        entries.forEach {
            Box(
                Modifier
                    .clickable { onEntryTileTapped(it.id) }
                    .padding(entryPadding)
            ) {
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
}