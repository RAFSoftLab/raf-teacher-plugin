package edu.raf.plugins.teacher.ui

import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel

class SubjectComboBox : JPanel() {
    val comboBox: JComboBox<String> = JComboBox()
    val label: JLabel = JLabel("Izaberite predmet:")

    init {
        add(label)
        add(comboBox)
    }

    // Metod koji omogućava kontroleru da ažurira listu predmeta u ComboBox-u
    fun updateSubjects(subjects: List<String>) {
        subjects.forEach { subject -> comboBox.addItem(subject) }
    }

    fun showError(message: String) {
        comboBox.addItem(message)
    }
}
