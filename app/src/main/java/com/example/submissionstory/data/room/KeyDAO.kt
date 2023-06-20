package com.example.submissionstory.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface KeyDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(key: List<Key>)

    @Query("SELECT * FROM keys WHERE id = :id")
    suspend fun selectKeyId(id: String): Key?

    @Query("DELETE FROM keys")
    suspend fun deleteKey()
}