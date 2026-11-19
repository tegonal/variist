package com.tegonal.variist.config.impl

import com.tegonal.variist.config.TestConfig
import com.tegonal.variist.config.TestProfiles
import com.tegonal.variist.utils.impl.checkIsNotBlank

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class DefaultTestProfiles(profiles: Map<String, Map<String, TestConfig>>) : TestProfiles {
	private val profiles: Map<String, Map<String, TestConfig>> = buildMap {
		check(profiles.isNotEmpty()) {
			"You need to define at least one test profile"
		}

		profiles.forEach { (profile, testConfigPerEnv) ->
			checkIsNotBlank(profile, "profile")
			check(testConfigPerEnv.isNotEmpty()) {
				"You need to define at least one environment in test profile $profile"
			}
			testConfigPerEnv.keys.forEach { env ->
				checkIsNotBlank(env, "env in profile $profile")
			}
			put(profile, testConfigPerEnv.toMap())
		}
	}

	override operator fun contains(profileName: String) = profiles.contains(profileName)

	override fun get(profileName: String, env: String): TestConfig {
		val envs = envsToTestConfigFor(profileName)
		return envs[env] ?: throw IllegalArgumentException(
			"env $env not defined in profile $profileName, available envs: ${
				envs.keys.joinToString(", ")
			}"
		)
	}

	override fun find(profileName: String, env: String): TestConfig? = profiles[profileName]?.get(env)

	override fun profileNames(): Set<String> = profiles.keys
	override fun envs(profileName: String): Set<String> =
		envsToTestConfigFor(profileName).keys

	private fun envsToTestConfigFor(profileName: String): Map<String, TestConfig> =
		profiles[profileName] ?: throw IllegalArgumentException(
			"profile $profileName not defined, available profiles: ${
				profiles.keys.joinToString(", ")
			}"
		)

	override fun toMutableMap(): MutableMap<String, MutableMap<String, TestConfig>> =
		HashMap<String, MutableMap<String, TestConfig>>(profiles.size)
			.also { it.putAll(profiles.entries.map { (k, v) -> k to HashMap(v) }) }
}
