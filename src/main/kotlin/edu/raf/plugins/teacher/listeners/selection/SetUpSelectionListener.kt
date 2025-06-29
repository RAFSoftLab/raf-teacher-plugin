package edu.raf.plugins.teacher.listeners.selection

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.SelectionEvent
import com.intellij.openapi.editor.event.SelectionListener
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.openapi.util.Disposer
import javax.swing.JTextArea

class SetUpSelectionListener(private val project: Project) {
    private var selectedText: String? = null
    private var filePath: String? = null
    private var startLine: Int = -1
    private var endLine: Int = -1

    fun setupEditorListener() {
        println("Pokrećem setup selection listener...")
        project.messageBus.connect().subscribe(
            FileEditorManagerListener.FILE_EDITOR_MANAGER,
            object : FileEditorManagerListener {
                override fun selectionChanged(event: FileEditorManagerEvent) {
                    val editor = event.manager.selectedTextEditor
                    if (editor != null) {
                        val listener = object : SelectionListener {
                            override fun selectionChanged(e: SelectionEvent) {
                                updateSelectionInfo(editor)
                            }
                        }

                        // Dodaj listener
                        editor.selectionModel.addSelectionListener(listener)

                        // Registruj za cleanup uz projekat
                        Disposer.register(project) {
                            editor.selectionModel.removeSelectionListener(listener)
                        }

                        // Ako već postoji selekcija
                        updateSelectionInfo(editor)
                    }
                }
            }
        )
    }

    private fun updateSelectionInfo(editor: Editor) {
        val selectionModel = editor.selectionModel
        selectedText = selectionModel.selectedText
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document)

        if (!selectedText.isNullOrEmpty()) {
            filePath = psiFile?.virtualFile?.path
            startLine = selectionModel.selectionStartPosition?.line ?: -1
            endLine = selectionModel.selectionEndPosition?.line ?: -1

            println("-------------------------")
            println("Selektovani tekst: $selectedText")
            println("Fajl: $filePath")
            println("Linije: $startLine - $endLine")

        }
    }
}
