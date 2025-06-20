package edu.raf.plugins.teacher.controllers

import edu.raf.plugins.teacher.services.CommentService
import edu.raf.plugins.teacher.ui.CommentsView

class CommentsController(private val view:  CommentsView) {
   private val commentService = CommentService()

    fun loadAndDisplayComments() {
        val comments = commentService.loadComments()
        view.updateComments(comments)
    }

}