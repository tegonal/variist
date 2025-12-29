package com.tegonal.variist.providers

import com.tegonal.variist.generators.*

/**
 * Provides predefined providers around [Boolean] based on the default [arb] and [ordered].
 *
 * @since 2.0.0
 */
interface PredefinedBooleanProviders {
	companion object {

		/**
		 * See [ordered].[boolean()][OrderedExtensionPoint.boolean]
		 */
		@JvmStatic
		fun boolean() = ordered.boolean()

		/**
		 * Returns an [OrderedArgsGenerator] which generates `false`, `true` or `null`.
		 */
		@JvmStatic
		fun booleanAndNull() = ordered.of(false, true, null)

		/**
		 * See [arb].[boolean()][ArbExtensionPoint.boolean]
		 */
		@JvmStatic
		fun arbBoolean() = arb.boolean()

		/**
		 * Returns an [ArbArgsGenerator] which generates `false`, `true` or `null`.
		 */
		@JvmStatic
		fun arbBooleanOrNull() = arb.of(false, true, null)
	}
}
