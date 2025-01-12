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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.liuvil.versati.framework.android.openURLExternally
import com.liuvil.versati.framework.date.formatHumanReadableLong
import com.liuvil.versati.framework.preferences.entry.content.css.DEFAULT_ENTRY_CONTENT_STYLESHEET
import com.liuvil.versati.framework.viewmodel.bindViewModel
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.time.OffsetDateTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryView(
    id: Int,
    onDismiss: () -> Unit
) {
    val viewModel = bindViewModel<Int, EntryViewModel>(id)
    val entry by viewModel.entry
    val enclosure by viewModel.enclosure
    val starred by viewModel.starred

    var showDetailsDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    entry.ifSuccess {
                        IconButton(
                            onClick = {
                                showDetailsDialog = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null
                            )
                        }
                    }

                    starred.ifSuccess { starred ->
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.toggleStarred()
                                }
                            }
                        ) {
                            Icon(
                                imageVector =
                                if (starred)
                                    Icons.Filled.Star
                                else
                                    Icons.Outlined.StarOutline,
                                contentDescription = null
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        entry.ifSuccess {
            val context = LocalContext.current
            val openURL: () -> Unit = remember {
                { openURLExternally(Uri.parse(it.url.toString()), context) }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
            ) {
                Text(
                    it.title,
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
                    getEntryShortDetailsText(
                        it.feedTitle,
                        it.author,
                        it.publishedAt
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
                    it.content,
                    DEFAULT_ENTRY_CONTENT_STYLESHEET
                )

                Button(onClick = openURL) {
                    Text("Open in web browser")
                }

                if (showDetailsDialog) {
                    starred.ifSuccess { starred ->
                        AlertDialog(
                            onDismissRequest = { showDetailsDialog = false },
                            title = { Text("Entry details") },
                            text = { Text(getEntryLongDetailsText(id, it, starred)) },
                            confirmButton = {
                                TextButton(onClick = { showDetailsDialog = false }) {
                                    Text("Close")
                                }
                            }
                        )
                    }
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

private fun getEntryShortDetailsText(
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

private fun getEntryLongDetailsText(
    entryId: Int,
    entry: Entry,
    starredEntry: Boolean
): AnnotatedString = buildAnnotatedString {
    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append("id: ")
    }
    append("$entryId")

    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append("\nURL: ")
    }
    withLink(
        LinkAnnotation.Url(
            entry.url.toString(),
            TextLinkStyles(SpanStyle(textDecoration = TextDecoration.Underline))
        )
    ) {
        append("${entry.url}")
    }

    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append("\nauthor: ")
    }
    entry.author?.let { author ->
        append(author)
    } ?: withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
        append("none")
    }

    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append("\ncreated at: ")
    }
    append("${entry.createdAt}")

    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append("\npublished at: ")
    }
    append("${entry.publishedAt}")

    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append("\nread: ")
    }
    append("${entry.read}")

    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append("\nstarred: ")
    }
    append("$starredEntry")
}