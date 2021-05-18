package com.zj.goodvideo.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList

abstract class AbsViewModel<Key, Value> : ViewModel() {

    private var dataSource: DataSource<Key, Value>? = null
    protected val boundaryPageData = MutableLiveData<Boolean>()
    private val pageData: LiveData<PagedList<Value>>

    private val factory: DataSource.Factory<Key, Value> = object : DataSource.Factory<Key, Value>() {
        override fun create(): DataSource<Key, Value> {
            if (dataSource == null || dataSource?.isInvalid == true) {
                dataSource = createDataSource()
            }
            return dataSource!!
        }
    }

    private val callback = object : PagedList.BoundaryCallback<Value>() {
        override fun onZeroItemsLoaded() {
            //新提交的PagedList中没有数据
            boundaryPageData.postValue(false)
        }

        override fun onItemAtFrontLoaded(itemAtFront: Value) {
            //新提交的PagedList中第一条数据被加载到列表上
            boundaryPageData.postValue(true)
        }

        override fun onItemAtEndLoaded(itemAtEnd: Value) {
            //新提交的PagedList中最后一条数据被加载到列表上
        }
    }

    init {
        val config: PagedList.Config = PagedList.Config.Builder()
            .setPageSize(10)
            .setInitialLoadSizeHint(12)
            // .setMaxSize(100)；
            // .setEnablePlaceholders(false)
            // .setPrefetchDistance()
            .build()

        pageData = LivePagedListBuilder(factory, config)
            .setInitialLoadKey(initialLoadKey())
            //.setFetchExecutor()
            .setBoundaryCallback(callback)
            .build()
    }

    abstract fun initialLoadKey(): Key

    abstract fun createDataSource(): DataSource<Key, Value>

    fun getPageData(): LiveData<PagedList<Value>> {
        return pageData
    }

    fun getDataSource(): DataSource<Key, Value>? {
        return dataSource
    }

    fun getBoundaryPageData(): LiveData<Boolean> {
        return boundaryPageData
    }

}