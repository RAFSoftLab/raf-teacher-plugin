package edu.raf.plugins.teacher.controllers

import edu.raf.plugins.teacher.listeners.StepNavigationListener
import edu.raf.plugins.teacher.models.Subject
import edu.raf.plugins.teacher.services.SubjectService
import edu.raf.plugins.teacher.ui.CreateExamView
import edu.raf.plugins.teacher.ui.GetStudentSolutionsView
import java.awt.CardLayout
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.SwingWorker

class StudentSolutionsController(private val view: GetStudentSolutionsView) : StepNavigationListener {
    init {
        view.listener = this //
    }

    private val subjectService = SubjectService()

    fun loadSubjectsOnServer() {
        view.showLoader(true)  // Prikazivanje loadera
        object : SwingWorker<List<String>, Void>() {
            override fun doInBackground(): List<String> {
                // Dugotrajna operacija
                return subjectService.getSubjectsOnServer()

            }

            override fun done() {

                view.showLoader(false)  // Prikazivanje loadera
                try {
                    view.enableSubmitButton()
                    val subjectsOnServer = get() // Rezultat poziva
                    view.updateOptions(subjectsOnServer)
                    // view.updateSubjects(subjects)
                } catch (e: Exception) {
                    JOptionPane.showMessageDialog(
                        null,
                        "Nije moguće povezati se na server. Proverite Vašu mrežnu i VPN konekciju.",
                        "Greška pri povezivanju",
                        JOptionPane.ERROR_MESSAGE
                    )

                    // Povratak na glavnu stranicu (Menu)

                    val parentPanel = view.parent as? JPanel
                    val cardLayout = parentPanel?.layout as? CardLayout
                    cardLayout?.show(parentPanel, "Menu")

                }
            }
        }.execute()
    }

    override fun onNextStep(currentStep: Int) {
        println("Kurrent " + currentStep)


        object : SwingWorker<List<String>, Void>() {
            override fun doInBackground(): List<String> {
                // Dugotrajna operacija
                if (currentStep == 0) {
                    print("Getuje se trenutna opcija")
                    val selectedSubject = view.getSelectedOption(currentStep)
                    println(selectedSubject)
                    return selectedSubject?.let {
                        subjectService.getYearsForSubjectOnServer(it)
                    } ?: emptyList()
                }

                if (currentStep == 1) {
                    print("Getuje se trenutna opcija 2")
                    val selectedYear = view.getSelectedOption(currentStep)
                    val selectedSubject = view.getSelectedOption(0) // Ponovo dohvata subject za prvi korak
                    println(selectedYear)

                    return if (selectedSubject != null && selectedYear != null) {
                        subjectService.getExamsPerYearForSubjectOnServer(selectedSubject, selectedYear)
                    } else {
                        emptyList()
                    }
                }

                return emptyList()
            }

            override fun done() {


                try {
                    view.enableSubmitButton()
                    val data = get() // Rezultat poziva
                    println(data)
                    view.updateOptions(data)
                    // view.updateSubjects(subjects)
                } catch (e: Exception) {
                    JOptionPane.showMessageDialog(
                        null,
                        "Greška: ${e.message}.",
                        "Greška na koraku ${currentStep+1}",
                        JOptionPane.ERROR_MESSAGE
                    )

                    // Povratak na glavnu stranicu (Menu)

                    val parentPanel = view.parent as? JPanel
                    val cardLayout = parentPanel?.layout as? CardLayout
                    cardLayout?.show(parentPanel, "Menu")

                }
            }
        }.execute()
    }


}