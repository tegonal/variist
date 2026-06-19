package com.tegonal.variist.providers

import com.tegonal.variist.generators.*

/**
 * Provides predefined providers around [Int], [Long] and other [Number] based on the default [arb] and [ordered].
 *
 * @since 2.3.0
 */
interface PredefinedDateRelatedProviders {
	companion object {
		/**
		 * See [arb.localTime()][ArbExtensionPoint.localTime]
		 */
		@JvmStatic
		fun arbLocalTime() = arb.localTime()

		/**
		 * See [arb.zoneId()][ArbExtensionPoint.zoneId]
		 */
		@JvmStatic
		fun arbZoneId() = arb.zoneId()

		/**
		 * See [arb.zoneOffset()][ArbExtensionPoint.zoneOffset]
		 */
		@JvmStatic
		fun arbZoneOffset() = arb.zoneOffset()
	}
}
