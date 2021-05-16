package com.zj.goodvideo.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.zj.goodvideo.R
import com.zj.hi_library.util.HiDisplayUtil

/**
 * 列表视频播放
 * @author zhang
 */
class ListPlayerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    var bufferView: View
    var cover: PPImageView
    var blur: PPImageView
    var playBtn: ImageView

    var mCategory: String? = null
    var mVideoUrl: String? = null
    var isPlaying = false
    var mWidthPx = 0
    var mHeightPx = 0

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
            blur.visibility = INVISIBLE
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
            layoutHeight = (heightPx / (widthPx * 1.0f / maxWidth)).toInt()
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

    fun getOwner(): ViewGroup {
        return this
    }

}