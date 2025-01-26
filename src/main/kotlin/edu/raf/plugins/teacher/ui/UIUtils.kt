package edu.raf.plugins.teacher.ui

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Font
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import javax.swing.*

class UIUtils {
    companion object {
        fun JTextField.addHint(hint: String) {
            val defaultColor = this.foreground
            val hintColor = Color.GRAY

            text = hint
            foreground = hintColor

            // Postavljamo italic font
            val font = this.font
            val italicFont = font.deriveFont(Font.ITALIC)  // Postavljamo italic font
            this.font = italicFont

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

         fun showToaster(message: String) {
            val dialog = JDialog()
            dialog.isUndecorated = true
            dialog.layout = BorderLayout()

            val label = JLabel(message, SwingConstants.CENTER)
            label.border = BorderFactory.createLineBorder(Color.BLACK, 1)
            dialog.add(label, BorderLayout.CENTER)

            dialog.pack()
            dialog.setLocationRelativeTo(null) // Centriranje na ekran
            dialog.isVisible = true

            // Automatsko zatvaranje nakon 2 sekunde
            Timer(10000) { dialog.dispose() }.start()
        }
    }
}