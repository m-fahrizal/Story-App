package com.example.submissionstory.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "keys")
data class Key(
    @PrimaryKey
    val id: String,
    val previousKey: Int?,
    val nextKey: Int?
)