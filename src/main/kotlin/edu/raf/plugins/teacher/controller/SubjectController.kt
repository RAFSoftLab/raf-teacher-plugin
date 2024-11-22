package edu.raf.plugins.teacher.controller

import edu.raf.plugins.teacher.services.SubjectService
import edu.raf.plugins.teacher.ui.SubjectComboBox
import javax.swing.SwingUtilities

class SubjectController(private val view: SubjectComboBox) {

    private val service = SubjectService()

    fun loadSubjects() {
        SwingUtilities.invokeLater {
            try {
                val subjects = service.getSubjects()
                SwingUtilities.invokeLater {
                    view.updateSubjects(subjects)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                SwingUtilities.invokeLater {
                    view.showError("Greška pri učitavanju")
                }
            }
        }
    }
}