package edu.raf.plugins.teacher.controllers

import com.intellij.openapi.project.Project
import edu.raf.plugins.teacher.services.CommentService
import edu.raf.plugins.teacher.ui.CommentsView

class CommentsController(private val view:  CommentsView) {
   private val commentService = CommentService()

    fun loadAndDisplayComments(project: Project) {
        val comments = commentService.loadCommentsForCurrentProject(project)
        view.updateComments(comments)
    }

}