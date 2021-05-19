package com.zj.goodvideo.http

data class ApiResponse<T>(
    val data: Data<T>,
    val message: String,
    val status: Int
)

data class Data<T>(
    val data: T
)
