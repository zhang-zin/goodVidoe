package com.zj.goodvideo.ui.home

import androidx.paging.DataSource
import com.zj.goodvideo.model.Feed
import com.zj.goodvideo.ui.AbsViewModel

class HomeViewModel : AbsViewModel<Feed>() {

    override fun createDataSource(): DataSource<Int, Feed> {
        TODO("Not yet implemented")
    }
}