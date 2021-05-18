package com.zj.goodvideo.http

import com.zj.goodvideo.model.Feed
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServer {

    @GET("feeds/queryHotFeedsList")
    suspend fun queryHotFeedsList(
        @Query("feedType") feedType: String,
        @Query("userId") userId: Long,
        @Query("feedId") feedId: Int,
        @Query("pageCount") pageCount: Int
    ): ApiResponse<List<Feed>>
}