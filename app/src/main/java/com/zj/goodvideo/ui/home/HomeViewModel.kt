package com.zj.goodvideo.ui.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.zj.goodvideo.http.RetrofitHelper
import com.zj.goodvideo.model.Feed
import com.zj.libcommon.ui.AbsViewModel
import kotlinx.coroutines.flow.Flow

class HomeViewModel : AbsViewModel<Int, Feed>() {

    private var feedType: String = "all"

    fun setFeedType(feedType: String) {
        this.feedType = feedType
    }

    override fun createPagingSource(): PagingSource<Int, Feed> {
        return FeedDataSource(RetrofitHelper.apiServer, feedType)
    }

    /**
     * 当前查询条件
     */
    private var currentQueryValue: String? = null

    /**
     * 缓存查询的值
     */
    private var currentSearchResult: Flow<PagingData<Feed>>? = null

    fun searchRepo(queryString: String): Flow<PagingData<Feed>> {
        /*val lastResult = currentSearchResult
        if (queryString == currentQueryValue && lastResult != null) {
            return lastResult
        }*/
//        currentQueryValue = queryString
        /**
         * 如果在Flow上进行一些操作（例如map和filter），需要在cachedIn之前
         */
        val newResult: Flow<PagingData<Feed>> = getResultStream()
            .cachedIn(viewModelScope) // 缓存Flow<PagingData>的内容
        currentSearchResult = newResult
        return newResult
    }

}