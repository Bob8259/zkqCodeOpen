package com.coc.zkqcode.utils.screencapture

import android.util.Log
import com.topjohnwu.superuser.Shell
import java.io.File
import java.util.regex.Pattern

object AutoGrantTool {
    private const val TAG = "AutoGrant"
    private const val DUMP_PATH = "/data/local/tmp/uidump.xml"

    fun autoClickStartNow(): Boolean {
        try {
            // 1. 导出当前 UI 布局到文件
            val dumpResult = Shell.cmd("uiautomator dump $DUMP_PATH").exec()
            if (!dumpResult.isSuccess) {
                Log.e(TAG, "无法导出 UI 布局: ${dumpResult.err}")
                return false
            }

            // 2. 读取导出的 XML 内容
            val xmlContent = Shell.cmd("cat $DUMP_PATH").exec().out.joinToString("\n")
            
            if (xmlContent.isEmpty()) {
                Log.e(TAG, "读取到的 XML 为空")
                return false
            }

            // 3. 查找按钮并解析坐标
            val bounds = findBounds(xmlContent)
            if (bounds == null) {
                Log.e(TAG, "在当前界面未找到录屏授权按钮")
                return false
            }

            // 4. 计算中心点坐标
            val (centerX, centerY) = calculateCenter(bounds)
            Log.d(TAG, "找到按钮坐标: $bounds -> 中心点: ($centerX, $centerY)")

            // 5. 执行点击
            Shell.cmd("input tap $centerX $centerY").exec()
            return true

        } catch (e: Exception) {
            Log.e(TAG, "自动授权发生异常", e)
            return false
        }
    }

    private fun findBounds(xml: String): String? {
        // 匹配 android:id/button1 或 “立即开始”所在的行，提取 bounds="[x1,y1][x2,y2]"
        val pattern = Pattern.compile(".*(?:android:id/button1|立即开始).*bounds=\"([^\"]+)\".*")
        val matcher = pattern.matcher(xml)
        return if (matcher.find()) matcher.group(1) else null
    }

    private fun calculateCenter(bounds: String): Pair<Int, Int> {
        // 这里的 bounds 格式通常是 [850,1254][1021,1317]
        val nums = Regex("\\d+").findAll(bounds).map { it.value.toInt() }.toList()
        if (nums.size < 4) return Pair(0, 0)
        val x1 = nums[0]
        val y1 = nums[1]
        val x2 = nums[2]
        val y2 = nums[3]
        return Pair((x1 + x2) / 2, (y1 + y2) / 2)
    }
}
