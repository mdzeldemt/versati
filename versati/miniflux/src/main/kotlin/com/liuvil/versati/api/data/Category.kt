package com.liuvil.versati.api.data

data class Category(
    val id: Int,
    val title: String,
    val userId: Int,
    val hideGlobally: Boolean
)