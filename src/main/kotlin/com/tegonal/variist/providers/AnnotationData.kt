package com.tegonal.variist.providers

import com.tegonal.variist.config.ArgsRangeOptions
import com.tegonal.variist.config.merge

/**
 * Data which is typically defined via an [Annotation] and is used as specific configuration for the test method/class.
 *
 * @since 2.0.0
 */
class AnnotationData(
	val argsRangeOptions: ArgsRangeOptions? = null,

	/**
	 * Offset to be used to derive a new offset or a new seed based on [com.tegonal.variist.config.VariistConfig.seed].
	 *
	 * This is useful when you want to generate different arguments for the same/similar ArgsProviders, e.g. for
	 * different test methods/classes.
	 *
	 * @since 3.0.0
	 */
	val offset: Int? = null,

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
	val thisArgsRangeOptions = this.argsRangeOptions
	val otherArgsRangeOptions = other.argsRangeOptions

	return AnnotationData(
		argsRangeOptions = when {
			thisArgsRangeOptions == null -> otherArgsRangeOptions
			otherArgsRangeOptions == null -> thisArgsRangeOptions
			else -> thisArgsRangeOptions.merge(otherArgsRangeOptions)
		},
		offset = other.offset ?: this.offset,
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
