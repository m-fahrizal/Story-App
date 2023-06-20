package com.example.submissionstory.data.utils

import androidx.paging.PagingData
import androidx.paging.PagingState
import com.example.submissionstory.data.api.ApiService
import com.example.submissionstory.data.preferences.AuthPreferences
import com.example.submissionstory.data.response.ListStory
import kotlinx.coroutines.flow.first

class PagingSource(
    private val authPreferences: AuthPreferences,
    private val apiService: ApiService
) : androidx.paging.PagingSource<Int, ListStory>() {
    override fun getRefreshKey(state: PagingState<Int, ListStory>): Int? {
        return state.anchorPosition?.let { anc ->
            val ancPage = state.closestPageToPosition(anc)
            ancPage?.prevKey?.plus(1) ?: ancPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStory> {
        return try {
            val page = params.key ?: init
            val key = authPreferences.getToken().first()
            val response = apiService.getStory("Bearer $key", params.loadSize, page)
            val listStory = response.listStory

            LoadResult.Page(
                listStory,
                prevKey = if (page == init) null else page - 1,
                nextKey = if (listStory.isEmpty()) null else page + 1
            )


        } catch (t: Throwable) {
            LoadResult.Error(t)
        }
    }

    companion object {
        const val init = 1

        fun snaps(items: List<ListStory>): PagingData<ListStory> {
            return PagingData.from(items)
        }
    }
}