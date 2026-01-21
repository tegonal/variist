package com.tegonal.variist.config.impl

import ch.tutteli.atrium.api.fluent.en_GB.feature
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.kbox.identity
import com.tegonal.variist.config.Env
import com.tegonal.variist.config.TestType
import com.tegonal.variist.config.VariistConfig
import com.tegonal.variist.config.VariistConfigBuilder
import com.tegonal.variist.config.VariistPropertiesLoaderConfig
import com.tegonal.variist.generators.*
import com.tegonal.variist.providers.ArgsSource
import org.junit.jupiter.params.ParameterizedTest
import java.util.*
import kotlin.test.Test

class VariistPropertiesParserTest {

	@ParameterizedTest
	@ArgsSource("propertiesSpecified")
	fun check_redefinition_works(propertiesSpecified: Set<String>) {
		checkSomePropertiesRedefined(propertiesSpecified, prefixForMerge = null, prefixForKey = "")
	}

	@ParameterizedTest
	@ArgsSource("propertiesSpecified")
	fun check_redefinition_works_also_with_prefix_without_dot(propertiesSpecified: Set<String>) {
		checkSomePropertiesRedefined(propertiesSpecified, prefixForMerge = "test", prefixForKey = "test.")
	}

	@ParameterizedTest
	@ArgsSource("propertiesSpecified")
	fun check_redefinition_works_also_with_prefix_with_dot(propertiesSpecified: Set<String>) {
		checkSomePropertiesRedefined(propertiesSpecified, prefixForMerge = "test.", prefixForKey = "test.")
	}

	@Test
	fun check_redefinition_does_not_work_using_prefix_but_keys_not_prefixed() {
		val configBuilder = setupConfigBuilder()
		val config = configBuilder.build()

		val properties = Properties()
		propertiesWithNewValues.forEach { (key, value) ->
			properties[key] = value
		}

		VariistPropertiesParser().mergePropertiesInto(
			properties,
			ConfigBuilderAndLoaderConfig(configBuilder, VariistPropertiesLoaderConfig()),
			prefix = "test"
		)

		expect(configBuilder) {
			feature { f(it::seed) }.toEqual(config.seed.value)
			feature { f(it::skip) }.toEqual(config.skip)
			feature { f(it::requestedMinArgs) }.toEqual(config.requestedMinArgs)
			feature { f(it::maxArgs) }.toEqual(config.maxArgs)
			feature { f(it::activeArgsRangeDecider) }.toEqual(config.activeArgsRangeDecider)
			feature { f(it::activeSuffixArgsGeneratorDecider) }.toEqual(config.activeSuffixArgsGeneratorDecider)
			feature { f(it::activeEnv) }.toEqual(config.activeEnv)
			feature { f(it::defaultProfile) }.toEqual(config.defaultProfile)
		}
	}


	private fun checkSomePropertiesRedefined(
		propertiesSpecified: Set<String>,
		prefixForMerge: String?,
		prefixForKey: String
	) {
		val configBuilder = setupConfigBuilder()
		val config = configBuilder.build()

		val properties = Properties()
		propertiesWithNewValues.forEach { (key, value) ->
			if (key in propertiesSpecified) properties[prefixForKey + key] = value
		}

		VariistPropertiesParser().mergePropertiesInto(
			properties,
			ConfigBuilderAndLoaderConfig(configBuilder, VariistPropertiesLoaderConfig()),
			prefix = prefixForMerge
		)
		fun <T> ifSpecified(prop: String, t: T, convert: (String) -> T): T =
			if (prop in propertiesSpecified) convert(propertiesWithNewValues[prop]!!) else t

		expect(configBuilder) {
			feature { f(it::seed) }.toEqual(ifSpecified("seed", config.seed.value) { it.toInt() })
			feature { f(it::skip) }.toEqual(ifSpecified("skip", config.skip) { it.toInt() })
			feature { f(it::requestedMinArgs) }.toEqual(
				ifSpecified("requestedMinArgs", config.requestedMinArgs) { it.toInt() }
			)
			feature { f(it::maxArgs) }.toEqual(ifSpecified("maxArgs", config.maxArgs) { it.toInt() })
			feature { f(it::activeArgsRangeDecider) }.toEqual(
				ifSpecified("activeArgsRangeDecider", config.activeArgsRangeDecider, ::identity)
			)
			feature { f(it::activeSuffixArgsGeneratorDecider) }.toEqual(
				ifSpecified(
					"activeSuffixArgsGeneratorDecider",
					config.activeSuffixArgsGeneratorDecider,
					::identity
				)
			)
			feature { f(it::activeEnv) }.toEqual(ifSpecified("activeEnv", config.activeEnv, ::identity))
			feature { f(it::defaultProfile) }.toEqual(
				ifSpecified("defaultProfile", config.defaultProfile, ::identity)
			)
		}
	}

	private fun setupConfigBuilder(): VariistConfigBuilder = VariistConfigBuilder(
		seed = 1,
		skip = 2,
		requestedMinArgs = 3,
		maxArgs = 4,
		activeArgsRangeDecider = "activeArgsRangeDecider",
		activeSuffixArgsGeneratorDecider = "activeSuffixArgsGeneratorDecider",
		activeEnv = Env.Local.name,
		defaultProfile = TestType.Integration.name,
		testProfiles = VariistConfig.defaultTestProfiles.toMutableMap(),
	)

	//TODO 2.1.0 tests for testProfiles

	companion object {
		val propertiesWithNewValues = mapOf(
			VariistConfigBuilder::seed.name to "10",
			VariistConfigBuilder::skip.name to "20",
			VariistConfigBuilder::requestedMinArgs.name to "30",
			VariistConfigBuilder::maxArgs.name to "40",
			VariistConfigBuilder::activeArgsRangeDecider.name to "rangeDecider",
			VariistConfigBuilder::activeSuffixArgsGeneratorDecider.name to "suffixArgsGeneratorDecider",
			VariistConfigBuilder::activeEnv.name to Env.Release.name,
			VariistConfigBuilder::defaultProfile.name to TestType.E2E.name,
		)

		@JvmStatic
		fun propertiesSpecified() =
			arb.mergeWeighted(
				5 to arb.of(emptySet()),
				95 to arb.boolean().chunked(propertiesWithNewValues.size).map { booleans ->
					propertiesWithNewValues.keys.mapIndexedNotNull { index, prop ->
						prop.takeIf { booleans[index] }
					}.toSet()
				}
			)
	}
}
