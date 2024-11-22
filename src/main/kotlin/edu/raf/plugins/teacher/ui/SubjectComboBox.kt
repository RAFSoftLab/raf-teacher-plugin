package edu.raf.plugins.teacher.ui

import edu.raf.plugins.teacher.services.SubjectService
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingUtilities

class SubjectComboBox : JPanel() {

    init {
        // Kreiranje JLabel-a sa tekstom
        val label = JLabel("Izaberite predmet:")

        // Kreiranje praznog ComboBox-a
        val comboBox = JComboBox<String>()

        // Dodavanje JLabel-a i ComboBox-a u panel
        add(label)
        add(comboBox)

        // Pozivanje API-ja u pozadini
        SwingUtilities.invokeLater {
            try {
                val subjects = SubjectService().getSubjects()
                SwingUtilities.invokeLater {
                    subjects.forEach { subject ->
                        comboBox.addItem(subject)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                SwingUtilities.invokeLater {
                    comboBox.addItem("Greška pri učitavanju")
                }
            }
        }
    }
}
