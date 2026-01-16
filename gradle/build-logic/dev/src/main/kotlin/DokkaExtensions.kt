import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import org.jetbrains.dokka.gradle.AbstractDokkaTask

fun AbstractDokkaTask.configurePlugins() {
	pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
		footerMessage = "Variist &copy; Copyright <a href=\"https://tegonal.com\">Tegonal Genossenschaft</a>"
	}
}
