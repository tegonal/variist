package com.tegonal.variist.config

/**
 * @since 2.0.0
 */
operator fun TestProfiles.contains(testType: TestType) = contains(testType.name)

/**
 * @since 2.0.0
 */
operator fun TestProfiles.get(testType: TestType, env: Env) = get(testType.name, env.name)

/**
 * Predefined test type names (e.g. to use as profile names for [TestProfiles]).
 *
 * @since 2.0.0
 */
enum class TestType {
	Unit,
	Integration,
	E2E,
	SystemIntegration,
	;

	/**
	 * Helper constants so that you can use them in [com.tegonal.variist.providers.ArgsSourceOptions].
	 */
	@Suppress("ConstPropertyName")
	object ForAnnotation {
		const val Unit = "Unit"
		const val Integration = "Integration"
		const val E2E = "E2E"
		const val SystemIntegration = "SystemIntegration"
	}
}

/**
 * Predefined environment names.
 *
 * The following descriptions are just suggestions, you can interpret them as you wish.
 *
 * @since 2.0.0
 */
enum class Env {
	/**
	 * Running tests on a local machine
	 */
	Local,

	/**
	 * Running tests on a push to a branch but only if neither to branch `main`, `test`, `int`
	 * nor to hotfix/.. or release/...
	 *
	 * E.g. a push to feature/..., bugfix/...,
	 */
	Push,

	/**
	 * Running tests in a PR pipeline which shall be merged (eventually back to the main branch)
	 */
	PR,

	/**
	 * Running tests on a push to main/ (e.g. PR was merged to main)
	 */
	Main,

	/**
	 * Running tests as part of a deployment to the Test staging environment, by convention on `the` test branch.
	 */
	DeployTest,

	/**
	 * Running tests as part of a deployment to the Int staging environment, by convention on the `int`  branch.
	 */
	DeployInt,

	/**
	 * Running tests as a nightly job on the Test staging environment.
	 */
	NightlyTest,

	/**
	 * Running tests as a nightly job on the Int staging environment.
	 */
	NightlyInt,

	/**
	 * Running tests in a PR pipeline which shall be merged to a hotfix/ branch
	 */
	HotfixPR,

	/**
	 * Running tests on a push to hotfix/ (e.g. once a hotfix PR is merged back to a hotfix/ branch).
	 */
	Hotfix,

	/**
	 * Running tests on a release/ branch.
	 */
	Release
	;
}

