package edu.raf.plugins.teacher.responseparsers

interface IParser<T> {
    fun parse(responseBody: String): List<T>
}
