package edu.raf.plugins.teacher.ui

import java.awt.*
import java.awt.event.ActionListener
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.border.LineBorder

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



    }
}