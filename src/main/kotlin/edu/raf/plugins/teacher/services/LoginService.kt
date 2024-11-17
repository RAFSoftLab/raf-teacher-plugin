package edu.raf.plugins.teacher.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger

@Service(Service.Level.PROJECT)
class LoginService {

    private val validUsers = mapOf(
        "nastavnik" to "1234",
        "admin" to "admin123"
    )

    fun authenticate(username: String, password: String): Boolean {
        val isValid = validUsers[username] == password
        thisLogger().info("Login attempt for user '$username': ${if (isValid) "SUCCESS" else "FAILURE"}")
        return isValid
    }
}
