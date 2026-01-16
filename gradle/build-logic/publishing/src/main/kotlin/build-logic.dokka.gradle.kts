import ch.tutteli.gradle.plugins.dokka.GhPages
import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask

plugins {
	id("build-logic.gradle-conventions")
	id("ch.tutteli.gradle.plugins.dokka")
}

val kdocDir = rootProject.projectDir.resolve("misc/kdoc")

tutteliDokka {
	githubUser.set("tegonal")
	writeTo.set(GhPages)
}

tasks.configureEach<AbstractDokkaLeafTask> {
	moduleName.set("Variist")
	dokkaSourceSets.configureEach {
		reportUndocumented.set(true)
		jdkVersion.set(buildParameters.defaultJdkVersion)
		includes.from(kdocDir.resolve("packages.md"))
		perPackageOption {
			matchingRegex.set("com\\.tegonal\\.variist\\.export\\..*")
			suppress.set(true)
		}
	}
	configurePlugins()
}
