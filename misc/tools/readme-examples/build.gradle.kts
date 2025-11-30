plugins {
	id("build-logic.kotlin-jvm")
}

description = "Runs examples, includes the code and the output in README.md"

kotlin {
	sourceSets {
		main {
			dependencies {
				implementation(project.rootProject)
				implementation(libs.atrium.fluent)
			}
		}
		val kotlinVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.DEFAULT.version
		configureEach {
			languageSettings.apply {
				languageVersion = kotlinVersion
				apiVersion = kotlinVersion
			}
		}
	}
}

tasks.test.configure {
	doFirst {
		val version = rootProject.version.toString()
		environment("README_SOURCETREE", if (version.endsWith("-SNAPSHOT")) "tree/main" else "tree/v$version")
	}
}
