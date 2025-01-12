package edu.raf.plugins.teacher.controllers

import Config

import RemoteScriptExecutor.runRemoteScript

import edu.raf.plugins.teacher.listeners.ExamViewListener
import edu.raf.plugins.teacher.models.Subject
import edu.raf.plugins.teacher.services.ExamService
import edu.raf.plugins.teacher.services.SubjectService
import edu.raf.plugins.teacher.ui.CreateExamView
import javax.swing.JOptionPane
import javax.swing.SwingWorker


class SubjectExamController(private val view: CreateExamView) : ExamViewListener {
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

    override fun onSubmitExam(subject: Subject, year: String, testName: String, group: String) {
        val updatedYear = year.replace("/", "_")
        val updatedTestName = testName.replace(" ", "_")
        view.showLoader(true)  // Prikazivanje loadera
        object : SwingWorker<Void, String>() {
            private var errorMessage: String? = null

            override fun doInBackground(): Void? {
                try {
                    val directoryResponse = examService.createDirectory(subject.shortName, updatedYear, updatedTestName, group)

                    // Provera statusa i inicijalizacije
                    if (directoryResponse.status != "success" || directoryResponse.gitInitialized != "true") {
                        errorMessage =
                            "Greška: Kreiranje direktorijuma nije uspelo. Status: ${directoryResponse.status}, Git inicijalizovan: ${directoryResponse.gitInitialized}."
                        return null
                    }

                    // Skripta za permisije

                    val resp = runRemoteScript(
                        Config.SERVER_HOST,
                        Config.SERVER_SSH_PORT,
                        Config.SERVER_USERNAME,
                        Config.SERVER_PASSWORD,
                        Config.REMOTE_SCRIPT_1,
                        directoryResponse.basePath
                    )
                    if (resp.contains("error") || resp.contains("password is required")) {
                        errorMessage = "Greška: Skripta nije uspešno izvršena. Odgovor: $resp"
                        return null
                    }
                    println("Skripta uspešno izvršena: $resp")

                    val remoteURL = "http://${Config.SERVER_HOST}${directoryResponse.basePath.substring(8)}"
                    println("Kreirani remote URL: $remoteURL")


                    Utils.pushCurrentProject(remoteURL, "main", "test")

                } catch (e: Exception) {
                    e.printStackTrace()
                    errorMessage = "Došlo je do greške prilikom kreiranja provere: ${e.message}"
                }
                return null
            }


            override fun done() {
                view.showLoader(false)  // Prikazivanje loadera
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
