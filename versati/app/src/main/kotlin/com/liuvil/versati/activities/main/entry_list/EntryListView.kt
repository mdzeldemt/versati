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
import com.liuvil.versati.api.data.EntryStatus
import com.liuvil.versati.framework.mimetype.isImageMimeType
import org.jsoup.Jsoup
import java.net.URL
import java.time.Duration
import java.time.OffsetDateTime

data class Entry(
    val id: Int,
    val title: String,
    val feedTitle: String,
    val publishedAt: OffsetDateTime,
    val content: EntryContent,
    val imageURL: URL?,
    val isRead: Boolean
)

data class EntryContent(
    val text: String,
    val imageURLs: List<URL>
)

@Composable
fun EntryListView(
    entries: List<Entry>,
    entryPadding: Dp = 10.dp,
    contentSpacing: Dp = 8.dp,
    onEntryTileClicked: (Int) -> Unit
) {
    Column (
        verticalArrangement = Arrangement.spacedBy(contentSpacing),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        entries.forEach {
            Box(
                Modifier
                    .clickable { onEntryTileClicked(it.id) }
                    .padding(entryPadding)
            ) {
                EntryTile(
                    title = it.title,
                    feedTitle = it.feedTitle,
                    timeSincePublished = Duration.between(it.publishedAt, OffsetDateTime.now()),
                    content = it.content.text,
                    imageUrl = it.imageURL ?: it.content.imageURLs.firstOrNull(),
                    isRead = it.isRead
                )
            }
        }
    }
}

fun buildFromAPIModel(entry: com.liuvil.versati.api.data.Entry): Entry =
    Entry(
        id = entry.id,
        title = entry.title,
        feedTitle = entry.feed.title,
        publishedAt = entry.publishedAt,
        content = parseEntryContent(entry.content),
        imageURL = entry.enclosures
            .find { isImageMimeType(it.mimeType)}
            ?.url
        ,
        isRead = entry.status == EntryStatus.READ
    )

// TODO: Move to separate package
fun parseEntryContent(entryContent: String): EntryContent {
    val document = Jsoup.parse(entryContent)
    return EntryContent(
        text = document.text(),
        imageURLs = document.getElementsByTag("img")
            .mapNotNull { it.attribute("src") }
            .map { URL(it.value) }
    )
}