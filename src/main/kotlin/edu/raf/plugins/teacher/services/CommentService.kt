package edu.raf.plugins.teacher.services

import edu.raf.plugins.teacher.constants.ConstantsUtil
import edu.raf.plugins.teacher.models.Comment
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File

class CommentService {
    private val jsonFormat = Json { ignoreUnknownKeys = true }

    fun loadComments(): List<Comment> {
        val logFile = File(
            System.getProperty(ConstantsUtil.COMMENTS_DIRECTORY) +
                    File.separator +
                    ConstantsUtil.COMMENTS_FILE
        )

        return if (logFile.exists()) {
            try {
                jsonFormat.decodeFromString<List<Comment>>(logFile.readText())
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