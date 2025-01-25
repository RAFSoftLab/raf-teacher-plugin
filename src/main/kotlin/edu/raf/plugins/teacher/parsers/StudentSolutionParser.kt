package edu.raf.plugins.teacher.parsers

import edu.raf.plugins.teacher.models.StudentSolution
import kotlinx.serialization.json.Json
import org.json.JSONArray

class StudentSolutionParser : IParser<StudentSolution> {
    override fun parse(responseBody: String): List<StudentSolution> {
        println("Student solution parser")
        val jsonArray = JSONArray(responseBody)
        val studentsSolutions = mutableListOf<StudentSolution>()

        for(i in 0 until jsonArray.length()) {
            val studentSolutionObject = jsonArray.getJSONObject(i)
            val id = studentSolutionObject.getInt("id")
            val groupNumber = studentSolutionObject.getString("groupNumber")
            val gitPath = studentSolutionObject.getString("gitPath")
            val testTypeId = studentSolutionObject.getInt("testTypeId")

            studentsSolutions.add(StudentSolution(id,groupNumber,gitPath,testTypeId))
        }

        return studentsSolutions
    }
}