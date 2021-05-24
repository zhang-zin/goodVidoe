package com.zj.goodvideo.ui.home

import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.zj.goodvideo.model.Feed
import com.zj.libcommon.ui.AbsListFragment
import com.zj.libnavannotation.FragmentDestination
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@FragmentDestination(pageUrl = "main/tabs/home", asStarter = true)
class HomeFragment : AbsListFragment<Int, Feed, HomeViewModel>() {

    private var requestJob: Job? = null

    override fun onRefresh(refreshLayout: RefreshLayout) {
        pagedListAdapter.refresh()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
    }

    override fun getAdapter(): PagingDataAdapter<Feed, RecyclerView.ViewHolder> {
        return FeedAdapter(
            requireContext(),
            "all"
        ) as PagingDataAdapter<Feed, RecyclerView.ViewHolder>
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
    }

    private fun getFeedListData() {
        requestJob?.cancel()
        requestJob = lifecycleScope.launch {
            mViewModel.searchRepo("all").collectLatest {
                pagedListAdapter.submitData(it)
            }
        }
    }
}