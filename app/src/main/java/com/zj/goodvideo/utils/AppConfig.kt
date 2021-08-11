package com.zj.goodvideo.utils

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.zj.goodvideo.model.BottomBar
import com.zj.goodvideo.model.Destination
import com.zj.goodvideo.model.SofaTab
import com.zj.hi_library.util.AppGlobals
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object AppConfig {

    private var sDestConfig: HashMap<String, Destination>? = null
    private var sTabsConfig: BottomBar? = null
    private var sSofaTab: SofaTab? = null

    fun getDestConfig(): HashMap<String, Destination> {
        if (sDestConfig == null) {
            val content: String = parseFile("destination.json")
            sDestConfig = JSON.parseObject(
                content,
                object : TypeReference<HashMap<String, Destination>>() {})
        }
        return sDestConfig!!
    }

    fun getTabsConfig(): BottomBar {
        if (sTabsConfig == null) {
            val content = parseFile("main_tabs_config.json")
            sTabsConfig = JSON.parseObject(content, BottomBar::class.java)
        }
        return sTabsConfig!!
    }

    fun getSofaTab(): SofaTab {
        if (sSofaTab == null) {
            val content = parseFile("sofa_tabs_config.json")
            sSofaTab = JSON.parseObject(content, SofaTab::class.java)
            sSofaTab?.tabs?.sortBy {
                it.index
            }
        }
        return sSofaTab!!
    }

    private fun parseFile(fileName: String): String {
        val assets = AppGlobals.get()?.assets
        val builder = StringBuilder()
        var line: String?
        assets?.run {
            val inputStream = assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            try {
                while (reader.readLine().also { line = it } != null) {
                    builder.append(line);
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                inputStream.close()
                reader.close()
            }
        }
        return builder.toString()
    }

}