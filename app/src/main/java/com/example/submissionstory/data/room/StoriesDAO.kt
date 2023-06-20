package com.example.submissionstory.data.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.submissionstory.data.response.ListStory

@Dao
interface StoriesDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(listStory: ListStory)

    @Query("SELECT * FROM stories ORDER BY createdAt DESC")
    fun getStories(): PagingSource<Int, ListStory>

    @Query("DELETE FROM stories")
    suspend fun deleteStories()
}