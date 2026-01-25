package com.liuvil.versati.activities.main.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.liuvil.versati.activities.main.main.home.HomeView
import com.liuvil.versati.activities.main.main.welcome.WelcomeView
import com.liuvil.versati.framework.viewmodel.viewOf

@Composable
fun MainView(
    onPreferencesClicked: () -> Unit
) = viewOf<MainViewModel> { viewModel ->
    val onboardingState by viewModel.onboardingState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        onboardingState.ifSuccess {
            when (it) {
                is OnboardingState.Incomplete ->
                    WelcomeView(
                        onPreferencesClicked = onPreferencesClicked
                    )

                is OnboardingState.Complete ->
                    HomeView(
                        onPreferencesClicked = onPreferencesClicked
                    )
            }
        }
    }
}
