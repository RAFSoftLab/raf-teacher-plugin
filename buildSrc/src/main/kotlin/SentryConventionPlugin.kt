import io.sentry.android.gradle.extensions.SentryPluginExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class SentryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        pluginManager.apply("io.sentry.jvm.gradle")

        extensions.configure(SentryPluginExtension::class.java) {
            it.includeSourceContext.set(true)
            it.org.set("raf-2p")
            it.projectName.set("teacher-plugin")
            it.authToken.set(providers.environmentVariable("SENTRY_AUTH_TOKEN"))
        }
    }
}
