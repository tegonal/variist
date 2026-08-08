package com.tegonal.variist.generators

import com.tegonal.variist.config._components
import com.tegonal.variist.generators.impl.*
import com.tegonal.variist.utils.BigInt
import com.tegonal.variist.utils.impl.checkIsPositive

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

/**
 * Returns an [ArbArgsGenerator] which generates compositions of the given [total] into [numberOfParts],
 * where each part is greater than or equal to [minPart].
 *
 * To put differently, each generated [List] contains exactly [numberOfParts] values, each greater than
 * or equal to [minPart], whose sum is [total].
 *
 * @since 3.0.0
 */
fun ArbExtensionPoint.intComposition(
	total: Int,
	numberOfParts: Int,
	minPart: Int = 1
): ArbArgsGenerator<List<Int>> {
	checkIsPositive(total, "total")
	checkIsPositive(numberOfParts, "numberOfParts")
	checkIsPositive(minPart, "minPart")

	// We treat this as a stars and bars problem, so minPart can also be thought of the min. number of stars in a part
	val totalMinStars = numberOfParts * minPart
	require(totalMinStars <= total) {
		"numberOfParts ($numberOfParts) * minPart ($minPart) must be less than or equal to total ($total)"
	}

	return if (numberOfParts == 1) {
		of(listOf(total))
	} else if (totalMinStars == total) {
		of((1..numberOfParts).map { minPart })
	} else {

		// Because each part has to contain minPart stars, we can only distribute the remaining stars
		val remainingStars = total - totalMinStars

		// We distribute the remainingStars among numberOfParts using `numberOfParts - 1` bars
		// (or think of number of cuts needed to split the remainingStars among the desired numberOfParts).
		val bars = numberOfParts - 1
		val remainingStarsPlusBars = remainingStars + bars
		intFromUntil(0, remainingStarsPlusBars)
			.chunkedDistinctValues(bars)
			.map { barPositions ->
				// Having the barPositions for the remainingStars we need to calculate the actual sizes of the parts
				// including the minPart requirement. Since we moved the min stars requirement out of remainingStars,
				// a bar could also be at position 0, i.e. the first part wouldn't get any remaining star.
				// Due to this we introduce a virtual bar -1 at the beginning so that we can use the same formula
				// also for the first part. Likewise, we add totalStarsAndBars as virtual bar at the end. Then
				// we can calculate the actual number of stars per part as: (distance between bars) + minPart
				// i.e. for this to work we need to sort the barPositions
				val sortedBarPositionsWithVirtual = listOf(-1) + barPositions.sorted() + listOf(remainingStarsPlusBars)
				sortedBarPositionsWithVirtual.zipWithNext { previousBarPosition, nextBarPosition ->
					// -1 as only one bar counts towards the distance.
					// For instance, if there is a bar at 3 and another at 5 then we only have 1 star at position 4,
					// i.e. the distance is 1 => 5 - 3 - 1 = 1
					(nextBarPosition - previousBarPosition - 1) + minPart
				}

				// Say total = 10 and parts = 4, minParts=2, remainingStars = 2, barPositions could be e.g. [0, 1, 4],
				// then the sortedBarPositionsWithVirtual are: -1, 0, 1, 4, 5
				// and the actual part sizes are:
				// 1 Part = 0 - (-1) - 1 + minPart = 1 - 1 + 2 = 2
				// 2 Part = 1 - 0 - 1 + 2 = 2
				// 3 Part = 4 - 1 - 1 + 2 = 4
				// 4 Part = 5 - 4 - 1 + 2 = 2
			}
	}
}
