package edu.raf.plugins.teacher.parsers

import edu.raf.plugins.teacher.models.Subject

interface IParser<T> {
    fun parse(responseBody: String): List<T>
}
