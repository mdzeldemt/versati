package com.liuvil.versati.activities.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.liuvil.versati.api.MinifluxApi
import com.liuvil.versati.api.data.Entry
import com.liuvil.versati.api.data.SortDirection
import com.liuvil.versati.components.EntryTile
import dagger.hilt.android.AndroidEntryPoint
import org.jsoup.Jsoup
import java.time.Duration
import java.time.OffsetDateTime
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity: ComponentActivity() {

    @Inject
    lateinit var minifluxApi: MinifluxApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val entries = remember { mutableStateListOf<Entry>() }

            LaunchedEffect(Unit) {
                entries.addAll(
                    minifluxApi.getEntries(
                        direction = SortDirection.DESCENDING,
                        limit = 10
                    ).entries
                )
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(4.dp)
            ) {
                entries.forEach {
                    item {
                        EntryTile(
                            title = it.title,
                            feedTitle = it.feed.title,
                            timeSincePublished = Duration.between(it.publishedAt, OffsetDateTime.now()),
                            content = Jsoup.parse(it.content).text(),
                            imageUrl = it.enclosures.firstOrNull()?.url
                        )
                    }
                }

                item {
                    Button(onClick = {}) {
                        Text("Mark this page as read")
                    }
                }
            }
        }
    }
}