package com.zj.goodvideo.ui.share

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zj.goodvideo.R
import com.zj.goodvideo.view.CornerFrameLayout
import com.zj.goodvideo.view.PPImageView
import com.zj.goodvideo.view.ViewHelper
import com.zj.hi_library.util.HiDisplayUtil
import java.util.*

class ShareDialog(context: Context) : AlertDialog(context) {

    lateinit var layout: CornerFrameLayout
    val adapter = ShareAdapter()
    var shareitems = mutableListOf<ResolveInfo>()
    private var shareContent: String? = ""
    private var clickListener: () -> Unit? = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        layout = CornerFrameLayout(context)
        layout.setBackgroundColor(Color.WHITE)
        layout.setViewOutline(HiDisplayUtil.dp2px(20f), ViewHelper.RADIUS_TOP)

        val gridView = RecyclerView(context)
        gridView.layoutManager = GridLayoutManager(context, 4)
        gridView.adapter = adapter

        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.gravity = Gravity.CENTER
        val margin = HiDisplayUtil.dp2px(20f)
        params.leftMargin = margin.also {
            params.topMargin = it
            params.rightMargin = it
            params.bottomMargin = it
        }

        layout.addView(gridView, params)

        setContentView(layout)
        window?.apply {
            setGravity(Gravity.BOTTOM)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        queryShareItems()
    }

    fun setShareContent(shareContent: String) {
        this.shareContent = shareContent
    }

    fun setListener(clickListener: () -> Unit) {
        this.clickListener = clickListener
    }

    private fun queryShareItems() {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "text/plain"

        val resolveInfos = context.packageManager.queryIntentActivities(intent, 0)
        for (resolveInfo in resolveInfos) {
            val packageName = resolveInfo.activityInfo.packageName
//            if (TextUtils.equals(packageName, "com.tencent.mm")
//                || TextUtils.equals(packageName, "com.tencent.mobileqq")
//            ) {
//                shareitems.add(resolveInfo)
//            }
            shareitems.add(resolveInfo)
        }
        if (shareitems.isEmpty()) {
            dismiss()
        }
        adapter.notifyDataSetChanged()
    }

    inner class ShareAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val packageManager = context.packageManager

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflate =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_share_item, parent, false)
            return object : RecyclerView.ViewHolder(inflate) {}
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val resolveInfo = shareitems[position]
            val shareText = holder.itemView.findViewById<TextView>(R.id.share_text)
            val shareIcon = holder.itemView.findViewById<PPImageView>(R.id.share_icon)
            shareText.text = resolveInfo.loadLabel(packageManager)
            shareIcon.setImageDrawable(resolveInfo.loadIcon(packageManager))

            holder.itemView.setOnClickListener {
                val packageName = resolveInfo.activityInfo.packageName
                val cls = resolveInfo.activityInfo.name;
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.type = "text/plain"
                intent.component = ComponentName(packageName, cls)
                intent.putExtra(Intent.EXTRA_TEXT, shareContent);

                context.startActivity(intent)
                clickListener.invoke()
                dismiss()
            }
        }

        override fun getItemCount() = shareitems.size
    }
}