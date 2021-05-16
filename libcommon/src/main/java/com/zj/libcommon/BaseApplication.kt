package com.zj.libcommon

import android.app.Application
import com.kingja.loadsir.core.LoadSir
import com.zj.hi_library.hiLog.HiLogManager
import com.zj.libcommon.callback.*

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        HiLogManager.init(this, BuildConfig.DEBUG, 0)
        //initLoadSir()
    }

    private fun initLoadSir() {
        LoadSir.beginBuilder()
            .addCallback(ErrorCallback()) //添加各种状态页
            .addCallback(EmptyCallback())
            .addCallback(LoadingCallback())
            .addCallback(TimeoutCallback())
            .addCallback(CustomCallback())
            .setDefaultCallback(LoadingCallback::class.java) //设置默认状态页
            .commit()
    }
}