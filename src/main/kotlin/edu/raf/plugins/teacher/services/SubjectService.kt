package edu.raf.plugins.teacher.services

import edu.raf.plugins.teacher.models.Subject
import edu.raf.plugins.teacher.parsers.SubjectOnServerParser
import edu.raf.plugins.teacher.parsers.SubjectParser
import edu.raf.plugins.teacher.utils.ApiClient
import edu.raf.plugins.teacher.utils.ConfigLoader
import org.json.JSONArray
import java.io.IOException

class SubjectService {

    private val apiClient = ApiClient(
        ConfigLoader.get("api.url"),
        ConfigLoader.get("api.token")
    )

    private fun getParser(endpoint: String) = when (endpoint) {
        "/subjects" -> SubjectParser()
        "/professor/tests/subjects" -> SubjectOnServerParser()
        else -> throw IllegalArgumentException("Nepoznat endpoint: $endpoint")
    }

    @Throws(IOException::class) // Naglašava da metoda može baciti izuzetak
    fun getSubjects(): List<Subject> {
        val endpoint = "/subjects"
        val responseBody = apiClient.get(endpoint)
            ?: throw IOException("Nije moguće dobiti odgovor sa servera.")

        return parseSubjects(responseBody, endpoint)
    }

    @Throws(IOException::class) // Naglašava da metoda može baciti izuzetak
    fun getSubjectsOnServer(): List<Subject> {
        val endpoint = "/professor/tests/subjects"
        val responseBody = apiClient.get(endpoint)
            ?: throw IOException("Nije moguće dobiti odgovor sa servera.")

        return parseSubjects(responseBody, endpoint)
    }

//    private fun parseSubjects(responseBody: String): List<Subject> {
//        return try {
//
//            val jsonArray = JSONArray(responseBody)
//            val subjects = mutableListOf<Subject>()
//
//            for (i in 0 until jsonArray.length()) {
//                val subjectObject = jsonArray.getJSONObject(i)
//                val name = subjectObject.getString("fullName")
//                val shortName = subjectObject.getString("shortName")
//                val subject = Subject(name, shortName)
//                subjects.add(subject)
//            }
//
//            subjects
//        } catch (e: Exception) {
//            throw IOException("Greška pri parsiranju JSON-a: ${e.message}", e)
//        }
//    }

    private fun parseSubjects(responseBody: String, endpoint: String): List<Subject> {
        val parser = getParser(endpoint)
        return parser.parse(responseBody)
    }
}
