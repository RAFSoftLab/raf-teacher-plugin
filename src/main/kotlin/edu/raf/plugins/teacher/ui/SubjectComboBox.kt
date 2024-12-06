package edu.raf.plugins.teacher.ui

import java.awt.Color
import java.awt.FlowLayout
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import javax.swing.*

class SubjectComboBox : JPanel() {
    val comboBox: JComboBox<String> = JComboBox()
    val label: JLabel = JLabel("Izaberite predmet:")

    // Dodati elementi
    val currentYearLabel: JLabel = JLabel("Tekuća godina:")
    val currentYearInput: JTextField = JTextField("2024/25", 7) // Kratko polje za unos godine
    val testNameLabel: JLabel = JLabel("Naziv provere znanja:")
    val testNameInput: JTextField = JTextField(12).apply {
        toolTipText = "Prvi kolokvijum" // Hint za unos
        addHint("Prvi kolokvijum")
    }
    val submitButton: JButton = JButton("Unesi")

    fun setLoading(loading: Boolean) {
        // Onemogući ili omogući sve komponente unutar panela
        components.forEach { it.isEnabled = !loading }

        // Ako je učitavanje u toku, prikaži poruku u ComboBox-u
        if (loading) {
            comboBox.removeAllItems()
            comboBox.addItem("Učitavanje...")
        } else {
            comboBox.removeAllItems() // Ukloni placeholder kada završi učitavanje
        }
    }


    init {
        // Podešavanje rasporeda elemenata na horizontalni
        layout = FlowLayout(FlowLayout.LEFT, 10, 10)

        // Dodavanje elemenata na panel
        add(label)
        add(comboBox)
        add(currentYearLabel)
        add(currentYearInput)
        add(testNameLabel)
        add(testNameInput)
        add(submitButton)

        // Postavljanje akcije za dugme
        submitButton.addActionListener {
            val selectedSubject = comboBox.selectedItem ?: "Nijedan predmet"
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
        comboBox.removeAllItems()
        subjects.forEach { subject -> comboBox.addItem(subject) }
    }

    // Prikaz greške
    fun showError(message: String) {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Greška pri povezivanju",
            JOptionPane.ERROR_MESSAGE
        )
    }

    // Ekstenzija za dodavanje placeholder teksta
    private fun JTextField.addHint(hint: String) {
        val defaultColor = this.foreground
        val hintColor = Color.GRAY

        text = hint
        foreground = hintColor

        addFocusListener(object : FocusListener {
            override fun focusGained(e: FocusEvent?) {
                if (text == hint) {
                    text = ""
                    foreground = defaultColor
                }
            }

            override fun focusLost(e: FocusEvent?) {
                if (text.isEmpty()) {
                    text = hint
                    foreground = hintColor
                }
            }
        })
    }


}
