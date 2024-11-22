package edu.raf.plugins.teacher.services

import edu.raf.plugins.teacher.utils.ConfigLoader
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.IOException
import javax.swing.JOptionPane
import okhttp3.Response

class SubjectService {

    private val client = OkHttpClient()
    private val apiUrl = ConfigLoader.get("api.url")
    private val apiToken = ConfigLoader.get("api.token")

    fun getSubjects(): List<String> {
        val request = Request.Builder()
            .url(apiUrl)
            .addHeader("Authorization", "Bearer $apiToken")
            .build()

        return try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                parseSubjects(responseBody)
            } else {
                println("API error: ${response.code}")
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Prikazuje pop-up greške
            JOptionPane.showMessageDialog(
                null,
                "Nije moguće povezati se na server. Proverite Vašu mrežnu konekciju.",
                "Greška pri povezivanju",
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