package com.zj.goodvideo.http

class ApiResponse<T> {
    var success = false
    var status = 0
    var message: String? = null
    var body: T? = null
}