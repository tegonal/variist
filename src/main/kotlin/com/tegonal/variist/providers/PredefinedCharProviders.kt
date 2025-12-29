package com.tegonal.variist.providers

import com.tegonal.variist.generators.ArbExtensionPoint
import com.tegonal.variist.generators.arb
import com.tegonal.variist.generators.char
import com.tegonal.variist.generators.ordered

/**
 * Provides predefined providers around [Char] based on the default [arb] and [ordered].
 *
 * @since 2.0.0
 */
interface PredefinedCharProviders {
	companion object {
		/**
		 * See [arb.char()][ArbExtensionPoint.char]
		 */
		@JvmStatic
		fun arbChar() = arb.char()
	}
}
