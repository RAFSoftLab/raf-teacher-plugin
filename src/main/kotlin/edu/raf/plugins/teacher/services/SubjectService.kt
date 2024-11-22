package edu.raf.plugins.teacher.services

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.IOException
import javax.swing.JOptionPane

class SubjectService {

    private val client = OkHttpClient()

    fun getSubjects(): List<String> {
        return try {
            val request = Request.Builder()
                .url("http://192.168.124.28:8091/api/v1/subjects")
                .addHeader(
                    "Authorization",
                    "Bearer L2aTA643Z0UJ43bIdBymFExVbpqZg7v5QJafYh6KFRjl04eV6w4TtdppkX41hEwo"
                )
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                parseSubjects(responseBody)
            } else {
                println("API returned an error: ${response.code} ${response.message}")
                emptyList()
            }
        } catch (e: IOException) {
            // Prikazuje pop-up prozor u slučaju greške povezivanja
            JOptionPane.showMessageDialog(
                null,
                "Nije moguće povezati se na server. Proverite Vašu mrežnu konekciju.",
                "Greška pri povezivanju",
                JOptionPane.ERROR_MESSAGE
            )
            e.printStackTrace()
            emptyList()
        } catch (e: Exception) {
            // Ostale greške (npr. parsiranje)
            println("Unexpected error: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
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

            println("Successfully parsed subjects: $subjects")
            subjects
        } catch (e: Exception) {
            println("Error parsing JSON: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
}
