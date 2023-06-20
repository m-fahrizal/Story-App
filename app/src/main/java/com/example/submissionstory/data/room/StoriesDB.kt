package com.example.submissionstory.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.submissionstory.data.response.ListStory

@Database(
    entities = [ListStory::class, Key::class],
    version = 1,
    exportSchema = false
)
abstract class StoriesDB : RoomDatabase() {
    abstract fun storiesDao(): StoriesDAO
    abstract fun keyDao(): KeyDAO

    companion object {
        @Volatile
        private var INSTANCE: StoriesDB? = null

        @JvmStatic
        fun getInstance(context: Context): StoriesDB {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoriesDB::class.java, "stories"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}