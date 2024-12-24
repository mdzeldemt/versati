package com.liuvil.versati.navigation

import kotlinx.serialization.Serializable

abstract class NavigationDestination {

    @Serializable
    object Main

    @Serializable
    data class Entry(
        val id: Int
    )

}
