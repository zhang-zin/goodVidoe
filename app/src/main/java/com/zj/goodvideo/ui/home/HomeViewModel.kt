package com.zj.goodvideo.ui.home

import androidx.paging.ItemKeyedDataSource
import com.zj.goodvideo.model.Feed
import com.zj.goodvideo.ui.AbsViewModel

class HomeViewModel : AbsViewModel<Int, Feed>() {

    override fun initialLoadKey() = 0

    override fun createDataSource() = FeedDataSource()

    class FeedDataSource : ItemKeyedDataSource<Int, Feed>() {
        override fun loadInitial(
            params: LoadInitialParams<Int>,
            callback: LoadInitialCallback<Feed>
        ) {
            TODO("Not yet implemented")
        }

        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Feed>) {
            TODO("Not yet implemented")
        }

        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Feed>) {
            TODO("Not yet implemented")
        }

        override fun getKey(item: Feed): Int {
            TODO("Not yet implemented")
        }
    }

}