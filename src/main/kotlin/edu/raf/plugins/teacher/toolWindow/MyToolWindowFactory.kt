package edu.raf.plugins.teacher.toolWindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import edu.raf.plugins.teacher.controllers.SubjectController
import edu.raf.plugins.teacher.ui.MainView



class MyToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        // Kreiranje instance klase sa ComboBox-om
        val mainView = MainView()

        // Kreiranje kontrolera sa viewom
        val controller = SubjectController(mainView)

        // Učitavanje predmeta
        controller.loadSubjects()

        // Dodavanje Content-a u ToolWindow
        val content = ContentFactory.getInstance().createContent(mainView, null, false)
        toolWindow.contentManager.addContent(content)
    }
    override fun shouldBeAvailable(project: Project) = true
}

