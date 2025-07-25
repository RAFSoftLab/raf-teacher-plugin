package edu.raf.plugins.teacher.toolWindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import edu.raf.plugins.teacher.listeners.selection.SetUpSelectionListener


class PluginStartupActivity : StartupActivity {

    override fun runActivity(project: Project) {
        ("Postavicu listener iz start upa")
//        val selectionListener = SetUpSelectionListener.getInstance(project)
//        selectionListener.setupEditorListener()
//
//
//        Sentry.init { options ->
//            options.dsn = "https://ded7d252c6c25bc6db783375495f383b@o4509723131838464.ingest.de.sentry.io/4509723138195536"
//            options.isDebug = true // Omogućava debug mod
//        }
    }
}