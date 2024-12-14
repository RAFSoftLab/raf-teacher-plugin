package edu.raf.plugins.teacher.controllers

import edu.raf.plugins.teacher.listeners.ExamViewListener
import edu.raf.plugins.teacher.models.Subject
import edu.raf.plugins.teacher.services.ExamService
import edu.raf.plugins.teacher.services.SubjectService
import edu.raf.plugins.teacher.ui.CreateExamView
import javax.swing.JOptionPane
import javax.swing.SwingWorker

class SubjectController(private val view: CreateExamView): ExamViewListener {
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
        object : SwingWorker<Void, Void>() {
            override fun doInBackground(): Void? {
                try {
                    examService.createExam(subject.shortName, updatedYear, testName)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return null
            }

            override fun done() {
                try {
                    // Prikaz poruke nakon uspešnog unosa
                    JOptionPane.showMessageDialog(
                        null,
                        "Provera je uspešno kreirana.",
                        "Uspeh",
                        JOptionPane.INFORMATION_MESSAGE
                    )
                } catch (e: Exception) {
                    JOptionPane.showMessageDialog(
                        null,
                        "Došlo je do greške prilikom kreiranja provere.",
                        "Greška",
                        JOptionPane.ERROR_MESSAGE
                    )
                }
            }

        }.execute()
    }
}
