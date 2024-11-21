package edu.raf.plugins.teacher.ui

import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel

class SubjectComboBox : JPanel() {

    init {
        // Kreiranje JLabel-a sa tekstom
        val label = JLabel("Izaberite predmet:")

        // Kreiranje ComboBox sa dva predmeta
        val comboBox = JComboBox(arrayOf("Dizajn softvera", "Testiranje softvera"))

        // Dodavanje JLabel-a i ComboBox-a u panel
        add(label)
        add(comboBox)
    }
}
