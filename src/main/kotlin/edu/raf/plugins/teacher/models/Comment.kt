package edu.raf.plugins.teacher.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import java.io.File

@Serializable
data class Comment(
    val id: Long,
    @SerialName("file_path") val filePath: String,
    @SerialName("comment_text") val commentText: String,
    @SerialName("start_line") val startLine: Int,
    @SerialName("end_line") val endLine: Int,
    val timestamp: String
) {
    // Prikazuje samo ime fajla (bez putanje)
    val fileName: String
        get() = File(filePath).name

    // Skraćeni komentar (prvih 20 karaktera)
    val shortComment: String
        get() = if (commentText.length <= 50) commentText
        else commentText.substring(0, 50) + "..."

}