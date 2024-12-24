package com.liuvil.versati.api

import com.liuvil.versati.api.data.Category
import com.liuvil.versati.api.data.EntriesResponse
import com.liuvil.versati.api.data.Entry
import com.liuvil.versati.api.data.Feed
import com.liuvil.versati.api.data.SortDirection

interface MinifluxApi {

    suspend fun getCategories(): List<Category>

    suspend fun getCategoryEntries(
        categoryId: Int,
        direction: SortDirection? = null,
        limit: Int? = null
    ): EntriesResponse

    suspend fun getEntries(
        direction: SortDirection? = null,
        limit: Int? = null
    ): EntriesResponse

    suspend fun getEntry(
        id: Int
    ): Entry

    suspend fun getFeedEntries(
        feedId: Int,
        direction: SortDirection? = null,
        limit: Int? = null
    ): EntriesResponse

    suspend fun getFeeds(): List<Feed>

}