package com.liuvil.versati.activities.main

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.liuvil.versati.activities.main.entry_list.Entry
import com.liuvil.versati.activities.main.entry_list.EntryListView
import com.liuvil.versati.framework.date.formatHumanReadable
import java.time.LocalDate

data class FeedViewContent(
    val entryGroups: List<EntryGroup>
)

sealed class EntryGroup(
    open val entries: List<Entry>
) {
    data class Timed(
        val date: LocalDate,
        override val entries: List<Entry>
    ): EntryGroup(entries)

    data class Categorized(
        val categoryTitle: String,
        override val entries: List<Entry>
    ): EntryGroup(entries)
}



@Composable
fun FeedView(
    content: FeedViewContent,
    onEntryTileClicked: (Int) -> Unit,
    entryTitlePadding: Dp = 10.dp
) {
    content.entryGroups.forEach { entryGroup ->
        Text(
            text = when (entryGroup) {
                is EntryGroup.Timed -> entryGroup.date.formatHumanReadable()
                is EntryGroup.Categorized -> entryGroup.categoryTitle
            },
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth().padding(
                start = entryTitlePadding,
                top = entryTitlePadding,
                end = entryTitlePadding
            )
        )

        EntryListView(
            entries = entryGroup.entries,
            onEntryTileClicked = onEntryTileClicked
        )
    }
}
