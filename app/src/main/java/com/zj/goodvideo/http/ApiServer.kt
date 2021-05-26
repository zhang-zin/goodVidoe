package com.zj.goodvideo.http

import com.zj.goodvideo.model.Feed
import com.zj.goodvideo.model.User
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

    @GET("user/insert")
    suspend fun insertUser(
        @Query("name") nickname: String,
        @Query("avatar") avatar: String,
        @Query("qqOpenId") qqOpenId: String,
        @Query("expires_time") expires_time: Long,
    ): ApiResponse<User>

    @GET("user/query")
    suspend fun queryUser(
        @Query("userId") userId: Long
    ): ApiResponse<User>
}