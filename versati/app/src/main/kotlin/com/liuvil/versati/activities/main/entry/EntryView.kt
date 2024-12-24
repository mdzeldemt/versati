package com.liuvil.versati.activities.main.entry

import android.net.Uri
import android.webkit.WebView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import coil3.compose.AsyncImage
import com.liuvil.versati.framework.android.openURLExternally
import com.liuvil.versati.framework.viewmodel.bindViewModel

@Composable
fun EntryView(
    id: Int
) {
    val viewModel = bindViewModel<Int, EntryViewModel>(id)
    val status by viewModel.status.collectAsState()
    val entry by viewModel.entry.collectAsState()
    val enclosure by viewModel.enclosure.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAll()
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
                Text(
                    it.title,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(
                        onClick = openURL,
                        indication = null,
                        interactionSource = null
                    )
                )

                enclosure?.let { enclosure ->
                    AsyncImage(
                        model = enclosure.url.toString(),
                        contentDescription = null
                    )
                }

                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            clipToOutline = true

                            loadData(it.content.html(), "text/html", "UTF-8")
                        }
                    }
                )

                Button(onClick = openURL) {
                    Text("Open in web browser")
                }
            }
        }
    }
}