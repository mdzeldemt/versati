package com.liuvil.versati.framework.navigation

import androidx.navigation.NavController

fun NavController.safePop() {
    if (previousBackStackEntry != null) {
        popBackStack()
    }
}