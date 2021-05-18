package com.zj.goodvideo.ui

import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.zj.goodvideo.R
import com.zj.goodvideo.databinding.LayoutRefreshViewBinding
import com.zj.libcommon.ui.BaseFragment
import java.lang.reflect.ParameterizedType

abstract class AbsListFragment<Key, T, M : AbsViewModel<Key, T>> :
    BaseFragment<LayoutRefreshViewBinding>(),
    OnRefreshListener,
    OnLoadMoreListener {

    lateinit var pagedListAdapter: PagedListAdapter<T, RecyclerView.ViewHolder>
    lateinit var decoration: DividerItemDecoration
    lateinit var mViewModel: M

    override fun getLayoutId() = R.layout.layout_refresh_view

    override fun init() {
        binding.root.fitsSystemWindows = true

        binding.refreshLayout.setEnableRefresh(true)
        binding.refreshLayout.setEnableLoadMore(true)
        binding.refreshLayout.setOnRefreshListener(this)
        binding.refreshLayout.setOnLoadMoreListener(this)

        pagedListAdapter = getAdapter()
        binding.recycleView.adapter = pagedListAdapter
        binding.recycleView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recycleView.itemAnimator = null

        //默认给列表中的Item 一个 10dp的ItemDecoration
        decoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        decoration.setDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.list_divider
            )!!
        )
        binding.recycleView.addItemDecoration(decoration)
        genericViewModel()
    }

    private fun genericViewModel() {

        //利用 子类传递的 泛型参数实例化出absViewModel 对象。
        val type = javaClass.genericSuperclass as ParameterizedType
        val arguments = type.actualTypeArguments
        if (arguments.size > 1) {
            val argument = arguments[2]
            val modelClazz: Class<M> =
                (argument as Class<*>).asSubclass(AbsViewModel::class.java) as Class<M>
            mViewModel = ViewModelProvider(this).get(modelClazz)

            //触发页面初始化数据加载的逻辑
            mViewModel.getPageData().observe(this) { pagedList -> submitList(pagedList) }

            //监听分页时有无更多数据,以决定是否关闭上拉加载的动画
            mViewModel.getBoundaryPageData().observe(this) { hasData -> finishRefresh(hasData) }
        }
    }

    fun submitList(result: PagedList<T>) {
        //只有当新数据集合大于0 的时候，才调用adapter.submitList
        //否则可能会出现 页面----有数据----->被清空-----空布局
        if (result.size > 0) {
            pagedListAdapter.submitList(result)
        }
        finishRefresh(result.size > 0)
    }

    fun finishRefresh(hasListData: Boolean) {
        val currentList: PagedList<T>? = pagedListAdapter.currentList
        val hasData = hasListData || currentList != null && currentList.size > 0
        val state: RefreshState = binding.refreshLayout.state
        if (state.isFooter && state.isOpening) {
            binding.refreshLayout.finishLoadMore()
        } else if (state.isHeader && state.isOpening) {
            binding.refreshLayout.finishRefresh()
        }
        if (hasData) {
            binding.emptyView.visibility = View.GONE
        } else {
            binding.emptyView.visibility = View.VISIBLE
        }
    }

    /**
     * 我们在 onCreateView的时候 创建了 PagedListAdapter
     * 所以，如果arguments 有参数需要传递到Adapter 中，那么需要在getAdapter()方法中取出参数。
     */
    abstract fun getAdapter(): PagedListAdapter<T, RecyclerView.ViewHolder>

}