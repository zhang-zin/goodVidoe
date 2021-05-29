package com.zj.goodvideo.ui.home

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.zj.goodvideo.exoplayer.PageListPlayDetector
import com.zj.goodvideo.exoplayer.PageListPlayManager
import com.zj.goodvideo.model.Feed
import com.zj.libcommon.ui.AbsListFragment
import com.zj.libnavannotation.FragmentDestination
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@FragmentDestination(pageUrl = "main/tabs/home", asStarter = true)
class HomeFragment : AbsListFragment<Int, Feed, HomeViewModel, FeedAdapter.ViewHolder>() {

    private var feedType: String = ""
    private var shouldPause = true
    private lateinit var playDetector: PageListPlayDetector

    companion object {
        fun newInstance(feedType: String): HomeFragment {
            val bundle = Bundle()
            bundle.putString("feedType", feedType)
            val homeFragment = HomeFragment()
            homeFragment.arguments = bundle
            return homeFragment
        }
    }

    private var requestJob: Job? = null

    override fun onRefresh(refreshLayout: RefreshLayout) {
        pagedListAdapter.refresh()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
    }

    override fun getAdapter(): PagingDataAdapter<Feed, FeedAdapter.ViewHolder> {
        feedType = if (arguments == null) "all" else
            requireArguments().getString("feedType").toString()

        return object : FeedAdapter(requireContext(), feedType) {

            override fun onViewAttachedToWindow(holder: FeedAdapter.ViewHolder) {
                if (holder.isVideoItem()) {
                    playDetector.addTarget(holder.getListPlayerView())
                }
            }

            override fun onViewDetachedFromWindow(holder: ViewHolder) {
                playDetector.removeTarget(holder.getListPlayerView())
            }

        }
    }

    override fun init() {
        super.init()
        getFeedListData()
        pagedListAdapter.addLoadStateListener {
            // CombinedLoadStates.refresh - 表示首次加载 PagingData 的加载状态。
            // CombinedLoadStates.prepend - 表示在列表开头加载数据时的加载状态。
            // CombinedLoadStates.append - 表示在列表末尾加载数据的加载状态。
        }
        /*
        // 从网络刷新列表时，滚动到顶部。
        lifecycleScope.launch {
            pagedListAdapter.loadStateFlow
                // 仅在REFRESH LoadState更改时发出。
                .distinctUntilChangedBy { it.refresh }
                // Only react to cases where REFRESH completes i.e., NotLoading.
                .filter { it.refresh is LoadState.NotLoading }
                .collect {
                    // 设置滑动位置
                }
        }
        */
        playDetector = PageListPlayDetector(this, binding.recycleView)
    }

    private fun getFeedListData() {
        requestJob?.cancel()
        requestJob = lifecycleScope.launch {
            mViewModel.searchRepo(feedType).collectLatest {
                pagedListAdapter.submitData(it)
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            playDetector.onPause()
        } else {
            playDetector.onResume()
        }
    }

    override fun onPause() {
        //如果是跳转到详情页就不需要 暂停视频播放了
        //如果是前后台切换 或者去别的页面了 都是需要暂停视频播放的
        if (shouldPause) {
            playDetector.onPause()
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        shouldPause = true
        //由于沙发Tab的几个子页面 复用了HomeFragment。
        //需要判断下 当前页面 它是否有ParentFragment.
        //当且仅当 它和它的ParentFragment均可见的时候，才能恢复视频播放
        if (parentFragment != null) {
            if (requireParentFragment().isVisible && isVisible) {
                playDetector.onResume()
            }
        } else {
            if (isVisible)
                playDetector.onResume()
        }
    }

    override fun onDestroy() {
        PageListPlayManager.release(feedType)
        super.onDestroy()
    }
}