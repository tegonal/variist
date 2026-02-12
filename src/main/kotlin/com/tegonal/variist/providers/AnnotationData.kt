package com.tegonal.variist.providers

import com.tegonal.variist.config.ArgsRangeOptions
import com.tegonal.variist.config.merge

/**
 * Data which is typically defined via an [Annotation] and is used as specific configuration for the test method/class.
 *
 * @since 2.0.0
 */
class AnnotationData(
	val argsRangeOptions: ArgsRangeOptions,

	/**
	 * Generic map for extensions of Variist (or your own custom code), intended to be filled by an
	 * [AnnotationDataDeducer] and later be consumed by e.g. [ArgsRangeDecider], [SuffixArgsGeneratorDecider].
	 */
	val extensionData: Map<String, Any> = emptyMap()
) {

	companion object
}


/**
 * Merges this [AnnotationData] with the given [other] where the other takes precedence for properties which are not
 * merged.
 *
 * @since 2.0.0
 */
fun AnnotationData.merge(other: AnnotationData): AnnotationData {
	return AnnotationData(
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
	argsSourceOptions: ArgsSourceOptions
): AnnotationData = AnnotationData(
	argsRangeOptions = ArgsRangeOptions(
		profile = argsSourceOptions.profile.takeIf { it.isNotEmpty() },
		requestedMinArgs = argsSourceOptions.requestedMinArgs.takeIf { it > 0 },
		minArgsOverridesSizeLimit = argsSourceOptions.minArgsOverridesSizeLimit,
		maxArgs = argsSourceOptions.maxArgs.takeIf { it > 0 },
	),
)
