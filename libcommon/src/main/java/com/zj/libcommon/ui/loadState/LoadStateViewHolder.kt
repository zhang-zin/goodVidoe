package com.zj.libcommon.ui.loadState

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.zj.libcommon.databinding.LayoutLoadStateFooterViewBinding

class LoadStateViewHolder(
    private val binding: LayoutLoadStateFooterViewBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.retryButton.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.errorMsg.text = loadState.error.localizedMessage
        }

        binding.errorMsg.isVisible = loadState is LoadState.Error
        binding.retryButton.isVisible = loadState is LoadState.Error
        binding.progressBar.isVisible = loadState is LoadState.Loading
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): LoadStateViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = LayoutLoadStateFooterViewBinding.inflate(inflater, parent, false)
            return LoadStateViewHolder(binding, retry)
        }
    }
}