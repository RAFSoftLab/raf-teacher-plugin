package edu.raf.plugins.teacher.utils

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException


class ApiClient(private val baseUrl: String, private val token: String) {

    private val client = OkHttpClient()

    @Throws(IOException::class)
    fun get(endpoint: String): String? {
        val request = Request.Builder()
            .url("$baseUrl$endpoint")
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build()

        return try {
            val response = client.newCall(request).execute()

            // Logovanje statusa i tela odgovora
            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: "No response body"
                println("Error: ${response.code} - $errorBody")
                return null
            }

            response.body?.string()
        } catch (e: IOException) {
            println("Network error: ${e.message}")
            throw e
        }
    }

    @Throws(IOException::class)
    fun post(endpoint: String, jsonBody: String): String {
        val url = "$baseUrl$endpoint"
        val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jsonBody)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .header("Authorization", "Bearer $token")
            .header("Content-Type", "application/json")
            .build()
        val response: Response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IOException("Neuspešan zahtev: ${response.code}")
        }
        return response.body?.string() ?: throw IOException("Prazan odgovor od servera.")
    }
}