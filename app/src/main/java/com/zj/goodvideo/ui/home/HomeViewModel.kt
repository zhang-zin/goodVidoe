package com.zj.goodvideo.ui.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.ItemKeyedDataSource
import com.zj.goodvideo.http.RetrofitHelper
import com.zj.goodvideo.model.Feed
import com.zj.goodvideo.ui.AbsViewModel
import kotlinx.coroutines.launch

class HomeViewModel : AbsViewModel<Int, Feed>() {

    private var feedType: String = "all"

    override fun initialLoadKey() = 0

    override fun createDataSource() = FeedDataSource()

    fun setFeedType(feedType: String) {
        this.feedType = feedType
    }

    inner class FeedDataSource : ItemKeyedDataSource<Int, Feed>() {
        override fun loadInitial(
            params: LoadInitialParams<Int>,
            callback: LoadInitialCallback<Feed>
        ) {
            //加载初始化数据
            loadData(0, params.requestedLoadSize, callback)
        }

        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Feed>) {
            //向后加载数据
            loadData(params.key, params.requestedLoadSize, callback)
        }

        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Feed>) {
            //能够向前加载数据的
            callback.onResult(emptyList())
        }

        override fun getKey(item: Feed) = item.id
    }

    private fun loadData(
        feedId: Int,
        pageCount: Int,
        callback: ItemKeyedDataSource.LoadCallback<Feed>
    ) {
        viewModelScope.launch {
            val queryHotFeedsList =
                RetrofitHelper.apiServer.queryHotFeedsList(feedType, 0, feedId, pageCount)
            Log.e("zhang", queryHotFeedsList.data.data.size.toString())
            val list = queryHotFeedsList.data.data
            callback.onResult(list)

            if (feedId > 0) {
                getBoundaryPageData().value == list.isNotEmpty()

            }
        }
    }

}