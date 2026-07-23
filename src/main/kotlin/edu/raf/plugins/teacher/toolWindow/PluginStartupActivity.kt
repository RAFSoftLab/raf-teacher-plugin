package edu.raf.plugins.teacher.toolWindow



import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.notification.*
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.ShowSettingsUtil
import edu.raf.plugins.teacher.utils.ConfigLoader
import edu.raf.plugins.teacher.utils.SentryInitializer
import io.sentry.Sentry
import java.net.URL

class PluginStartupActivity : StartupActivity {

    override fun runActivity(project: Project) {
        SentryInitializer.init()
        ApplicationManager.getApplication().executeOnPooledThread {
            checkPluginUpdate(project)
        }
    }

    private fun checkPluginUpdate(project: Project) {
        try {
            val pluginId = PluginId.getId(ConfigLoader.get("plugin.id"))
            val currentVersion = PluginManagerCore.getPlugin(pluginId)?.version ?: "0.0.0"

            val xmlContent = URL(ConfigLoader.get("plugin.xml")).readText()

            // REGEX objašnjenje:
            // version=" -> traži bukvalan tekst
            // (.*?)     -> hvata sve unutar (to je naša verzija)
            // "         -> do sledećeg navodnika
            val regex = "version=\"(.*?)\"".toRegex()

            // Uzimamo SVA pojavljivanja i biramo POSLEDNJE (najnovija verzija)
            val matches = regex.findAll(xmlContent)
            val latestVersion = matches.lastOrNull()?.groupValues?.get(1) ?: ""

            if (latestVersion.isNotEmpty() && latestVersion != currentVersion) {
                showUpdateNotification(project, currentVersion, latestVersion)
            }
        } catch (e: Exception) {
            Sentry.captureException(e)
        }
    }

    private fun showUpdateNotification(project: Project, oldVersion: String, newVersion: String) {
       println("Nova verzija nastavničkog plugina je dostupna: ($newVersion)")
        val notificationGroup = NotificationGroupManager.getInstance()
            .getNotificationGroup("RAF LMS Updates")

        val notification = notificationGroup.createNotification(
            "🚀 Nova verzija je dostupna!",
            "Vaša verzija: $oldVersion -> Nova: $newVersion.\nAžurirajte plugin za nove funkcije.",
            NotificationType.IDE_UPDATE
        )

        notification.addAction(NotificationAction.createSimple("Otvori Plugins") {
            ShowSettingsUtil.getInstance().showSettingsDialog(project, "Plugins")
        })

        Notifications.Bus.notify(notification, project)
    }
}