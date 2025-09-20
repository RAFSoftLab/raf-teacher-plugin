package edu.raf.plugins.teacher.responseparsers

import io.sentry.Sentry
import org.json.JSONArray
import org.json.JSONException

class JSONToStringListParser : IParser<String> {
    override fun parse(responseBody: String): List<String> {
        return try {
            val jsonArray = JSONArray(responseBody)
            List(jsonArray.length()) { i ->
                jsonArray.getString(i) // Vraća samo stringove umesto Subject objekata
            }
        } catch (e: JSONException) {
            Sentry.captureException(e)
            // Ako dođe do greške, vraća praznu listu stringova
            emptyList<String>()
        }
    }

}