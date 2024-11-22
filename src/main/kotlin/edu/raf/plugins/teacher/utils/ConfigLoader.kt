package edu.raf.plugins.teacher.utils

import java.util.Properties

object ConfigLoader {
    private val properties = Properties()

    init {
        val inputStream = ConfigLoader::class.java.classLoader.getResourceAsStream("config.properties")
            ?: throw IllegalStateException("Konfiguracioni fajl 'config.properties' nije pronađen!")
        properties.load(inputStream)
    }

    fun get(key: String): String {
        return properties.getProperty(key)
            ?: throw IllegalArgumentException("Konfiguracioni ključ '$key' nije pronađen!")
    }
}
