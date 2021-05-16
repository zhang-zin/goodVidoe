package com.zj.goodvideo.utils

import android.content.ComponentName
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphNavigator
import com.zj.goodvideo.navigator.FixFragmentNavigator
import com.zj.hi_library.util.AppGlobals


object NavGraphBuilder {

    fun build(navController: NavController, activity: FragmentActivity, containerId: Int) {
        val provider = navController.navigatorProvider
        val activityNavigator = provider.getNavigator(ActivityNavigator::class.java)

        val hostFragment: Fragment? =
            activity.supportFragmentManager.findFragmentById(containerId)
        val childFragmentManager: FragmentManager =
            hostFragment?.childFragmentManager ?: activity.supportFragmentManager
        val fixFragmentNavigator = FixFragmentNavigator(activity, childFragmentManager, containerId)
        provider.addNavigator(fixFragmentNavigator)

        val navGraph = NavGraph(NavGraphNavigator(provider))

        val destConfig = AppConfig.getDestConfig()
        for (value in destConfig.values) {
            if (value.isFragment) {
                val destination = fixFragmentNavigator.createDestination()
                destination.id = value.id
                destination.className = value.className
                destination.addDeepLink(value.pageUrl)
                navGraph.addDestination(destination)
            } else {
                val destination = activityNavigator.createDestination()
                destination.id = value.id
                destination.addDeepLink(value.pageUrl)
                destination.setComponentName(
                    ComponentName(
                        AppGlobals.get()?.packageName ?: "",
                        value.className
                    )
                )
                navGraph.addDestination(destination)
            }

            if (value.asStarter) {
                navGraph.startDestination = value.id
            }
        }

        navController.graph = navGraph
    }
}