package com.zj.goodvidoe.utils

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.zj.goodvidoe.model.BottomBar
import com.zj.goodvidoe.model.Destination
import com.zj.hi_library.util.AppGlobals
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object AppConfig {

    private var sDestConfig: HashMap<String, Destination>? = null
    private var sTabsConfig: BottomBar? = null

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