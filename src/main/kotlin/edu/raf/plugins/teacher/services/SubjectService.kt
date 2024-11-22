package edu.raf.plugins.teacher.services
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.IOException

class SubjectService {

    private val client = OkHttpClient()

    fun getSubjects(): List<String> {
        return try {

            val request = Request.Builder()
                .url("http://192.168.124.28:8091/api/v1/subjects")
                .addHeader("Authorization", "Bearer L2aTA643Z0UJ43bIdBymFExVbpqZg7v5QJafYh6KFRjl04eV6w4TtdppkX41hEwo")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            println("Response: $responseBody") // Ispisuje ceo JSON odgovor

            if (response.isSuccessful && responseBody != null) {
                val jsonArray = JSONArray(responseBody)
                val subjects = mutableListOf<String>()

                println("Number of subjects: ${jsonArray.length()}") // Broj elemenata u nizu

                for (i in 0 until jsonArray.length()) {
                    val subjectObject = jsonArray.getJSONObject(i)
                    println("Subject object: $subjectObject") // Ispisuje ceo objekat

                    val name = subjectObject.getString("fullName")
                    println("Subject name: $name") // Ispisuje ime predmeta
                    subjects.add(name)
                }

                println("Subjects list: $subjects") // Ispisuje listu svih imena
                subjects
            } else {
                println("API error: ${response.code}") // Ispisuje kod greške ako API ne uspe
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("Exception occurred: ${e.message}") // Ispisuje grešku
            emptyList()
        }
    }

}