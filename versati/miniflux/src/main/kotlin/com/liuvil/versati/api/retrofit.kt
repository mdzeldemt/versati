package com.liuvil.versati.api

import com.liuvil.versati.api.data.Category
import com.liuvil.versati.api.data.Enclosure
import com.liuvil.versati.api.data.EntriesGetResponse
import com.liuvil.versati.api.data.EntriesUpdateRequest
import com.liuvil.versati.api.data.Entry
import com.liuvil.versati.api.data.EntryStatus
import com.liuvil.versati.api.data.Feed
import com.liuvil.versati.api.data.FeedCountersResponse
import com.liuvil.versati.api.data.Icon
import com.liuvil.versati.api.data.SortDirection
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MinifluxRetrofitApi: MinifluxApi {

    @GET("/v1/categories")
    override suspend fun getCategories(): List<Category>

    @GET("/v1/categories/{categoryId}/entries")
    override suspend fun getCategoryEntries(
        @Path("categoryId") categoryId: Int,
        @Query("status") status: EntryStatus?,
        @Query("direction") direction: SortDirection?,
        @Query("offset") offset: Int?,
        @Query("before_entry_id") beforeEntryId: Int?,
        @Query("after_entry_id") afterEntryId: Int?,
        @Query("limit") limit: Int?
    ): EntriesGetResponse

    @GET("/v1/enclosures/{id}")
    override suspend fun getEnclosure(
        @Path("id") id: Int
    ): Enclosure

    @GET("/v1/entries")
    override suspend fun getEntries(
        @Query("status") status: EntryStatus?,
        @Query("direction") direction: SortDirection?,
        @Query("starred") starred: Boolean?,
        @Query("offset") offset: Int?,
        @Query("before_entry_id") beforeEntryId: Int?,
        @Query("after_entry_id") afterEntryId: Int?,
        @Query("globally_visible") globallyVisible: Boolean?,
        @Query("limit") limit: Int?
    ): EntriesGetResponse

    @GET("/v1/entries/{id}")
    override suspend fun getEntry(@Path("id") id: Int): Entry

    @GET("/v1/feeds/counters")
    override suspend fun getFeedCounters(): FeedCountersResponse

    @GET("/v1/feeds/{feedId}/entries")
    override suspend fun getFeedEntries(
        @Path("feedId") feedId: Int,
        @Query("status") status: EntryStatus?,
        @Query("direction") direction: SortDirection?,
        @Query("offset") offset: Int?,
        @Query("before_entry_id") beforeEntryId: Int?,
        @Query("after_entry_id") afterEntryId: Int?,
        @Query("limit") limit: Int?
    ): EntriesGetResponse

    @GET("/v1/icons/{id}")
    override suspend fun getFeedIcon(
        @Path("id") id: Int
    ): Icon

    @GET("/v1/feeds/{feedId}/icon")
    override suspend fun getFeedIconByFeedId(
        @Path("feedId") feedId: Int
    ): Icon

    @GET("/v1/feeds")
    override suspend fun getFeeds(): List<Feed>

    @PUT("/v1/entries")
    override suspend fun updateEntries(
        @Body request: EntriesUpdateRequest
    )

}