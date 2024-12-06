package edu.raf.plugins.teacher.services

import edu.raf.plugins.teacher.utils.ApiClient
import edu.raf.plugins.teacher.utils.ConfigLoader
import org.json.JSONArray
import java.io.IOException

class SubjectService {

    private val apiClient = ApiClient(
        ConfigLoader.get("api.url"),
        ConfigLoader.get("api.token")
    )

    @Throws(IOException::class) // Naglašava da metoda može baciti izuzetak
    fun getSubjects(): List<String> {
        val responseBody = apiClient.get("/subjects")
            ?: throw IOException("Nije moguće dobiti odgovor sa servera.")

        return parseSubjects(responseBody)
    }

    private fun parseSubjects(responseBody: String): List<String> {
        return try {
            val jsonArray = JSONArray(responseBody)
            val subjects = mutableListOf<String>()

            for (i in 0 until jsonArray.length()) {
                val subjectObject = jsonArray.getJSONObject(i)
                val name = subjectObject.getString("fullName")
                subjects.add(name)
            }

            subjects
        } catch (e: Exception) {
            throw IOException("Greška pri parsiranju JSON-a: ${e.message}", e)
        }
    }
}
