package edu.raf.plugins.teacher.listeners

import edu.raf.plugins.teacher.models.Comment

interface  DeleteCommentListener {
    fun onDeleteComment(comment: Comment)
}