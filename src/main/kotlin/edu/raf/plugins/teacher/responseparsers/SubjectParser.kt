package edu.raf.plugins.teacher.responseparsers

import edu.raf.plugins.teacher.models.Subject
import org.json.JSONArray

class SubjectParser: IParser<Subject> {
    override fun parse(responseBody: String): List<Subject> {
        print("Subject srv parser")
        val jsonArray = JSONArray(responseBody)
        val subjects = mutableListOf<Subject>()

        for (i in 0 until jsonArray.length()) {
            val subjectObject = jsonArray.getJSONObject(i)
            val name = subjectObject.getString("fullName")
            val shortName = subjectObject.getString("shortName")
            subjects.add(Subject(name, shortName))
        }

        return subjects
    }
}