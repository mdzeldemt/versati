package com.liuvil.versati.activities.main.navigation

import kotlinx.serialization.Serializable

abstract class NavigationDestination {

    @Serializable
    object Main

    @Serializable
    data class Entry(
        val id: Int
    )

}
