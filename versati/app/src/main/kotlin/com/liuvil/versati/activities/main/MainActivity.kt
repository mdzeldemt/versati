package com.liuvil.versati.activities.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.liuvil.versati.api.MinifluxApi
import com.liuvil.versati.api.data.Entry
import com.liuvil.versati.api.data.SortDirection
import dagger.hilt.android.AndroidEntryPoint
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

            Column {
                entries.forEach {
                    Text("${it.id} - ${it.title}")
                }
            }
        }
    }
}