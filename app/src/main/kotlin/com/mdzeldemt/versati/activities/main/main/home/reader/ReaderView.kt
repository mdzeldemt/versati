package com.mdzeldemt.versati.activities.main.main.home.reader

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import coil3.compose.AsyncImage
import com.mdzeldemt.versati.components.LargeActionButton
import com.mdzeldemt.versati.framework.android.openShareSheet
import com.mdzeldemt.versati.framework.android.openUrlExternally
import com.mdzeldemt.versati.framework.compose.bold
import com.mdzeldemt.versati.framework.compose.scaledTo
import com.mdzeldemt.versati.framework.date.formatHumanReadableLong
import com.mdzeldemt.versati.framework.html.parse.HtmlDocument
import com.mdzeldemt.versati.framework.html.view.HtmlElement
import com.mdzeldemt.versati.framework.viewmodel.status.Status
import com.mdzeldemt.versati.framework.viewmodel.status.fold
import com.mdzeldemt.versati.framework.viewmodel.viewOf
import kotlinx.coroutines.launch
import java.net.URL
import java.time.OffsetDateTime
import java.time.ZoneId

private const val HEADING_LARGE_FONT_SIZE_FACTOR = 5f / 4
private const val HEADING_MEDIUM_FONT_SIZE_FACTOR = 4.5f / 4
private const val HEADING_SMALL_FONT_SIZE_FACTOR = 4f / 4
private val FAB_BAR_HEIGHT = 76.dp

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
fun ReaderView(
    entryId: Int,
    onDismiss: () -> Unit
) = viewOf<InitData, ReaderViewModel>(
    InitData(entryId)
) { viewModel ->
    val entry by viewModel.entry.collectAsState()
    val starred by viewModel.starred.collectAsState()

    val getEntryStatus by viewModel.getEntryStatus.collectAsState()
    val toggleStarredStatus by viewModel.toggleStarredStatus.collectAsState()

    var activeDialog by remember { mutableStateOf<Dialog?>(null) }

    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.onLoadEntry()
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
                        enabled = getEntryStatus == Status.Success,
                        onClick = {
                            context.openShareSheet(
                                title = "Share entry link",
                                content = entry!!.url.toString()
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null
                        )
                    }

                    IconButton(
                        enabled = fold(
                            getEntryStatus,
                            toggleStarredStatus
                        ) == Status.Success,
                        onClick = {
                            coroutineScope.launch {
                                viewModel.onToggleStarred()
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

                    IconButton(
                        enabled = getEntryStatus == Status.Success,
                        onClick = {
                            activeDialog = Dialog.Details(
                                id = entryId,
                                url = entry!!.url,
                                author = entry!!.author,
                                createdAt = entry!!.createdAt,
                                publishedAt = entry!!.publishedAt,
                                read = entry!!.read
                            )
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
        MaterialTheme(
            typography = MaterialTheme.typography
                .copy(
                    headlineLarge = MaterialTheme.typography.bodyLarge
                        .scaledTo(HEADING_LARGE_FONT_SIZE_FACTOR)
                        .bold(),
                    headlineMedium = MaterialTheme.typography.bodyLarge
                        .scaledTo(HEADING_MEDIUM_FONT_SIZE_FACTOR)
                        .bold(),
                    headlineSmall = MaterialTheme.typography.bodyLarge
                        .scaledTo(HEADING_SMALL_FONT_SIZE_FACTOR)
                        .bold()
                )
        ) {
            when (getEntryStatus) {
                is Status.Loading ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        CircularProgressIndicator()
                    }

                is Status.Success -> {
                    val openUrl: () -> Unit = remember {
                        {
                            context.openUrlExternally(
                                Uri.parse(entry!!.url.toString())
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(padding)
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(bottom = FAB_BAR_HEIGHT)
                        ) {
                            Text(
                                text = entry!!.title,
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        onClick = openUrl,
                                        indication = null,
                                        interactionSource = null
                                    )
                            )

                            Text(
                                getEntryShortDetailsText(
                                    entry!!.feedTitle,
                                    entry!!.author,
                                    entry!!.publishedAt
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            entry!!.imageUrl?.let { imageUrl ->
                                AsyncImage(
                                    model = imageUrl.toString(),
                                    contentDescription = null,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }

                            SelectionContainer {
                                EntryContentView(
                                    entry!!.document
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .height(FAB_BAR_HEIGHT)
                                .padding(16.dp)
                                .align(Alignment.BottomCenter)
                        ) {
                            LargeActionButton(
                                text = {
                                    Text("Open in web browser")
                                },
                                onClick = openUrl
                            )
                        }
                    }
                }

                is Status.Failure -> {
                    // TODO: Add error message
                }
            }
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

@Composable
private fun EntryContentView(
    document: HtmlDocument
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
    ) {
        document.content.forEach { block ->
            HtmlElement(block)
        }
    }
}

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