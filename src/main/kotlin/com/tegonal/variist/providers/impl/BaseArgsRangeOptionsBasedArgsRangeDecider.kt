package com.tegonal.variist.providers.impl

import ch.tutteli.kbox.letIf
import com.tegonal.variist.config.ArgsRangeOptions
import com.tegonal.variist.config.VariistConfig
import com.tegonal.variist.config._components
import com.tegonal.variist.config.config
import com.tegonal.variist.generators.ArbArgsGenerator
import com.tegonal.variist.generators.ArgsGenerator
import com.tegonal.variist.generators.SemiOrderedArgsGenerator
import com.tegonal.variist.generators.impl.throwUnsupportedArgsGenerator
import com.tegonal.variist.providers.AnnotationData
import com.tegonal.variist.providers.ArgsRange
import com.tegonal.variist.providers.ArgsRangeDecider

/**
 * Not really a good name, but hard to come up with a good one.
 *
 * This class is responsible to get an [com.tegonal.variist.providers.ArgsRange] from a subclass based on a given profile, env and ArgsGenerator
 * and then restrict it based on [ArgsRangeOptions] and [ArgsGenerator].
 *
 *
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
abstract class BaseArgsRangeOptionsBasedArgsRangeDecider : ArgsRangeDecider {

	final override fun decide(argsGenerator: ArgsGenerator<*>, annotationData: AnnotationData?): ArgsRange {
		val config = argsGenerator._components.config
		val profile = annotationData?.argsRangeOptions?.profile ?: config.defaultProfile

		return decideArgsRange(profile, config.activeEnv, argsGenerator)
			.adjustTakeIfNecessary(config, annotationData?.argsRangeOptions, argsGenerator)
	}

	/**
	 * Returns the [ArgsRange] solely based on the given [profileName], [env] and [argsGenerator].
	 *
	 * Restricting the choice based on given [ArgsRangeOptions] and [VariistConfig] is the
	 * responsibility of [BaseArgsRangeOptionsBasedArgsRangeDecider].
	 */
	protected abstract fun decideArgsRange(
		profileName: String,
		env: String,
		argsGenerator: ArgsGenerator<*>
	): ArgsRange

	private fun ArgsRange.adjustTakeIfNecessary(
		config: VariistConfig,
		argsRangeOptions: ArgsRangeOptions?,
		argsGenerator: ArgsGenerator<*>
	): ArgsRange = let { argsRange ->
		val maxArgs = config.maxArgs ?: argsRangeOptions?.maxArgs
		val requestedMinArgs = config.requestedMinArgs ?: argsRangeOptions?.requestedMinArgs
		val newTake = argsRange.take
			.letIf(maxArgs != null) { take ->
				// maxArgs defined in
				minOf(maxArgs!!, take)
			}.let { take ->
				when (argsGenerator) {
					is SemiOrderedArgsGenerator -> {
						// don't take more than the generator size (otherwise we repeat values) unless we allow it
						// yet, take could also be smaller than the size...
						minOf(argsGenerator.size, take)
							// ... hence if requestedMinArgs is greater we increase ...
							.increaseToRequestedMinArgsIfConfigMaxArgsNotDefined(requestedMinArgs, config)
							// ... but only if we allow to go beyond size
							.letIf(argsRangeOptions?.minArgsOverridesSizeLimit != true) { newTake ->
								minOf(argsGenerator.size, newTake)
							}

						// Note, we don't use offset=0 in case generatorSize is less than `take` (i.e. which means we
						// can run all combinations), because, who knows, maybe the tests are dependent somehow
						// and we want to be sure we uncover this via different offsets
					}

					is ArbArgsGenerator ->
						take.increaseToRequestedMinArgsIfConfigMaxArgsNotDefined(requestedMinArgs, config)

					else -> throwUnsupportedArgsGenerator(argsGenerator)
				}
			}

		argsRange.letIf(newTake != argsRange.take) {
			ArgsRange(offset = argsRange.offset, take = newTake)
		}
	}

	private fun Int.increaseToRequestedMinArgsIfConfigMaxArgsNotDefined(
		requestedMinArgs: Int?,
		config: VariistConfig
	): Int = letIf(
		// the following condition might seem strange at first but we only need to consider requestedMinArgs if
		// config.maxArgs is null because if maxArgs is not null, then requestedMinArgs < maxArgs due to invariants
		// requestedMinArgs > maxArgs happens if requestedMinArgs was defined in config and maxArgs in argsRangeOptions.
		// config.maxArgs takes precedence in such a case which is done via maxOf below
		requestedMinArgs != null && config.maxArgs == null
	) { take ->
		maxOf(requestedMinArgs!!, take)
	}
}
