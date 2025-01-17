package edu.raf.plugins.teacher.parsers

import edu.raf.plugins.teacher.models.Subject
import org.json.JSONArray

class SubjectOnServerParser : ISubjectParser {
    override fun parse(responseBody: String): List<Subject> {
        val jsonArray = JSONArray(responseBody)
        val subjects = mutableListOf<Subject>()

        for (i in 0 until jsonArray.length()) {
            val subjectName = jsonArray.getString(i)
            subjects.add(Subject(subjectName, subjectName))  // Ime i skraćenica su isti u ovom primeru
        }

        return subjects
    }
}