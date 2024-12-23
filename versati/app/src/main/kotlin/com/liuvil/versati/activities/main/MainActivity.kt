package com.liuvil.versati.activities.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.liuvil.versati.api.MinifluxApi
import com.liuvil.versati.api.data.SortDirection
import dagger.hilt.android.AndroidEntryPoint
import org.jsoup.Jsoup
import java.net.URL
import javax.inject.Inject

fun parseEntryContent(entryContent: String): EntryContent {
    val document = Jsoup.parse(entryContent)
    return EntryContent(
        text = document.text(),
        imageURLs = document.getElementsByTag("img")
            .mapNotNull { it.attribute("src") }
            .map { URL(it.value) }
    )
}

@AndroidEntryPoint
class MainActivity: ComponentActivity() {

    @Inject
    lateinit var minifluxApi: MinifluxApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var loading by remember { mutableStateOf(true) }
            val items = remember { mutableStateListOf<Entry>() }

            LaunchedEffect(Unit) {
                items.addAll(
                    minifluxApi.getEntries(
                        direction = SortDirection.DESCENDING,
                        limit = 10
                    ).entries.map { entry ->
                        Entry(
                            id = entry.id,
                            title = entry.title,
                            feedTitle = entry.feed.title,
                            publishedAt = entry.publishedAt,
                            content = parseEntryContent(entry.content),
                            enclosures = entry.enclosures.map { enclosure ->
                                Enclosure(enclosure.url)
                            }
                        )
                    }
                )

                loading = false
            }

            LazyColumn (
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                if (loading) {
                    item {
                        CircularProgressIndicator()
                    }
                } else {
                    item {
                        EntryListView(
                            items,
                            onEntryTileTapped = {}
                        )
                    }

                    item {
                        Button(
                            onClick = {},
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text("Mark this page as read")
                        }
                    }
                }
            }
        }
    }
}