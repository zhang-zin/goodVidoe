package com.zj.goodvideo.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class CornerFrameLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    init {
        ViewHelper.setViewOutline(this, attrs, defStyleAttr, defStyleRes)
    }

    fun setViewOutline(radius: Int, radiusSide: Int) {
        ViewHelper.setViewOutline(this, radius, radiusSide)
    }
}