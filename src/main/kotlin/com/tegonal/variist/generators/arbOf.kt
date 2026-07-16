package com.tegonal.variist.generators

import ch.tutteli.kbox.mapSecond
import com.tegonal.variist.config._components
import com.tegonal.variist.generators.impl.ConstantArbArgsGenerator

/**
 * Returns an [ArbArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
// note: not `arg: T, vararg args: T` on purpose for performance reasons, we have a check on size
fun <T> ArbExtensionPoint.of(vararg args: T): ArbArgsGenerator<T> =
	fromArray(args)

/**
 * Returns an [ArbArgsGenerator] based on the given weight/value pairs where it picks the next value
 * according to the defined weights.
 *
 * See [mergeWeighted] for an explanation of the weights.
 * Note, if you want equal probability for each element, then use
 * [arb]`.`&nbsp;[of][ArbExtensionPoint.of]`(...)` instead.
 *
 * @param firstWeightWithValue The first value with an associated weight
 * @param secondWeightWithValue The second value with an associated weight
 * @param othersWeightWithValue Optional further weight/value pairs
 *
 * @throws IllegalArgumentException in case a weight is wrong (is less than 1)
 * @throws ArithmeticException in case the weights sum up to [Int.MAX_VALUE] or more.
 *
 * @return The resulting [ArbArgsGenerator].
 *
 * @since 3.0.0
 */
fun <T> ArbExtensionPoint.ofWeighted(
	firstWeightWithValue: Pair<Int, T>,
	secondWeightWithValue: Pair<Int, T>,
	vararg othersWeightWithValue: Pair<Int, T>
): ArbArgsGenerator<T> {
	//TODO 3.5.0 we could introduce a specialised version which does not require ArbArgsGenerator but works on the list directly
	return mergeWeighted(
		firstWeightWithValue.mapSecond { ConstantArbArgsGenerator(_components, it) },
		secondWeightWithValue.mapSecond { ConstantArbArgsGenerator(_components, it) },
		others = othersWeightWithValue.map { pair ->
			pair.mapSecond { ConstantArbArgsGenerator(_components, it) }
		}.toTypedArray()
	)
}
