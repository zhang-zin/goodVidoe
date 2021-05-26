package com.zj.goodvideo.kt

import android.widget.Toast
import com.zj.hi_library.util.AppGlobals

fun String.toast(duration: Int = Toast.LENGTH_SHORT) {
    val application = AppGlobals.get()
    application?.let {
        Toast.makeText(it, this, duration).show()
    }
}