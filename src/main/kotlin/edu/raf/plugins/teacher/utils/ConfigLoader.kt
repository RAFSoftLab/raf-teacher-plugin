package edu.raf.plugins.teacher.utils

import io.sentry.Sentry
import java.util.Properties

object ConfigLoader {
    private val properties = Properties()

    init {
        runCatching {
            val inputStream = ConfigLoader::class.java.classLoader.getResourceAsStream("config.properties")
                ?: throw IllegalStateException("Konfiguracioni fajl 'config.properties' nije pronađen!")
            properties.load(inputStream)
        }.onFailure { e ->
            Sentry.captureException(e)
        }.getOrThrow()
    }

    fun get(key: String): String {
        return runCatching {
            properties.getProperty(key)
                ?: throw IllegalArgumentException("Konfiguracioni ključ '$key' nije pronađen!")
        }.onFailure { e ->
            Sentry.captureException(e)
        }.getOrThrow()
    }
}
