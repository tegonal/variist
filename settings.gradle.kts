pluginManagement {
	repositories {
//        mavenLocal()
		gradlePluginPortal()
	}
	includeBuild("gradle/build-logic")
	includeBuild("gradle/build-logic-conventions")
	includeBuild("gradle/code-generation")
}

dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		mavenCentral()
	}
}

rootProject.name = "variist"

includeProject("misc/tools", "readme-examples")

fun Settings.includeProject(subPath: String, projectName: String) {
	val dir = file("$rootDir/$subPath/$projectName")
	require(dir.isDirectory) {
		"Cannot include project `$projectName` because its projectDir file://$dir does not exist."
	}
	include(projectName)
	project(":$projectName").projectDir = dir
}
