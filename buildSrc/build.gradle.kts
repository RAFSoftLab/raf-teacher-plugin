plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("io.sentry:sentry-android-gradle-plugin:3.14.0")
}

gradlePlugin {
    plugins {
        create("sentryConvention") {
            id = "sentry-convention"
            implementationClass = "SentryConventionPlugin"
        }
    }
}
