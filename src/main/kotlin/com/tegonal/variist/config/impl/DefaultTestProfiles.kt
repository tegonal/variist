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
			testConfigPerEnv.keys.forEach { it ->
				checkIsNotBlank(it, "env in profile $profile")
			}
			put(profile, testConfigPerEnv.toMap())
		}
	}

	override operator fun contains(profileName: String) = profiles.contains(profileName)

	override fun get(profileName: String, env: String): TestConfig =
		find(profileName, env) ?: throw IllegalArgumentException(
			"profile $profileName not defined, available profiles: ${
				profiles.keys.joinToString(", ")
			}"
		)

	override fun find(profileName: String, env: String): TestConfig? = profiles[profileName]?.get(env)

	override fun profileNames(): Set<String> = profiles.keys
	override fun envs(profileName: String): Set<String> = run {
		profiles[profileName] ?: error("profile $profileName does not exist")
	}.keys

	override fun toMutableList(): MutableList<Pair<String, MutableList<Pair<String, TestConfig>>>> =
		ArrayList<Pair<String, MutableList<Pair<String, TestConfig>>>>(profiles.size).also {
			it.addAll(profiles.entries.map { (k, v) ->
				k to ArrayList<Pair<String, TestConfig>>(v.size).also { envs ->
					envs.addAll(v.entries.map { (k, v) -> k to v })
				}
			})
		}
}
