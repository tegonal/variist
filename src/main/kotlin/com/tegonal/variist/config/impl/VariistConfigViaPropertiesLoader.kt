package com.tegonal.variist.config.impl

import ch.tutteli.kbox.blankToNull
import ch.tutteli.kbox.takeIf
import com.tegonal.variist.config.*
import com.tegonal.variist.config.impl.VariistPropertiesParser.Companion.ERROR_DEADLINES_PREFIX
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
		val configFileSpecifics = ConfigFileSpecifics()
		initialConfig.toBuilder()
			.apply {
				setByPropertiesInResource("/variist.properties", configFileSpecifics, parser).also { exists ->
					if (exists) {
						check(seed == initialConfig.seed.value) {
							errorMessageNotAllowedToModify("seed")
						}
						check(skip == initialConfig.skip) {
							errorMessageNotAllowedToModify("skip")
						}
						check(requestedMinArgs == initialConfig.requestedMinArgs) {
							errorMessageNotAllowedToModify("requestedMinArgs")
						}
						check(maxArgs == initialConfig.maxArgs) {
							errorMessageNotAllowedToModify("maxArgs")
						}
					}
				}
			}
			.apply { setByEnv() }
			.apply {
				setByPropertiesInResource("/variist.local.properties", configFileSpecifics, parser).let { exists ->
					if (exists &&
						configFileSpecifics.variistPropertiesDir.resolve("variist.local.properties").exists().not()
					) {
						error("variist.local.properties was found via classloader but is not defined in ${configFileSpecifics.variistPropertiesDir}. Adjust variistPropertiesDir accordingly")
					}
				}
			}
			.apply {
				val fixedSeed = seed != initialConfig.seed.value

				println("Variist${if (fixedSeed) " fixed" else ""} seed $seed ${if (skip != null) "skipping $skip " else ""}in env $activeEnv")

				with(configFileSpecifics) {
					checkIsPositive(
						remindAboutFixedPropertiesAfterMinutes,
						"remindAboutFixedPropertiesAfterMinutes"
					)
					val projectRootDir = Paths.get("").toAbsolutePath().normalize()
					val localPropertiesPath =
						configFileSpecifics.variistPropertiesDir.resolve("variist.local.properties")
							.toAbsolutePath()
							.normalize()
					check(localPropertiesPath.startsWith(projectRootDir)) {
						"localPropertiesPath (l) must be within the projects root directory (p)\nl: $localPropertiesPath\np: $projectRootDir"
					}
					checkDeadline(localPropertiesPath, seed.takeIf { fixedSeed }, "seed")
					checkDeadline(localPropertiesPath, skip, "skip")
					checkDeadline(localPropertiesPath, maxArgs, "maxArgs")
					checkDeadline(localPropertiesPath, requestedMinArgs, "requestedMinArgs")
				}
			}.build()

	}

	private fun errorMessageNotAllowedToModify(what: String) =
		"You are not allowed to modify $what via variist.properties use variist.local.properties to fix a seed"


	private fun ConfigFileSpecifics.checkDeadline(
		localPropertiesPath: Path,
		propertyValue: Any?,
		propertyName: String,
	) {
		val definedDeadline = errorDeadlines[propertyName]
		val deadlinePropertyName = """${ERROR_DEADLINES_PREFIX}${propertyName}"""
		if (propertyValue == null) {
			if (definedDeadline != null) {
				localPropertiesPath.unsetErrorDeadlineFor(deadlinePropertyName)
			}
		} else {
			if (definedDeadline == null) {
				localPropertiesPath.setErrorDeadlineFor(
					propertyName,
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
		deadline: LocalDateTime
	) {
		appendText(
			"""
            |
            |# You have set `$propertyName` and this deadline will remind you to remove it again.
            |${ERROR_DEADLINES_PREFIX}$propertyName=${deadline.format(DateTimeFormatter.ISO_DATE_TIME)}
			|
            """.trimMargin()
		)
	}

	private fun Path.unsetErrorDeadlineFor(deadlinePropertyName: String) {
		replaceText {
			it.replace(Regex("\n(#.*\n)*${deadlinePropertyName}=.*"), "")
		}
	}

	private fun Path.replaceText(replace: (String) -> String) {
		val content = replace(readText())
		writeText(content)
	}


	private fun VariistConfigBuilder.setByPropertiesInResource(
		propertiesFile: String,
		configFileSpecifics: ConfigFileSpecifics,
		parser: VariistPropertiesParser
	): Boolean = this::class.java.getResourceAsStream(propertiesFile)?.also {
		it.use { input ->
			val props = Properties()
			props.load(input)
			parser.mergeWithProperties(this, configFileSpecifics, props)
		}
	} != null

	private fun VariistConfigBuilder.setByEnv() {
		determineEnv()?.also { activeEnv = it }
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

/**
 * Contains properties which are not exposed via [VariistConfig] and are used during parsing a [VariistConfig]
 * file.
 *
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class ConfigFileSpecifics(
	/**
	 * Defines in about how many minutes the reminder triggers when fixing a property (such as
	 * [VariistConfigBuilder.seed], [VariistConfigBuilder.skip],
	 * [VariistConfigBuilder.requestedMinArgs], [VariistConfigBuilder.maxArgs]).
	 */
	var remindAboutFixedPropertiesAfterMinutes: Int = 60,
	var variistPropertiesDir: Path = Paths.get("./src/test/resources"),
	var errorDeadlines: HashMap<String, LocalDateTime> = HashMap(),
)
