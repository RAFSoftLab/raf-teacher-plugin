package edu.raf.plugins.teacher.models

import com.intellij.openapi.project.Project
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import java.io.File

@Serializable
data class Comment(
    val id: Long,
    @SerialName("project_name") val projectName: String,
    @SerialName("relative_path") val relativePath: String,
    @SerialName("comment_text") val commentText: String,
    @SerialName("start_line") val startLine: Int,
    @SerialName("end_line") val endLine: Int,
    val timestamp: String
) {
    fun matchesProject(currentProject: Project): Boolean {
        return currentProject.name == projectName
    }

    fun resolveAbsolutePath(baseDir: String): String {
        return "$baseDir${File.separator}$relativePath"
    }

    // Skraćeni komentar (prvih 20 karaktera...)
    val shortComment: String
        get() = if (commentText.length <= 35) commentText
        else commentText.substring(0, 35) + "..."

}