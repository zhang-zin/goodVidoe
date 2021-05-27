package com.zj.goodvideo

import android.app.Application
import com.zj.goodvideo.ui.login.UserManager
import com.zj.libcommon.BaseApplication

class App : BaseApplication() {
    override fun onCreate() {
        super.onCreate()
        UserManager.getUser()
    }
}