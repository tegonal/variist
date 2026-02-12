package com.tegonal.variist.providers.impl

import com.tegonal.variist.config._components
import com.tegonal.variist.config.config
import com.tegonal.variist.config.toOffset
import com.tegonal.variist.generators.ArgsGenerator
import com.tegonal.variist.providers.ArgsRange

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
		argsGenerator: ArgsGenerator<*>
	): ArgsRange = argsGenerator._components.config.let { config ->
		ArgsRange(
			offset = config.seed.toOffset(),
			take = config.testProfiles.get(profileName, env).maxArgs
		)
	}
}
