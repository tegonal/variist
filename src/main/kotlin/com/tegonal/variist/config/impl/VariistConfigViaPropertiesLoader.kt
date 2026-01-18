package com.tegonal.variist.config.impl

import ch.tutteli.kbox.blankToNull
import ch.tutteli.kbox.takeIf
import com.tegonal.variist.config.*
import com.tegonal.variist.config.impl.VariistPropertiesParser.Companion.ERROR_DEADLINES_PREFIX
import com.tegonal.variist.utils.impl.checkIsNotBlank
import com.tegonal.variist.utils.impl.checkIsPositive
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.io.path.appendText
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class VariistConfigViaPropertiesLoader {
	val config: VariistConfig by lazy {
		val parser = VariistPropertiesParser()
		val initialConfig = VariistConfig()
		val builder = initialConfig.toBuilder()
		val variistPropertiesLoaderConfig = VariistPropertiesLoaderConfig()
		ConfigBuilderAndLoaderConfig(builder, variistPropertiesLoaderConfig)
			.setByVariistPropertiesFile(parser, initialConfig)
			.setByEnv()
			.setByVariistLocalPropertiesFile(parser)
			.checkInvariantsAndDeadlinesPrintValues(initialConfig)
			.build()
	}

	private fun ConfigBuilderAndLoaderConfig.setByVariistPropertiesFile(
		parser: VariistPropertiesParser,
		initialConfig: VariistConfig
	) = apply {
		setByPropertiesFileInResource("/variist.properties", prefix = null, parser).also { exists ->
			if (exists) {
				checkValuesNotFixedShouldBeDoneInLocalProperties(initialConfig)
			}
		}
	}

	private fun ConfigBuilderAndLoaderConfig.checkValuesNotFixedShouldBeDoneInLocalProperties(
		initialConfig: VariistConfig
	) {
		val errorMessageNotAllowedToModify = { what: String ->
			"You are not allowed to modify $what via variist.properties use ${
				getLocalPropertiesResourceNameAndPrefix(loaderConfig).first
			} to fix a seed"
		}
		check(builder.seed == initialConfig.seed.value) {
			errorMessageNotAllowedToModify("seed")
		}
		check(builder.skip == initialConfig.skip) {
			errorMessageNotAllowedToModify("skip")
		}
		check(builder.requestedMinArgs == initialConfig.requestedMinArgs) {
			errorMessageNotAllowedToModify("requestedMinArgs")
		}
		check(builder.maxArgs == initialConfig.maxArgs) {
			errorMessageNotAllowedToModify("maxArgs")
		}
	}

	private fun ConfigBuilderAndLoaderConfig.setByVariistLocalPropertiesFile(
		parser: VariistPropertiesParser
	) = apply {
		loaderConfig.checkVariistConfigLoaderConfigInvariants()

		val (localPropertiesResourceName, prefix) = getLocalPropertiesResourceNameAndPrefix(loaderConfig)

		setByPropertiesFileInResource("/$localPropertiesResourceName", prefix = prefix, parser).let { exists ->
			if (exists &&
				loaderConfig.localPropertiesDir.resolve(localPropertiesResourceName).exists().not()
			) {
				error("$localPropertiesResourceName was found via classloader but is not defined in ${loaderConfig.localPropertiesDir}. Adjust ${VariistPropertiesLoaderConfig::localPropertiesDir.name} accordingly.")
			}
		}
	}

	private fun getLocalPropertiesResourceNameAndPrefix(variistPropertiesLoaderConfig: VariistPropertiesLoaderConfig): Pair<String, String?> {
		val localPropertiesResourceName =
			variistPropertiesLoaderConfig.localPropertiesResourceName ?: "variist.local.properties"

		val prefix = variistPropertiesLoaderConfig.localPropertiesPrefix?.let {
			if (it.endsWith(".")) it else "$it."
		}
		return localPropertiesResourceName to prefix
	}

	private fun ConfigBuilderAndLoaderConfig.checkInvariantsAndDeadlinesPrintValues(
		initialConfig: VariistConfig
	) = apply {
		loaderConfig.checkVariistConfigLoaderConfigInvariants()

		val fixedSeed = builder.seed != initialConfig.seed.value

		println("Variist${if (fixedSeed) " fixed" else ""} seed ${builder.seed} ${if (builder.skip != null) "skipping ${builder.skip} " else ""}in env ${builder.activeEnv}")

		val (localPropertiesResourceName, prefix) = getLocalPropertiesResourceNameAndPrefix(loaderConfig)
		with(loaderConfig) {
			val projectRootDir = Paths.get("").toAbsolutePath().normalize()
			val localPropertiesPath =
				localPropertiesDir.resolve(localPropertiesResourceName)
					.toAbsolutePath()
					.normalize()
			check(localPropertiesPath.startsWith(projectRootDir)) {
				"localPropertiesPath (l) must be within the projects root directory (p)\nl: $localPropertiesPath\np: $projectRootDir"
			}
			val nonNullPrefix = prefix ?: ""
			checkDeadline(localPropertiesPath, nonNullPrefix, builder.seed.takeIf { fixedSeed }, "seed")
			checkDeadline(localPropertiesPath, nonNullPrefix, builder.skip, "skip")
			checkDeadline(localPropertiesPath, nonNullPrefix, builder.maxArgs, "maxArgs")
			checkDeadline(localPropertiesPath, nonNullPrefix, builder.requestedMinArgs, "requestedMinArgs")
		}
	}

	private fun VariistPropertiesLoaderConfig.checkVariistConfigLoaderConfigInvariants() {
		localPropertiesResourceName?.let {
			checkIsNotBlank(it, "localPropertiesResourceName")
			// although this file could be in a different directory we don't want that confusion
			check(it != "variist.properties") {
				"you are not allowed to define \"variist.properties\" as ${VariistPropertiesLoaderConfig::localPropertiesResourceName.name}"
			}
		}

		localPropertiesPrefix?.let {
			checkIsNotBlank(it, "localPropertiesPrefix")
		}

		check((localPropertiesResourceName == null) == (localPropertiesPrefix == null)) {
			"Either you define both ${VariistPropertiesLoaderConfig::localPropertiesResourceName.name} (${localPropertiesResourceName}) and ${VariistPropertiesLoaderConfig::localPropertiesPrefix.name} (${localPropertiesPrefix}) or none."
		}

		checkIsPositive(
			remindAboutFixedPropertiesAfterMinutes,
			"remindAboutFixedPropertiesAfterMinutes"
		)
	}

	private fun VariistPropertiesLoaderConfig.checkDeadline(
		localPropertiesPath: Path,
		localPropertiesPrefix: String,
		propertyValue: Any?,
		propertyName: String,
	) {
		val definedDeadline = errorDeadlines[propertyName]
		val deadlinePropertyName = deadlinePropertyName(localPropertiesPrefix, propertyName)
		if (propertyValue == null) {
			if (definedDeadline != null) {
				localPropertiesPath.unsetErrorDeadlineFor(deadlinePropertyName, localPropertiesPrefix)
			}
		} else {
			if (definedDeadline == null) {
				localPropertiesPath.setErrorDeadlineFor(
					propertyName,
					localPropertiesPrefix,
					LocalDateTime.now().plusMinutes(remindAboutFixedPropertiesAfterMinutes.toLong())
				)
			} else if (definedDeadline.isBefore(LocalDateTime.now())) {
				throw VariistDeadlineException(
					"""
                        |$propertyName is still set (is $propertyValue) and $deadlinePropertyName (which is $definedDeadline) passed.
                        |Either:
						|a) remove/comment out the property `$propertyName`
						|b) remove $deadlinePropertyName (in which case a new deadline is set)
						|c) set $deadlinePropertyName manually to a later date/time
						|The adjustments need to be made in the following file:
                        |${localPropertiesPath.toUri()}
                        |
                        """.trimMargin()
				)
			}
		}
	}

	private fun Path.setErrorDeadlineFor(
		propertyName: String,
		localPropertiesPrefix: String,
		deadline: LocalDateTime
	) {
		appendText(
			"""
            |
            |# You have set `$localPropertiesPrefix$propertyName` and this deadline will remind you to remove it again.
            |${
				deadlinePropertyName(
					localPropertiesPrefix,
					propertyName
				)
			}=${deadline.format(DateTimeFormatter.ISO_DATE_TIME)}
			|
            """.trimMargin()
		)
	}

	private fun deadlinePropertyName(localPropertiesPrefix: String, propertyName: String): String {
		val deadlinePropertyName = "$localPropertiesPrefix$ERROR_DEADLINES_PREFIX$propertyName"
		return deadlinePropertyName
	}

	private fun Path.unsetErrorDeadlineFor(deadlinePropertyName: String, localPropertiesPrefix: String) {
		replaceText {
			it.replace(Regex("\n(#.*\n)*${localPropertiesPrefix}${deadlinePropertyName}=.*"), "")
		}
	}

	private fun Path.replaceText(replace: (String) -> String) {
		val content = replace(readText())
		writeText(content)
	}

	private fun ConfigBuilderAndLoaderConfig.setByPropertiesFileInResource(
		propertiesFile: String,
		prefix: String?,
		parser: VariistPropertiesParser
	): Boolean = this::class.java.getResourceAsStream(propertiesFile)?.also {
		it.use { input ->
			val props = Properties()
			props.load(input)
			parser.mergePropertiesInto(props, this, prefix)
		}
	} != null

	private fun ConfigBuilderAndLoaderConfig.setByEnv() = apply {
		builder.determineEnv()?.also { builder.activeEnv = it }
	}

	private fun VariistConfigBuilder.determineEnv(): String? =
		System.getenv("VARIIST_ENV") ?: run {
			val envs = testProfiles.firstOrNull { it.first == defaultProfile }?.second?.map { it.first }?.toSet()
				?: error("profile $defaultProfile does not exist")

			// only determine envs if at least one standard env is defined (as others we don't know how to map)
			takeIf(Env.entries.any { it.name in envs }) {
				determineEnvBasedOnGitHubActions()
					?: determineEnvBasedOnGitLab()
					?: determineEnvBasedOnBitBucket()
			}?.let { it.name.takeIf { env -> env in envs } }
		}

	private fun determineEnvBasedOnGitHubActions(): Env? =
		System.getenv("GITHUB_EVENT_NAME")?.blankToNull()?.let { event ->
			when (event) {
				"pull_request" -> determinePrEnv(getGithubEnv("GITHUB_BASE_REF"))
				"push" -> determinePushEnv(getGithubEnv("GITHUB_REF_NAME"))
				else -> null
			}
		}

	private fun getGithubEnv(envName: String): String =
		System.getenv(envName) ?: error("$envName is not set but should in a github-action")

	private fun determineEnvBasedOnGitLab(): Env? =
		determineBasedOnMrAndPushEnv("CI_MERGE_REQUEST_TARGET_BRANCH_NAME", "CI_COMMIT_BRANCH")

	private fun determineEnvBasedOnBitBucket(): Env? =
		determineBasedOnMrAndPushEnv("BITBUCKET_PR_DESTINATION_BRANCH", "BITBUCKET_BRANCH")

	private fun determineBasedOnMrAndPushEnv(
		mergeRequestTargetBranchEnvName: String,
		pushBranchEnvName: String,
	): Env? =
		System.getenv(mergeRequestTargetBranchEnvName)?.blankToNull()?.let {
			determinePrEnv(it)
		} ?: System.getenv(pushBranchEnvName)?.blankToNull()?.let {
			determinePushEnv(it)
		}

	private fun determinePrEnv(targetBranch: String): Env =
		when {
			targetBranch.startsWith("hotfix/") -> Env.HotfixPR
			else -> Env.PR
		}

	private fun determinePushEnv(branch: String): Env =
		when {
			branch == "main" -> Env.Main
			branch == "test" -> Env.DeployTest
			branch == "int" -> Env.DeployInt
			branch.startsWith("hotfix/") -> Env.Hotfix
			branch.startsWith("release/") -> Env.Release
			else -> Env.Push
		}
}
