package com.liuvil.versati.activities.main.entry

import android.net.Uri
import android.text.Html
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.liuvil.versati.framework.entry.content.parseContentItems
import com.liuvil.versati.framework.view.Status
import com.liuvil.versati.framework.view.rememberViewStatusScope
import com.liuvil.versati.framework.viewmodel.bindViewModel
import java.time.OffsetDateTime
import java.time.ZoneId

@Composable
fun EntryView(
    id: Int
) {
    val statusScope = rememberViewStatusScope()
    val status by statusScope.status

    val viewModel = bindViewModel<Int, EntryViewModel>(id)
    val entry by viewModel.entry
    val enclosure by viewModel.enclosure

    LaunchedEffect(Unit) {
        statusScope.launchLoading {
            viewModel.loadAll()
        }
    }

    when (status) {
        Status.UNINITIALIZED, Status.LOADING ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                CircularProgressIndicator()
            }

        Status.IDLE -> entry?.let {
            val context = LocalContext.current
            val openURL: () -> Unit = remember {
                { openURLExternally(Uri.parse(it.url.toString()), context) }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        it.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(
                            onClick = openURL,
                            indication = null,
                            interactionSource = null
                        )
                    )

                    Text(
                        getEntryDetailsText(it.feedTitle, it.author, it.publishedAt)
                    )
                }

                enclosure?.let { enclosure ->
                    AsyncImage(
                        model = enclosure.url.toString(),
                        contentDescription = null,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                EntryContentView(
                    parseContentItems(it.content.html())
                )

                Button(onClick = openURL) {
                    Text("Open in web browser")
                }
            }
        }
    }
}

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