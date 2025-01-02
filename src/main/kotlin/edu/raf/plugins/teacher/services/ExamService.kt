package edu.raf.plugins.teacher.services

import edu.raf.plugins.teacher.utils.ApiClient
import edu.raf.plugins.teacher.utils.ConfigLoader
import org.json.JSONObject
import java.io.IOException

class ExamService {

    private val apiClient = ApiClient(
        ConfigLoader.get("api.url"),
        ConfigLoader.get("api.token")
    )

    @Throws(IOException::class)
    fun createExam(subject: String, year: String, testType: String, group: String) {
        val endpoint = "/directories/create"
        val jsonBody = """
            {
                "subject": "$subject",
                "year": "$year",
                "testType": "$testType",
                "group": "$group"
            }
        """.trimIndent()

        val responseBody = apiClient.post(endpoint, jsonBody)
            ?: throw IOException("Nije moguće dobiti odgovor sa servera.")

        parseAndPrintResponse(responseBody)
    }

    private fun parseAndPrintResponse(responseBody: String) {
        try {
            val jsonObject = JSONObject(responseBody)
            val fullPath = jsonObject.getString("fullPath")
            val basePath = jsonObject.getString("basePath")
            val status = jsonObject.getString("status")

            println("Full Path: $fullPath")
            println("Full Path: $basePath")
            println("Status: $status")
        } catch (e: Exception) {
            throw IOException("Greška pri parsiranju odgovora: ${e.message}", e)
        }
    }
}
