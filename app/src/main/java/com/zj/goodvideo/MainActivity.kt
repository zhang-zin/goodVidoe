package com.zj.goodvideo

import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zj.goodvideo.ui.login.UserManager
import com.zj.goodvideo.utils.AppConfig
import com.zj.goodvideo.utils.NavGraphBuilder
import com.zj.hi_library.util.HiStatusBar

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    lateinit var navView: BottomNavigationView
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        HiStatusBar.setStatusBar(this, darkContent = true, translucent = false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)

        NavGraphBuilder.build(navController, this, R.id.nav_host_fragment)
        navView.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val destConfig = AppConfig.getDestConfig()
        val iterator = destConfig.entries.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            val value = next.value
            if (!UserManager.isLogin() && value.needLogin) {
                UserManager.login(this).observe(this) { user ->
                    user?.let { navView.selectedItemId = item.itemId }
                }
                return false
            }
        }
        navController.navigate(item.itemId)
        return !TextUtils.isEmpty(item.title)
    }
}