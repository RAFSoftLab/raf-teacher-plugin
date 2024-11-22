package edu.raf.plugins.teacher.services

import edu.raf.plugins.teacher.utils.ConfigLoader
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.IOException
import javax.swing.JOptionPane
import okhttp3.Response


class SubjectService {

    private val apiClient = APIClient()
    private val apiUrl = ConfigLoader.get("api.url")
    private val apiToken = ConfigLoader.get("api.token")

    fun getSubjects(): List<String> {
        val response: Response? = apiClient.sendGetRequest(
            "http://192.168.124.28:8091/api/v1/subjects",
            "L2aTA643Z0UJ43bIdBymFExVbpqZg7v5QJafYh6KFRjl04eV6w4TtdppkX41hEwo"
        )

        return if (response != null && response.isSuccessful) {
            val responseBody = response.body?.string()
            parseSubjects(responseBody)
        } else {
            JOptionPane.showMessageDialog(
                null,
                "Greška pri povezivanju sa serverom. Proverite mrežnu konekciju.",
                "Greška",
                JOptionPane.ERROR_MESSAGE
            )
            emptyList()
        }
    }

    private fun parseSubjects(responseBody: String?): List<String> {
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
            println("Greška pri parsiranju JSON-a: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
}