package edu.raf.plugins.teacher.utils

import io.sentry.Sentry

object SentryInitializer {

    @Volatile
    private var initialized = false

    fun init() {
        if (initialized) return
        synchronized(this) {
            if (initialized) return
            Sentry.init { options ->
                options.dsn = ConfigLoader.get("sentry.dsn")
                options.isDebug = true
            }
            initialized = true
        }
    }
}
