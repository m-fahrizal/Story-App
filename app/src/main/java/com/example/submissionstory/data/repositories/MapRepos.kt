package com.example.submissionstory.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.submissionstory.data.api.ApiConfig
import com.example.submissionstory.data.response.ListStory
import com.example.submissionstory.data.response.MainResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapRepos {

    private val getStoryLoc = MutableLiveData<List<ListStory>>()

    fun getStoryWithLocation(token: String): LiveData<List<ListStory>> {

        ApiConfig.getApiService().getStoryWithLocation(token, 1)
            .enqueue(object : Callback<MainResponse> {
                override fun onResponse(
                    call: Call<MainResponse>,
                    response: Response<MainResponse>
                ) {
                    if (response.isSuccessful) {
                        getStoryLoc.value = response.body()?.listStory
                    } else {
                        Log.e(TAG, response.message())
                    }
                }

                override fun onFailure(call: Call<MainResponse>, t: Throwable) {
                    Log.e(TAG, "onFailure: ${t.message}")
                }
            })
        return getStoryLoc
    }

    fun getStories(): LiveData<List<ListStory>> {
        return getStoryLoc
    }

    companion object {
        private const val TAG = "MapRepos"
    }
}