package com.zj.goodvideo.exoplayer

import android.view.ViewGroup

interface IPlayTarget {

    fun getOwner(): ViewGroup

    /**
     * 活跃状态，视频可以播放
     */
    fun onActive()

    /**
     * 非活跃状态，状态播放
     */
    fun inActive()

    fun isPlaying(): Boolean
}