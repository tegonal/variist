package com.tegonal.variist.generators

import com.tegonal.variist.config._components
import com.tegonal.variist.generators.impl.*
import com.tegonal.variist.utils.BigInt

/**
 * Returns an [ArbArgsGenerator] which generates [Int]s ranging from
 * [Int.MIN_VALUE] (inclusive) to [Int.MAX_VALUE] (inclusive).
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.int(): ArbArgsGenerator<Int> =
	ArbIntArgsGenerator(_components)

/**
 * Returns an [ArbArgsGenerator] which generates [Int]s ranging from
 * [Long.MIN_VALUE] (inclusive) to [Long.MAX_VALUE] (inclusive).
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.long(): ArbArgsGenerator<Long> =
	ArbLongArgsGenerator(_components)

/**
 * Returns an [ArbArgsGenerator] which generates [Int]s ranging from
 * [Double.MIN_VALUE] (inclusive) to [Double.MAX_VALUE] (inclusive).
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.double(): ArbArgsGenerator<Double> =
	ArbDoubleArgsGenerator(_components)

/**
 * Returns an [ArbArgsGenerator] which generates [Int]s ranging from
 * 1 (inclusive) to [Int.MAX_VALUE] (inclusive).
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.intPositive(): ArbArgsGenerator<Int> =
	intFromTo(1, Int.MAX_VALUE)

/**
 * Returns an [ArbArgsGenerator] which generates [Long]s ranging from
 * 1 (inclusive) to [Long.MAX_VALUE] (inclusive).
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.longPositive(): ArbArgsGenerator<Long> =
	longFromTo(1, Long.MAX_VALUE)

/**
 * Returns an [ArbArgsGenerator] which generates [Int]s ranging from
 * 0 (inclusive) to [Int.MAX_VALUE] (inclusive) -- i.e. positive numbers and 0.
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.intPositiveAndZero(): ArbArgsGenerator<Int> =
	intFromTo(0, Int.MAX_VALUE)

/**
 * Returns an [ArbArgsGenerator] which generates [Long]s ranging from
 * 0 (inclusive) to [Long.MAX_VALUE] (inclusive) -- i.e. positive numbers and 0.
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.longPositiveAndZero(): ArbArgsGenerator<Long> =
	longFromTo(0, Long.MAX_VALUE)

/**
 * Returns an [ArbArgsGenerator] which generates [Int]s ranging from
 * [Int.MIN_VALUE] (inclusive) to 0 (exclusive).
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.intNegative(): ArbArgsGenerator<Int> =
	intFromUntil(Int.MIN_VALUE, 0)

/**
 * Returns an [ArbArgsGenerator] which generates [Int]s ranging from
 * [Long.MIN_VALUE] (inclusive) to 0 (exclusive).
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.longNegative(): ArbArgsGenerator<Long> =
	longFromUntil(Long.MIN_VALUE, 0)

/**
 * Returns an [ArbArgsGenerator] which generates [Int]s ranging from
 * [Int.MIN_VALUE] (inclusive) to 0 (inclusive).
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.intNegativeAndZero(): ArbArgsGenerator<Int> =
	intFromUntil(Int.MIN_VALUE, 1)

/**
 * Returns an [ArbArgsGenerator] which generates [Long]s ranging from
 * [Long.MIN_VALUE] (inclusive) to 0 (inclusive) -- i.e. negative numbers and 0.
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.longNegativeAndZero(): ArbArgsGenerator<Long> =
	longFromUntil(Long.MIN_VALUE, 1)

/**
 * Returns an [ArbArgsGenerator] which generates [Int]s ranging [from] (inclusive) until [toExclusive].
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.intFromUntil(
	from: Int,
	toExclusive: Int,
): ArbArgsGenerator<Int> = IntFromUntilArbArgsGenerator(_components, from, toExclusive)

/**
 * Returns an [ArbArgsGenerator] which generates [Long]s ranging [from] (inclusive) until [toExclusive].
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.longFromUntil(
	from: Long,
	toExclusive: Long,
): ArbArgsGenerator<Long> = LongFromUntilArbArgsGenerator(_components, from, toExclusive)

/**
 * Returns an [ArbArgsGenerator] which generates [Double]s ranging [from] (inclusive) until [toExclusive].
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.doubleFromUntil(
	from: Double,
	toExclusive: Double,
): ArbArgsGenerator<Double> =
	DoubleFromUntilArbArgsGenerator(_components, from, toExclusive)

/**
 * Returns an [ArbArgsGenerator] which generates [BigInt]s ranging [from] (inclusive) until [toExclusive].
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.bigIntFromUntil(
	from: BigInt,
	toExclusive: BigInt
): ArbArgsGenerator<BigInt> =
	BigIntFromUntilArbArgsGenerator(_components, from, toExclusive)


/**
 * Returns an [ArbArgsGenerator] which generates [Int]s ranging [from] (inclusive) to [toInclusive].
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.intFromTo(
	from: Int,
	toInclusive: Int,
): ArbArgsGenerator<Int> = IntFromToArbArgsGenerator(_components, from, toInclusive)

/**
 * Returns an [ArbArgsGenerator] which generates [Long]s ranging [from] (inclusive) to [toInclusive].
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.longFromTo(
	from: Long,
	toInclusive: Long,
): ArbArgsGenerator<Long> = LongFromToArbArgsGenerator(_components, from, toInclusive)

/**
 * Returns an [ArbArgsGenerator] which generates [BigInt]s ranging [from] (inclusive) to [toInclusive].
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.bigIntFromTo(
	from: BigInt,
	toInclusive: BigInt,
): ArbArgsGenerator<BigInt> = bigIntFromUntil(from, toInclusive + BigInt.ONE)
