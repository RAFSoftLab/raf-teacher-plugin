package edu.raf.plugins.teacher.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import edu.raf.plugins.teacher.constants.ConstantsUtil
import edu.raf.plugins.teacher.models.Comment
import edu.raf.plugins.teacher.observer.Publisher
import edu.raf.plugins.teacher.observer.Subscriber
import io.sentry.Sentry
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.security.MessageDigest

@Service(Service.Level.PROJECT)
class CommentService : Publisher {
    private val jsonFormat = Json { ignoreUnknownKeys = true }
    private val subscribers = mutableListOf<Subscriber>()

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
                Sentry.captureException(e)
                println("Greška pri parsiranju komentara: ${e.message}")
                emptyList()
            } catch (e: Exception) {
                Sentry.captureException(e)
                println("Greška pri čitanju fajla: ${e.message}")
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    fun deleteComment(commentToDelete: Comment, project: Project): List<Comment> {
        val logFile = File(
            System.getProperty(ConstantsUtil.COMMENTS_DIRECTORY) +
                    File.separator +
                    ConstantsUtil.COMMENTS_FILE
        )

        if (!logFile.exists()) {
            println("Fajl sa komentarima ne postoji: ${logFile.absolutePath}")
            return emptyList()
        }

        return try {
            // Read existing comments
            val allComments = jsonFormat.decodeFromString<List<Comment>>(logFile.readText())

            // Filter out only the comment to delete
            val updatedComments = allComments.filter { it.id != commentToDelete.id }

            // Write updated comments back to the file
            logFile.writeText(jsonFormat.encodeToString(updatedComments))

            // Return only comments for the current project
            updatedComments.filter { it.matchesProject(project) }
        } catch (e: SerializationException) {
            Sentry.captureException(e)
            println("Greška pri parsiranju komentara: ${e.message}")
            emptyList()
        } catch (e: Exception) {
            Sentry.captureException(e)
            println("Greška pri radu sa fajlom: ${e.message}")
            emptyList()
        }
    }

    fun updateComment(updatedComment: Comment, project: Project): List<Comment> {
        val logFile = File(
            System.getProperty(ConstantsUtil.COMMENTS_DIRECTORY) +
                    File.separator +
                    ConstantsUtil.COMMENTS_FILE
        )

        if (!logFile.exists()) {
            println("Fajl sa komentarima ne postoji: ${logFile.absolutePath}")
            return emptyList()
        }

        return try {
            // Read existing comments
            val allComments = jsonFormat.decodeFromString<List<Comment>>(logFile.readText())

            // Update the specific comment
            val updatedComments = allComments.map { comment ->
                if (comment.id == updatedComment.id && comment.matchesProject(project)) {
                    updatedComment
                } else {
                    comment
                }
            }

            // Write updated comments back to the file
            logFile.writeText(jsonFormat.encodeToString(updatedComments))

            // Return only comments matching the current project
            updatedComments.filter { it.matchesProject(project) }
        } catch (e: SerializationException) {
            Sentry.captureException(e)
            println("Greška pri parsiranju komentara: ${e.message}")
            emptyList()
        } catch (e: Exception) {
            Sentry.captureException(e)
            println("Greška pri radu sa fajlom: ${e.message}")
            emptyList()
        }
    }


    fun saveComment(commentText: String, filePath: String?, startLine: Int, endLine: Int, project: Project): Boolean {
        val logFile = File(
            System.getProperty(ConstantsUtil.COMMENTS_DIRECTORY) +
                    File.separator +
                    ConstantsUtil.COMMENTS_FILE
        )

        if (filePath == null) {
            println("File path is null. Cannot save comment.")
            return false
        }

        val newComment = Comment(
            id = generateUniqueId(filePath, startLine, endLine),
            relativePath = filePath,
            commentText = commentText,
            startLine = startLine,
            endLine = endLine,
            timestamp = System.currentTimeMillis().toString(),
            projectName = project.name
        )

        return try {
            // Read existing comments
            val existingComments = if (logFile.exists() && logFile.readText().isNotBlank()) {
                jsonFormat.decodeFromString<List<Comment>>(logFile.readText())
            } else {
                emptyList()
            }

            // Check if a comment with the same ID already exists
            if (existingComments.any { it.id == newComment.id }) {
                println("Comment with the same ID already exists.")
                return false
            }

            // Add the new comment
            val updatedComments = existingComments + newComment
            // Filter before notifying
            val projectComments = updatedComments.filter { it.matchesProject(project) }
            notifySubscribers(projectComments)

            // Write updated comments back to the file
            logFile.writeText(jsonFormat.encodeToString(updatedComments))

            true
        } catch (e: SerializationException) {
            Sentry.captureException(e)
            println("Error parsing comments: ${e.message}")
            false
        } catch (e: Exception) {
            Sentry.captureException(e)
            println("Error working with file: ${e.message}")
            false
        }
    }

    private fun generateUniqueId(filePath: String, startLine: Int, endLine: Int): Long {
        val input = "$filePath$startLine$endLine"
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.fold(0L) { acc, byte -> acc * 31 + byte }
    }

    override fun addSubscriber(subscriber: Subscriber) {
        println("Dodajem pretplatnika na promene komentara")
        if (!subscribers.contains(subscriber)) {
            subscribers.add(subscriber)
        }
        println("Broj pretplatnika: ${subscribers.size}")
    }

    override fun removeSubscriber(subscriber: Subscriber) {
        subscribers.remove(subscriber)
    }

    override fun notifySubscribers(data: Any) {
        println("Obavestavam pretplatnike o promenama komentara")
        println(subscribers.size)
        for (subscriber in subscribers) {
            subscriber.update(data)
        }
    }

}