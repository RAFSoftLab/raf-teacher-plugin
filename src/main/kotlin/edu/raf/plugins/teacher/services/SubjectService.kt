package edu.raf.plugins.teacher.services

import edu.raf.plugins.teacher.models.Subject
import edu.raf.plugins.teacher.parsers.IParser
import edu.raf.plugins.teacher.parsers.JSONToStringListParser
import edu.raf.plugins.teacher.parsers.StudentSolutionParser
import edu.raf.plugins.teacher.parsers.SubjectParser
import edu.raf.plugins.teacher.utils.ApiClient
import edu.raf.plugins.teacher.utils.ConfigLoader
import java.io.IOException

class SubjectService {

    private val apiClient = ApiClient(
        ConfigLoader.get("api.url"),
        ConfigLoader.get("api.token")
    )

    private fun <T> getParser(endpoint: String): IParser<T> = when {
        // Prepoznajemo statički endpoint "/subjects"
        endpoint == "/subjects" -> SubjectParser() as IParser<T>

        // Prepoznajemo endpoint "/professor/tests/subjects" i sve njegove podputanje
        endpoint.matches(Regex("/professor/tests/subjects(/.*)?")) -> JSONToStringListParser() as IParser<T>

        // Prepoznajemo endpoint sa dinamičkim delovima za "/professor/tests/subjects/{OOP}/years"
        endpoint.matches(Regex("/professor/tests/subjects/[^/]+/years(/.*)?")) -> JSONToStringListParser() as IParser<T>

        // Prepoznajemo endpoint sa još dubljim dinamičkim delovima
        endpoint.matches(Regex("/professor/tests/subjects/[^/]+/years/[^/]+/types(/.*)?")) -> JSONToStringListParser() as IParser<T>

        // Prepoznajemo specifičan endpoint "/professor/tests/subjects/{OOP}/years/{2024_25}/types/{gif4}/groups"
        endpoint.matches(Regex("/professor/tests/subjects/[^/]+/years/[^/]+/types/[^/]+/groups")) -> StudentSolutionParser() as IParser<T> //

        // Default slučaj za neprepoznate endpoint-e
        else -> throw IllegalArgumentException("Nepoznat endpoint: $endpoint")
    }



    @Throws(IOException::class) // Naglašava da metoda može baciti izuzetak
    fun getSubjects(): List<Subject> {
        val endpoint = "/subjects"
        val responseBody = apiClient.get(endpoint)
            ?: throw IOException("Nije moguće dobiti odgovor sa servera.")

        return parseSubjects(responseBody, endpoint)
    }

    @Throws(IOException::class) // Naglašava da metoda može baciti izuzetak
    fun getSubjectsOnServer(): List<String> {
        val endpoint = "/professor/tests/subjects"
        val responseBody = apiClient.get(endpoint)
            ?: throw IOException("Nije moguće dobiti odgovor sa servera.")

        return parseSubjects(responseBody, endpoint)
    }

    @Throws(IOException::class) // Naglašava da metoda može baciti izuzetak
    fun getYearsForSubjectOnServer(subjectName: String): List<String> {
        val endpoint = "/professor/tests/subjects/${subjectName}/years"
        val responseBody = apiClient.get(endpoint)
            ?: throw IOException("Nije moguće dobiti odgovor sa servera.")

        return parseSubjects(responseBody, endpoint)
    }

    @Throws(IOException::class) // Naglašava da metoda može baciti izuzetak
    fun getExamsPerYearForSubjectOnServer(subjectName: String, year:String): List<String> {
        val endpoint = "/professor/tests/subjects/${subjectName}/years/${year}/types"
        val responseBody = apiClient.get(endpoint)
            ?: throw IOException("Nije moguće dobiti odgovor sa servera.")

        return parseSubjects(responseBody, endpoint)
    }


    @Throws(IOException::class) // Naglašava da metoda može baciti izuzetak
    fun getGroupsForExamPerYearForSubjectOnServer(subjectName: String, year:String, examName: String): List<String> {

        val endpoint = "/professor/tests/subjects/${subjectName}/years/${year}/types/${examName}/groups"



        val responseBody = apiClient.get(endpoint)
            ?: throw IOException("Nije moguće dobiti odgovor sa servera.")

        return parseSubjects(responseBody, endpoint)
    }



//    private fun parseSubjects(responseBody: String): List<Subject> {
//        return try {
//
//            val jsonArray = JSONArray(responseBody)
//            val subjects = mutableListOf<Subject>()
//
//            for (i in 0 until jsonArray.length()) {
//                val subjectObject = jsonArray.getJSONObject(i)
//                val name = subjectObject.getString("fullName")
//                val shortName = subjectObject.getString("shortName")
//                val subject = Subject(name, shortName)
//                subjects.add(subject)
//            }
//
//            subjects
//        } catch (e: Exception) {
//            throw IOException("Greška pri parsiranju JSON-a: ${e.message}", e)
//        }
//    }

    private fun <T> parseSubjects(responseBody: String, endpoint: String): List<T> {
        val parser = getParser<T>(endpoint)
        return parser.parse(responseBody)
    }

}
