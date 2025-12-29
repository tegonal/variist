package com.tegonal.variist.providers

import com.tegonal.variist.config.ArgsRangeOptions
import com.tegonal.variist.config.merge

/**
 * Data which is typically defined via an [Annotation] and is used as specific configuration for the test method/class.
 *
 * @since 2.0.0
 */
class AnnotationData(
	val argsSourceMethodName: String,
	val argsRangeOptions: ArgsRangeOptions,
	/**
	 * Generic map for extensions of Variist (or your own custom code), intended to be filled by an
	 * [AnnotationDataDeducer] and later be consumed by e.g. [ArgsRangeDecider], [SuffixArgsGeneratorDecider].
	 */
	val extensionData: Map<String, Any> = emptyMap()
) {
	init {
		check(argsSourceMethodName.isNotBlank()) {
			"It looks like you forgot to specify the methodName (was empty or blank)"
		}
	}

	companion object
}

/**
 * Merges this [AnnotationData] with the given [other] where the other takes precedence for properties which are not
 * merged.
 *
 * @since 2.0.0
 */
fun AnnotationData.merge(other: AnnotationData): AnnotationData {
	check(other.argsSourceMethodName == this.argsSourceMethodName) {
		val className = AnnotationData::class.simpleName
		"looks like an error, this $className ${AnnotationData::argsSourceMethodName.name} ($argsSourceMethodName) is not the same as the other (${other.argsSourceMethodName}). Most likely the two $className should not be merged"
	}
	return AnnotationData(
		argsSourceMethodName,
		argsRangeOptions = this.argsRangeOptions.merge(other.argsRangeOptions),
		extensionData = this.extensionData + other.extensionData
	)
}


/**
 * Helper method to create [AnnotationData] based on [ArgsSourceOptions].
 *
 * @since 2.0.0
 */
fun AnnotationData.Companion.fromOptions(
	argsSourceMethodName: String,
	argsSourceOptions: ArgsSourceOptions
): AnnotationData = AnnotationData(
	argsSourceMethodName = argsSourceMethodName,
	argsRangeOptions = ArgsRangeOptions(
		profile = argsSourceOptions.profile.takeIf { it.isNotEmpty() },
		requestedMinArgs = argsSourceOptions.requestedMinArgs.takeIf { it > 0 },
		maxArgs = argsSourceOptions.maxArgs.takeIf { it > 0 },
	),
)

/**
 * @since 2.0.0
 */
fun AnnotationData.Companion.outsideParameterizedTest(
	argsRangeOptions: ArgsRangeOptions,
	extensionData: Map<String, Any> = emptyMap()
): AnnotationData = AnnotationData("<not-specified>", argsRangeOptions, extensionData)

