package edu.raf.plugins.teacher.listeners.selection

import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.SelectionEvent
import com.intellij.openapi.editor.event.SelectionListener
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.Disposer
import edu.raf.plugins.teacher.constants.ConstantsUtil
import edu.raf.plugins.teacher.observer.Publisher
import edu.raf.plugins.teacher.observer.Subscriber
import edu.raf.plugins.teacher.services.CommentService
import edu.raf.plugins.teacher.utils.ImageLoader
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import java.awt.Image
import java.net.URL
import javax.swing.*

@Service(Service.Level.PROJECT)
class SetUpSelectionListener(private val project: Project) {

    private val commentService: CommentService = project.getService(CommentService::class.java)
    private var currentPopup: com.intellij.openapi.ui.popup.JBPopup? = null

    companion object {
        fun getInstance(project: Project): SetUpSelectionListener {
            return project.getService(SetUpSelectionListener::class.java)
        }
    }

    fun setupEditorListener() {
        println("Postavljen listener")
        project.messageBus.connect().subscribe(
            FileEditorManagerListener.FILE_EDITOR_MANAGER,
            object : FileEditorManagerListener {
                override fun selectionChanged(event: FileEditorManagerEvent) {
                    event.manager.selectedTextEditor?.let { editor ->
                        val listener = object : SelectionListener {
                            override fun selectionChanged(e: SelectionEvent) {
                                handleSelectionChange(editor)
                            }
                        }

                        editor.selectionModel.addSelectionListener(listener)
                        Disposer.register(project) {
                            editor.selectionModel.removeSelectionListener(listener)
                            closePopup()
                        }

                        handleSelectionChange(editor)
                    }
                }
            }
        )
    }

    private fun handleSelectionChange(editor: Editor) {
        val selectionModel = editor.selectionModel
        val selectedText = selectionModel.selectedText
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document)

        closePopup() // Uvek zatvorimo prethodni popup kada se promeni selekcija

        if (!selectedText.isNullOrEmpty()) {
            val filePathRelative = psiFile?.virtualFile?.path?.substringAfterLast('/') ?: psiFile?.virtualFile?.path
            val startLine = selectionModel.selectionStartPosition?.line ?: -1
            val endLine = selectionModel.selectionEndPosition?.line ?: -1

            // Auomatski je za jednu liniju manje nego inace, jer brojanje krece od 0
            showPopup(editor, filePathRelative, startLine + 1, endLine + 1)
        }
    }

    private fun showPopup(editor: Editor, filePathRelative: String?, startLine: Int, endLine: Int) {

        val insertCommentIcon = ImageIcon(URL(ImageLoader.getImageUrl(ConstantsUtil.COMMENT_ENTER_IMAGE)))

        val insertCommentButton: JButton = JButton("Unesi komentar").apply {
            font = font.deriveFont(Font.ITALIC, 12f) // Manji font, stil Italic
            preferredSize = Dimension(180, 30) // Manje dugme
            val resizedIcon =
                ImageIcon(insertCommentIcon.image.getScaledInstance(16, 16, Image.SCALE_SMOOTH)) // Smanjivanje ikone
            icon = resizedIcon
            addActionListener {
                if (filePathRelative != null) {
                    val dialog = JDialog().apply {
                        title = "Dodavanje komentara"
                        setSize(400, 300)
                        setLocationRelativeTo(null)
                        layout = BorderLayout()
                        isModal = true
                    }

                    val infoLabel =
                        JLabel("<html>Fajl: <b>$filePathRelative</b><br>Označene linije: <b>$startLine - $endLine</b></html>").apply {
                            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
                        }

                    val commentField = JTextArea().apply {
                        lineWrap = true
                        wrapStyleWord = true
                        border = BorderFactory.createTitledBorder("Tekst komentara")
                    }

                    val submitButton = JButton("Unesi").apply {
                        addActionListener {
                            val commentText = commentField.text.trim()
                            if (commentText.isNotEmpty()) {
                                println("Comment submitted for file: $filePathRelative, lines: $startLine - $endLine")
                                println("Comment: $commentText")
                                val isSavedComment = commentService.saveComment(
                                    commentText,
                                    filePathRelative,
                                    startLine,
                                    endLine,
                                    project
                                )

                                if (!isSavedComment)
                                    JOptionPane.showMessageDialog(
                                        dialog,
                                        "Komentar nije sačuvan, proverite da komentar sa označenim linijama koda već ne postoji.",
                                        "Greška",
                                        JOptionPane.ERROR_MESSAGE
                                    )

                                dialog.dispose()
                            } else {
                                JOptionPane.showMessageDialog(
                                    dialog,
                                    "Komentar mora imati tekst.",
                                    "Greška",
                                    JOptionPane.ERROR_MESSAGE
                                )
                            }
                        }
                    }

                    val panel = JPanel(BorderLayout()).apply {
                        add(infoLabel, BorderLayout.NORTH)
                        add(JScrollPane(commentField), BorderLayout.CENTER)
                        add(submitButton, BorderLayout.SOUTH)
                    }

                    dialog.add(panel)
                    dialog.isVisible = true
                } else {
                    println("File path is not available.")
                }
            }
        }

//

        val panel = JPanel(BorderLayout()).apply {
            add(insertCommentButton, BorderLayout.CENTER)
        }

        currentPopup = JBPopupFactory.getInstance()
            .createComponentPopupBuilder(panel, insertCommentButton)
            .setResizable(false)
            .setMovable(true)
            .setRequestFocus(false)
            .createPopup()
            .also { popup ->
                popup.showInBestPositionFor(editor)
                Disposer.register(project) { popup.dispose() }
            }
    }

    private fun closePopup() {
        currentPopup?.dispose()
        currentPopup = null
    }


}