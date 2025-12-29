package com.tegonal.variist.providers

import com.tegonal.variist.generators.*

/**
 * Provides predefined providers around [Int], [Long] and other [Number] based on the default [arb] and [ordered].
 *
 * @since 2.0.0
 */
interface PredefinedNumberProviders {
	companion object {
		/**
		 * See [arb.int()][ArbExtensionPoint.int]
		 */
		@JvmStatic
		fun arbInt() = arb.int()

		/**
		 * See [arb.long()][ArbExtensionPoint.long]
		 */
		@JvmStatic
		fun arbLong() = arb.long()

		/**
		 * See [arb.double()][ArbExtensionPoint.double]
		 */
		@JvmStatic
		fun arbDouble() = arb.double()

		/**
		 * See [arb.intPositive()][ArbExtensionPoint.intPositive]
		 */
		@JvmStatic
		fun arbIntPositive() = arb.intPositive()


		/**
		 * See [arb.longPositive()][ArbExtensionPoint.longPositive]
		 */
		@JvmStatic
		fun arbLongPositive() = arb.longPositive()

		/**
		 * See [arb.intPositiveAndZero()][ArbExtensionPoint.intPositiveAndZero]
		 */
		@JvmStatic
		fun arbIntPositiveAndZero() = arb.intPositiveAndZero()

		/**
		 * See [arb.longPositiveAndZero()][ArbExtensionPoint.longPositiveAndZero]
		 */
		@JvmStatic
		fun arbLongPositiveAndZero() = arb.longPositiveAndZero()

		/**
		 * See [arb.intNegative()][ArbExtensionPoint.intNegative]
		 */
		@JvmStatic
		fun arbIntNegative() = arb.intNegative()

		/**
		 * See [arb.longNegative()][ArbExtensionPoint.longNegative]
		 */
		@JvmStatic
		fun arbLongNegative() = arb.longNegative()

		/**
		 * See [arb.intNegativeAndZero()][ArbExtensionPoint.intNegativeAndZero]
		 */
		@JvmStatic
		fun arbIntNegativeAndZero() = arb.intNegativeAndZero()

		/**
		 * See [arb.longNegativeAndZero()][ArbExtensionPoint.longNegativeAndZero]
		 */
		@JvmStatic
		fun arbLongNegativeAndZero() = arb.longNegativeAndZero()
	}
}
