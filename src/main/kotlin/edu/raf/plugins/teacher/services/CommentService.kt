package edu.raf.plugins.teacher.services

import com.intellij.openapi.project.Project
import edu.raf.plugins.teacher.constants.ConstantsUtil
import edu.raf.plugins.teacher.models.Comment
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File

class CommentService {
    private val jsonFormat = Json { ignoreUnknownKeys = true }

    fun loadCommentsForCurrentProject(project: Project): List<Comment> {
        val logFile = File(
            System.getProperty(ConstantsUtil.COMMENTS_DIRECTORY) +
                    File.separator +
                    ConstantsUtil.COMMENTS_FILE
        )

        return if (logFile.exists()) {
            try {
                val allComments = jsonFormat.decodeFromString<List<Comment>>(logFile.readText())

                // Filtriranje komentara za trenutni projekat
                allComments.filter { it.matchesProject(project) }.map { comment ->
                    // Ažuriranje putanje ako je potrebno
                    comment.copy()
                }
            } catch (e: SerializationException) {
                println("Greška pri parsiranju komentara: ${e.message}")
                emptyList()
            } catch (e: Exception) {
                println("Greška pri čitanju fajla: ${e.message}")
                emptyList()
            }
        } else {
            emptyList()
        }
    }
}