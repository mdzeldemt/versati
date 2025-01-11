package com.liuvil.versati.activities.main.entry

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.liuvil.versati.framework.android.openURLExternally
import com.liuvil.versati.framework.date.formatHumanReadableLong
import com.liuvil.versati.framework.preferences.entry.content.css.DEFAULT_ENTRY_CONTENT_STYLESHEET
import com.liuvil.versati.framework.viewmodel.bindViewModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.time.OffsetDateTime
import java.time.ZoneId

@Composable
fun EntryView(
    id: Int
) {
    val viewModel = bindViewModel<Int, EntryViewModel>(id)
    val entry by viewModel.entry
    val enclosure by viewModel.enclosure

    LaunchedEffect(Unit) {
        viewModel.loadEntry()

        entry.ifSuccess { entry ->
            if (!Jsoup.parse(entry.content).containsImages()) {
                entry.enclosureId?.let { enclosureId ->
                    viewModel.loadEnclosure(id = enclosureId)
                }
            }
        }
    }

    entry.let {
        it.ifSuccess { entry ->
            val context = LocalContext.current
            val openURL: () -> Unit = remember {
                { openURLExternally(Uri.parse(entry.url.toString()), context) }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                Text(
                    entry.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            onClick = openURL,
                            indication = null,
                            interactionSource = null
                        )
                )

                Text(
                    getEntryDetailsText(
                        entry.feedTitle,
                        entry.author,
                        entry.publishedAt
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                enclosure.ifSuccess { enclosure ->
                    AsyncImage(
                        model = enclosure.url.toString(),
                        contentDescription = null,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                EntryContentView(
                    entry.content,
                    DEFAULT_ENTRY_CONTENT_STYLESHEET
                )

                Button(onClick = openURL) {
                    Text("Open in web browser")
                }
            }
        } ?: Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            CircularProgressIndicator()
        }
    }
}

private fun Document.containsImages(): Boolean =
    select("img").isNotEmpty()

private fun getEntryDetailsText(
    feedTitle: String,
    author: String?,
    publishedAt: OffsetDateTime
): String =
    listOfNotNull(
        feedTitle,
        author?.let { "by $it" },
        publishedAt.atZoneSameInstant(ZoneId.systemDefault())
            .toOffsetDateTime()
            .formatHumanReadableLong()
    ).joinToString(separator = " / ")