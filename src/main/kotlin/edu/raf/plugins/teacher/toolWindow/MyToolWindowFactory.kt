package edu.raf.plugins.teacher.toolWindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import edu.raf.plugins.teacher.controllers.SubjectController
import edu.raf.plugins.teacher.ui.CreateTestView
import java.awt.CardLayout
import javax.swing.JButton
import javax.swing.JPanel

class MyToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val cardLayout = CardLayout()
        val mainPanel = JPanel(cardLayout)

        // Početni meni
        val menuPanel = JPanel()
        val createTestButton = JButton("Kreiraj proveru znanja")
        val retrieveTestButton = JButton("Preuzmi proveru znanja")

        menuPanel.add(createTestButton)
        menuPanel.add(retrieveTestButton)

        // Dodavanje početnog menija u CardLayout
        mainPanel.add(menuPanel, "Menu")

        // Akcija za dugme "Kreiraj proveru znanja"
        createTestButton.addActionListener {
            // Dinamičko kreiranje sadržaja za "Kreiraj proveru znanja"
            val createTestView = CreateTestView()
            val controller = SubjectController(createTestView)
            controller.loadSubjects()

            // Dodavanje novog sadržaja u CardLayout (ako nije već dodato)
            if (mainPanel.components.none { it == createTestView }) {
                mainPanel.add(createTestView, "CreateTest")
            }

            // Prebacivanje na ekran za "Kreiraj proveru znanja"
            cardLayout.show(mainPanel, "CreateTest")
        }

        retrieveTestButton.addActionListener {
            // Ovdje možeš dodati logiku za "Preuzmi proveru znanja"
        }

        // Dodavanje glavnog panela u ToolWindow
        val content = ContentFactory.getInstance().createContent(mainPanel, null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true
}
