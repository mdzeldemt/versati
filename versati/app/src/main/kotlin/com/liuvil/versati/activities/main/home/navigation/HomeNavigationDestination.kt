package com.liuvil.versati.activities.main.home.navigation

import kotlinx.serialization.Serializable

abstract class HomeNavigationDestination {

    @Serializable
    object Feed

    @Serializable
    data class Entry(
        val id: Int
    )

}