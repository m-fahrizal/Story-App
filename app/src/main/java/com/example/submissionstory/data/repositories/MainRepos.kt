package com.example.submissionstory.data.repositories

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.example.submissionstory.data.api.ApiService
import com.example.submissionstory.data.preferences.AuthPreferences
import com.example.submissionstory.data.response.ListStory
import com.example.submissionstory.data.room.RemoteMediator
import com.example.submissionstory.data.room.StoriesDB
import javax.inject.Inject

class MainRepos @Inject constructor(
    val authPreferences: AuthPreferences,
    private val apiService: ApiService,
    private val storiesDB: StoriesDB

) {

    fun getStory(): LiveData<PagingData<ListStory>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = 3),
            remoteMediator = RemoteMediator(storiesDB, apiService, authPreferences),
            pagingSourceFactory = {
                com.example.submissionstory.data.utils.PagingSource(authPreferences, apiService)
            }
        ).liveData
    }
}
