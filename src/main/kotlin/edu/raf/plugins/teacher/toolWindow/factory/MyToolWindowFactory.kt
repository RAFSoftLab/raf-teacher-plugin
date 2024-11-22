package edu.raf.plugins.teacher.toolWindow.factory

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import edu.raf.plugins.teacher.toolWindow.uitmp.LoginToolWindow

class MyToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val loginToolWindow = LoginToolWindow(project, toolWindow)
        val content = ContentFactory.getInstance().createContent(loginToolWindow.getContent(), "Login", false)
        toolWindow.contentManager.addContent(content)
    }
}
