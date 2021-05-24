package com.zj.goodvideo.ui.home

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.zj.goodvideo.http.ApiServer
import com.zj.goodvideo.model.Feed
import okio.IOException
import retrofit2.HttpException

/***
 * 定义数据源以及如何从这里检索数据。pagingData对象会查询来自PagingSource的数据，响应Recycleview中
 */
private const val STARTING_PAGE_INDEX = 0

class FeedDataSource(private val api: ApiServer, private val feedType: String) :
    PagingSource<Int, Feed>() {

    /**
     * The refresh key is used for subsequent refresh call
     * to PagingSource.load after the initial load
     */
    override fun getRefreshKey(state: PagingState<Int, Feed>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    /**
     * 以异步的方式加载更多数据
     * [params] 保存与加载操作相关的信息：1、要加载页面的键。如果是第一次调用，LoadParams.key将为null
     *          2、加载大小
     * return LoadResult.Page 返回成功
     *        LoadResult.Error 发生错误
     */
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