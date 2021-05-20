package com.zj.goodvideo.ui.home

import androidx.lifecycle.lifecycleScope
import androidx.paging.PagedListAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.zj.goodvideo.model.Feed
import com.zj.goodvideo.ui.AbsListFragment
import com.zj.libnavannotation.FragmentDestination
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@FragmentDestination(pageUrl = "main/tabs/home", asStarter = true)
class HomeFragment : AbsListFragment<Int, Feed, HomeViewModel>() {

    override fun onRefresh(refreshLayout: RefreshLayout) {
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
    }

    override fun getAdapter(): PagingDataAdapter<Feed, RecyclerView.ViewHolder> {
        return FeedAdapter(
            requireContext(),
            "all"
        ) as PagingDataAdapter<Feed, RecyclerView.ViewHolder>
    }
}