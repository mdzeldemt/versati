package com.liuvil.versati.repository.cache.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.liuvil.versati.repository.cache.data.Entry

@Dao
interface EntryCache {
    @Query("SELECT * FROM entries WHERE id = :id")
    suspend fun getEntryById(id: Int): Entry?

    @Query(
        """
            SELECT entry.* FROM entries entry
            WHERE read = :read
                AND starred = :starred
                AND EXISTS (
                    SELECT 1 from feeds feed
                    WHERE feed.id = entry.feedId
                        AND feed.hideGlobally = NOT :globallyVisible
                )
                AND content LIKE :search
            ORDER BY publishedAt DESC
            LIMIT :limit
            OFFSET :offset
        """
    )
    suspend fun getAllEntries(
        read: Boolean? = null,
        starred: Boolean? = null,
        globallyVisible: Boolean? = null,
        search: String? = null,
        offset: Int? = null,
        limit: Int? = null
    ): List<Entry>

    @Query(
        """
            SELECT entry.* FROM entries entry
            WHERE read = :read
                AND EXISTS (
                    SELECT 1 from feeds feed
                    WHERE feed.id = entry.feedId
                        AND feed.categoryId = :categoryId
                )
            ORDER BY publishedAt DESC
            LIMIT :limit
            OFFSET :offset
        """
    )
    suspend fun getEntriesFromCategory(
        categoryId: Int,
        read: Boolean? = null,
        offset: Int? = null,
        limit: Int? = null
    ): List<Entry>

    @Query(
        """
            SELECT * FROM entries
            WHERE read = :read
                AND feedId = :feedId
            ORDER BY publishedAt DESC
            LIMIT :limit
            OFFSET :offset
        """
    )
    suspend fun getEntriesFromFeed(
        feedId: Int,
        read: Boolean? = null,
        offset: Int? = null,
        limit: Int? = null
    ): List<Entry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: Entry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntries(entries: List<Entry>)

    @Query("UPDATE entries SET read = :read WHERE id = :id")
    suspend fun updateEntryRead(id: Int, read: Boolean)

    @Query("UPDATE entries SET read = :read WHERE id IN (:ids)")
    suspend fun updateEntriesRead(ids: List<Int>, read: Boolean)

    @Query("UPDATE entries SET starred = :starred WHERE id = :id")
    suspend fun updateEntryStarred(id: Int, starred: Boolean)
}