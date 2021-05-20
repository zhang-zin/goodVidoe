package com.zj.goodvideo.ui.home

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.zj.goodvideo.http.ApiServer
import com.zj.goodvideo.model.Feed
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE_INDEX = 0

class FeedDataSource(private val api: ApiServer, private val feedType: String) :
    PagingSource<Int, Feed>() {

    override fun getRefreshKey(state: PagingState<Int, Feed>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Feed> {
        val position = params.key ?: STARTING_PAGE_INDEX
        Log.e("zhang", "position: $position")
        return try {
            val response = api.queryHotFeedsList(feedType, 0, position, params.loadSize)
            val feedList = response.data.data
            val nextKey = if (feedList.isEmpty()) {
                null
            } else {
                params.key ?: 0 + 1
            }
            LoadResult.Page(
                data = feedList,
                prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
}