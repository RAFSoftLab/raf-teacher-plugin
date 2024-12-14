package edu.raf.plugins.teacher.ui

import edu.raf.plugins.teacher.constants.ConstantsUtil
import edu.raf.plugins.teacher.services.ExamService
import edu.raf.plugins.teacher.ui.UIUtils.Companion.addHint
import edu.raf.plugins.teacher.utils.ImageLoader
import edu.raf.plugins.teacher.utils.Utils.Companion.generateSchoolYear
import java.awt.*
import java.io.IOException
import java.net.URL
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import javax.swing.*

class CreateExamView : JPanel() {
    val comboBoxSubjects: JComboBox<String> = JComboBox()
    val labelChooseSubject: JLabel = JLabel("Izaberite predmet:")

    val currentYearLabel: JLabel = JLabel("Tekuća godina:")
    val currentYearInput: JTextField = JTextField(generateSchoolYear(LocalDate.now()), 7)

    val testNameLabel: JLabel = JLabel("Naziv provere znanja:")
    val testNameInput: JTextField = JTextField(12).apply {
        toolTipText = "Prvi kolokvijum"
        addHint("Prvi kolokvijum...")
    }

    val submitButton: JButton = JButton("Unesi")
    val iconContent = ImageIcon(URL(ImageLoader.getImageUrl(ConstantsUtil.UPLOAD_IMAGE)))
    val postaviButton: JButton = JButton("Postavi").apply {
        isEnabled = false // Na početku je dugme onemogućeno
        icon = iconContent
    }

    init {
        // Koristimo GridBagLayout za fleksibilnost u rasporedu
        layout = GridBagLayout()

        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.insets = Insets(5, 5, 5, 5) // Dodajemo razmake između komponenti

        // Prvi red (label + comboBox)
        constraints.gridx = 0
        constraints.gridy = 0
        add(labelChooseSubject, constraints)

        constraints.gridx = 1
        add(comboBoxSubjects, constraints)

        // Drugi red (label + godina)
        constraints.gridx = 0
        constraints.gridy = 1
        add(currentYearLabel, constraints)

        constraints.gridx = 1
        add(currentYearInput, constraints)

        // Treći red (label + testName)
        constraints.gridx = 0
        constraints.gridy = 2
        add(testNameLabel, constraints)

        constraints.gridx = 1
        add(testNameInput, constraints)

        // Četvrti red (dugme za unos)
        constraints.gridx = 0
        constraints.gridy = 3
        constraints.gridwidth = 2 // Dugme zauzima celu širinu
        add(submitButton, constraints)

        // Peti red (dugme za postavljanje)
        constraints.gridx = 0
        constraints.gridy = 4
        constraints.gridwidth = 2
        add(postaviButton, constraints)

        // Akcija na dugme "Unesi"
        submitButton.addActionListener {
            val selectedSubject = comboBoxSubjects.selectedItem ?: "Nijedan predmet"
            val year = currentYearInput.text
            val updatedYear = year.replace("/", "_")
            val testName = testNameInput.text

            JOptionPane.showMessageDialog(
                null,
                "Uneti podaci:\nPredmet: $selectedSubject\nGodina: $year\nNaziv provere: $testName",
                "Informacije",
                JOptionPane.INFORMATION_MESSAGE
            )
            val examService = ExamService()
            try {
                examService.createExam("OOP", updatedYear, testName)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            // Simulacija poziva na API
            simulateApiCall()
        }

        // Akcija na dugme "Postavi"
        postaviButton.addActionListener {
            JOptionPane.showMessageDialog(
                null,
                "Podaci su postavljeni.",
                "Uspešno",
                JOptionPane.INFORMATION_MESSAGE
            )
        }
    }

    // Lažni API poziv koji simulira uspešan odgovor
    private fun simulateApiCall() {
        // Koristimo SwingWorker da simuliramo API poziv
        object : SwingWorker<Void, Void>() {
            override fun doInBackground(): Void? {
                // Simulacija trajanja poziva
                try {
                    TimeUnit.SECONDS.sleep(2)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                return null
            }

            override fun done() {
                // Simuliramo odgovor "success"
                val success = true
                if (success) {
                    postaviButton.isEnabled = true // Omogućavamo dugme "Postavi"
                } else {
                    JOptionPane.showMessageDialog(
                        null,
                        "Došlo je do greške prilikom postavljanja podataka.",
                        "Greška",
                        JOptionPane.ERROR_MESSAGE
                    )
                }
            }
        }.execute()
    }

    // Metod za ažuriranje ComboBox-a
    fun updateSubjects(subjects: List<String>) {
        comboBoxSubjects.removeAllItems()
        subjects.forEach { subject -> comboBoxSubjects.addItem(subject) }
    }
}
