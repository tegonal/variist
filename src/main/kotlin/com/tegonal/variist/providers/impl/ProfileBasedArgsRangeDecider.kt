package com.tegonal.variist.providers.impl

import com.tegonal.variist.config._components
import com.tegonal.variist.config.config
import com.tegonal.variist.generators.ArgsGenerator
import com.tegonal.variist.providers.ArgsRange
import com.tegonal.variist.utils.deriveChildSeedOffset
import com.tegonal.variist.utils.seedToOffset
import kotlin.math.absoluteValue

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class ProfileBasedArgsRangeDecider : BaseArgsRangeOptionsBasedArgsRangeDecider() {

	override fun decideArgsRange(
		profileName: String,
		env: String,
		argsGenerator: ArgsGenerator<*>,
		seedOffset: Int?
	): ArgsRange {
		val config = argsGenerator._components.config
		val configSeed = config.seed.value

		val seed = if (seedOffset != null && seedOffset != 0) {
			deriveChildSeedOffset(configSeed, seedOffset.absoluteValue)
		} else {
			configSeed
		}

		return ArgsRange(
			offset = seedToOffset(seed),
			take = config.testProfiles.get(profileName, env).maxArgs
		)
	}
}
