package edu.raf.plugins.teacher.listeners.selection

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
import edu.raf.plugins.teacher.utils.ImageLoader
import javax.swing.JButton
import javax.swing.JPanel
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import java.awt.Image
import java.net.URL
import javax.swing.ImageIcon

class SetUpSelectionListener(private val project: Project) {
    private var currentPopup: com.intellij.openapi.ui.popup.JBPopup? = null

    fun setupEditorListener() {
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
            val filePath = psiFile?.virtualFile?.path
            val startLine = selectionModel.selectionStartPosition?.line ?: -1
            val endLine = selectionModel.selectionEndPosition?.line ?: -1

            showPopup(editor, filePath, startLine, endLine)
        }
    }

    private fun showPopup(editor: Editor, filePath: String?, startLine: Int, endLine: Int) {

         val insertCommentIcon = ImageIcon(URL(ImageLoader.getImageUrl(ConstantsUtil.COMMENT_ENTER_IMAGE)))

         val insertCommentButton: JButton = JButton("Unesi komentar").apply {
            font = font.deriveFont(Font.ITALIC, 12f) // Manji font, stil Italic
            preferredSize = Dimension(180, 30) // Manje dugme
            val resizedIcon = ImageIcon(insertCommentIcon.image.getScaledInstance(16, 16, Image.SCALE_SMOOTH)) // Smanjivanje ikone
            icon = resizedIcon
            addActionListener {
                if (filePath != null) {
                    // Logika za unos komentara
                    println("Unos komentara za fajl: $filePath, linije: $startLine - $endLine")

                } else {
                    println("Nije moguće uneti komentar jer fajl nije pronađen.")
                }
                closePopup()
            }

        }

//        val button = JButton("Prikaži informacije").apply {
//            addActionListener {
//                println("Fajl: $filePath")
//                println("Linije: $startLine - $endLine")
//            }
//        }

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