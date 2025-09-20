package edu.raf.plugins.teacher.controllers

import com.intellij.openapi.project.Project
import edu.raf.plugins.teacher.listeners.DeleteCommentListener
import edu.raf.plugins.teacher.listeners.EditCommentListener
import edu.raf.plugins.teacher.models.Comment
import edu.raf.plugins.teacher.services.CommentService
import edu.raf.plugins.teacher.views.CommentsView
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.*

class CommentsController(private val view: CommentsView,  private val project: Project,private val commentService: CommentService) : EditCommentListener, DeleteCommentListener {

    init {

        view.listenerEdit = this
        view.listenerDelete = this
        commentService.addSubscriber(view)
    }


    fun loadAndDisplayComments() {
        val comments = commentService.loadCommentsForCurrentProject(project)
        view.updateComments(comments)
    }
 // Bzvz commit za novu verziju
    override fun onEditComment(comment: Comment) {
        val dialog = JDialog().apply {
            title = "Izmena komentara"
            setSize(400, 300)
            setLocationRelativeTo(view)
            layout = BorderLayout()
            isModal = true
        }

        val textArea = JTextArea(comment.commentText).apply {
            lineWrap = true
            wrapStyleWord = true
        }

        val saveButton = JButton("Sačuvaj").apply {
            addActionListener {
                val updatedText = textArea.text.trim()
                if (updatedText.isNotEmpty()) {
                    val updatedComment = comment.copy(commentText = updatedText)
                    val updatedComments = commentService.updateComment(updatedComment, project)
                    view.updateComments(updatedComments)
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

        val cancelButton = JButton("Nazad").apply {
            addActionListener { dialog.dispose() }
        }

        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT)).apply {
            add(saveButton)
            add(cancelButton)
        }

        dialog.add(JScrollPane(textArea), BorderLayout.CENTER)
        dialog.add(buttonPanel, BorderLayout.SOUTH)
        dialog.isVisible = true
    }

    override fun onDeleteComment(comment: Comment) {
        val options = arrayOf("Da", "Ne")
        val confirmation = JOptionPane.showOptionDialog(
            view,
            "Da li ste sigurni da želite da obrišete ovaj komentar?",
            "Potvrda brisanja",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            options,
            options[0]
        )

        if (confirmation == JOptionPane.YES_OPTION) {
            val updatedComments =  commentService.deleteComment(comment, project)
            view.updateComments(updatedComments)
        }

    }

}