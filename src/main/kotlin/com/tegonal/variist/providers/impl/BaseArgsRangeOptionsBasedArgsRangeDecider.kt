package com.tegonal.variist.providers.impl

import ch.tutteli.kbox.letIf
import com.tegonal.variist.config.ArgsRangeOptions
import com.tegonal.variist.config.VariistConfig
import com.tegonal.variist.config._components
import com.tegonal.variist.config.config
import com.tegonal.variist.generators.ArbArgsGenerator
import com.tegonal.variist.generators.ArgsGenerator
import com.tegonal.variist.generators.OrderedArgsGenerator
import com.tegonal.variist.generators.SemiOrderedArgsGenerator
import com.tegonal.variist.generators.impl.throwUnsupportedArgsGenerator
import com.tegonal.variist.providers.AnnotationData
import com.tegonal.variist.providers.ArgsRange
import com.tegonal.variist.providers.ArgsRangeDecider
import com.tegonal.variist.utils.seedToOffset

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
			.restrictBasedOnConfigAndArgsRangeOptions(config, annotationData?.argsRangeOptions, argsGenerator)

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

	private fun ArgsRange.restrictBasedOnConfigAndArgsRangeOptions(
		config: VariistConfig,
		argsRangeOptions: ArgsRangeOptions?,
		argsGenerator: ArgsGenerator<*>
	): ArgsRange =
		run {
			config.skip
				?.let { ArgsRange(offset = seedToOffset(this.offset + it), take = this.take) }
				?: this
		}.let { argsRange ->
			val maxArgs = config.maxArgs ?: argsRangeOptions?.maxArgs
			val requestedMinArgs = config.requestedMinArgs ?: argsRangeOptions?.requestedMinArgs
			val newTake = argsRange.take
				.letIf(maxArgs != null) { take ->
					// maxArgs defined in
					minOf(maxArgs!!, take)
				}.let { take ->
					when (argsGenerator) {
						is OrderedArgsGenerator -> {
							take.letIf(
								// requestedMinArgs < maxArgs if defined at the same place but
								// requestedMinArgs > maxArgs if requestedMinArgs was defined
								// in config and maxArgs in argsRangeOptions. Config takes precedence
								requestedMinArgs != null && config.maxArgs == null
							) { newTake ->
								maxOf(requestedMinArgs!!, newTake)
							}.let { newTake ->
								//
								// no need to take more as we would start to repeat values
								// we can ignore requestedMinArgs for OrderedArgsGenerator
								minOf(argsGenerator.size, newTake)
							}

							// Note, we don't use offset=0 in case generatorSize is less than `take` (i.e. which means we can
							// run all combinations), because, who knows, maybe the tests are dependent somehow
							// and we want to be sure we cover this via different offsets
						}

						is SemiOrderedArgsGenerator ->
							minOf(argsGenerator.size, take)
								.letIf(
									// requestedMinArgs < maxArgs if defined at the same place but
									// requestedMinArgs > maxArgs if requestedMinArgs was defined
									// in config and maxArgs in argsRangeOptions. Config takes precedence
									requestedMinArgs != null && config.maxArgs == null
								) { newTake ->
									// in contract to OrderedArgsGenerator we start to repeat the fixed part of a
									// SemiOrderedArgsGenerator if one requested more than SemiOrderedArgsGenerator.size
									maxOf(requestedMinArgs!!, newTake)
								}

						is ArbArgsGenerator ->
							take.letIf(requestedMinArgs != null && config.maxArgs == null) {
								// it could be that requestedMinArgs > maxArgs in case requestedMinArgs was defined in
								// config and maxArgs in argsRangeOptions.
								// This is because config has precedence over argsRangeOptions.
								maxOf(requestedMinArgs!!, take)
							}

						else -> throwUnsupportedArgsGenerator(argsGenerator)
					}
				}

			argsRange.letIf(newTake != argsRange.take) {
				ArgsRange(offset = argsRange.offset, take = newTake)
			}
		}
}
