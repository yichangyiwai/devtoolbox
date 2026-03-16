package com.yichangyiwai.devtoolbox

import com.yichangyiwai.devtoolbox.ui.ByteDecoderPanel
import com.yichangyiwai.devtoolbox.ui.ByteParserPanel
import com.yichangyiwai.devtoolbox.ui.JsonFormatterPanel
import com.yichangyiwai.devtoolbox.ui.TcpReceiverPanel
import com.yichangyiwai.devtoolbox.ui.TcpSenderPanel
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ToolWindowType
import org.jetbrains.jewel.bridge.addComposeTab

class DevToolsWindowFactory : ToolWindowFactory {
    override fun shouldBeAvailable(project: Project) = true

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        toolWindow.setTitleActions(listOf(OpenWindowedViewAction(toolWindow)))

        toolWindow.addComposeTab("JSON 格式化", focusOnClickInside = true) {
            JsonFormatterPanel()
        }

        toolWindow.addComposeTab("进制解码器", focusOnClickInside = true) {
            ByteDecoderPanel()
        }

        toolWindow.addComposeTab("字节解析器", focusOnClickInside = true) {
            ByteParserPanel()
        }

        toolWindow.addComposeTab("TCP 发送", focusOnClickInside = true) {
            TcpSenderPanel()
        }

        toolWindow.addComposeTab("TCP 接收与解析", focusOnClickInside = true) {
            TcpReceiverPanel()
        }
    }
}

private class OpenWindowedViewAction(private val toolWindow: ToolWindow) : DumbAwareAction(
    "放大查看",
    "在独立可调整大小窗口中查看当前工具",
    AllIcons.Actions.MoveToWindow
) {
    override fun update(e: AnActionEvent) {
        val isWindowed = toolWindow.type == ToolWindowType.WINDOWED
        e.presentation.text = if (isWindowed) "还原停靠" else "放大查看"
        e.presentation.description = if (isWindowed) {
            "将独立窗口还原为右侧停靠工具窗口"
        } else {
            "在独立可调整大小窗口中放大查看当前工具"
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val targetType = if (toolWindow.type == ToolWindowType.WINDOWED) {
            ToolWindowType.DOCKED
        } else {
            ToolWindowType.WINDOWED
        }
        toolWindow.setType(targetType, null)
        toolWindow.show(null)
        toolWindow.activate(null)
    }
}
