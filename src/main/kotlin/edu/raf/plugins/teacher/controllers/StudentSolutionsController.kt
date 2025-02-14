package edu.raf.plugins.teacher.controllers


import edu.raf.plugins.teacher.listeners.StepNavigationListener
import edu.raf.plugins.teacher.listeners.StudentSolutionsListener
import edu.raf.plugins.teacher.models.StudentSolution
import edu.raf.plugins.teacher.services.SubjectService
import edu.raf.plugins.teacher.ui.GetStudentSolutionsView
import edu.raf.plugins.teacher.utils.Utils.Companion.openDownloadedProject;
import java.awt.CardLayout
import java.io.File
import javax.swing.*

class StudentSolutionsController(private val view: GetStudentSolutionsView) : StepNavigationListener,
    StudentSolutionsListener {
    init {
        view.listenerStep = this //
        view.listenerSubmit = this
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
                        // Pp da getYearsForSubjectOnServer vraća listu godina u formatu '2023_24'
                        val yearsList = subjectService.getYearsForSubjectOnServer(it)
                        //  '_' sa '/' u svakoj godini
                        yearsList.map { it.replace('_', '/') }
                    } ?: emptyList()
                }


                if (currentStep == 1) {
                    print("Getuje se trenutna opcija 2")
                    val selectedYear = view.getSelectedOption(currentStep)?.replace('/', '_')
                    val selectedSubject = view.getSelectedOption(0) // Ponovo dohvata subject za prvi korak
                    println(selectedYear)

                    return if (selectedSubject != null && selectedYear != null) {
                        subjectService.getExamsPerYearForSubjectOnServer(selectedSubject, selectedYear)
                    } else {
                        emptyList()
                    }
                }

                if (currentStep == 2) {
                    print("Getuje se trenutna opcija 3")
                    val selectedYear = view.getSelectedOption(1)?.replace('/', '_')
                    val selectedSubject = view.getSelectedOption(0) // Ponovo dohvata subject za prvi korak
                    val selectedExam = view.getSelectedOption(currentStep)
                    println(selectedExam)

                    return if (selectedSubject != null && selectedYear != null && selectedExam != null) {
                        // val result = subjectService.getGroupsForExamPerYearForSubjectOnServer(selectedSubject, selectedYear, selectedExam)
                        val result: List<StudentSolution> = subjectService.getGroupsForExamPerYearForSubjectOnServer(
                            selectedSubject,
                            selectedYear,
                            selectedExam
                        )
                        view.selectedSolutions = result
                        val groupNumbers = result.map { it.groupNumber }
                        println("RSSLT")
                        println(result)
                        groupNumbers
                    } else {
                        emptyList() // Ako su neki parametri null, vraća praznu listu
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
                        "Greška na koraku ${currentStep + 1}",
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

    override fun onSubmit(studentSolution: StudentSolution, chosenPath: File) {
        println("***IZABRANO*****")

        val examPath = studentSolution.gitPath.substring(8)
        val localBaseDir = chosenPath.absolutePath

        println(examPath)
        println(localBaseDir)

        // asinhroni zadatak
        object : SwingWorker<Unit, Unit>() {
            override fun doInBackground() {

                view.showLoader(true) // Prikazujemo loader
                GitRepoManager.downloadAllStudentWork(examPath, localBaseDir)
            }

            override fun done() {
                SwingUtilities.invokeLater {
                    view.showLoader(false) // Sakrivamo loader kada se završi proces

                    JOptionPane.showMessageDialog(
                        null,
                        "Uspešno preuzeto!",
                        "Obaveštenje",
                        JOptionPane.INFORMATION_MESSAGE
                    )

                    openDownloadedProject(localBaseDir)
                }
            }
        }.execute()

    }


}