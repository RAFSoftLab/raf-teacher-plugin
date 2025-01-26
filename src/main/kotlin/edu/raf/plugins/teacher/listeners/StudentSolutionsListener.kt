package edu.raf.plugins.teacher.listeners

import edu.raf.plugins.teacher.models.StudentSolution
import java.io.File

interface StudentSolutionsListener {
    fun onSubmit(studentSolution: StudentSolution, chosenPath: File)
}