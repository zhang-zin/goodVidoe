package com.zj.goodvideo.view

import android.graphics.Outline
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import com.zj.goodvideo.R

object ViewHelper {

    const val RADIUS_ALL = 0
    const val RADIUS_LEFT = 1
    const val RADIUS_TOP = 2
    const val RADIUS_RIGHT = 3
    const val RADIUS_BOTTOM = 4

    fun setViewOutline(view: View, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val typedArray = view.context.obtainStyledAttributes(
            attrs,
            R.styleable.viewOutLineStrategy,
            defStyleAttr,
            defStyleRes
        )
        val radius =
            typedArray.getDimensionPixelSize(R.styleable.viewOutLineStrategy_clip_radius, 0)
        val hideSide = typedArray.getInt(R.styleable.viewOutLineStrategy_clip_side, 0)
        typedArray.recycle()
        setViewOutline(view, radius, hideSide)
    }

    fun setViewOutline(owner: View, radius: Int, radiusSide: Int) {
        owner.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                view?.apply {
                    if (width == 0 || height == 0) return
                    if (radiusSide != RADIUS_ALL) {
                        var left = 0
                        var top = 0
                        var right = width
                        var bottom = height
                        when (radiusSide) {
                            RADIUS_LEFT -> right += radius
                            RADIUS_TOP -> bottom += radius
                            RADIUS_RIGHT -> left -= radius
                            RADIUS_BOTTOM -> top -= radius
                        }
                        outline?.setRoundRect(left, top, right, bottom, radius.toFloat())
                        return
                    }

                    val left = 0
                    val top = 0
                    val right = width
                    val bottom = height
                    if (radius <= 0) {
                        outline?.setRect(left, top, right, bottom)
                    } else {
                        outline?.setRoundRect(left, top, right, bottom, radius.toFloat())
                    }
                }
            }
        }
        owner.clipToOutline = radius > 0
        owner.invalidate()
    }
}