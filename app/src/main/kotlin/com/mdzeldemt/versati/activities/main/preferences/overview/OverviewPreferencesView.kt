package com.mdzeldemt.versati.activities.main.preferences.overview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Feed
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mdzeldemt.versati.components.form.action.SimpleActionTile
import com.mdzeldemt.versati.components.scaffold.action.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewPreferencesView(
    onConnectionClicked: () -> Unit,
    onBrowsingClicked: () -> Unit,
    onMiscellaneousClicked: () -> Unit,
    onDismiss: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Preferences")
                },
                navigationIcon = {
                    BackButton {
                        onDismiss()
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                ConnectionTile(
                    onClick = onConnectionClicked
                )
            }

            item {
                BrowsingTile(
                    onClick = onBrowsingClicked
                )
            }

            item {
                MiscellaneousTile(
                    onClick = onMiscellaneousClicked
                )
            }
        }
    }
}

@Composable
private fun ConnectionTile(
    onClick: () -> Unit
) {
    SimpleActionTile(
        title = "Connection",
        subtitle = "Server connection details",
        icon = Icons.Default.Public,
        onClick = onClick
    )
}

@Composable
private fun BrowsingTile(
    onClick: () -> Unit
) {
    SimpleActionTile(
        title = "Browsing",
        subtitle = "List of entries and feeds preferences",
        icon = Icons.AutoMirrored.Filled.Feed,
        onClick = onClick
    )
}

@Composable
private fun MiscellaneousTile(
    onClick: () -> Unit
) {
    SimpleActionTile(
        title = "Miscellaneous",
        subtitle = "Other preferences",
        icon = Icons.Default.Settings,
        onClick = onClick
    )
}