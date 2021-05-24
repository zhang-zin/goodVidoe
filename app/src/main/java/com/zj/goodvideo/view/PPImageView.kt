package com.zj.goodvideo.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.zj.goodvideo.R
import com.zj.hi_library.util.HiDisplayUtil
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class PPImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    companion object {
        @BindingAdapter(value = ["image_url", "isCircle"])
        @JvmStatic
        fun setImageUrl(view: PPImageView, imageUrl: String, isCircle: Boolean) {
            setImageUrl(view, imageUrl, isCircle, 0)
        }

        @BindingAdapter(value = ["image_url", "isCircle", "radius"], requireAll = false)
        @JvmStatic
        fun setImageUrl(view: PPImageView, imageUrl: String?, isCircle: Boolean, radius: Int) {
            if (imageUrl == null) return
            val builder = Glide.with(view).load(imageUrl)
            if (isCircle) {
                builder.transform(CircleCrop())
            } else if (radius > 0) {
                builder.transform(
                    RoundedCornersTransformation(
                        HiDisplayUtil.dp2px(radius.toFloat()),
                        0
                    )
                )
            }
            val layoutParams = view.layoutParams
            if (layoutParams != null && layoutParams.width > 0 && layoutParams.height > 0) {
                builder.override(layoutParams.width, layoutParams.height)
            }
            builder.error(R.drawable.icon_cell_comment).into(view)
        }

        @BindingAdapter(value = ["blur_url", "radius"])
        @JvmStatic
        fun setBlurImageUrl(imageView: ImageView, blurUrl: String?, radius: Int) {
            if (blurUrl == null) return
            Glide.with(imageView).load(blurUrl).override(radius)
                .transform(BlurTransformation())
                .dontAnimate()
                .into(object : SimpleTarget<Drawable?>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable?>?
                    ) {
                        imageView.background = resource
                    }
                })
        }
    }

    init {
        ViewHelper.setViewOutline(this, attrs, defStyleAttr, 0)
    }

    fun setImageUrl(imageUrl: String) {
        setImageUrl(this, imageUrl, false)
    }

    fun bindData(widthPx: Int, heightPx: Int, marginLeft: Int, imageUrl: String) {
        bindData(
            widthPx,
            heightPx,
            marginLeft,
            HiDisplayUtil.getScreenWidth(),
            HiDisplayUtil.getScreenHeight(),
            imageUrl
        )
    }

    fun bindData(
        widthPx: Int,
        heightPx: Int,
        marginLeft: Int,
        maxWidth: Int,
        maxHeight: Int,
        imageUrl: String
    ) {
        visibility = if (imageUrl.isEmpty()) {
            View.GONE
        } else {
            View.INVISIBLE
        }

        if (widthPx <= 0 || heightPx <= 0) {
            Glide.with(this).load(imageUrl).into(object : SimpleTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    val width = resource.intrinsicWidth
                    val height = resource.intrinsicHeight
                    setSize(width, height, marginLeft, maxWidth, maxHeight)
                    setImageDrawable(resource)
                }
            })
            return
        }
        setSize(widthPx, heightPx, marginLeft, maxWidth, maxHeight)
        setImageUrl(this, imageUrl, false)
    }

    private fun setSize(width: Int, height: Int, marginLeft: Int, maxWidth: Int, maxHeight: Int) {
        val finalWidth: Int
        val finalHeight: Int
        if (width > height) {
            finalWidth = maxWidth
            finalHeight = (height / (width * 1.0f / finalWidth)).toInt()
        } else {
            finalHeight = maxHeight
            finalWidth = (width / (height * 1.0f / finalHeight)).toInt()
        }

        val params = layoutParams
        params.width = finalWidth
        params.height = finalHeight
        if (params is FrameLayout.LayoutParams) {
            params.leftMargin = if (height > width) HiDisplayUtil.dp2px(marginLeft.toFloat()) else 0
        } else if (params is LinearLayout.LayoutParams) {
            params.leftMargin = if (height > width) HiDisplayUtil.dp2px(marginLeft.toFloat()) else 0
        }
        layoutParams = params
    }
}