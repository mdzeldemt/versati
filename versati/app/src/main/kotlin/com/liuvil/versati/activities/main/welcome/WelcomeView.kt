package com.liuvil.versati.activities.main.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.liuvil.versati.activities.main.preferences.PreferencesView
import com.liuvil.versati.framework.navigation.safePop
import kotlinx.serialization.Serializable

private object NavigationDestination {
    @Serializable
    object WelcomeMessage

    @Serializable
    object Preferences
}

@Composable
fun WelcomeView() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavigationDestination.WelcomeMessage
    ) {
        composable<NavigationDestination.WelcomeMessage> {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text("You haven't set up any connections yet.")

                Button(
                    onClick = {
                        navController.navigate(NavigationDestination.Preferences)
                    }
                ) {
                    Text("Set up a connection")
                }
            }
        }

        composable<NavigationDestination.Preferences> {
            PreferencesView(
                onDismiss = {
                    navController.safePop()
                }
            )
        }
    }
}
