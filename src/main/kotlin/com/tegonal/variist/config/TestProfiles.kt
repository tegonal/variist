package com.tegonal.variist.config

import com.tegonal.variist.config.impl.DefaultTestProfiles

/**
 * A collection of [TestConfig]s grouped by environment and profile name.
 *
 * I.e. a data structure which maps profile names to [TestConfig] per environment.
 *
 * @since 2.0.0
 */
interface TestProfiles {
	/**
	 * Indicates if the given [profileName] is part of this collection or not.
	 */
	operator fun contains(profileName: String): Boolean

	/**
	 * Returns the [TestConfig] of the given [profileName] and [env].
	 */
	fun get(profileName: String, env: String): TestConfig

	/**
	 * Returns the [TestConfig] of the given [profileName] and [env] or `null` in case the profile or
	 * environment does not exist.
	 */
	fun find(profileName: String, env: String): TestConfig?

	companion object {
		/**
		 * Creates a [TestProfiles] based on the given [profiles] which allows to
		 * specify custom category names.
		 *
		 * Also take a look at the overload which expects one of the predefined [TestType] as category names.
		 */
		fun create(profiles: Map<String, Map<String, TestConfig>>): TestProfiles =
			DefaultTestProfiles(profiles)
	}

	/**
	 * Returns all specified profile names.
	 */
	fun profileNames(): Set<String>

	/**
	 * Returns all specified environments for the given [profileName] and throws in case the profile name is
	 * not specified in this collection.
	 *
	 * @throws IllegalStateException if the given [profileName] is not part of this collection.
	 */
	fun envs(profileName: String): Set<String>


	/**
	 * Returns a copy of this collection as a [MutableMap] where the keys are the profile names
	 * and the values is again a [MutableMap] where the keys the envs and the values the associated [TestConfig].
	 */
	fun toMutableMap(): MutableMap<String, MutableMap<String, TestConfig>>
}
