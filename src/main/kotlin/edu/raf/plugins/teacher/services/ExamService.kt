package edu.raf.plugins.teacher.services

import edu.raf.plugins.teacher.utils.ApiClient
import edu.raf.plugins.teacher.utils.ConfigLoader
import org.json.JSONObject
import java.io.IOException
import edu.raf.plugins.teacher.models.Directory

class ExamService {

    private val apiClient = ApiClient(
        ConfigLoader.get("api.url"),
        ConfigLoader.get("api.token")
    )

    @Throws(IOException::class)
    fun createDirectory(subject: String, year: String, testType: String, group: String): Directory {
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

        return parseResponse(responseBody)
    }


    private fun parseResponse(responseBody: String): Directory {
        try {
            val jsonObject = JSONObject(responseBody)
            val fullPath = jsonObject.getString("fullPath")
            val basePath = jsonObject.getString("basePath")
            val status = jsonObject.getString("status")
            val gitInitialized = jsonObject.getString("gitInitialized")


            return Directory(
                fullPath = fullPath,
                basePath = basePath,
                status = status,
                gitInitialized = gitInitialized
            )
        } catch (e: Exception) {
            throw IOException("Greška pri parsiranju odgovora: ${e.message}", e)
        }
    }
}
