package com.liuvil.versati.repository.api.data

data class Category(
    val id: Int,
    val title: String,
    val userId: Int,
    val hideGlobally: Boolean
)