package edu.raf.plugins.teacher.toolWindow.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.ui.content.ContentFactory
import edu.raf.plugins.teacher.services.LoginService
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JOptionPane
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent

class LoginToolWindow(private val project: Project, private val toolWindow: ToolWindow) {

    private val panel = JBPanel<JBPanel<*>>()
    private var loggedInUser: String? = null
    private val loginServise = project.getService(LoginService::class.java)

    fun getContent() = panel.apply {
        layout = null

        // Korisničko ime
        val usernameLabel = JBLabel("Korisničko ime:").apply { setBounds(20, 20, 120, 25) }
        val usernameField = JBTextField().apply { setBounds(140, 20, 200, 25) }
        add(usernameLabel)
        add(usernameField)

        // Lozinka
        val passwordLabel = JBLabel("Lozinka:").apply { setBounds(20, 60, 120, 25) }
        val passwordField = JBPasswordField().apply { setBounds(140, 60, 200, 25) }
        add(passwordLabel)
        add(passwordField)

        // Login dugme
        val loginButton = JButton("Uloguj se").apply {
            setBounds(140, 100, 200, 30)
            addActionListener {
                handleLogin(usernameField, passwordField)
            }
        }
        add(loginButton)

        //  KeyListener na polja za korisničko ime i lozinku da moze i na enter
        val keyListener = object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ENTER) {
                    handleLogin(usernameField, passwordField)
                }
            }
        }
        usernameField.addKeyListener(keyListener)
        passwordField.addKeyListener(keyListener)
    }

    private fun handleLogin(usernameField: JBTextField, passwordField: JBPasswordField) {
        val username = usernameField.text
        val password = String(passwordField.password)

        if (loginServise.authenticate(username, password)) {
            JOptionPane.showMessageDialog(null, "Dobrodošli, $username!")
            loggedInUser = username
            showSubjectSelection()
        } else {
            JOptionPane.showMessageDialog(null, "Pogrešno korisničko ime ili lozinka!", "Greška", JOptionPane.ERROR_MESSAGE)
        }
    }


    private fun showSubjectSelection() {
        val subjectPanel = JBPanel<JBPanel<*>>().apply {
            layout = null

            val welcomeLabel = JBLabel("Dobrodošli, $loggedInUser!").apply { setBounds(20, 20, 300, 25) }
            add(welcomeLabel)

            val subjectLabel = JBLabel("Odaberite predmet:").apply { setBounds(20, 60, 120, 25) }
            add(subjectLabel)

            val subjects = arrayOf("Dizajn Softvera", "Testiranje Softvera", "Verovatnoća i statistika")
            val subjectComboBox = JComboBox(subjects).apply { setBounds(140, 60, 200, 25) }
            add(subjectComboBox)

            val confirmButton = JButton("Potvrdi").apply {
                setBounds(140, 100, 200, 30)
                addActionListener {
                    val selectedSubject = subjectComboBox.selectedItem
                    JOptionPane.showMessageDialog(null, "Izabrali ste predmet: $selectedSubject")
                }
            }
            add(confirmButton)
        }

        toolWindow.contentManager.removeAllContents(true)
        val subjectContent = ContentFactory.getInstance().createContent(subjectPanel, "Odabir predmeta", false)
        toolWindow.contentManager.addContent(subjectContent)
    }
}
