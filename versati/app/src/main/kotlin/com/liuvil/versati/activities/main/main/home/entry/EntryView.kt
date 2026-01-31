package com.liuvil.versati.activities.main.main.home.entry

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
import androidx.compose.material.icons.filled.Share
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
import com.liuvil.versati.framework.android.openShareSheet
import com.liuvil.versati.framework.android.openURLExternally
import com.liuvil.versati.framework.date.formatHumanReadableLong
import com.liuvil.versati.framework.lazy.Success
import com.liuvil.versati.framework.preferences.entry.content.css.DEFAULT_ENTRY_CONTENT_STYLESHEET
import com.liuvil.versati.framework.viewmodel.viewOf
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL
import java.time.OffsetDateTime
import java.time.ZoneId

private sealed class Dialog {
    data class Details(
        val id: Int,
        val url: URL,
        val author: String?,
        val createdAt: OffsetDateTime,
        val publishedAt: OffsetDateTime,
        val read: Boolean
    ): Dialog()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryView(
    entryID: Int,
    onDismiss: () -> Unit
) = viewOf<InitData, EntryViewModel>(
    InitData(entryID)
) { viewModel ->
    val entry by viewModel.entry
    val starred by viewModel.starred

    var activeDialog by remember { mutableStateOf<Dialog?>(null) }

    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadEntry()
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
                    IconButton(
                        enabled = entry is Success,
                        onClick = {
                            entry.ifSuccess { entry ->
                                context.openShareSheet(
                                    title = "Share entry link",
                                    content = entry.url.toString()
                                )
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null
                        )
                    }

                    IconButton(
                        enabled = starred is Success,
                        onClick = {
                            coroutineScope.launch {
                                viewModel.toggleStarred()
                            }
                        }
                    ) {
                        Icon(
                            imageVector =
                                starred.ifSuccess { starred ->
                                    if (starred)
                                        Icons.Filled.Star
                                    else
                                        Icons.Outlined.StarOutline
                                } ?: Icons.Filled.Star,
                            contentDescription = null
                        )
                    }

                    IconButton(
                        enabled = entry is Success,
                        onClick = {
                            entry.ifSuccess {
                                activeDialog = Dialog.Details(
                                    id = entryID,
                                    url = it.url,
                                    author = it.author,
                                    createdAt = it.createdAt,
                                    publishedAt = it.publishedAt,
                                    read = it.read
                                )
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->
        entry.ifSuccess {
            val openURL: () -> Unit = remember {
                {
                    context.openURLExternally(
                        Uri.parse(it.url.toString())
                    )
                }
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

                if (!Jsoup.parse(it.content).containsImages()) {
                    it.enclosureUrl?.let { enclosureUrl ->
                        AsyncImage(
                            model = enclosureUrl.toString(),
                            contentDescription = null,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }

                EntryContentView(
                    it.content,
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

    activeDialog?.let {
        when (it) {
            is Dialog.Details ->
                AlertDialog(
                    onDismissRequest = { activeDialog = null },
                    title = {
                        Text("Entry details")
                    },
                    text = {
                        Text(
                            getEntryLongDetailsText(
                                id = it.id,
                                url = it.url,
                                author = it.author,
                                createdAt = it.createdAt,
                                publishedAt = it.publishedAt,
                                read = it.read
                            )
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = { activeDialog = null }) {
                            Text("Close")
                        }
                    }
                )
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
    id: Int,
    url: URL,
    author: String?,
    createdAt: OffsetDateTime,
    publishedAt: OffsetDateTime,
    read: Boolean
): AnnotatedString = buildAnnotatedString {
    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append("id: ")
    }
    append("$id")

    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append("\nURL: ")
    }
    withLink(
        LinkAnnotation.Url(
            url.toString(),
            TextLinkStyles(SpanStyle(textDecoration = TextDecoration.Underline))
        )
    ) {
        append(url.toString())
    }

    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append("\nauthor: ")
    }
    author?.let { author ->
        append(author)
    } ?: withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
        append("none")
    }

    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append("\ncreated at: ")
    }
    append(createdAt.formatHumanReadableLong())

    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append("\npublished at: ")
    }
    append(publishedAt.formatHumanReadableLong())

    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append("\nread: ")
    }
    append(if (read) "yes" else "no")
}