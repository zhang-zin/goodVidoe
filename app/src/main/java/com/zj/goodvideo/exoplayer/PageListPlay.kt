package com.zj.goodvideo.exoplayer

import android.view.LayoutInflater
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.zj.goodvideo.R
import com.zj.hi_library.util.AppGlobals

class PageListPlay {

    var exoPlayer: SimpleExoPlayer? = null
    var playerView: PlayerView? = null
    var controlView: PlayerControlView? = null
    var playUrl:String = ""

    init {
        val application = AppGlobals.get()
        exoPlayer = ExoPlayerFactory.newSimpleInstance(
            application,
            DefaultRenderersFactory(application), //视频每一帧如何渲染默认实现类
            DefaultTrackSelector(), //视频的音轨如何加载
            DefaultLoadControl() //视频缓存控制逻辑
        )

        //加载布局层级展示视频画面的View
        playerView =
            LayoutInflater.from(application)
                .inflate(R.layout.layout_exo_player_view, null, false) as PlayerView
        //视频播放控制器
        controlView = LayoutInflater.from(application)
            .inflate(R.layout.layout_exo_player_contorller_view, null, false) as PlayerControlView

        //把播放器实例 和 playerView，controlView相关联
        //如此视频画面才能正常显示,播放进度条才能自动更新
        playerView?.player = exoPlayer
        controlView?.player = exoPlayer
    }

    fun release() {
        exoPlayer?.apply {
            playWhenReady = false
            stop(true)
            release()
            exoPlayer = null
        }

        playerView?.apply {
            player = null
            playerView = null
        }

        controlView?.apply {
            player = null
            setVisibilityListener(null)
            controlView = null
        }
    }

    /**
     * 切换与播放器[exoPlayer] 绑定的exoplayerView，用于页面切换视频无缝续播的场景
     */
    fun switchPlayerView(newPlayerView: PlayerView, attach: Boolean) {
        playerView?.player = if (attach) null else exoPlayer
        newPlayerView.player = if (attach) exoPlayer else null
    }
}