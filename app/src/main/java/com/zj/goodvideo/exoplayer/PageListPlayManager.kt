package com.zj.goodvideo.exoplayer

import android.net.Uri
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.FileDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.*
import com.google.android.exoplayer2.util.Util
import com.zj.hi_library.util.AppGlobals

object PageListPlayManager {

    private val mediaSourceFactory: ProgressiveMediaSource.Factory
    private val sPageListPlayHashMap = HashMap<String, PageListPlay?>()

    init {
        val application = AppGlobals.get()
        //创建http视频如何加载的工厂对象
        val dataSourceFactory =
            DefaultHttpDataSourceFactory(Util.getUserAgent(application, application?.packageName))
        //创建缓存，指定缓存位置，和缓存策略为最近最少使用原则，最大为200M
        val cache =
            SimpleCache(application?.cacheDir, LeastRecentlyUsedCacheEvictor(1024 * 1024 * 200))
        //把缓存对象cache和负责缓存数据读取、写入的工厂类CacheDataSinkFactory 相关联
        val cacheDataSinkFactory = CacheDataSinkFactory(cache, Long.MAX_VALUE)

        //创建能够边播放边缓存的 本地资源加载和http网络数据写入的工厂类
        val cacheDataSourceFactory = CacheDataSourceFactory(
            cache, //缓存写入策略和缓存写入位置
            dataSourceFactory, //http视频资源如何加载的工厂对象
            FileDataSourceFactory(), //本地缓存数据如何读取的工厂对象
            cacheDataSinkFactory, //网络数据如何写入本地缓存的工厂对象
            CacheDataSource.FLAG_BLOCK_ON_CACHE, //加载本地缓存数据进行播放时的策略,如果遇到该文件正在被写入数据,或读取缓存数据发生错误时的策略
            null //缓存数据读取的回调
        )

        //最后 还需要创建一个 MediaSource 媒体资源 加载的工厂类
        //因为由它创建的MediaSource 能够实现边缓冲边播放的效果,
        //如果需要播放hls,m3u8 则需要创建DashMediaSource.Factory()
        mediaSourceFactory = ProgressiveMediaSource.Factory(cacheDataSourceFactory)
    }

    fun createMediaSource(url: String): ProgressiveMediaSource? {
        return mediaSourceFactory.createMediaSource(Uri.parse(url))
    }

    fun get(pageName: String): PageListPlay {
        var pageListPlay = sPageListPlayHashMap[pageName]
        if (pageListPlay == null) {
            pageListPlay = PageListPlay()
            sPageListPlayHashMap[pageName] = pageListPlay
        }
        return pageListPlay
    }

    fun release(pageName: String) {
        val pageListPlay = sPageListPlayHashMap.remove(pageName)
        pageListPlay?.release()
    }
}