package edu.raf.plugins.teacher.toolWindow


import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import edu.raf.plugins.teacher.constants.ConstantsUtil
import edu.raf.plugins.teacher.controllers.CommentsController
import edu.raf.plugins.teacher.controllers.StudentSolutionsController
import edu.raf.plugins.teacher.controllers.SubjectExamController
import edu.raf.plugins.teacher.listeners.selection.SetUpSelectionListener
import edu.raf.plugins.teacher.services.CommentService
import edu.raf.plugins.teacher.ui.CommentsView
import edu.raf.plugins.teacher.ui.CreateExamView
import edu.raf.plugins.teacher.ui.GetStudentSolutionsView
import edu.raf.plugins.teacher.utils.ImageLoader
import io.sentry.Sentry

import java.awt.CardLayout
import java.awt.Image
import java.net.URL
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JPanel

class MyToolWindowFactory : ToolWindowFactory {


    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
 //2.1.9 verzija
        Sentry.init { options ->
            options.dsn = "https://ded7d252c6c25bc6db783375495f383b@o4509723131838464.ingest.de.sentry.io/4509723138195536"
            options.isDebug = true // Omogućava debug mod
        }

        ("Postavicu listener")
        val commentService = project.getService(CommentService::class.java)
        val selectionListener = SetUpSelectionListener.getInstance(project)
        selectionListener.setupEditorListener()


        val cardLayout = CardLayout()
        val mainPanel = JPanel(cardLayout)

        // Početni meni
        val menuPanel = JPanel()
        val createExamIcon = ImageIcon(URL(ImageLoader.getImageUrl(ConstantsUtil.CREATE_EXAM_IMAGE)))
        val downloadExamIcon = ImageIcon(URL(ImageLoader.getImageUrl(ConstantsUtil.DOWNLOAD_EXAM_IMAGE)))
        val commentsSectionIcon = ImageIcon(URL(ImageLoader.getImageUrl(ConstantsUtil.COMMENT_IMAGE)))

        val createTestButton: JButton = JButton("Postavi proveru znanja").apply {
            icon = ImageIcon(createExamIcon.image.getScaledInstance(45, 45, Image.SCALE_SMOOTH))
        }

        val downloadExamButton: JButton = JButton("Preuzmi studentska rešenja").apply {
            icon = ImageIcon(downloadExamIcon.image.getScaledInstance(45, 45, Image.SCALE_SMOOTH))
        }

        val commentsSectionButton: JButton = JButton("Komentari").apply {
            icon = ImageIcon(commentsSectionIcon.image.getScaledInstance(45, 45, Image.SCALE_SMOOTH))
        }


        menuPanel.add(createTestButton)
        menuPanel.add(downloadExamButton)
        menuPanel.add(commentsSectionButton)

        // Dodavanje početnog menija u CardLayout
        mainPanel.add(menuPanel, "Menu")

        // Akcija za dugme "Kreiraj proveru znanja"
        createTestButton.addActionListener {
            // Dinamičko kreiranje sadržaja za "Kreiraj proveru znanja"
            val createExamView = CreateExamView()
            val subjectExamController = SubjectExamController(createExamView)
            subjectExamController.loadSubjects()

            // Dodavanje novog sadržaja u CardLayout (ako nije već dodato)
            if (mainPanel.components.none { it == createExamView }) {
                mainPanel.add(createExamView, "CreateTest")
            }

            // Prebacivanje na ekran za "Kreiraj proveru znanja"
            cardLayout.show(mainPanel, "CreateTest")
        }

        downloadExamButton.addActionListener {
            //Logika za "Preuzmi proveru znanja"
            val getStudentsSolutionsView = GetStudentSolutionsView();
            val studentSolutionsController = StudentSolutionsController(getStudentsSolutionsView)
            studentSolutionsController.loadSubjectsOnServer()

            // Dodavanje novog sadržaja u CardLayout (ako nije već dodato)
            if (mainPanel.components.none { it == getStudentsSolutionsView }) {
                mainPanel.add(getStudentsSolutionsView, "StudentSolutions")
            }

            // Prebacivanje na ekran za "Preuzmi proveru znanja"
            cardLayout.show(mainPanel, "StudentSolutions")
        }

        commentsSectionButton.addActionListener {
            // Logika za "Komentari"
            val commentsView = CommentsView()

            val commentsController = CommentsController(commentsView, project, commentService)
            commentsController.loadAndDisplayComments()
            // Dodavanje novog sadržaja u CardLayout (ako nije već dodato)
            if (mainPanel.components.none { it == commentsView }) {
                mainPanel.add(commentsView, "Comments")
            }

            // Prebacivanje na ekran za "Komentari"
            cardLayout.show(mainPanel, "Comments")

        }

        // Dodavanje glavnog panela u ToolWindow
        val content = ContentFactory.getInstance().createContent(mainPanel, null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true
}
