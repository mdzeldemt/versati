package com.liuvil.versati.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.liuvil.versati.activities.main.MainScreen

@Composable
fun NavigationRoot() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "/"
    ) {
        composable("/") {
            MainScreen()
        }
    }
}