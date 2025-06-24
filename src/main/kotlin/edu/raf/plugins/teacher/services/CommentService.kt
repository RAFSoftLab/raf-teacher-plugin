package edu.raf.plugins.teacher.services

import com.intellij.openapi.project.Project
import edu.raf.plugins.teacher.constants.ConstantsUtil
import edu.raf.plugins.teacher.models.Comment
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class CommentService {
    private val jsonFormat = Json { ignoreUnknownKeys = true }

    public fun loadCommentsForCurrentProject(project: Project): List<Comment> {
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

    fun deleteComment(commentToDelete: Comment) {
        val logFile = File(
            System.getProperty(ConstantsUtil.COMMENTS_DIRECTORY) +
                    File.separator +
                    ConstantsUtil.COMMENTS_FILE
        )

        if (!logFile.exists()) {
            println("Fajl sa komentarima ne postoji: ${logFile.absolutePath}")
            return
        }

        try {
            // Read existing comments
            val allComments = jsonFormat.decodeFromString<List<Comment>>(logFile.readText())

            // Filter out the comment to delete
            val updatedComments = allComments.filter { it.id != commentToDelete.id }

            // Write updated comments back to the file
            logFile.writeText(jsonFormat.encodeToString(updatedComments))
            println("Komentar sa ID-jem ${commentToDelete.id} je uspešno obrisan.")
        } catch (e: SerializationException) {
            println("Greška pri parsiranju komentara: ${e.message}")
        } catch (e: Exception) {
            println("Greška pri radu sa fajlom: ${e.message}")
        }
    }
}