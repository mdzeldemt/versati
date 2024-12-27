package com.liuvil.versati.activities.main.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.liuvil.versati.framework.entry.content.EntryContentItem
import com.liuvil.versati.framework.entry.content.EntryImageItem
import com.liuvil.versati.framework.entry.content.EntryTextParagraphItem

@Composable
internal fun EntryContentView(
    content: List<EntryContentItem>
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        content.forEach {
            when (it) {
                is EntryTextParagraphItem ->
                    Text(
                        text = it.text,
                        modifier = Modifier.fillMaxWidth()
                    )

                is EntryImageItem ->
                    AsyncImage(
                        model = it.url,
                        contentDescription = null
                    )
            }
        }
    }
}
