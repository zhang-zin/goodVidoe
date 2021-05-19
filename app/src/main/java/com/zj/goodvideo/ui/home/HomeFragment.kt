package com.zj.goodvideo.ui.home

import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.zj.goodvideo.model.Feed
import com.zj.goodvideo.ui.AbsListFragment
import com.zj.libnavannotation.FragmentDestination

@FragmentDestination(pageUrl = "main/tabs/home", asStarter = true)
class HomeFragment : AbsListFragment<Int, Feed, HomeViewModel>() {

    override fun onRefresh(refreshLayout: RefreshLayout) {
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
    }

    override fun getAdapter(): PagedListAdapter<Feed, RecyclerView.ViewHolder> {
        return FeedAdapter(
            requireContext(),
            "all"
        ) as PagedListAdapter<Feed, RecyclerView.ViewHolder>
    }
}