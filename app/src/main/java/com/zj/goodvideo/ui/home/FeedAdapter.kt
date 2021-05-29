package com.zj.goodvideo.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.zj.goodvideo.BR
import com.zj.goodvideo.R
import com.zj.goodvideo.databinding.LayoutFeedTypeImageBinding
import com.zj.goodvideo.databinding.LayoutFeedTypeVideoBinding
import com.zj.goodvideo.model.Feed
import com.zj.goodvideo.view.ListPlayerView
import com.zj.goodvideo.view.PPImageView
import com.zj.libcommon.extention.AbsPagedListAdapter

open class FeedAdapter(private val context: Context, val category: String) :
    AbsPagedListAdapter<Feed, FeedAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<Feed>() {
        override fun areItemsTheSame(oldItem: Feed, newItem: Feed): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Feed, newItem: Feed): Boolean {
            return oldItem == newItem
        }
    }) {

    private val inflater = LayoutInflater.from(context)

    override fun getItemViewType(position: Int): Int {
        val feed = getItem(position)
        return when (feed?.itemType) {
            Feed.TYPE_IMAGE_TEXT -> {
                R.layout.layout_feed_type_image
            }
            Feed.TYPE_VIDEO -> {
                R.layout.layout_feed_type_video
            }
            else -> 0
        }
    }

    override fun onCreateViewHolder2(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, viewType, parent, false)
        return ViewHolder(binding.root, binding)
    }

    override fun onBindViewHolder2(holder: ViewHolder, position: Int) {
        holder.bindData(getItem(position))

        holder.itemView.setOnClickListener {

        }
    }

    inner class ViewHolder(itemView: View, val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(itemView) {

        private var listPlayerView: ListPlayerView? = null
        private var feedImage: PPImageView? = null

        fun bindData(item: Feed?) {
            item?.run {
                binding.setVariable(BR.feed, this)
                binding.setVariable(BR.lifeCycleOwner, context)
                if (binding is LayoutFeedTypeVideoBinding) {
                    val videoBinding = binding
                    videoBinding.listPlayerView.bindData(category, width, height, cover, url)
                    listPlayerView = videoBinding.listPlayerView
                } else if (binding is LayoutFeedTypeImageBinding) {
                    val imageBinding = binding
                    imageBinding.feedImage.bindData(width, height, 16, cover ?: "")
                    feedImage = imageBinding.feedImage
                }
            }
        }

        fun isVideoItem() = binding is LayoutFeedTypeVideoBinding

        fun getListPlayerView() = listPlayerView
    }

}