package com.liuvil.versati.activities.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.liuvil.versati.activities.main.home.HomeView
import com.liuvil.versati.activities.main.welcome.WelcomeView
import com.liuvil.versati.framework.viewmodel.bindViewModel

@Composable
fun RootView() {
    val viewModel = bindViewModel<RootViewModel>()
    val connectionID by viewModel.connectionID

    LaunchedEffect(Unit) {
        viewModel.reloadConnectionID()
    }

    connectionID.ifSuccess {
        if (it != null) {
            HomeView(
                connectionID = it
            )
        } else {
            WelcomeView()
        }
    } ?: Box(
        modifier = Modifier.fillMaxSize()
    )
}