package com.tegonal.variist.generators

import com.tegonal.variist.config._components
import com.tegonal.variist.config.createVariistRandom
import com.tegonal.variist.generators.impl.mapIndexedInternal
import com.tegonal.variist.utils.impl.failIfNegative
import kotlin.random.Random

/**
 * Returns an [ArbArgsGenerator] which generates [String]s with [minLength] and [maxLength] (inclusive) using
 * the given allowed [UnicodeRange]s.
 *
 * You can use one of the predefined [UnicodeRanges.ranges] such as [UnicodeRanges.ASCII_PRINTABLE]
 *
 * @param maxLength defines the maximum length the string can have.
 * @param minLength defined the minimum length the string shall have.
 * @param allowedRanges [UnicodeRange]s which define what Unicode chars can be used.
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.string(
	maxLength: Int,
	minLength: Int = 0,
	allowedRanges: Array<out UnicodeRange> = UnicodeRanges.UTF_8_PRINTABLE.ranges
): ArbArgsGenerator<String> {
	failIfNegative(minLength, "minLength")
	require(minLength <= maxLength) {
		"minLength ($minLength) must be less than or equal to maxLength ($maxLength)"
	}
	require(allowedRanges.isNotEmpty()) {
		"you need to define at least one ${UnicodeRange::class.simpleName}"
	}
	// we check in createNextRandomCodePointFun if the given allowedRanges allow to generate a string with minLength/maxLength

	val (nextCodePoint, nextBmpCodePoint) = createNextRandomCodePointFun(allowedRanges, minLength, maxLength)

	val componentFactoryContainer = _components
	return intFromTo(minLength, maxLength).mapIndexedInternal { index, length, seedOffset ->
		// +1 so we don't use the same seed as intFromTo otherwise for the first string it will always be:
		// length of the resulting string = first code point
		componentFactoryContainer.createVariistRandom(seedBaseOffset + seedOffset + index + 1).let { random ->
			val sb = StringBuilder(length)
			var count = 0

			while (count + 2 <= length) {
				val codePoint = random.nextCodePoint()
				sb.appendCodePoint(codePoint)
				count += if (Character.isBmpCodePoint(codePoint)) 1 else 2
			}

			// can only happen if the requested length == minLength and minLength is odd
			// in such a case we exit early in the while loop as non-bpm chars would exceed the chosen `length`, which
			// means if length=maxLength then we would generate a string which is longer than requested
			if (sb.length < minLength) {
				if (nextBmpCodePoint != null) {
					val codePoint = random.nextBmpCodePoint()
					sb.appendCodePoint(codePoint)
				} else {
					error("buggy constellation detected, nextBmpCodePoint is null but we were not able to reach length $minLength - i.e. checkCanGenerateRequiredLength doesn't seem to work correctly")
				}
			}
			sb.toString()
		}
	}
}

private fun createNextRandomCodePointFun(
	allowedRanges: Array<out UnicodeRange>,
	minLength: Int,
	maxLength: Int
): Pair<Random.() -> Int, (Random.() -> Int)?> = when (allowedRanges.size) {
	1 -> allowedRanges.first().let { range ->
		val isBmpRange = range.isBmpRange()
		checkCanGenerateRequiredLength(
			minLength = minLength,
			maxLength = maxLength,
			hasAtLeastOneBmpRange = isBmpRange
		)
		val lambda: Random.() -> Int = { nextInt(range.start, range.endInclusive + 1) }
		lambda to lambda.takeIf { range.isBmpRange() }
	}

	else -> {
		val (bmpRanges, nonBmpRanges) = allowedRanges.partition {
			Character.isBmpCodePoint(it.start)
		}

		val hasBmpRange = bmpRanges.isNotEmpty()
		checkCanGenerateRequiredLength(
			minLength = minLength,
			maxLength = maxLength,
			hasAtLeastOneBmpRange = hasBmpRange
		)

		fun sizes(range: List<UnicodeRange>) =
			range.map {
				// Int is safe for Unicode, no need to convert toLong() (unless someone defined overlapping ranges,
				// we ignore this case here, if it overflows then it can be seen as consequential bug)
				it.endInclusive - it.start + 1
			}

		val bmpSizes = sizes(bmpRanges)
		val nonBmpSizes = sizes(nonBmpRanges)

		val bmpCumulativeSizes = bmpSizes.runningReduce(Int::plus)
		val bmpTotalSize = bmpCumulativeSizes.last()
		val (cumulativeSizes, totalSize) = when (nonBmpSizes.size) {
			0 -> bmpCumulativeSizes to bmpTotalSize
			1 -> (nonBmpSizes.first() + bmpTotalSize).let { (bmpCumulativeSizes + listOf(it)) to it }
			else -> run {
				bmpCumulativeSizes +
					(listOf(nonBmpSizes.first() + bmpTotalSize) + nonBmpSizes.drop(1)).runningReduce(Int::plus)
			}.let {
				it to it.last()
			}
		}

		Pair(
			{
				nextCodepoint(allowedRanges, cumulativeSizes, totalSize)
			},
			{
				nextCodepoint(bmpRanges.toTypedArray(), bmpCumulativeSizes, bmpTotalSize)
			}
		)
	}
}

private fun Random.nextCodepoint(ranges: Array<out UnicodeRange>, cumulativeSizes: List<Int>, totalSize: Int): Int {
	//TODO 2.1.0 bench if it would be faster to use linear search in case there are not many allowedRanges
	val index = nextInt(totalSize)
	return cumulativeSizes.binarySearch(index).let {
		if (it >= 0) {
			// the randomly chosen index was exactly the cumulative size of a range
			// i.e. we don't need to calculate the range offset
			ranges[it].start
		} else {
			//  binarySearch returned the `inverted insertion point` and we invert it again to retrieve
			//  the rangeIndex where the cumulative size was greater than index
			val rangeIndex = -it - 1
			val range = ranges[rangeIndex]
			// but that means that we need to calculate the offset in that range
			val cumulativeSizeOfPreviousRange = if (rangeIndex == 0) 0 else cumulativeSizes[rangeIndex - 1]
			val offsetInRange = index - cumulativeSizeOfPreviousRange
			range.start + offsetInRange
		}
	}
}

private fun checkCanGenerateRequiredLength(
	minLength: Int,
	maxLength: Int,
	hasAtLeastOneBmpRange: Boolean
) {
	require(hasAtLeastOneBmpRange || minLength % 2 == 0) {
		"minLength (${minLength}) is odd but none of the allowedRanges is in the BPM domain, which means Variist will not be able to generate such a string"
	}
	require(hasAtLeastOneBmpRange || maxLength % 2 == 0) {
		"maxLength (${maxLength}) is odd but none of the allowedRanges is in the BPM domain, which means Variist will not be able to generate such a string"
	}
}

//TODO 2.1.0 combinations on case
//arb.stringRandomCase(s: String)

