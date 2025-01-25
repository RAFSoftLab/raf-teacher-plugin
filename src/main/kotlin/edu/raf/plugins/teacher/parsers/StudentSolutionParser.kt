package edu.raf.plugins.teacher.parsers

import edu.raf.plugins.teacher.models.StudentSolution
import kotlinx.serialization.json.Json

class StudentSolutionParser : IParser<StudentSolution> {
    override fun parse(responseBody: String): List<StudentSolution> {
        print("Student solution parser")
        return Json.decodeFromString(responseBody)
    }
}