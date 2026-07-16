package com.tegonal.variist.generators

import com.tegonal.variist.config._components
import com.tegonal.variist.generators.impl.ConstantArbArgsGenerator
import com.tegonal.variist.generators.impl.checkNotEmptyReturnNullIfOneElementAndOtherwiseIntFromUntilSize

/**
 * Returns an [ArbArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
@JvmName("fromValueList")
fun <T> ArbExtensionPoint.fromList(args: List<T>): ArbArgsGenerator<T> =
	checkNotEmptyReturnNullIfOneElementAndOtherwiseIntFromUntilSize(args.size)?.map(args::get)
		?: ConstantArbArgsGenerator(_components, args.first())

/**
 * Returns an [ArbArgsGenerator] based on the given [weightWithValueList] which picks the next value
 * according to the defined weights.
 *
 * See [mergeWeighted] for an explanation of the weights.
 * Note, if you want equal probability for each element, then use
 * [arb]`.`&nbsp;[fromList][ArbExtensionPoint.fromList]`(...)` instead.
 *
 * @param weightWithValueList A list of values with associated weights.
 *
 * @throws IllegalArgumentException in case a weight is wrong (is less than 1).
 * @throws IllegalArgumentException if less than two pairs are passed.
 * @throws ArithmeticException in case the weights sum up to [Int.MAX_VALUE] or more.
 *
 * @return The resulting [ArbArgsGenerator].
 *
 * @since 2.2.0
 */
fun <T> ArbExtensionPoint.fromListWeighted(weightWithValueList: List<Pair<Int, T>>): ArbArgsGenerator<T> {
	require(weightWithValueList.size >= 2) {
		"At least two values must be provided to fromListWeighted, given ${weightWithValueList.size}"
	}
	return ofWeighted(
		weightWithValueList[0],
		weightWithValueList[1],
		othersWeightWithValue = weightWithValueList.drop(2).toTypedArray()
	)
}
