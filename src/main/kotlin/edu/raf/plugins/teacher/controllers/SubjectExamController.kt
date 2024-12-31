package edu.raf.plugins.teacher.controllers

import edu.raf.plugins.teacher.listeners.ExamViewListener
import edu.raf.plugins.teacher.models.Subject
import edu.raf.plugins.teacher.services.ExamService
import edu.raf.plugins.teacher.services.SubjectService
import edu.raf.plugins.teacher.ui.CreateExamView
import javax.swing.JOptionPane
import javax.swing.SwingWorker

class SubjectExamController(private val view: CreateExamView): ExamViewListener {
    init {
        view.listener = this //
    }

    private val service = SubjectService()
    private val examService = ExamService()

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

    override fun onSubmitExam(subject: Subject, year: String, testName: String) {
        val updatedYear = year.replace("/", "_")
        object : SwingWorker<Void, String>() {
            private var errorMessage: String? = null

            override fun doInBackground(): Void? {
                try {
                    examService.createExam(subject.shortName, updatedYear, testName)
                } catch (e: Exception) {
                    e.printStackTrace()
                    errorMessage = "Došlo je do greške prilikom kreiranja provere: ${e.message}"
                }
                return null
            }

            override fun done() {
                if (errorMessage != null) {
                    // Prikaz poruke o grešci
                    JOptionPane.showMessageDialog(
                        null,
                        errorMessage,
                        "Greška",
                        JOptionPane.ERROR_MESSAGE
                    )
                } else {
                    // Prikaz poruke o uspehu
                    JOptionPane.showMessageDialog(
                        null,
                        "Provera je uspešno kreirana.\n" +
                                "Predmet: ${subject.name} \n Školska godina: $year \n Naziv: $testName",
                        "Uspeh",
                        JOptionPane.INFORMATION_MESSAGE
                    )
                }
            }
        }.execute()
    }

}
