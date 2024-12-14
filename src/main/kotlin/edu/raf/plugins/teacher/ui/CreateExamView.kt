package edu.raf.plugins.teacher.ui

import edu.raf.plugins.teacher.constants.ConstantsUtil
import edu.raf.plugins.teacher.listeners.ExamViewListener
import edu.raf.plugins.teacher.models.Subject
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
    var listener: ExamViewListener? = null

    var onReturnToMenu: (() -> Unit)? = null // Callback za povratak na meni

    val comboBoxSubjects: JComboBox<Subject> = JComboBox()
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

        // Postavljanje prilagođenog renderera za comboBox
        comboBoxSubjects.renderer = object : ListCellRenderer<Subject> {
            private val renderer = DefaultListCellRenderer()
            override fun getListCellRendererComponent(
                list: JList<out Subject>?,
                value: Subject?,
                index: Int,
                isSelected: Boolean,
                cellHasFocus: Boolean
            ): Component {
                val component = renderer.getListCellRendererComponent(list, value?.name, index, isSelected, cellHasFocus)
                (component as JLabel).toolTipText = value?.shortName
                return component
            }
        }

        // Akcija na dugme "Unesi"
        submitButton.addActionListener {
            val selectedSubject = comboBoxSubjects.selectedItem as? Subject
            val year = currentYearInput.text
            val testName = testNameInput.text

            if (selectedSubject == null || year.isBlank() || testName.isBlank()) {
                JOptionPane.showMessageDialog(
                    null,
                    "Popunite sva polja.",
                    "Greška",
                    JOptionPane.ERROR_MESSAGE
                )
            } else {
                // Obavesti kontroler o akciji korisnika
                listener?.onSubmitExam(selectedSubject, year, testName)
            }
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
    fun updateSubjects(subjects: List<Subject>) {
        comboBoxSubjects.removeAllItems()
        subjects.forEach { subject -> comboBoxSubjects.addItem(subject) }
    }
}
