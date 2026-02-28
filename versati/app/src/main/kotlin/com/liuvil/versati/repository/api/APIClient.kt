package com.liuvil.versati.repository.api

import com.liuvil.versati.repository.api.data.Category
import com.liuvil.versati.repository.api.data.CreateCategoryRequest
import com.liuvil.versati.repository.api.data.CreateFeedRequest
import com.liuvil.versati.repository.api.data.CreateFeedResponse
import com.liuvil.versati.repository.api.data.Enclosure
import com.liuvil.versati.repository.api.data.EntriesGetResponse
import com.liuvil.versati.repository.api.data.EntriesUpdateRequest
import com.liuvil.versati.repository.api.data.Entry
import com.liuvil.versati.repository.api.data.EntryStatus
import com.liuvil.versati.repository.api.data.Feed
import com.liuvil.versati.repository.api.data.FeedCountersResponse
import com.liuvil.versati.repository.api.data.Icon
import com.liuvil.versati.repository.api.data.Version
import com.liuvil.versati.repository.api.data.SortDirection
import com.liuvil.versati.repository.api.data.UpdateCategoryRequest
import com.liuvil.versati.repository.api.data.UpdateFeedRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface APIClient {

    @GET("/v1/categories")
    suspend fun getCategories(): List<Category>

    @POST("/v1/categories")
    suspend fun createCategory(
        @Body request: CreateCategoryRequest
    ): Category

    @PUT("/v1/categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: Int,
        @Body request: UpdateCategoryRequest
    ): Category

    @DELETE("/v1/categories/{id}")
    suspend fun deleteCategory(
        @Path("id") id: Int
    )

    @GET("/v1/enclosures/{id}")
    suspend fun getEnclosure(
        @Path("id") id: Int
    ): Enclosure

    @GET("/v1/entries")
    suspend fun getEntries(
        @Query("status") status: EntryStatus?,
        @Query("direction") direction: SortDirection?,
        @Query("starred") starred: Boolean?,
        @Query("offset") offset: Int?,
        @Query("globally_visible") globallyVisible: Boolean?,
        @Query("search") search: String?,
        @Query("limit") limit: Int?
    ): EntriesGetResponse

    @GET("/v1/categories/{categoryId}/entries")
    suspend fun getCategoryEntries(
        @Path("categoryId") categoryId: Int,
        @Query("status") status: EntryStatus?,
        @Query("direction") direction: SortDirection?,
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?
    ): EntriesGetResponse

    @GET("/v1/feeds/{feedId}/entries")
    suspend fun getFeedEntries(
        @Path("feedId") feedId: Int,
        @Query("status") status: EntryStatus?,
        @Query("direction") direction: SortDirection?,
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?
    ): EntriesGetResponse

    @GET("/v1/entries/{id}")
    suspend fun getEntry(@Path("id") id: Int): Entry

    @PUT("/v1/entries/{id}/bookmark")
    suspend fun toggleEntryBookmark(
        @Path("id") id: Int
    )

    @PUT("/v1/entries")
    suspend fun updateEntries(
        @Body request: EntriesUpdateRequest
    )

    @GET("/v1/feeds/{id}")
    suspend fun getFeed(
        @Path("id") id: Int
    ): Feed

    @GET("/v1/feeds")
    suspend fun getFeeds(): List<Feed>

    @POST("/v1/feeds")
    suspend fun createFeed(
        @Body request: CreateFeedRequest
    ): CreateFeedResponse

    @PUT("/v1/feeds/{id}/refresh")
    suspend fun refreshFeed(
        @Path("id") id: Int
    )

    @PUT("/v1/feeds/{id}")
    suspend fun updateFeed(
        @Path("id") id: Int,
        @Body request: UpdateFeedRequest
    ): Feed

    @DELETE("/v1/feeds/{id}")
    suspend fun deleteFeed(
        @Path("id") id: Int
    )

    @GET("/v1/feeds/counters")
    suspend fun getFeedCounters(): FeedCountersResponse

    @GET("/v1/icons/{id}")
    suspend fun getFeedIcon(
        @Path("id") id: Int
    ): Icon

    @GET("/v1/version")
    suspend fun getVersion(): Version
}