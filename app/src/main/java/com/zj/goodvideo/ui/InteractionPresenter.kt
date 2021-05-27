package com.zj.goodvideo.ui

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.zj.goodvideo.model.Feed
import com.zj.goodvideo.model.User
import com.zj.goodvideo.ui.login.UserManager
import com.zj.hi_library.util.AppGlobals

/**
 * 给一个帖子点赞/取消点赞，它和帖子踩一踩是互斥的
 */
fun toggleFeedLike(owner: LifecycleOwner, feed: Feed) {
    val isLogin = isLogin(owner) {

    }
    if (isLogin) {

    }
}

/**
 *
 */
fun toggleFeedDiss(owner: LifecycleOwner, feed: Feed) {

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
