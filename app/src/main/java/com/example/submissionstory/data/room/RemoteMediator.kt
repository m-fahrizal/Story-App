package com.example.submissionstory.data.room

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import com.example.submissionstory.data.api.ApiService
import com.example.submissionstory.data.preferences.AuthPreferences
import com.example.submissionstory.data.response.ListStory
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class RemoteMediator @Inject constructor(
    private val storiesDB: StoriesDB,
    private val apiService: ApiService,
    private val authPreferences: AuthPreferences
) : RemoteMediator<Int, ListStory>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ListStory>
    ): MediatorResult {
        val paging = when (loadType) {
            LoadType.REFRESH -> {
                val key = getClosestKey(state)
                key?.nextKey?.minus(1) ?: init
            }
            LoadType.PREPEND -> {
                val key = getFirstKey(state)
                val previousKey = key?.previousKey ?: return MediatorResult.Success(
                    endOfPaginationReached = key != null
                )
                previousKey
            }
            LoadType.APPEND -> {
                val key = getLastKey(state)
                val nextKey = key?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = key != null
                )
                nextKey
            }
        }
        return try {

            val token: String = authPreferences.getToken().first()
            val response = apiService.getStory("Bearer $token", state.config.pageSize, paging)
            val pageEnd = response.listStory.isEmpty()

            storiesDB.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    storiesDB.keyDao().deleteKey()
                    storiesDB.storiesDao().deleteStories()
                }
                val previousKey = if (paging == 1) null else paging - 1
                val nextKey = if (pageEnd) null else paging + 1
                val key = response.listStory.map {
                    Key(id = it.id, previousKey = previousKey, nextKey = nextKey)
                }
                storiesDB.keyDao().insert(key)
                response.listStory.forEach { stories ->
                    val item = ListStory(
                        stories.id,
                        stories.name,
                        stories.description,
                        stories.photoUrl,
                        stories.createdAt,
                        stories.lat,
                        stories.lon
                    )

                    storiesDB.storiesDao().insertStories(item)
                }
            }
            MediatorResult.Success(endOfPaginationReached = pageEnd)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getFirstKey(state: PagingState<Int, ListStory>): Key? {
        return state.pages.firstOrNull {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let { data ->
            storiesDB.keyDao().selectKeyId(data.id)
        }
    }

    private suspend fun getLastKey(state: PagingState<Int, ListStory>): Key? {
        return state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { data ->
            storiesDB.keyDao().selectKeyId(data.id)
        }
    }

    private suspend fun getClosestKey(state: PagingState<Int, ListStory>): Key? {
        return state.anchorPosition?.let { pos ->
            state.closestItemToPosition(pos)?.id?.let { id ->
                storiesDB.keyDao().selectKeyId(id)
            }
        }
    }

    companion object {
        const val init = 1
    }
}