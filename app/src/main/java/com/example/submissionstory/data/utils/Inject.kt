package com.example.submissionstory.data.utils

import android.content.Context
import com.example.submissionstory.data.api.ApiConfig
import com.example.submissionstory.data.preferences.AuthPreferences
import com.example.submissionstory.data.repositories.MainRepos
import com.example.submissionstory.data.room.StoriesDB

object Inject {
    fun mainRep(
        authPreferences: AuthPreferences,
        context: Context
    ) : MainRepos {
        val storiesDB = StoriesDB.getInstance(context)
        val apiService = ApiConfig.getApiService()
        return MainRepos(authPreferences,apiService, storiesDB)
    }
}