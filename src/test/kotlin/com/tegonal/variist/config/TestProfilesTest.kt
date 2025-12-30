package com.tegonal.variist.config

import ch.tutteli.atrium.api.fluent.en_GB.message
import ch.tutteli.atrium.api.fluent.en_GB.messageToContain
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import kotlin.test.Test

class TestProfilesTest {
	@Test
	fun `errors on duplicate testTypes`() {
		expect {
			TestProfiles.create(
				TestType.Unit to listOf(
					Env.Local to TestConfig(maxArgs = 2),
				),
				TestType.Integration to listOf(
					Env.Local to TestConfig(maxArgs = 2),
				),
				TestType.Integration to listOf(
					Env.Local to TestConfig(maxArgs = 2),
				),
				TestType.E2E to listOf(
					Env.Local to TestConfig(maxArgs = 2),
				)
			)
		}.toThrow<IllegalArgumentException> {
			message.toEqual("Looks like you defined some profiles multiple times: Integration")
		}
	}

	@Test
	fun `errors on duplicate envs, fails fast`() {
		expect {
			TestProfiles.create(
				TestType.Unit to listOf(
					Env.Local to TestConfig(maxArgs = 1),
					Env.PR to TestConfig(maxArgs = 2),
					Env.PR to TestConfig(maxArgs = 3),
					Env.Local to TestConfig(maxArgs = 1),
				),
				TestType.Integration to listOf(
					Env.Local to TestConfig(maxArgs = 1),
					Env.PR to TestConfig(maxArgs = 2),
					Env.NightlyTest to TestConfig(maxArgs = 3),
					Env.PR to TestConfig(maxArgs = 4),
					Env.Local to TestConfig(maxArgs = 4),
				)
			)
		}.toThrow<IllegalArgumentException> {
			messageToContain("Looks like you defined some envs in profile Unit multiple times", "PR", "Local")
		}
	}
}
