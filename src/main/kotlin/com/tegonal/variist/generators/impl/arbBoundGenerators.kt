package com.tegonal.variist.generators.impl

import ch.tutteli.kbox.takeIf
import com.tegonal.variist.generators.*
import com.tegonal.variist.utils.BigInt
import com.tegonal.variist.utils.impl.checkIsPositive
import com.tegonal.variist.utils.toBigInt

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
fun <T> ArbExtensionPoint.createIntDomainBasedBoundsArbGenerator(
	minInclusive: Int,
	maxInclusive: Int,
	minSize: Int,
	maxSize: Int?,
	factory: (lowerBound: Int, upperBound: Int) -> T
): ArbArgsGenerator<T> {
	val (effectiveMaxSize, possibleMaxSize) = validateNumbersAndReturnEffectiveAndPossibleMaxSize(
		minInclusive = minInclusive.toLong(),
		maxInclusive = maxInclusive.toLong(),
		minSize = minSize.toLong(),
		maxSize = maxSize?.toBigInt()
	)
	check(possibleMaxSize.toLong() <= possibleMaxSizeSafeInIntDomain) {
		"only use createIntDomainBasedClosedRangeArbGenerator if you are sure that possibleMaxSize is less than or equal to $possibleMaxSizeSafeInIntDomain (was $possibleMaxSize)"
	}
	val possibleMaxSizeI = possibleMaxSize.toInt()
	return arbBoundsIntBased(
		minInclusive = minInclusive,
		minSize = minSize,
		effectiveMaxSize = effectiveMaxSize.toInt(),
		possibleMaxSize = possibleMaxSizeI,
		factory = factory
	)
}

// our implementation uses a prefixSum function which is implemented as arithmetic series formula:
// Sₙ = n * (numOfRangesWithSize(minSize) + numOfRangesWithSize(rangeSize)) / 2
// where n = rangeSize - minSize + 1 -> all the considered sizes up to rangeSize
// We want to use Int arithmetic if it doesn't overflow, Long next and in the worst case BigInt.
// I.e. we need to find out, up to what rangeSize it is safe to use Int, Long respectively.
// numOfRangesWithSize is defined as: possibleMaxSize - rangeSize + 1
// let's substitute the arithmetic series formula:
// Int.MAX_SIZE ≥ (maxSize - minSize + 1) * ((possibleMaxSize - minSize + 1) + (possibleMaxSize - maxSize + 1)) / 2
// ==> using minSize=1, yielding the biggest n and maxSize=possibleMaxSize, yielding the biggest maxSize
// Int.MAX_SIZE ≥ (possibleMaxSize - 1 + 1) * ((possibleMaxSize - 1 + 1) + (possibleMaxSize - possibleMaxSize + 1)) / 2
// Int.MAX_SIZE ≥ possibleMaxSize * (possibleMaxSize + 1) / 2
// 2 * Int.MAX_SIZE ≥ possibleMaxSize² + possibleMaxSize
// using the quadratic formula yields approximately: 65535.5 so if possibleMaxSize is less or equal to 65535 we are
// fine to use Int arithmetic, using the same approach for Long.MAX_SIZE yields: 4294967295.5
/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
const val possibleMaxSizeSafeInIntDomain = 65_535

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
const val possibleMaxSizeSafeInLongDomain = 4_294_967_295L

// since we first multiply and then divide, it is actually only safe for
// Int.MAX_SIZE ≥ possibleMaxSize * (possibleMaxSize + 1) which yields 46340.5
// as a trick we first divide possibleMaxSize or possibleMaxSize + 1 (depending on which one is even, we don't
// want to truncate due to Int division) but for this we need one additional if
/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
const val possibleMaxSizeSafeInIntDomainWithoutDivideTrick = 46_340

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
const val possibleMaxSizeSafeInLongDomainWithoutDivideTrick = 3_037_000_499L

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
fun <T> ArbExtensionPoint.createBoundsArbGenerator(
	minInclusive: Long,
	maxInclusive: Long,
	minSize: Long,
	maxSize: BigInt?,
	factory: (lowerBound: Long, upperBound: Long) -> T
): ArbArgsGenerator<T> {
	val (effectiveMaxSize, possibleMaxSize) = validateNumbersAndReturnEffectiveAndPossibleMaxSize(
		minInclusive = minInclusive,
		maxInclusive = maxInclusive,
		minSize = minSize,
		maxSize = maxSize
	)
	val bitLength = possibleMaxSize.bitLength()
	return if (bitLength <= 31) {
		val possibleMaxSizeI = possibleMaxSize.toInt()
		val effectiveMaxSizeI = effectiveMaxSize.toInt()

		if (
		// see explanation above (jump to the possibleMaxSizeSafeInIntDomain definition)
		// why we know we are safe in the Int domain in this case ...
			possibleMaxSizeI <= possibleMaxSizeSafeInIntDomain || this.run {
				// ... otherwise we check if the biggest prefixSum still fits into the Int domain in case
				// effectiveMaxSizeI is less than the possibleMaxSize or minSize > 1
				(effectiveMaxSizeI < possibleMaxSizeI || minSize > 1) && this@createBoundsArbGenerator.run {
					val effectiveMaxSizeL = effectiveMaxSizeI.toLong()
					val possibleMaxSizeL = possibleMaxSizeI.toLong()
					val maxPrefixSum = (effectiveMaxSizeL - minSize + 1) *
						(((possibleMaxSizeL - minSize + 1) + (possibleMaxSizeL - effectiveMaxSizeL + 1)))

					// faster than checking prefixSum <= Int.MAX_VALUE.toLong()
					maxPrefixSum == maxPrefixSum.toInt().toLong()
				}
			}
		) {
			if (minInclusive >= Int.MIN_VALUE && maxInclusive <= Int.MAX_VALUE) {
				arbBoundsIntBased(
					minInclusive = minInclusive.toInt(),
					minSize = minSize.toInt(),
					effectiveMaxSize = effectiveMaxSizeI,
					possibleMaxSize = possibleMaxSizeI
				) { start, end ->
					factory(start.toLong(), end.toLong())
				}
			} else {
				// we know that the possibleMaxSize <= Int.MAX_VALUE so we can safely shift the range
				arbBoundsIntBased(
					minInclusive = 0,
					minSize = minSize.toInt(),
					effectiveMaxSize = effectiveMaxSizeI,
					possibleMaxSize = possibleMaxSizeI
				) { start, end -> factory(start.toLong() + minInclusive, end.toLong() + minInclusive) }
			}
		} else {
			val effectiveMaxSizeL = effectiveMaxSizeI.toLong()
			val possibleMaxSizeL = possibleMaxSizeI.toLong()
			arbBoundsLongBased(
				minInclusive = minInclusive,
				minSize = minSize,
				effectiveMaxSize = effectiveMaxSizeL,
				possibleMaxSize = possibleMaxSizeL,
				factory = factory
			)
		}
	} else {
		takeIf<ArbArgsGenerator<T>?>(bitLength <= 63) {
			val possibleMaxSizeL = possibleMaxSize.toLong()
			val effectiveMaxSizeL = effectiveMaxSize.toLong()
			takeIf<ArbArgsGenerator<T>>(possibleMaxSizeL < possibleMaxSizeSafeInLongDomain || this.run {
				(effectiveMaxSizeL < possibleMaxSizeL || minSize > 1) && this@createBoundsArbGenerator.run {
					val maxPrefixSum =
						(effectiveMaxSizeL - minSize + 1).toBigInt() * this@createBoundsArbGenerator.run {
							(possibleMaxSizeL - minSize + 1).toBigInt() +
								(possibleMaxSizeL - effectiveMaxSizeL + 1).toBigInt()
						}
					maxPrefixSum.bitLength() <= 63
				}
			}) {
				arbBoundsLongBased(
					minInclusive = minInclusive,
					minSize = minSize,
					effectiveMaxSize = effectiveMaxSizeL,
					possibleMaxSize = possibleMaxSizeL,
					factory = factory
				)
			}
		} ?: arbBoundsBigIntBased(
			minInclusive = minInclusive.toBigInt(),
			possibleMaxSize = possibleMaxSize,
			minSize = minSize.toBigInt(),
			maxSize = effectiveMaxSize
		) { a, b -> factory(a.toLong(), b.toLong()) }
	}
}

private fun validateNumbersAndReturnEffectiveAndPossibleMaxSize(
	minInclusive: Long,
	maxInclusive: Long,
	minSize: Long,
	maxSize: BigInt?
): Pair<BigInt, BigInt> {
	checkIsPositive(minSize, "minSize")
	maxSize?.also {
		checkIsPositive(maxSize, "maxSize")
		check(minSize.toBigInt() <= maxSize) {
			"minSize ($minSize) must be less than or equal to maxSize ($maxSize)"
		}
	}
	val maxInclusiveMinusMinSizePlusOne = Math.subtractExact(maxInclusive, minSize) + 1
	check(minInclusive <= maxInclusiveMinusMinSizePlusOne) {
		"minInclusive ($minInclusive) must be less than or equal to maxInclusive ($maxInclusive) - minSize ($minSize) + 1 which is $maxInclusiveMinusMinSizePlusOne"
	}

	val possibleMaxSize = maxInclusive.toBigInt() - minInclusive.toBigInt() + BigInt.ONE
	val effectiveMaxSize = when {
		maxSize == null -> possibleMaxSize
		maxSize <= possibleMaxSize -> maxSize
		else -> error(
			"maxSize ($maxSize) was greater than the possible maxSize $possibleMaxSize based on the given minInclusive ($minInclusive) and maxInclusive ($maxInclusive)"
		)
	}
	return Pair(effectiveMaxSize, possibleMaxSize)
}

private fun <T> ArbExtensionPoint.arbBoundsIntBased(
	minInclusive: Int,
	minSize: Int,
	effectiveMaxSize: Int,
	possibleMaxSize: Int,
	factory: (lowerBound: Int, upperBound: Int) -> T
): ArbArgsGenerator<T> = arbBoundsNumberBased(
	minInclusive = minInclusive,
	minSize = minSize,
	effectiveMaxSize = effectiveMaxSize,
	possibleMaxSize = possibleMaxSize,
	factory = factory,
	numOfRangesWithSize = ::numOfRangesWithSize,
	// micro-optimisation, prefixSumFast is ~twice as fast as prefixSumSlower but in the nanoseconds range
	prefixSum = if (possibleMaxSize < possibleMaxSizeSafeInIntDomainWithoutDivideTrick) ::prefixSumFast else ::prefixSumSlower,
	findSizeMatchingPrefixSumIndex = ::findSizeMatchingPrefixSumIndex,
	arbNumberFromUntil = { from, toExclusive -> arb.intFromUntil(from, toExclusive) },
	plus = Int::plus,
	minus = Int::minus,
	zero = 0,
	one = 1
)

private fun <T> ArbExtensionPoint.arbBoundsLongBased(
	minInclusive: Long,
	minSize: Long,
	effectiveMaxSize: Long,
	possibleMaxSize: Long,
	factory: (lowerBound: Long, upperBound: Long) -> T
): ArbArgsGenerator<T> = arbBoundsNumberBased(
	minInclusive = minInclusive,
	minSize = minSize,
	effectiveMaxSize = effectiveMaxSize,
	possibleMaxSize = possibleMaxSize,
	factory = factory,
	numOfRangesWithSize = ::numOfRangesWithSize,
	prefixSum = if (possibleMaxSize <= possibleMaxSizeSafeInLongDomainWithoutDivideTrick) ::prefixSumFast else ::prefixSumSlower,
	findSizeMatchingPrefixSumIndex = ::findSizeMatchingPrefixSumIndex,
	arbNumberFromUntil = { from, toExclusive -> arb.longFromUntil(from, toExclusive) },
	plus = Long::plus,
	minus = Long::minus,
	zero = 0L,
	one = 1L
)


private fun <T> ArbExtensionPoint.arbBoundsBigIntBased(
	minInclusive: BigInt,
	possibleMaxSize: BigInt,
	minSize: BigInt,
	maxSize: BigInt,
	factory: (lowerBound: BigInt, upperBound: BigInt) -> T
): ArbArgsGenerator<T> = arbBoundsNumberBased(
	minInclusive,
	minSize,
	maxSize,
	possibleMaxSize,
	factory,
	::numOfRangesWithSize,
	::prefixSum,
	::findSizeMatchingPrefixSumIndex,
	{ from, toExclusive -> arb.bigIntFromUntil(from, toExclusive) },
	BigInt::plus,
	BigInt::minus,
	BigInt.ZERO,
	BigInt.ONE
)

private inline fun <NumberT, T> ArbExtensionPoint.arbBoundsNumberBased(
	minInclusive: NumberT,
	minSize: NumberT,
	effectiveMaxSize: NumberT,
	possibleMaxSize: NumberT,
	crossinline factory: (NumberT, NumberT) -> T,
	numOfRangesWithSize: (NumberT, NumberT) -> NumberT,
	crossinline prefixSum: (NumberT, NumberT, NumberT, NumberT) -> NumberT,
	crossinline findSizeMatchingPrefixSumIndex: (NumberT, NumberT, NumberT, NumberT, NumberT) -> NumberT,
	arbNumberFromUntil: ArbExtensionPoint.(NumberT, NumberT) -> ArbArgsGenerator<NumberT>,
	crossinline plus: (NumberT, NumberT) -> NumberT,
	crossinline minus: (NumberT, NumberT) -> NumberT,
	zero: NumberT,
	one: NumberT,
): ArbArgsGenerator<T> where NumberT : Number, NumberT : Comparable<NumberT> {
	// taking the example of intRanges from 1 .. 4 with minSize = 2 there are the following candidates
	// 1..2, 1..3, 1..4
	// 2..3, 2..4,
	// 3..4
	// We know split the ranges according to their sizes using a prefix sum (we don't calculate all of them but for
	// your record in our example those are):
	// 2 = 3 candidates (prefix sum = 3)
	// 3 = 2 (prefix sum 3+2 = 5)
	// 4 = 1 (prefix sum 5 + 1 = 6
	// we don't need to calculate the intermediate sums because the number of ranges with a specific size form an
	// arithmetic progression (with delta = -1), hence we can use the arithmetic series formula:
	// Sn = n * (first + last) / 2 where n = how many range sizes do we summon over, which also corresponds the
	// desired prefixSum functionality.
	// TODO 2.1.0 maybe worth to cache the prefix sum in case of small possibleMaxSize?
	val numOfRangesWithMinSize = numOfRangesWithSize(minSize, possibleMaxSize)
	val accumulatedSum = prefixSum(effectiveMaxSize, numOfRangesWithMinSize, minSize, possibleMaxSize)
	// we use a zero-index based approach because fromUntil is cheaper than fromTo and we want to add the offset to
	// minInclusive in the end and since it is inclusive, using zero is a good fit too. So the (virtual) indices become:
	// 2 = index 0..2
	// 3 = index 3..4
	// 4 = index 5
	return arbNumberFromUntil(zero, accumulatedSum).map { index ->
		val size = findSizeMatchingPrefixSumIndex(
			index,
			numOfRangesWithMinSize,
			minSize,
			effectiveMaxSize,
			possibleMaxSize,
		)
		// say index = 2 which results in size = 2 we calculate the offset within that block using: index - prefixSum of
		// previous block. For index = 2 there isn't any smaller size block, prefixSum for minSize - 1 will yield 0.
		// I.e. in this case the index is already the offset.
		// Say index = 4, size = 3, 4 - previous => 4 - 3 = 1, offset 1 in sizeBlock 3
		val prefixSumOfPreviousSizeBlock = prefixSum(minus(size, one), numOfRangesWithMinSize, minSize, possibleMaxSize)
		val offsetInSizeBlock = minus(index, prefixSumOfPreviousSizeBlock)
		// each sizeBlock starts with a range `from = minInclusive`. So to get the
		// desired range with offset = 2 we calculate:
		val from = plus(minInclusive, offsetInSizeBlock)
		// and for toInclusive we just need to add the size and subtract 1 because both bounds are inclusive
		// taking the two examples from above, for index = 2 we get from= 1 + 2 = 3, toInclusive = 3 + 2 - 1 = 4 => 3..4
		// for index = 4, from = 1 + 1 = 2, toInclusive = 2 + 2 -1 = 3 => 2..3
		val toInclusive = plus(from, minus(size, one))
		factory(from, toInclusive)
	}
}

private fun numOfRangesWithSize(size: Int, possibleMaxSize: Int) =
	numOfRangesWithSize(
		size = size,
		possibleMaxSize = possibleMaxSize,
		plus = Int::plus,
		minus = Int::minus,
		one = 1
	)

private fun prefixSumFast(size: Int, numOfRangesWithMinSize: Int, minSize: Int, possibleMaxSize: Int) =
	prefixSumFast(
		size = size,
		numOfRangesWithMinSize = numOfRangesWithMinSize,
		minSize = minSize,
		possibleMaxSize = possibleMaxSize,
		plus = Int::plus,
		minus = Int::minus,
		times = Int::times,
		divide = Int::div,
		one = 1,
		two = 2
	)

private fun prefixSumSlower(size: Int, numOfRangesWithMinSize: Int, minSize: Int, possibleMaxSize: Int): Int =
	prefixSumSlower(
		size = size,
		numOfRangesWithMinSize = numOfRangesWithMinSize,
		minSize = minSize,
		possibleMaxSize = possibleMaxSize,
		plus = Int::plus,
		minus = Int::minus,
		times = Int::times,
		divide = Int::div,
		mod = Int::rem,
		zero = 0,
		one = 1,
		two = 2
	)

private fun findSizeMatchingPrefixSumIndex(
	index: Int,
	numOfRangesWithMinSize: Int,
	minSize: Int,
	effectiveMaxSize: Int,
	possibleMaxSize: Int,
) = findSizeMatchingPrefixSumIndex(
	index = index,
	numOfRangesWithMinSize = numOfRangesWithMinSize,
	minSize = minSize,
	effectiveMaxSize = effectiveMaxSize,
	possibleMaxSize = possibleMaxSize,
	plus = Int::plus,
	divide = Int::div,
	one = 1,
	two = 2,
	prefixSum = if (possibleMaxSize <= possibleMaxSizeSafeInIntDomainWithoutDivideTrick) ::prefixSumFast else ::prefixSumSlower
)

private fun numOfRangesWithSize(size: Long, possibleMaxSize: Long) =
	numOfRangesWithSize(
		size = size,
		possibleMaxSize = possibleMaxSize,
		plus = Long::plus,
		minus = Long::minus,
		one = 1L
	)

private fun prefixSumFast(size: Long, numOfRangesWithMinSize: Long, minSize: Long, possibleMaxSize: Long) =
	prefixSumFast(
		size = size,
		numOfRangesWithMinSize = numOfRangesWithMinSize,
		minSize = minSize,
		possibleMaxSize = possibleMaxSize,
		plus = Long::plus,
		minus = Long::minus,
		times = Long::times,
		divide = Long::div,
		one = 1L,
		two = 2L
	)

private fun prefixSumSlower(size: Long, numOfRangesWithMinSize: Long, minSize: Long, possibleMaxSize: Long) =
	prefixSumSlower(
		size = size,
		numOfRangesWithMinSize = numOfRangesWithMinSize,
		minSize = minSize,
		possibleMaxSize = possibleMaxSize,
		plus = Long::plus,
		minus = Long::minus,
		times = Long::times,
		divide = Long::div,
		mod = Long::rem,
		zero = 0L,
		one = 1L,
		two = 2L
	)

private fun findSizeMatchingPrefixSumIndex(
	index: Long,
	numOfRangesWithMinSize: Long,
	minSize: Long,
	effectiveMaxSize: Long,
	possibleMaxSize: Long,
) = findSizeMatchingPrefixSumIndex(
	index = index,
	numOfRangesWithMinSize = numOfRangesWithMinSize,
	minSize = minSize,
	effectiveMaxSize = effectiveMaxSize,
	possibleMaxSize = possibleMaxSize,
	plus = Long::plus,
	divide = Long::div,
	one = 1L,
	two = 2L,
	prefixSum = if (possibleMaxSize <= possibleMaxSizeSafeInLongDomainWithoutDivideTrick) ::prefixSumFast else ::prefixSumSlower
)

private fun numOfRangesWithSize(size: BigInt, possibleMaxSize: BigInt) =
	numOfRangesWithSize(
		size = size,
		possibleMaxSize = possibleMaxSize,
		plus = BigInt::plus,
		minus = BigInt::minus,
		one = BigInt.ONE
	)

private fun prefixSum(size: BigInt, numOfRangesWithMinSize: BigInt, minSize: BigInt, possibleMaxSize: BigInt) =
	prefixSumFast(
		size = size,
		numOfRangesWithMinSize = numOfRangesWithMinSize,
		minSize = minSize,
		possibleMaxSize = possibleMaxSize,
		plus = BigInt::plus,
		minus = BigInt::minus,
		times = BigInt::times,
		divide = BigInt::div,
		one = BigInt.ONE,
		two = BigInt.TWO
	)

private fun findSizeMatchingPrefixSumIndex(
	index: BigInt,
	numOfRangesWithMinSize: BigInt,
	minSize: BigInt,
	effectiveMaxSize: BigInt,
	possibleMaxSize: BigInt
) = findSizeMatchingPrefixSumIndex(
	index = index,
	numOfRangesWithMinSize = numOfRangesWithMinSize,
	minSize = minSize,
	effectiveMaxSize = effectiveMaxSize,
	possibleMaxSize = possibleMaxSize,
	plus = BigInt::plus,
	divide = BigInt::div,
	one = BigInt.ONE,
	two = BigInt.TWO,
	prefixSum = ::prefixSum
)


// we count the valid start of the ranges, the first starts at index=0 and the last possibleMaxSize - size (+1 as the last is inclusive)
private inline fun <NumberT> numOfRangesWithSize(
	size: NumberT,
	possibleMaxSize: NumberT,
	plus: (NumberT, NumberT) -> NumberT,
	minus: (NumberT, NumberT) -> NumberT,
	one: NumberT,
): NumberT where NumberT : Number, NumberT : Comparable<NumberT> = plus(minus(possibleMaxSize, size), one)

private inline fun <NumberT> prefixSumFast(
	size: NumberT,
	numOfRangesWithMinSize: NumberT,
	minSize: NumberT,
	possibleMaxSize: NumberT,
	crossinline plus: (NumberT, NumberT) -> NumberT,
	crossinline minus: (NumberT, NumberT) -> NumberT,
	crossinline times: (NumberT, NumberT) -> NumberT,
	crossinline divide: (NumberT, NumberT) -> NumberT,
	one: NumberT,
	two: NumberT,
): NumberT where NumberT : Number, NumberT : Comparable<NumberT> {
	val n = plus(minus(size, minSize), one)
	val numOfRangesWithGivenSize = numOfRangesWithSize(size, possibleMaxSize, plus, minus, one)
	// note, as far as I can see it is not possible that both n and numOfRangesWithMinSize + numOfRangesWithGivenSize
	// are odd, i.e. the result is always even, and therefore / 2 is safe (no truncation because of Int/Long arithmetic
	// happens)
	return divide(times(n, plus(numOfRangesWithMinSize, numOfRangesWithGivenSize)), two)
}

private inline fun <NumberT> prefixSumSlower(
	size: NumberT,
	numOfRangesWithMinSize: NumberT,
	minSize: NumberT,
	possibleMaxSize: NumberT,
	crossinline plus: (NumberT, NumberT) -> NumberT,
	crossinline minus: (NumberT, NumberT) -> NumberT,
	crossinline times: (NumberT, NumberT) -> NumberT,
	crossinline divide: (NumberT, NumberT) -> NumberT,
	crossinline mod: (NumberT, NumberT) -> NumberT,
	zero: NumberT,
	one: NumberT,
	two: NumberT,
): NumberT where NumberT : Number, NumberT : Comparable<NumberT> {
	val n = plus(minus(size, minSize), one)
	val numOfRangesWithGivenSize = numOfRangesWithSize(size, possibleMaxSize, plus, minus, one)
	// usual formula: n * (first + last) / 2 would overflow, hence we divide first. In order not to truncate (due to
	// int division) need to check if n is even or not (one of both is always even).
	return if (mod(n, two) == zero) {
		times(divide(n, two), plus(numOfRangesWithMinSize, numOfRangesWithGivenSize))
	} else {
		times(n, divide(plus(numOfRangesWithMinSize, numOfRangesWithGivenSize), two))
	}
}

// Binary search size such that prefixSum(size) > index
private inline fun <NumberT> findSizeMatchingPrefixSumIndex(
	index: NumberT,
	numOfRangesWithMinSize: NumberT,
	minSize: NumberT,
	effectiveMaxSize: NumberT,
	possibleMaxSize: NumberT,
	crossinline plus: (NumberT, NumberT) -> NumberT,
	crossinline divide: (NumberT, NumberT) -> NumberT,
	one: NumberT,
	two: NumberT,
	prefixSum: (NumberT, NumberT, NumberT, NumberT) -> NumberT
): NumberT where NumberT : Number, NumberT : Comparable<NumberT> {
	var low = minSize
	var high = effectiveMaxSize
	while (low < high) {
		val mid = divide(plus(low, high), two)
		if (prefixSum(mid, numOfRangesWithMinSize, minSize, possibleMaxSize) > index) {
			high = mid
		} else {
			low = plus(mid, one)
		}
	}
	return low
}
