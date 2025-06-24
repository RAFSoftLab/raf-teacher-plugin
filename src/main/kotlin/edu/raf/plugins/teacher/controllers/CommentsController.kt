package edu.raf.plugins.teacher.controllers

import com.intellij.openapi.project.Project
import edu.raf.plugins.teacher.listeners.DeleteCommentListener
import edu.raf.plugins.teacher.listeners.EditCommentListener
import edu.raf.plugins.teacher.models.Comment
import edu.raf.plugins.teacher.services.CommentService
import edu.raf.plugins.teacher.ui.CommentsView
import javax.swing.JOptionPane

class CommentsController(private val view: CommentsView,  private val project: Project) : EditCommentListener, DeleteCommentListener {
    init {
        view.listenerEdit = this
        view.listenerDelete = this
    }

    private val commentService = CommentService()

    fun loadAndDisplayComments() {
        val comments = commentService.loadCommentsForCurrentProject(project)
        view.updateComments(comments)
    }

    override fun onEditComment(comment: Comment, newText: String) {
        TODO("Not yet implemented")
    }

    override fun onDeleteComment(comment: Comment) {
        println("Deleting comment: ${comment.id}")
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