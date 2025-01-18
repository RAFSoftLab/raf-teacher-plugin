package edu.raf.plugins.teacher.controllers

import edu.raf.plugins.teacher.models.Subject
import edu.raf.plugins.teacher.services.SubjectService
import edu.raf.plugins.teacher.ui.CreateExamView
import edu.raf.plugins.teacher.ui.GetStudentSolutionsView
import java.awt.CardLayout
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.SwingWorker

class StudentSolutionsController(private val view: GetStudentSolutionsView) {
    private val subjectService = SubjectService()

    fun loadSubjectsOnServer() {
        object : SwingWorker<List<Subject>, Void>() {
            override fun doInBackground(): List<Subject> {
                // Dugotrajna operacija
                return subjectService.getSubjectsOnServer()
            }

            override fun done() {

//                view.showLoader(false)  // Prikazivanje loadera
//                try {
//                    view.enableSubmitButton()
                val subjects = get() // Rezultat poziva
                print(subjects)
//                    view.updateSubjects(subjects)
//                } catch (e: Exception) {
//                    JOptionPane.showMessageDialog(
//                        null,
//                        "Nije moguće povezati se na server. Proverite Vašu mrežnu i VPN konekciju.",
//                        "Greška pri povezivanju",
//                        JOptionPane.ERROR_MESSAGE
//                    )
//
//                    // Povratak na glavnu stranicu (Menu)
//
//                    val parentPanel = view.parent as? JPanel
//                    val cardLayout = parentPanel?.layout as? CardLayout
//                    cardLayout?.show(parentPanel, "Menu")
//
//                }
            }
        }.execute()
    }
}