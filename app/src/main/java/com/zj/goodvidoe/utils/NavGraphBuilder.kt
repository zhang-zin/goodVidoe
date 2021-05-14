package com.zj.goodvidoe.utils

import android.content.ComponentName
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphNavigator
import androidx.navigation.fragment.FragmentNavigator
import com.zj.hi_library.util.AppGlobals

object NavGraphBuilder {

    fun build(navController: NavController) {
        val provider = navController.navigatorProvider
        val activityNavigator = provider.getNavigator(ActivityNavigator::class.java)
        val fragmentNavigator = provider.getNavigator(FragmentNavigator::class.java)

        val navGraph = NavGraph(NavGraphNavigator(provider))

        val destConfig = AppConfig.getDestConfig()
        for (value in destConfig.values) {
            if (value.isFragment) {
                val destination = fragmentNavigator.createDestination()
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