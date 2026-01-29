package com.yichangyiwai.devtoolbox

import com.yichangyiwai.devtoolbox.ui.ByteDecoderPanel
import com.yichangyiwai.devtoolbox.ui.ByteParserPanel
import com.yichangyiwai.devtoolbox.ui.JsonFormatterPanel
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import org.jetbrains.jewel.bridge.addComposeTab

class DevToolsWindowFactory : ToolWindowFactory {
    override fun shouldBeAvailable(project: Project) = true

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        toolWindow.addComposeTab("JSON 格式化", focusOnClickInside = true) {
            JsonFormatterPanel()
        }

        toolWindow.addComposeTab("字节解码器", focusOnClickInside = true) {
            ByteDecoderPanel()
        }

        toolWindow.addComposeTab("字节解析器", focusOnClickInside = true) {
            ByteParserPanel()
        }
    }
}
