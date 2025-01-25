package edu.raf.plugins.teacher.models

import kotlinx.serialization.Serializable

@Serializable
data class StudentSolution(
    val id: Int,
    val groupNumber: String,
    val gitPath: String,
    val testTypeId: Int
)
