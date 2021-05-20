package com.zj.goodvideo.ui.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.zj.goodvideo.http.RetrofitHelper
import com.zj.goodvideo.model.Feed
import com.zj.goodvideo.ui.AbsViewModel
import kotlinx.coroutines.flow.Flow

class HomeViewModel : AbsViewModel<Int, Feed>() {

    private var feedType: String = "all"

    fun setFeedType(feedType: String) {
        this.feedType = feedType
    }

    override fun createPagingSource(): PagingSource<Int, Feed> {
        return FeedDataSource(RetrofitHelper.apiServer, feedType)
    }

    private var currentQueryValue: String? = null

    private var currentSearchResult: Flow<PagingData<Feed>>? = null

    fun searchRepo(queryString: String): Flow<PagingData<Feed>> {
        /*val lastResult = currentSearchResult
        if (queryString == currentQueryValue && lastResult != null) {
            return lastResult
        }*/
//        currentQueryValue = queryString
        val newResult: Flow<PagingData<Feed>> = getResultStream()
            .cachedIn(viewModelScope)
//        currentSearchResult = newResult
        return newResult
    }

}