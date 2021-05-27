package com.zj.goodvideo.ui

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.alibaba.fastjson.JSONObject
import com.zj.goodvideo.http.ApiResponse
import com.zj.goodvideo.http.RetrofitHelper
import com.zj.goodvideo.kt.toast
import com.zj.goodvideo.model.Feed
import com.zj.goodvideo.model.User
import com.zj.goodvideo.ui.login.UserManager
import com.zj.hi_library.util.AppGlobals
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 给一个帖子点赞/取消点赞，它和帖子踩一踩是互斥的
 */
fun toggleFeedLike(owner: LifecycleOwner, feed: Feed) {
    val isLogin = isLogin(owner) {
        //等登录成功进行点赞
        toggleFeedLikeInternal(feed)
    }
    if (isLogin) {
        toggleFeedLikeInternal(feed)
    }
}

/**
 *
 */
fun toggleFeedDiss(owner: LifecycleOwner, feed: Feed) {
    val isLogin = isLogin(owner) {
        toggleFeedDissInternal(feed)
    }
    if (isLogin) {
        toggleFeedDissInternal(feed)
    }
}

private fun toggleFeedLikeInternal(feed: Feed) {
    val toggleFeedLike =
        RetrofitHelper.apiServer.toggleFeedLike(UserManager.getUserId(), feed.itemId)
    toggleFeedLike.enqueue(object : Callback<ApiResponse<JSONObject>> {
        override fun onResponse(
            call: Call<ApiResponse<JSONObject>>,
            response: Response<ApiResponse<JSONObject>>
        ) {
            if (response.body() != null) {
                val hasLiked = response.body()!!.data?.data?.getBoolean("hasLiked") ?: false
                feed.getUgc().isHasLiked = hasLiked
            }
        }

        override fun onFailure(call: Call<ApiResponse<JSONObject>>, t: Throwable) {
            "点赞失败".toast()
        }
    })
}

private fun toggleFeedDissInternal(feed: Feed) {
    val toggleFeedDiss =
        RetrofitHelper.apiServer.toggleFeedDiss(UserManager.getUserId(), feed.itemId)
    toggleFeedDiss.enqueue(object : Callback<ApiResponse<JSONObject>> {
        override fun onResponse(
            call: Call<ApiResponse<JSONObject>>,
            response: Response<ApiResponse<JSONObject>>
        ) {
            val hasLiked = response.body()!!.data?.data?.getBoolean("hasLiked") ?: false
            feed.getUgc().isHasdiss = hasLiked
        }

        override fun onFailure(call: Call<ApiResponse<JSONObject>>, t: Throwable) {
            "踩失败".toast()
        }
    })
}

private fun isLogin(owner: LifecycleOwner?, observer: Observer<User>?): Boolean {
    if (!UserManager.isLogin()) {
        val userData = UserManager.login(AppGlobals.get())
        if (owner == null) {
            userData.observeForever(loginObserver(observer, userData))
        } else {
            userData.observe(owner, loginObserver(observer, userData))
        }
        return false
    }
    return true
}

private fun loginObserver(observer: Observer<User>?, liveData: LiveData<User?>): Observer<User?> {
    return object : Observer<User?> {
        override fun onChanged(user: User?) {
            liveData.removeObserver(this)
            if (user != null && observer != null) {
                observer.onChanged(user)
            }
        }
    }
}
