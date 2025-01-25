package edu.raf.plugins.teacher.services

import edu.raf.plugins.teacher.models.StudentSolution
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
        endpoint == "/subjects" -> {
            println("Endpoint je /subjects")
            SubjectParser() as IParser<T>
        }
        endpoint.matches(Regex("/professor/tests/subjects/[^/]+/years/[^/]+/types/[^/]+/groups")) -> {
            println("Endpoint je za grupe - StudentSolutionParser")
            StudentSolutionParser() as IParser<T>
        }

        endpoint.matches(Regex("/professor/tests/subjects/[^/]+/years/[^/]+/types(/.*)?")) -> {
            println("Endpoint je za vrste testova")
            JSONToStringListParser() as IParser<T>
        }
        endpoint.matches(Regex("/professor/tests/subjects/[^/]+/years(/.*)?")) -> {
            println("Endpoint je za godine")
            JSONToStringListParser() as IParser<T>
        }

        endpoint.matches(Regex("/professor/tests/subjects(/.*)?")) -> {
            println("Endpoint je /professor/tests/subjects")
            JSONToStringListParser() as IParser<T>
        }


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
    fun getGroupsForExamPerYearForSubjectOnServer(subjectName: String, year:String, examName: String): List<StudentSolution> {

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
