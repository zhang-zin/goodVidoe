package com.zj.goodvideo.ui.sofa

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.zj.goodvideo.R
import com.zj.goodvideo.databinding.FragmentSofaBinding
import com.zj.goodvideo.model.SofaTab
import com.zj.goodvideo.ui.home.HomeFragment
import com.zj.goodvideo.utils.AppConfig
import com.zj.libcommon.ui.BaseFragment
import com.zj.libnavannotation.FragmentDestination

@FragmentDestination(pageUrl = "main/tabs/sofa")
class SofaFragment : BaseFragment<FragmentSofaBinding>() {

    private lateinit var tabConfig: SofaTab
    private lateinit var tabs: List<SofaTab.Tabs>
    private lateinit var mediator: TabLayoutMediator

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            val tabCount = binding.tabLayout.tabCount
            for (i in 0 until tabCount) { //[0,tabCount-1]
                val tab = binding.tabLayout.getTabAt(i)
                val customView = tab?.customView as TextView
                if (tab.position == position) {
                    customView.textSize = tabConfig.activeSize.toFloat()
                    customView.typeface = Typeface.DEFAULT_BOLD
                } else {
                    customView.textSize = tabConfig.normalSize.toFloat()
                    customView.typeface = Typeface.DEFAULT
                }
            }
        }
    }

    override fun getLayoutId() = R.layout.fragment_sofa

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tabConfig = getTabConfig()
        tabs = tabConfig.tabs.filter { tab ->
            tab.enable
        }

        //现在页面预加载
        binding.viewPage.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
        binding.viewPage.adapter = object : FragmentStateAdapter(childFragmentManager, lifecycle) {
            override fun getItemCount() = tabs.size

            override fun createFragment(position: Int): Fragment {
                //FragmentStateAdapter内部会管理已实例化的fragment对象
                return getTabFragment(position)
            }
        }

        binding.tabLayout.tabGravity = tabConfig.tabGravity

        mediator = TabLayoutMediator(
            binding.tabLayout, binding.viewPage, true
        ) { tab, position ->
            tab.customView = makeTextView(position)
        }
        mediator.attach()

        binding.viewPage.registerOnPageChangeCallback(pageChangeCallback)
        binding.viewPage.post { binding.viewPage.setCurrentItem(tabConfig.select, false) }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        val fragments = childFragmentManager.fragments
        for (fragment in fragments) {
            if (fragment.isAdded && fragment.isVisible) {
                fragment.onHiddenChanged(hidden)
                break
            }
        }
    }

    override fun onDestroy() {
        mediator.detach()
        binding.viewPage.unregisterOnPageChangeCallback(pageChangeCallback)
        super.onDestroy()
    }

    private fun makeTextView(position: Int): View {
        val tabView = TextView(context)
        val states = arrayOfNulls<IntArray>(2)
        states[0] = intArrayOf(android.R.attr.state_selected)
        states[1] = intArrayOf()

        val colors = intArrayOf(
            Color.parseColor(tabConfig.activeColor),
            Color.parseColor(tabConfig.normalColor)
        )
        val stateList = ColorStateList(states, colors)
        tabView.setTextColor(stateList)
        tabView.text = tabs[position].title
        tabView.textSize = tabConfig.normalSize.toFloat()
        return tabView
    }

    private fun getTabFragment(position: Int): Fragment {
        return HomeFragment.newInstance(tabs[position].tag)
    }

    private fun getTabConfig() = AppConfig.getSofaTab()

}