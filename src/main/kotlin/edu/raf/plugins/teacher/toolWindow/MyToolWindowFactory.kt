package edu.raf.plugins.teacher.toolWindow


import com.intellij.ide.actions.runAnything.RunAnythingContext.BrowseRecentDirectoryContext.label
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
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
import edu.raf.plugins.teacher.views.CommentsView
import edu.raf.plugins.teacher.views.CreateExamView
import edu.raf.plugins.teacher.views.GetStudentSolutionsView
import edu.raf.plugins.teacher.utils.ImageLoader
import io.sentry.Sentry
import kotlinx.html.InputType
import raflms.teacherstub.api.TeacherStubService
import raflms.teacherstub.config.ConfigFactory
import raflms.teacherstub.config.TeacherStubConfig

import java.awt.CardLayout
import java.awt.Image
import java.net.URL
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JPanel

class MyToolWindowFactory : ToolWindowFactory {

    // Koristimo companion object da se Sentry inicijalizuje samo JEDNOM po pokretanju IDE-a
    companion object {
        private var sentryInitialized = false
        fun initSentry() {
            if (!sentryInitialized) {
                //2.1.9 verzija
                Sentry.init { options ->
                    options.dsn =
                        "https://ded7d252c6c25bc6db783375495f383b@o4509723131838464.ingest.de.sentry.io/4509723138195536"
                    options.isDebug = true // Omogućava debug mod
                }
                sentryInitialized = true
            }
        }
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        initSentry() // Inicijalizacija Sentry-a

        // POKRETANJE TESTA U POZADINI
        object : Task.Backgroundable(project, "Povezivanje sa backendom") {
            override fun run(indicator: ProgressIndicator) {
                try {
                    // 1. Inicijalizacija
                    val config = ConfigFactory.createConfig()
                    val service = TeacherStubService(config)

                    // 2. Poziv metode
                    println("STUB TEST: Pokrećem registraciju...")
                    val uspeh = service.registerStudentForTest(
                        "mojsupertest", "Zarko", "Test", 1, "RN", "2026"
                    )

                    // 3. Ažuriranje UI-ja sa rezultatom
                    ApplicationManager.getApplication().invokeLater {
                        val message = if (uspeh) {
                            "STUB RADI: Student registrovan!"
                        } else {
                            "STUB ODGOVORIO: Neuspešna registracija (proveri parametre na backendu)."
                        }

                        // Standardni IntelliJ popup dijalog koji uvek radi
                        com.intellij.openapi.ui.Messages.showInfoMessage(
                            project,
                            message,
                            "Rezultat Stub Testiranja"
                        )
                    }
                } catch (e: Exception) {
                    println("STUB TEST GREŠKA: ${e.message}")
                    print(e)
                }
            }
        }.queue()

        val commentService = project.getService(CommentService::class.java)
        val selectionListener = SetUpSelectionListener.getInstance(project)
        selectionListener.setupEditorListener()


        val cardLayout = CardLayout()
        val mainPanel = JPanel(cardLayout)

        // Početni meni
        // 1. Inicijalizacija dugmad bez ikona (da bi se odmah pojavila)
        val menuPanel = JPanel()
        val createTestButton = JButton("Postavi proveru znanja")
        val downloadExamButton = JButton("Preuzmi studentska rešenja")
        val commentsSectionButton = JButton("Komentari")

        menuPanel.add(createTestButton)
        menuPanel.add(downloadExamButton)
        menuPanel.add(commentsSectionButton)

// 2. Pokrece učitavanje ikona u pozadinskom thread-u
        ApplicationManager.getApplication().executeOnPooledThread {
            try {
                // Mapa koja povezuje dugme sa njegovim URL-om
                val jobMap = mapOf(
                    createTestButton to ConstantsUtil.CREATE_EXAM_IMAGE,
                    downloadExamButton to ConstantsUtil.DOWNLOAD_EXAM_IMAGE,
                    commentsSectionButton to ConstantsUtil.COMMENT_IMAGE
                )

                for ((button, imageConst) in jobMap) {
                    val urlString = ImageLoader.getImageUrl(imageConst)
                    val icon = ImageIcon(URL(urlString))
                    val scaledIcon = ImageIcon(icon.image.getScaledInstance(45, 45, Image.SCALE_SMOOTH))

                    // 3. Kada je slika spremna, vrati se na UI thread da je postaviš na dugme
                    ApplicationManager.getApplication().invokeLater {
                        button.icon = scaledIcon
                    }
                }
            } catch (e: Exception) {
                println("Greška pri učitavanju ikona: ${e.message}")
            }
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
