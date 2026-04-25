package com.mdzeldemt.versati.repository.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mdzeldemt.versati.repository.cache.data.Feed

@Dao
interface FeedCache {
    @Query("SELECT * FROM feeds WHERE id = :id")
    suspend fun getFeedById(id: Int): Feed?

    @Query("SELECT * FROM feeds")
    suspend fun getAllFeeds(): List<Feed>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeed(feed: Feed)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeeds(feeds: List<Feed>)

    @Query("DELETE FROM feeds WHERE id = :id")
    suspend fun deleteFeed(id: Int)

    @Query("DELETE FROM feeds")
    suspend fun deleteAllFeeds()
}