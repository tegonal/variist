package com.tegonal.variist.config.impl

import ch.tutteli.atrium.api.fluent.en_GB.feature
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.kbox.identity
import com.tegonal.variist.config.VariistConfigBuilder
import com.tegonal.variist.generators.*
import com.tegonal.variist.providers.ArgsSource
import org.junit.jupiter.params.ParameterizedTest
import java.util.*

class VariistPropertiesParserTest {

	@ParameterizedTest
	@ArgsSource("propertiesSpecified")
	fun checkOverridingSeedWorks(propertiesSpecified: Set<String>) {
		val configBuilder = VariistConfigBuilder(
			seed = 1,
			skip = 2,
			requestedMinArgs = 3,
			maxArgs = 4,
			activeArgsRangeDecider = "activeArgsRangeDecider",
			activeSuffixArgsGeneratorDecider = "activeSuffixArgsGeneratorDecider",
			activeEnv = "activeEnv",
			defaultProfile = "defaultProfile",
			testProfiles = HashMap(),
		)

		val properties = Properties()
		propertiesWithNewValues.forEach { (key, value) ->
			if (key in propertiesSpecified) properties[key] = value
		}

		VariistPropertiesParser().mergeWithProperties(
			configBuilder,
			ConfigFileSpecifics(),
			properties
		)
		fun <T> ifSpecified(prop: String, t: T, convert: (String) -> T): T =
			if (prop in propertiesSpecified) convert(propertiesWithNewValues[prop]!!) else t

		expect(configBuilder) {
			feature { f(it::seed) }.toEqual(ifSpecified("seed", configBuilder.seed) { it.toInt() })
			feature { f(it::skip) }.toEqual(ifSpecified("skip", configBuilder.skip) { it.toInt() })
			feature { f(it::requestedMinArgs) }.toEqual(
				ifSpecified("requestedMinArgs", configBuilder.requestedMinArgs) { it.toInt() }
			)
			feature { f(it::maxArgs) }.toEqual(ifSpecified("maxArgs", configBuilder.maxArgs) { it.toInt() })
			feature { f(it::activeArgsRangeDecider) }.toEqual(
				ifSpecified("activeArgsRangeDecider", configBuilder.activeArgsRangeDecider, ::identity)
			)
			feature { f(it::activeSuffixArgsGeneratorDecider) }.toEqual(
				ifSpecified(
					"activeSuffixArgsGeneratorDecider",
					configBuilder.activeSuffixArgsGeneratorDecider,
					::identity
				)
			)
			feature { f(it::activeEnv) }.toEqual(ifSpecified("activeEnv", configBuilder.activeEnv, ::identity))
			feature { f(it::defaultProfile) }.toEqual(
				ifSpecified("defaultProfile", configBuilder.defaultProfile, ::identity)
			)
		}
	}

	//TODO 2.1.0 tests for testProfiles

	companion object {
		val propertiesWithNewValues = mapOf(
			VariistConfigBuilder::seed.name to "10",
			VariistConfigBuilder::skip.name to "20",
			VariistConfigBuilder::requestedMinArgs.name to "30",
			VariistConfigBuilder::maxArgs.name to "40",
			VariistConfigBuilder::activeArgsRangeDecider.name to "rangeDecider",
			VariistConfigBuilder::activeSuffixArgsGeneratorDecider.name to "suffixArgsGeneratorDecider",
			VariistConfigBuilder::activeEnv.name to "env",
			VariistConfigBuilder::defaultProfile.name to "profile",
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
