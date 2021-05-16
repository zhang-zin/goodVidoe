package com.zj.goodvideo.ui.publish

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zj.goodvideo.R
import com.zj.libnavannotation.ActivityDestination

@ActivityDestination(pageUrl = "main/tabs/publish", needLogin = true)
class PublishActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publish)
    }
}