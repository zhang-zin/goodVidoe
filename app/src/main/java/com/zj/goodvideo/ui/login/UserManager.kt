package com.zj.goodvideo.ui.login

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.zj.goodvideo.http.RetrofitHelper
import com.zj.goodvideo.kt.toast
import com.zj.goodvideo.model.User
import com.zj.hi_library.cache.HiStorage
import com.zj.hi_library.executor.HiExecutor
import com.zj.hi_library.util.AppGlobals

object UserManager {
    private const val KEY_CACHE_USER = "cache_user"
    private val userLiveData = MutableLiveData<User>()
    private var mUser: User? = null

    init {
        HiExecutor.execute {
            val cache = HiStorage.getCache<User>(KEY_CACHE_USER)
            if (cache != null && cache.expires_time > System.currentTimeMillis()) {
                mUser = cache
            }
        }
    }

    fun save(user: User) {
        mUser = user
        HiExecutor.execute { HiStorage.saveCache(KEY_CACHE_USER, user) }
        if (userLiveData.hasActiveObservers()) {
            userLiveData.postValue(user)
        }
    }

    fun login(context: Context?): LiveData<User?> {
        val intent = Intent(context, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(intent)
        return userLiveData;
    }

    fun isLogin(): Boolean {
        return if (mUser == null) false else mUser!!.expires_time > System.currentTimeMillis()
    }

    fun getUser(): User? = mUser

    fun getUserId(): Long {
        return if (mUser == null) 0L else mUser!!.userId
    }

    fun logout() {
        HiStorage.deleteCache(KEY_CACHE_USER)
        mUser = null
    }

    suspend fun refresh(): LiveData<User?> {
        if (!isLogin()) {
            return login(AppGlobals.get())
        }

        val queryUser = RetrofitHelper.apiServer.queryUser(getUserId())
        val liveData = MutableLiveData<User?>()
        if (queryUser.data != null) {
            save(queryUser.data.data)
        } else {
            queryUser.message.toast()
        }
        liveData.postValue(queryUser.data?.data)
        return liveData
    }
}