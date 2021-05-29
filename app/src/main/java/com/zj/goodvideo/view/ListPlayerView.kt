package com.zj.goodvideo.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerControlView
import com.zj.goodvideo.R
import com.zj.goodvideo.exoplayer.IPlayTarget
import com.zj.goodvideo.exoplayer.PageListPlayManager
import com.zj.hi_library.util.HiDisplayUtil

/**
 * 列表视频播放
 * @author zhang
 */
class ListPlayerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes), IPlayTarget,
    PlayerControlView.VisibilityListener, Player.EventListener {

    private var bufferView: View
    var cover: PPImageView
    private var blur: PPImageView
    private var playBtn: ImageView

    private var mCategory: String = ""
    private var mVideoUrl: String = ""
    private var isPlaying = false
    private var mWidthPx = 0
    private var mHeightPx = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_player_view, this, true)

        //缓冲转圈圈的view
        bufferView = findViewById(R.id.buffer_view)
        //封面
        cover = findViewById(R.id.cover)
        //高斯模糊背景
        blur = findViewById(R.id.blur_background)
        //播放暂停的按钮
        playBtn = findViewById(R.id.play_btn)

        playBtn.setOnClickListener {
            if (isPlaying) {
                inActive()
            } else {
                onActive()
            }
        }
        transitionName = "listPlayerView"
    }

    fun bindData(
        category: String,
        widthPx: Int,
        heightPx: Int,
        coverUrl: String,
        videoUrl: String
    ) {
        mCategory = category
        mVideoUrl = videoUrl
        mWidthPx = widthPx
        mHeightPx = heightPx
        cover.setImageUrl(coverUrl)

        if (widthPx < heightPx) {
            PPImageView.setBlurImageUrl(blur, coverUrl, 10)
            blur.visibility = VISIBLE
        } else {
            blur.visibility = GONE
        }

        setSize(widthPx, heightPx)
    }

    private fun setSize(widthPx: Int, heightPx: Int) {
        //这里主要是做视频宽大与高,或者高大于宽时  视频的等比缩放
        val maxWidth = HiDisplayUtil.getScreenWidth()
        val maxHeight = maxWidth

        val layoutWidth = maxWidth
        val layoutHeight: Int
        val coverWidth: Int
        var coverHeight = 0
        if (widthPx >= heightPx) {
            coverWidth = maxWidth
            layoutHeight = (heightPx / (widthPx * 1.0f / maxWidth)).toInt().also {
                coverHeight = it
            }
        } else {
            layoutHeight = maxHeight.also { coverHeight = it }
            coverWidth = (widthPx / (heightPx * 1.0f / maxHeight)).toInt()
        }

        val params = layoutParams
        params.width = layoutWidth
        params.height = layoutHeight
        layoutParams = params

        val blurParams = blur.layoutParams
        blurParams.width = layoutWidth
        blurParams.height = layoutHeight
        blur.layoutParams = blurParams

        val coverParams = cover.layoutParams as LayoutParams
        coverParams.width = coverWidth
        coverParams.height = coverHeight
        coverParams.gravity = Gravity.CENTER
        cover.layoutParams = coverParams

        val playBtnParams = playBtn.layoutParams as LayoutParams
        playBtnParams.gravity = Gravity.CENTER
        playBtn.layoutParams = playBtnParams

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        //点击视频区域时，主动让视频控制器显示
        val pageListPlay = PageListPlayManager.get(mCategory)
        pageListPlay.controlView?.show()
        return true
    }

    override fun getOwner() = this

    override fun onActive() {
        //视频播放或者恢复播放

        //通过该View所在页面的mCategory（比如首页列表tab_all，沙发的tab_video，标签帖子聚合的tag_feed）字段
        //取出管理该页面的ExoPlayer播放器，ExoplayerView播放View,控制器对象PageListPlay
        val pageListPlay = PageListPlayManager.get(mCategory)
        val playerView = pageListPlay.playerView
        val controlView = pageListPlay.controlView
        val exoPlayer = pageListPlay.exoPlayer

        if (playerView == null)
            return

        /**
         * 主动调用一次switchPlayerView，把播放器ExoPlayer和展示画面的View ExoplayerView相关联
         * 因为，在列表页点击视频item跳转到视频详情页的时候详情页会复用列表页的播放器ExoPlayer，然后和新创建的展示视频画面View ExoplayerView相关联相关联
         * 如果 再次返回列表页，则需要再次把播放器和ExoplayerView相关联
         */
        pageListPlay.switchPlayerView(playerView, true)
        val parent = playerView.parent
        if (parent != this) {
            //把展示视频画面的View添加到ItemView的容器上
            if (parent != null) {
                (parent as ViewGroup).removeView(playerView)
                //还应该暂停掉列表上正在播放的那个
                (parent as ListPlayerView).inActive()
            }

            val coverParams = cover.layoutParams
            this.addView(playerView, 1, coverParams)
        }

        val controlParent = controlView?.parent
        if (controlParent != this) {
            //视频控制器，添加到ItemView的容器上
            if (controlParent != null) {
                (controlParent as ViewGroup).removeView(controlView)
            }
            val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            params.gravity = Gravity.BOTTOM
            this.addView(controlView, params)
        }

        //如果是同一个视频资源则不需要创建MediaSource
        //但需要onPlayerStateChanged 否则不会触发onPlayerStateChanged()
        if (TextUtils.equals(pageListPlay.playUrl, mVideoUrl)) {
            onPlayerStateChanged(true, Player.STATE_READY)
        } else {
            val mediaSource = PageListPlayManager.createMediaSource(mVideoUrl)
            exoPlayer?.apply {
                prepare(mediaSource)
                repeatMode = Player.REPEAT_MODE_ONE
            }
            pageListPlay.playUrl = mVideoUrl
        }

        controlView?.show()
        controlView?.setVisibilityListener(this)
        exoPlayer?.addListener(this)
        exoPlayer?.playWhenReady = true
    }

    override fun inActive() {
        //暂停播放并让封面和开始播放按钮显示
        val pageListPlay = PageListPlayManager.get(mCategory)
        if (pageListPlay.exoPlayer == null || pageListPlay.controlView == null) return
        pageListPlay.exoPlayer!!.playWhenReady =false
        pageListPlay.exoPlayer!!.removeListener(this)
        pageListPlay.controlView!!.setVisibilityListener(null)

        cover.visibility = VISIBLE
        playBtn.visibility = VISIBLE
        playBtn.setImageResource(R.drawable.icon_video_play)
    }

    override fun isPlaying() = isPlaying

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isPlaying =false
        bufferView.visibility = GONE
        cover.visibility = VISIBLE
        playBtn.visibility = VISIBLE
        playBtn.setImageResource(R.drawable.icon_video_play)
    }

    override fun onVisibilityChange(visibility: Int) {
        playBtn.visibility = visibility
        playBtn.setImageResource(if (isPlaying()) R.drawable.icon_video_pause else R.drawable.icon_video_play)
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        //监听视频播放的状态
        val pageListPlay = PageListPlayManager.get(mCategory)
        val exoPlayer = pageListPlay.exoPlayer as SimpleExoPlayer
        if (playbackState == Player.STATE_READY && exoPlayer.bufferedPosition != 0L && playWhenReady) {
            cover.visibility = GONE
            bufferView.visibility = GONE
        } else if (playbackState == Player.STATE_BUFFERING) {
            bufferView.visibility = VISIBLE
        }
        isPlaying =
            playbackState == Player.STATE_READY && exoPlayer.bufferedPosition != 0L && playWhenReady
        playBtn.setImageResource(if (isPlaying) R.drawable.icon_video_pause else R.drawable.icon_video_play)
    }

    fun getPlayController(): PlayerControlView? {
        val listPlay = PageListPlayManager.get(mCategory!!)
        return listPlay.controlView
    }

}