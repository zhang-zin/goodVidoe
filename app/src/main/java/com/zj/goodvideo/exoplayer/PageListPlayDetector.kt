package com.zj.goodvideo.exoplayer

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.zj.goodvideo.view.ListPlayerView

/**
 * 列表视频自动播放检测逻辑
 */
class PageListPlayDetector(owner: LifecycleOwner, val recyclerView: RecyclerView) {

    //正在播放的
    private var playingTarget: IPlayTarget? = null

    private val mTargets = mutableListOf<IPlayTarget>()

    private val mDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            postAutoPlay()
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                autoPlay()
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dx == 0 && dy == 0) {
                //时序问题。当执行了AdapterDataObserver#onItemRangeInserted  可能还没有被布局到RecyclerView上。
                //所以此时 recyclerView.getChildCount()还是等于0的。
                //等childView 被布局到RecyclerView上之后，会执行onScrolled（）方法
                //并且此时 dx,dy都等于0
                postAutoPlay()
            } else {
                playingTarget?.run {
                    //如果正在播放，且滑动时滑出了屏幕 暂停播放
                    if (isPlaying() && !isTargetInBounds(this)) {
                        inActive()
                    }
                }
            }
        }
    }

    private val delayAutoPlay = Runnable {
        autoPlay()
    }

    init {
        owner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    recyclerView.adapter?.unregisterAdapterDataObserver(mDataObserver)
                    recyclerView.removeOnScrollListener(scrollListener)
                    owner.lifecycle.removeObserver(this)
                }
            }
        })

        recyclerView.adapter?.registerAdapterDataObserver(mDataObserver)
        recyclerView.addOnScrollListener(scrollListener)
    }


    fun addTarget(target: IPlayTarget?) {
        if (target != null) {
            mTargets.add(target)
        }
    }

    fun removeTarget(target: IPlayTarget?) {
        if (target != null) {
            mTargets.remove(target)
        }
    }

    private fun postAutoPlay() {
        recyclerView.post(delayAutoPlay)
    }

    private fun autoPlay() {
        if (mTargets.isEmpty() || recyclerView.childCount <= 0)
            return

        if (playingTarget != null &&
            playingTarget?.isPlaying() == true && isTargetInBounds(playingTarget!!)
        )
            return

        var activeTarget: IPlayTarget? = null
        for (target in mTargets) {
            if (isTargetInBounds(target)) {
                activeTarget = target
                break
            }
        }
        activeTarget?.run {
            playingTarget?.inActive()
            playingTarget = activeTarget
            // TODO: 2021/5/29 控制加载列表item自动播放 
            //onActive()
        }
    }

    /**
     * 检测IPlayTarget是在的ViewGroup是否至少还有一半在屏幕内
     */
    private fun isTargetInBounds(iPlayTarget: IPlayTarget): Boolean {
        val owner = iPlayTarget.getOwner()
        ensureRecycleViewLocation()
        if (!owner.isShown || !owner.isAttachedToWindow)
            return false

        val location = IntArray(2)
        owner.getLocationOnScreen(location)

        val center = location[1] + owner.height / 2

        //承载视频播放画面的ViewGroup它需要至少一半的大小 在RecyclerView上下范围内
        return if (rvLocation != null) {
            center >= rvLocation!!.first && center <= rvLocation!!.second
        } else
            false
    }

    private var rvLocation: Pair<Int, Int>? = null

    /**
     * 得到RecycledView的top和bottom坐标
     */
    private fun ensureRecycleViewLocation(): Pair<Int, Int> {
        if (rvLocation == null) {
            val location = IntArray(2)
            recyclerView.getLocationOnScreen(location)

            val top = location[1]
            val bottom = top + recyclerView.height
            rvLocation = Pair(top, bottom)
        }

        return rvLocation!!
    }

    fun onPause() {
        playingTarget?.inActive()
    }

    fun onResume() {
        playingTarget?.onActive()
    }

}