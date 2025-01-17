package edu.raf.plugins.teacher.parsers

import edu.raf.plugins.teacher.models.Subject

interface ISubjectParser {
    fun parse(responseBody: String): List<Subject>
}
