package edu.raf.plugins.teacher.utils

import java.time.LocalDate
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ex.ProjectManagerEx
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.nio.file.Path

class Utils {
    companion object {
        @JvmStatic
        fun generateSchoolYear(date: LocalDate): String {
            // Trenutna godina i mesec iz prosleđenog datuma
            val currentYear = date.year
            val currentMonth = date.monthValue

            // Ako je mesec između oktobra i decembra (10-12), koristi trenutnu godinu i sledeću
            if (currentMonth in 10..12) {
                return "$currentYear/${(currentYear + 1) % 100}"
            }
            // Ako je mesec između januara i septembra (1-9), koristi prethodnu godinu i trenutnu
            return "${currentYear - 1}/${currentYear % 100}"
        }

        fun openDownloadedProject(localBaseDir: String) {
            val projectManager = ProjectManager.getInstance()

            ApplicationManager.getApplication().invokeLater {
                val virtualFile: VirtualFile? = LocalFileSystem.getInstance().refreshAndFindFileByPath(localBaseDir)
                if (virtualFile != null) {
                    val existingProjects = projectManager.openProjects
                    val currentProject: Project? = existingProjects.firstOrNull() // Uzmi prvi otvoreni projekat

                    val newProject = ProjectManagerEx.getInstanceEx().loadAndOpenProject(localBaseDir)
                    if (newProject != null && currentProject != null) {
                        ProjectManagerEx.getInstanceEx()
                            .closeAndDispose(currentProject) // Zatvori stari projekat ako treba
                    }
                }
            }
        }
    }
}