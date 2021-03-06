package com.zj.libcommon.ui

import androidx.lifecycle.ViewModel
import androidx.paging.*
import kotlinx.coroutines.flow.Flow

abstract class AbsViewModel<Key : Any, Value : Any> : ViewModel() {

    /**
     * 配置PagingData加载行为
     */
    private val config = PagingConfig(pageSize = 10, enablePlaceholders = false)

    init {

    }

    abstract fun createPagingSource(): PagingSource<Key, Value>

    fun getResultStream(): Flow<PagingData<Value>> {
        return Pager(config = config, pagingSourceFactory = { createPagingSource() }).flow
    }

}