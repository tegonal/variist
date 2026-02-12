package com.tegonal.variist.config

import com.tegonal.variist.generators.ArgsGenerator
import com.tegonal.variist.providers.ArgsRange
import com.tegonal.variist.providers.ArgsRangeDecider
import com.tegonal.variist.utils.impl.checkIsNotBlank
import com.tegonal.variist.utils.impl.checkRequestedMinArgsMaxArgs
import com.tegonal.variist.generators.SemiOrderedArgsGenerator

/**
 * Represents options which influences an [ArgsRangeDecider] on what [ArgsRange] to choose.
 *
 * @since 2.0.0
 */
class ArgsRangeOptions(
	/**
	 * Will take precedence over [VariistConfig.defaultProfile].
	 */
	val profile: String? = null,

	/**
	 * Should influence an [ArgsRangeDecider]'s choice of [ArgsRange.take], signalling that it should be at least
	 * the specified amount, unless the [ArgsGenerator] repeats values beforehand.
	 */
	val requestedMinArgs: Int? = null,

	/**
	 * If `true` then [SemiOrderedArgsGenerator.size] does not limit the range size
	 * in case [requestedMinArgs] is greater.
	 */
	val minArgsOverridesSizeLimit: Boolean = false,

	/**
	 * Should influence an [ArgsRangeDecider]'s choice of [ArgsRange.take], signalling that it should not be greater
	 * than the specified amount.
	 */
	val maxArgs: Int? = null,
) {
	init {
		profile?.also { checkIsNotBlank(it, "profile") }
		checkRequestedMinArgsMaxArgs(requestedMinArgs, maxArgs)
	}
}

/**
 * Merges this [ArgsRangeOptions] with the given [other] where the properties of the [other] [ArgsRangeOptions] take
 * precedence.
 *
 * @since 2.0.0
 */
fun ArgsRangeOptions.merge(other: ArgsRangeOptions): ArgsRangeOptions {
	return ArgsRangeOptions(
		profile = other.profile ?: this.profile,
		requestedMinArgs = other.requestedMinArgs ?: this.requestedMinArgs,
		maxArgs = other.maxArgs ?: this.maxArgs,
	)
}
