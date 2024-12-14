package edu.raf.plugins.teacher.controllers

import edu.raf.plugins.teacher.models.Subject
import edu.raf.plugins.teacher.services.SubjectService
import edu.raf.plugins.teacher.ui.CreateExamView
import javax.swing.JOptionPane
import javax.swing.SwingWorker

class SubjectController(private val view: CreateExamView) {

    private val service = SubjectService()

    fun loadSubjects() {
        object : SwingWorker<List<Subject>, Void>() {
            override fun doInBackground(): List<Subject> {
                // Dugotrajna operacija
                return service.getSubjects()
            }

            override fun done() {
                try {
                    val subjects = get() // Rezultat poziva
                    view.updateSubjects(subjects)
                } catch (e: Exception) {
                    JOptionPane.showMessageDialog(
                        null,
                        "Nije moguće povezati se na server. Proverite Vašu mrežnu konekciju.",
                        "Greška pri povezivanju",
                        JOptionPane.ERROR_MESSAGE
                    )
                }
            }
        }.execute()
    }
}
