package edu.raf.plugins.teacher.services

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

class APIClient {
    private val client = OkHttpClient()

    // Metod za kreiranje i slanje GET zahteva
    fun sendGetRequest(url: String, token: String): Response? {
        val request = Request.Builder()
            .url(url.toHttpUrlOrNull()!!)
            .addHeader("Authorization", "Bearer $token")
            .build()

        return try {
            client.newCall(request).execute()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}