package com.tegonal.variist.config.impl

import com.tegonal.variist.config.TestConfig
import com.tegonal.variist.config.VariistConfigBuilder
import com.tegonal.variist.config.VariistParseException
import com.tegonal.variist.config.VariistPropertiesLoaderConfig
import com.tegonal.variist.utils.impl.FEATURE_REQUEST_URL
import com.tegonal.variist.utils.impl.toIntOrErrorNotValid
import com.tegonal.variist.utils.impl.toPositiveIntOrErrorNotValid
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.*

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class VariistPropertiesParser {

	/**
	 * @param prefix if defined, then only properties with the given [prefix] are considered to be Variist properties.
	 */
	fun mergePropertiesInto(
		properties: Properties,
		builderAndLoaderConfig: ConfigBuilderAndLoaderConfig,
		prefix: String? = null
	) {
		// we only check for what Java considers to be blank, good enough (e.g. we allow a zero-width prefix)
		check(prefix == null || prefix.isNotBlank()) {
			"prefix cannot be blank if defined, was `$prefix`"
		}
		val prefixWithDot = prefix?.let { if (it.endsWith(".")) it else "$it." }
		properties.forEach { (keyAny, valueAny) ->
			try {
				val key = keyAny as? String
					?: error("property key was not a String, was ${keyAny::class.qualifiedName} ($keyAny)")
				val value = valueAny as? String
					?: error("property value was not a String, was ${valueAny::class.qualifiedName} ($valueAny)")

				if (prefixWithDot == null || key.startsWith(prefixWithDot)) {
					val keyWithoutPrefix = if (prefixWithDot == null) key else key.substringAfter(prefixWithDot)
					builderAndLoaderConfig.parseProperty(keyWithoutPrefix, value)
				}
			} catch (m: VariistParseException) {
				throw m
			} catch (e: Exception) {
				throw VariistParseException("could not parse $keyAny=$valueAny", e)
			}
		}
	}

	private fun ConfigBuilderAndLoaderConfig.parseProperty(
		key: String,
		value: String
	) {
		val supportedKeys = mutableListOf(PROFILES_PREFIX)

		fun isKey(supportedKey: String) = (key == supportedKey).also {
			supportedKeys.add(supportedKey)
		}
		when {
			isKey("seed") -> builder.seed = value.toIntOrErrorNotValid(key)
			isKey("skip") -> builder.skip = value.toIntOrErrorNotValid(key)

			isKey("maxArgs") -> builder.maxArgs = value.toIntOrErrorNotValid(key)
			isKey("requestedMinArgs") -> builder.requestedMinArgs = value.toIntOrErrorNotValid(key)
			isKey("activeArgsRangeDecider") -> builder.activeArgsRangeDecider = value
			isKey("activeSuffixArgsGeneratorDecider") -> builder.activeSuffixArgsGeneratorDecider = value
			isKey("activeEnv") -> builder.activeEnv = value
			isKey("defaultProfile") -> builder.defaultProfile = value

			isKey(PROFILES) -> {
				if (value == "clear") builder.testProfiles.clear()
				else parseError("don't know how to interpret $value for $key")
			}

			key.startsWith(PROFILES_PREFIX) -> builder.parseTestProfile(key, value)

			// ---------------------------------------------------------------------------------------------
			// VariistPropertiesLoaderConfig -------------------------------------------------------------------------
			// ---------------------------------------------------------------------------------------------

			isKey("variistPropertiesDir") -> loaderConfig.localPropertiesDir = Paths.get(value)
			isKey("remindAboutFixedPropertiesAfterMinutes") ->
				loaderConfig.remindAboutFixedPropertiesAfterMinutes = value.toIntOrErrorNotValid(key)

			key.startsWith(ERROR_DEADLINES_PREFIX) -> loaderConfig.parseErrorDeadlines(key, value)

			isKey("localPropertiesResourceName") -> loaderConfig.localPropertiesResourceName = value
			isKey("localPropertiesPrefix") -> loaderConfig.localPropertiesPrefix = value

			else -> throwUnknownProperty(key, value, supportedKeys)
		}
	}

	private fun VariistConfigBuilder.parseTestProfile(key: String, value: String) {
		val remainingAfterPrefix = key.substringAfter(PROFILES_PREFIX)
		val profileName = remainingAfterPrefix.substringBefore(".")
		if (remainingAfterPrefix == profileName) {
			if (value == "clear") {
				testProfiles[profileName]?.clear()
			} else parseError("don't know how to interpret $value for $key")
		} else {
			val testConfigsPerEnv = testProfiles.computeIfAbsent(profileName) { HashMap<String, TestConfig>() }
			val remainingAfterProfile = remainingAfterPrefix.substringAfter("$profileName.")
			val envName = remainingAfterProfile.substringBefore(".")
			if (remainingAfterProfile == envName) {
				if (value == "clear") {
					testConfigsPerEnv.remove(envName)
				} else parseError("don't know how to interpret $value for $key")
			} else {
				testConfigsPerEnv.mergeOrPutTestConfig(envName, remainingAfterProfile, value, key)
			}
		}
	}

	private fun MutableMap<String, TestConfig>.mergeOrPutTestConfig(
		envName: String,
		remainingAfterProfile: String,
		value: String,
		key: String
	) {
		val testConfig = when (remainingAfterProfile.substringAfter("$envName.")) {
			"maxArgs" -> TestConfig(maxArgs = value.toPositiveIntOrErrorNotValid(key))
			else -> throwUnknownProperty(
				key,
				value,
				supportedKeys = listOf("maxArgs"),
				within = "$PROFILES_PREFIX.$envName."
			)
		}
		// currently we only have 1 property, so we can simply replace, not yet a need to merge
		put(envName, testConfig)
	}

	private fun VariistPropertiesLoaderConfig.parseErrorDeadlines(key: String, value: String) {
		val remainingAfterPrefix = key.substringAfter(ERROR_DEADLINES_PREFIX)
		errorDeadlines[remainingAfterPrefix] = LocalDateTime.parse(value)
	}

	private fun throwUnknownProperty(
		key: String,
		value: String,
		supportedKeys: List<String>,
		within: String? = null
	): Nothing {
		throw VariistParseException(
			"Unknown Variist config property $key with value $value -- if you want to introduce custom config properties, then please open a feature request: $FEATURE_REQUEST_URL&title=custom%20config%20properties\nSupported Keys${within?.let { " within $it" } ?: ""}: ${
				supportedKeys.sorted().joinToString(", ")
			}"
		)
	}

	private fun parseError(message: String): Nothing = throw VariistParseException(message)

	companion object {
		const val PROFILES = "profiles"
		const val PROFILES_PREFIX = "$PROFILES."
		const val ERROR_DEADLINES_PREFIX = "errorDeadlines."
	}
}

