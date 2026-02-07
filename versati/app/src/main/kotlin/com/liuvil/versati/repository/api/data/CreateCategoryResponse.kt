package com.liuvil.versati.repository.api.data

data class CreateCategoryResponse(
    val id: Int,
    val userId: Int,
    val title: String,
    val hideGlobally: Boolean
)
