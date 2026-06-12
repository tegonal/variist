package com.tegonal.variist.generators

import com.tegonal.variist.config._components
import com.tegonal.variist.generators.impl.*
import com.tegonal.variist.utils.BigInt

/**
 * Returns an [OrderedArgsGenerator] which generates [Int]s ranging [from] (inclusive) until [toExclusive].
 *
 * @since 2.0.0
 */
fun OrderedExtensionPoint.intFromUntil(
	from: Int,
	toExclusive: Int,
): OrderedArgsGenerator<Int> = IntFromUntilOrderedArgsGenerator(
	_components, from, toExclusive, step = 1
)

/**
 * Returns an [OrderedArgsGenerator]which generates [Long]s ranging [from] (inclusive) until [toExclusive].
 *
 * @since 2.0.0
 */
fun OrderedExtensionPoint.longFromUntil(
	from: Long,
	toExclusive: Long,
): OrderedArgsGenerator<Long> = LongFromUntilOrderedArgsGenerator(
	_components, from, toExclusive, step = 1
)

/**
 * Returns an [OrderedArgsGenerator] which generates [BigInt]s ranging [from] (inclusive) until [toExclusive].
 *
 * @since 2.0.0
 */
fun OrderedExtensionPoint.bigIntFromUntil(
	from: BigInt,
	toExclusive: BigInt,
): OrderedArgsGenerator<BigInt> = BigIntFromUntilOrderedArgsGenerator(
	_components, from, toExclusive, step = BigInt.ONE
)


/**
 * Returns an [OrderedArgsGenerator] which generates [Int]s ranging [from] (inclusive) to [toInclusive].
 *
 * @since 2.0.0
 */
fun OrderedExtensionPoint.intFromTo(
	from: Int,
	toInclusive: Int,
): OrderedArgsGenerator<Int> = IntFromToOrderedArgsGenerator(
	_components, from, toInclusive, step = 1
)

/**
 * Returns an [OrderedArgsGenerator] which generates [Long]s ranging [from] (inclusive) to [toInclusive].
 *
 * @since 2.0.0
 */
fun OrderedExtensionPoint.longFromTo(
	from: Long,
	toInclusive: Long,
): OrderedArgsGenerator<Long> = LongFromToOrderedArgsGenerator(
	_components, from, toInclusive, step = 1L
)

/**
 * Returns an [OrderedArgsGenerator] which generates [BigInt]s ranging [from] (inclusive) to [toInclusive].
 *
 * @since 2.0.0
 */
fun OrderedExtensionPoint.bigIntFromTo(
	from: BigInt,
	toInclusive: BigInt,
): OrderedArgsGenerator<BigInt> = BigIntFromUntilOrderedArgsGenerator(
	_components, from, toInclusive + BigInt.ONE, step = BigInt.ONE
)
