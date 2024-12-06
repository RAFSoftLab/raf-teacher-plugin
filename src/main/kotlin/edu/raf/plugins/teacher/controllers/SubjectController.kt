package edu.raf.plugins.teacher.controllers

import edu.raf.plugins.teacher.services.SubjectService
import edu.raf.plugins.teacher.ui.SubjectComboBox
import javax.swing.JOptionPane
import javax.swing.SwingUtilities

class SubjectController(private val view: SubjectComboBox) {

    private val service = SubjectService()

    fun loadSubjects() {
        SwingUtilities.invokeLater {
            try {
                val subjects = service.getSubjects()

                print("Uspesno jee!")

                // Ako je učitavanje uspešno, osveži prikaz
                SwingUtilities.invokeLater {
                   // view.setLoading(false)
                    view.updateSubjects(subjects)
                }
            } catch (e: Exception) {
                e.printStackTrace()

                view.isVisible = false // Sakriva view

                // Prikazuje poruku o grešci korisniku
                SwingUtilities.invokeLater {
                    JOptionPane.showMessageDialog(
                        null,
                        "Nije moguće povezati se na server. Proverite Vašu mrežnu konekciju.",
                        "Greška pri povezivanju",
                        JOptionPane.ERROR_MESSAGE
                    )
                   // view.setLoading(false) // Zaustavlja animaciju učitavanja
                    view.isVisible = false // Sakriva view
                }
            }
        }
    }
}
