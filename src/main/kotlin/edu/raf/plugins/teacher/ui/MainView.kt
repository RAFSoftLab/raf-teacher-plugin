package edu.raf.plugins.teacher.ui

import edu.raf.plugins.teacher.ui.UIUtils.Companion.addHint
import java.awt.*
import javax.swing.*

class MainView : JPanel() {
    val comboBoxSubjects: JComboBox<String> = JComboBox()
    val labelChooseSubject: JLabel = JLabel("Izaberite predmet:")

    val currentYearLabel: JLabel = JLabel("Tekuća godina:")
    val currentYearInput: JTextField = JTextField("2024/25", 7)

    val testNameLabel: JLabel = JLabel("Naziv provere znanja:")
    val testNameInput: JTextField = JTextField(12).apply {
        toolTipText = "Prvi kolokvijum"
        addHint("Prvi kolokvijum...")
    }

    val submitButton: JButton = JButton("Unesi")

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
        constraints.gridy = 0
        add(comboBoxSubjects, constraints)

        // Drugi red (label + godina)
        constraints.gridx = 0
        constraints.gridy = 1
        add(currentYearLabel, constraints)

        constraints.gridx = 1
        constraints.gridy = 1
        add(currentYearInput, constraints)

        // Treći red (label + testName)
        constraints.gridx = 0
        constraints.gridy = 2
        add(testNameLabel, constraints)

        constraints.gridx = 1
        constraints.gridy = 2
        add(testNameInput, constraints)

        // Četvrti red (dugme za unos)
        constraints.gridx = 0
        constraints.gridy = 3
        constraints.gridwidth = 2 // Dugme zauzima celu širinu
        add(submitButton, constraints)

        // Akcija na dugme
        submitButton.addActionListener {
            val selectedSubject = comboBoxSubjects.selectedItem ?: "Nijedan predmet"
            val year = currentYearInput.text
            val testName = testNameInput.text

            JOptionPane.showMessageDialog(
                null,
                "Uneti podaci:\nPredmet: $selectedSubject\nGodina: $year\nNaziv provere: $testName",
                "Informacije",
                JOptionPane.INFORMATION_MESSAGE
            )
        }
    }

    // Metod za ažuriranje ComboBox-a
    fun updateSubjects(subjects: List<String>) {
        comboBoxSubjects.removeAllItems()
        subjects.forEach { subject -> comboBoxSubjects.addItem(subject) }
    }
}
