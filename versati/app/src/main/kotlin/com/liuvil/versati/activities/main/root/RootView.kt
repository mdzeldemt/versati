package com.liuvil.versati.activities.main.root

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.liuvil.versati.activities.main.home.HomeView
import com.liuvil.versati.framework.viewmodel.bindViewModel

@Composable
fun RootView() {
    val viewModel = bindViewModel<RootViewModel>()
    val activeServerID by viewModel.activeServerID.collectAsState()

    if (activeServerID != null) {
        HomeView(
            serverID = activeServerID!!
        )
    } else {
        Text("No server was set up")
    }
}