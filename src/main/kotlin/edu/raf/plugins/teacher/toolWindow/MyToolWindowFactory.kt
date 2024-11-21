package edu.raf.plugins.teacher.toolWindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import edu.raf.plugins.teacher.ui.SubjectComboBox



class MyToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        // Kreiranje instance nove klase koja sadrži ComboBox
        val subjectComboBox = SubjectComboBox()
        val content = ContentFactory.getInstance().createContent(subjectComboBox, null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true
}
