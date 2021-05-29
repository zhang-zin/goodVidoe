package com.zj.libcommon.extention

import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class AbsPagedListAdapter<T : Any, VH : RecyclerView.ViewHolder>(diffCallback: DiffUtil.ItemCallback<T>) :
    PagingDataAdapter<T, VH>(
        diffCallback
    ) {

    private val mHeaders = SparseArray<View>()
    private val mFooters = SparseArray<View>()

    private var BASE_ITEM_TYPE_HEADER = 100000
    private var BASE_ITEM_TYPE_FOOTER = 200000

    //region 头布局和脚布局操作
    fun addHeaderView(view: View) {
        //判断view是否还没有存储在mHeaders数组里
        if (mHeaders.indexOfValue(view) < 0) {
            mHeaders.put(BASE_ITEM_TYPE_HEADER++, view)
            notifyDataSetChanged()
        }
    }

    /**
     * 移除头部
     */
    fun removeHeaderView(view: View) {
        val index = mHeaders.indexOfValue(view)
        if (index < 0) return
        mHeaders.removeAt(index)
        notifyDataSetChanged()
    }

    fun getHeaderCount() = mHeaders.size()

    fun addFooterView(view: View) {
        //判断view是否是没有储在mFooters数组里
        if (mFooters.indexOfValue(view) < 0) {
            mFooters.put(BASE_ITEM_TYPE_FOOTER++, view)
            notifyDataSetChanged()
        }
    }

    fun removeFooterView(view: View) {
        val index = mFooters.indexOfValue(view)
        if (index < 0) return
        mFooters.removeAt(index)
        notifyDataSetChanged()
    }

    fun getFooterCount() = mFooters.size()

    //endregion

    /**
     * 获取真正的数量
     */
    fun getOriginalItemCount() = itemCount - mHeaders.size() - mFooters.size()

    override fun getItemCount(): Int {
        val itemCount = super.getItemCount()
        return itemCount + mHeaders.size() + mFooters.size()
    }

    private fun isHeaderPosition(position: Int): Boolean {
        return position < mHeaders.size()
    }

    private fun isFooterPosition(position: Int): Boolean {
        return position >= getOriginalItemCount() + mHeaders.size()
    }

    override fun getItemViewType(position: Int): Int {
        if (isHeaderPosition(position)) {
            return mHeaders.keyAt(position)
        }

        if (isFooterPosition(position)) {
            val footerPosition = position - getOriginalItemCount() - mHeaders.size()
            return mFooters.keyAt(footerPosition)
        }

        val realPosition = position - mHeaders.size()
        return getItemViewType2(realPosition)
    }

    protected fun getItemViewType2(position: Int): Int {
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        if (mHeaders.indexOfKey(viewType) >= 0) {
            val view = mHeaders.get(viewType)
            return object : RecyclerView.ViewHolder(view) {} as VH
        }

        if (mFooters.indexOfKey(viewType) >= 0) {
            val view = mFooters.get(viewType)
            return object : RecyclerView.ViewHolder(view) {} as VH
        }

        return onCreateViewHolder2(parent, viewType)
    }

    abstract fun onCreateViewHolder2(parent: ViewGroup, viewType: Int): VH

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (isHeaderPosition(position) || isFooterPosition(position))
            return
        val realPosition = position - mHeaders.size()
        onBindViewHolder2(holder, realPosition)
    }

    abstract fun onBindViewHolder2(holder: VH, position: Int)

    override fun onViewAttachedToWindow(holder: VH) {
        if (!isHeaderPosition(holder.bindingAdapterPosition) && !isFooterPosition(holder.bindingAdapterPosition)) {
            this.onViewAttachedToWindow2(holder)
        }
    }

    fun onViewAttachedToWindow2(holder: VH) {}

    override fun onViewDetachedFromWindow(holder: VH) {
        if (!isHeaderPosition(holder.bindingAdapterPosition) && !isFooterPosition(holder.bindingAdapterPosition)) {
            this.onViewDetachedFromWindow2(holder)
        }
    }

    fun onViewDetachedFromWindow2(holder: VH) {}

//    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
//        super.registerAdapterDataObserver(AdapterDataObserverProxy(observer))
//    }

    /**
     * 当先添加了headerView，而后网络数据回来再更新到列表上
     * Paging在计算列表item的位置时，并不会顾及添加的headerView,就会出现列表定位问题
     * 实际上 RecyclerView#setAdapter方法，它会给Adapter注册了一个AdapterDataObserver
     * 可以代理registerAdapterDataObserver()传递进来的observer。在各个方法的实现中，把headerView的个数算上，再中转出去即可
     */
    private inner class AdapterDataObserverProxy(private val observer: RecyclerView.AdapterDataObserver) :
        RecyclerView.AdapterDataObserver() {

        override fun onChanged() {
            observer.onChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            observer.onItemRangeChanged(positionStart + mHeaders.size(), itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            observer.onItemRangeChanged(positionStart + mHeaders.size(), itemCount, payload)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            observer.onItemRangeInserted(positionStart + mHeaders.size(), itemCount)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            observer.onItemRangeRemoved(positionStart + mHeaders.size(), itemCount)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            observer.onItemRangeMoved(
                fromPosition + mHeaders.size(),
                toPosition + mHeaders.size(),
                itemCount
            )
        }
    }
}