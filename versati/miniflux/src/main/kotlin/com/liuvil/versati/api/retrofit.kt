package com.liuvil.versati.api

import com.liuvil.versati.api.data.Category
import com.liuvil.versati.api.data.Enclosure
import com.liuvil.versati.api.data.EntriesResponse
import com.liuvil.versati.api.data.Entry
import com.liuvil.versati.api.data.Feed
import com.liuvil.versati.api.data.SortDirection
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MinifluxRetrofitApi: MinifluxApi {

    @GET("/v1/categories")
    override suspend fun getCategories(): List<Category>

    @GET("/v1/categories/{categoryId}/entries")
    override suspend fun getCategoryEntries(
        @Path("categoryId") categoryId: Int,
        @Query("direction") direction: SortDirection?,
        @Query("limit") limit: Int?
    ): EntriesResponse

    @GET("/v1/enclosures/{id}")
    override suspend fun getEnclosure(
        @Path("id") id: Int
    ): Enclosure

    @GET("/v1/entries")
    override suspend fun getEntries(
        @Query("direction") direction: SortDirection?,
        @Query("limit") limit: Int?
    ): EntriesResponse

    @GET("/v1/entries/{id}")
    override suspend fun getEntry(@Path("id") id: Int): Entry

    @GET("/v1/feeds/{feedId}/entries")
    override suspend fun getFeedEntries(
        @Path("feedId") feedId: Int,
        @Query("direction") direction: SortDirection?,
        @Query("limit") limit: Int?
    ): EntriesResponse

    @GET("/v1/feeds")
    override suspend fun getFeeds(): List<Feed>

}