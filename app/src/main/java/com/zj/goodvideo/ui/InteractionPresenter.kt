package com.zj.goodvideo.ui

import android.content.Context
import android.text.TextUtils
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
import com.zj.goodvideo.ui.share.ShareDialog
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

fun showShare(context: Context, feed: Feed) {
    var shareContent = feed.feeds_text
    if (!TextUtils.isEmpty(feed.url)) {
        shareContent = feed.url
    } else if (!TextUtils.isEmpty(feed.cover)) {
        shareContent = feed.cover
    }
    val shareDialog = ShareDialog(context)
    shareDialog.setShareContent(shareContent)
    shareDialog.setListener {
        RetrofitHelper.apiServer
            .share(feed.itemId)
            .enqueue(object : Callback<ApiResponse<JSONObject>> {
                override fun onResponse(
                    call: Call<ApiResponse<JSONObject>>,
                    response: Response<ApiResponse<JSONObject>>
                ) {
                    if (response.body() != null) {
                        val count = response.body()!!.data?.data?.getIntValue("count")
                            ?: feed.getUgc().getShareCount()
                        feed.getUgc().setShareCount(count)
                    }
                }
                override fun onFailure(call: Call<ApiResponse<JSONObject>>, t: Throwable) {
                }

            })
    }
    shareDialog.show()
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
