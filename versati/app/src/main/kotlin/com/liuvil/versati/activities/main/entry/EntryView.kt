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
import com.liuvil.versati.framework.lazy.Success
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
        when (it) {
            is Success -> {
                val context = LocalContext.current
                val openURL: () -> Unit = remember {
                    { openURLExternally(Uri.parse(it.value.url.toString()), context) }
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
                        it.value.title,
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
                            it.value.feedTitle,
                            it.value.author,
                            it.value.publishedAt
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

                    EntryContentView(it.value.content, DEFAULT_STYLE)

                    Button(onClick = openURL) {
                        Text("Open in web browser")
                    }
                }
            }

            else -> Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

// TODO: Save as a default in an asset file and make configurable
private const val DEFAULT_STYLE = """
    body {
        margin: 0;
        padding 0;
        font-size: 12pt !important;
    }

    img {
        max-width: 100%;
    }
"""

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