package com.zj.libcommon.ui

import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.zj.libcommon.R
import com.zj.libcommon.databinding.LayoutRefreshViewBinding
import com.zj.libcommon.ui.loadState.ReposLoadStateAdapter
import java.lang.reflect.ParameterizedType

abstract class AbsListFragment<Key : Any, T : Any, M : AbsViewModel<Key, T>> :
    BaseFragment<LayoutRefreshViewBinding>(),
    OnRefreshListener,
    OnLoadMoreListener {

    lateinit var pagedListAdapter: PagingDataAdapter<T, RecyclerView.ViewHolder>
    lateinit var decoration: DividerItemDecoration
    lateinit var mViewModel: M

    override fun getLayoutId() = R.layout.layout_refresh_view

    override fun init() {
        binding.root.fitsSystemWindows = true

        binding.refreshLayout.setEnableRefresh(true)
        binding.refreshLayout.setEnableLoadMore(false)
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
        initAdapter()
    }

    private fun initAdapter() {
        /**
         * 绑定页脚适配器
         * withLoadStateHeader - 只显示页眉
         * withLoadStateFooter - 只显示页脚
         * withLoadStateHeaderAndFooter - 同时显示页眉和页脚
         */
        pagedListAdapter.withLoadStateHeaderAndFooter(
            header = ReposLoadStateAdapter { pagedListAdapter.retry() },
            footer = ReposLoadStateAdapter { pagedListAdapter.retry() }
        )

        pagedListAdapter.addLoadStateListener { loaderState ->
            //显示空布局
            val isListEmpty =
                loaderState.refresh is LoadState.NotLoading && pagedListAdapter.itemCount == 0
            finishRefresh(isListEmpty, loaderState)
            binding.refreshLayout.isVisible =
                loaderState.source.refresh is LoadState.NotLoading || pagedListAdapter.itemCount > 0

            binding.progressBar.isVisible =
                loaderState.source.refresh is LoadState.Loading && pagedListAdapter.itemCount == 0
            binding.retryButton.isVisible =
                loaderState.source.refresh is LoadState.Error && pagedListAdapter.itemCount == 0

            if (loaderState.source.refresh is LoadState.Error && pagedListAdapter.itemCount > 0) {
                Toast.makeText(
                    requireContext(),
                    "\uD83D\uDE28 刷新失败",
                    Toast.LENGTH_LONG
                ).show()
            }

            val errorState = loaderState.source.append as? LoadState.Error
                ?: loaderState.source.prepend as? LoadState.Error
                ?: loaderState.append as? LoadState.Error
                ?: loaderState.prepend as? LoadState.Error

            errorState?.let {
                Toast.makeText(
                    requireContext(),
                    "\uD83D\uDE28 Wooops ${it.error}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.retryButton.setOnClickListener { pagedListAdapter.retry() }
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

        }
    }

    private fun finishRefresh(hasListData: Boolean, loaderState: CombinedLoadStates) {
        if (loaderState.refresh is LoadState.Loading) {
            binding.refreshLayout.finishRefresh()
        }/* else {
            binding.refreshLayout.finishLoadMore()
        }*/
        binding.emptyView.isVisible = hasListData
    }

    /**
     * 我们在 onCreateView的时候 创建了 PagedListAdapter
     * 所以，如果arguments 有参数需要传递到Adapter 中，那么需要在getAdapter()方法中取出参数。
     */
    abstract fun getAdapter(): PagingDataAdapter<T, RecyclerView.ViewHolder>

}