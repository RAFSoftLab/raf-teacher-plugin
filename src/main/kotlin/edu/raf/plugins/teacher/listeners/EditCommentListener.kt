package edu.raf.plugins.teacher.listeners

import edu.raf.plugins.teacher.models.Comment

interface EditCommentListener {
    fun onEditComment(comment: Comment, newText: String)
}