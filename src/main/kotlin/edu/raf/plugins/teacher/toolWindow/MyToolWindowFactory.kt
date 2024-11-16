package edu.raf.plugins.teacher.toolWindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.ui.content.ContentFactory
import javax.swing.JButton
import javax.swing.JOptionPane

class MyToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        // Prvi prikazivanje login prozora
        val loginToolWindow = LoginToolWindow(project, toolWindow)
        val content = ContentFactory.getInstance().createContent(loginToolWindow.getContent(), "Login", false)
        toolWindow.contentManager.addContent(content)
    }

    class LoginToolWindow(val project: Project, val toolWindow: ToolWindow) {

        private val panel = JBPanel<JBPanel<*>>()
        private var loggedInUser: String? = null

        // Metoda koja kreira sadržaj login prozora
        fun getContent() = panel.apply {
            layout = null // Koristimo null layout za jednostavnost

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
                    val username = usernameField.text
                    val password = String(passwordField.password)

                    // Simulacija logovanja
                    if (isValidLogin(username, password)) {
                        JOptionPane.showMessageDialog(null, "Dobrodošli, $username!")
                        loggedInUser = username
                        showWelcomeMessage() // Pokreni prikazivanje poruke dobrodošlice
                    } else {
                        JOptionPane.showMessageDialog(null, "Pogrešno korisničko ime ili lozinka!", "Greška", JOptionPane.ERROR_MESSAGE)
                    }
                }
            }
            add(loginButton)
        }

        // Fiktivna logika za proveru korisničkog imena i lozinke
        private fun isValidLogin(username: String, password: String): Boolean {
            return username == "nastavnik" && password == "1234"
        }

        // Funkcija koja menja UI nakon logovanja
        private fun showWelcomeMessage() {
            // Uklanja prethodni sadržaj
            val welcomePanel = JBPanel<JBPanel<*>>().apply {
                layout = null
                // Dodajemo labelu sa dobrodošlicom
                add(JBLabel("Dobrodošli, $loggedInUser!").apply { setBounds(20, 20, 300, 25) })
            }

            // Zamenjujemo sadržaj u ToolWindow-u
            toolWindow.contentManager.removeAllContents(true)
            val welcomeContent = ContentFactory.getInstance().createContent(welcomePanel, "Welcome", false)
            toolWindow.contentManager.addContent(welcomeContent)
        }
    }
}
