package edu.raf.plugins.teacher.toolWindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import edu.raf.plugins.teacher.constants.ConstantsUtil
import edu.raf.plugins.teacher.controllers.SubjectController
import edu.raf.plugins.teacher.ui.CreateExamView
import edu.raf.plugins.teacher.utils.ImageLoader
import java.awt.CardLayout
import java.net.URL
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JPanel

class MyToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val cardLayout = CardLayout()
        val mainPanel = JPanel(cardLayout)

        // Početni meni
        val menuPanel = JPanel()
        val iconCreatTest = ImageIcon(URL(ImageLoader.getImageUrl(ConstantsUtil.CREATE_TEST_IMAGE)))

        val createTestButton: JButton = JButton("Postavi proveru znanja").apply {
            icon = iconCreatTest
        }
        val retrieveTestButton = JButton("Preuzmi proveru znanja")

        menuPanel.add(createTestButton)
        menuPanel.add(retrieveTestButton)

        // Dodavanje početnog menija u CardLayout
        mainPanel.add(menuPanel, "Menu")

        // Akcija za dugme "Kreiraj proveru znanja"
        createTestButton.addActionListener {
            // Dinamičko kreiranje sadržaja za "Kreiraj proveru znanja"
            val createExamView = CreateExamView()
            val controller = SubjectController(createExamView)
            controller.loadSubjects()

            // Dodavanje novog sadržaja u CardLayout (ako nije već dodato)
            if (mainPanel.components.none { it == createExamView }) {
                mainPanel.add(createExamView, "CreateTest")
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
