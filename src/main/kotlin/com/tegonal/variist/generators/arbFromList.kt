package com.tegonal.variist.generators

import ch.tutteli.kbox.mapSecond
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
		?: ConstantArbArgsGenerator(_components, seedBaseOffset, args.first())

/**
 * Returns an [ArbArgsGenerator] based on the given [weightWithValueList] which picks the next value
 * according to the defined weights.
 *
 * See [mergeWeighted] for an explanation of the weights.
 * Note, if you want equal probability for each element, then use [arb]`.`&nbsp;[fromList]`(...)` instead.
 *
 * @param weightWithValueList A list of values with associated weights where the values will be converted into
 *   a constant [ArbArgsGenerator]
 *
 * @throws IllegalArgumentException in case a weight is wrong (is less than 1)
 * @throws ArithmeticException in case the weights sum up to [Int.MAX_VALUE] or more.
 *
 * @return The resulting [ArbArgsGenerator].
 *
 * @since 2.2.0
 */
fun <T> ArbExtensionPoint.fromListWeighted(
	weightWithValueList: List<Pair<Int, T>>,
): ArbArgsGenerator<T> {
	require(weightWithValueList.size >= 2) {
		"At least two values must be provided to fromListWeighted"
	}
	val weightWithGeneratorList = weightWithValueList.map { weightWithValue ->
		weightWithValue.mapSecond { arb.of(it) }
	}
	//TODO 2.5.0 we could introduce a specialised version which does not require ArbArgsGenerator but works on the list directly
	return mergeWeighted(
		weightWithGeneratorList[0],
		weightWithGeneratorList[1],
		others = weightWithGeneratorList.drop(2).toTypedArray()
	)
}
