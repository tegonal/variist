package com.tegonal.variist.utils.impl

import ch.tutteli.kbox.failIf
import com.tegonal.variist.utils.BigInt

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
fun String.toPositiveIntOrErrorNotValid(convertTo: String): Int =
	toIntOrErrorNotValid(convertTo).also {
		checkIsPositive(it, convertTo)
	}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
fun String.toIntOrErrorNotValid(convertTo: String): Int =
	toIntOrNull() ?: error("$this is not a valid Int, cannot use it for $convertTo")

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
fun checkIsPositive(value: Int, description: String) =
	checkIsPositive(value, 0, description)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
fun checkIsPositive(value: Int, description: () -> String) =
	check(value > 0) { "$value is not a valid ${description()}, must be greater than 0" }

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
fun checkIsPositive(value: Long, description: String) =
	checkIsPositive(value, 0L, description)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
fun checkIsPositive(value: BigInt, description: String) =
	checkIsPositive(value, BigInt.ZERO, description)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
@Suppress("NOTHING_TO_INLINE")
private inline fun <NumberT : Comparable<NumberT>> checkIsPositive(value: NumberT, zero: NumberT, description: String) {
	check(value > zero) { "$value is not a valid $description, must be greater than 0" }
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
fun failIfNegative(value: Int, description: String) = failIfNegative(value, 0, description)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
fun failIfNegative(value: Long, description: String) = failIfNegative(value, 0, description)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
@Suppress("NOTHING_TO_INLINE")
private inline fun <NumberT : Comparable<NumberT>> failIfNegative(value: NumberT, zero: NumberT, description: String) =
	failIf(value < zero) { "$value is not a valid $description, must be be greater than or equal to $zero" }


/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
fun checkIsNotBlank(value: String, description: String) =
	check(value.isNotBlank()) { "$description was blank (was $value)" }

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
fun <T> requireNoDuplicates(list: List<T>, errMsg: (duplicates: Set<T>) -> String) {
	val seen = HashSet<T>(list.size)
	val duplicates = HashSet<T>(4)
	list.forEach {
		if (seen.add(it).not()) {
			duplicates.add(it)
		}
	}
	require(duplicates.isEmpty()) {
		errMsg(duplicates)
	}
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
fun <E : Comparable<E>> requireFromLessThanToExclusive(from: E, toExclusive: E) =
	require(from < toExclusive) {
		"from ($from) must be be less than toExclusive ($toExclusive)"
	}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
fun <E : Comparable<E>> requireFromLessThanOrEqualToExclusive(from: E, toInclusive: E) =
	require(from <= toInclusive) {
		"from ($from) must be be less than or equal to toInclusive ($toInclusive)"
	}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
fun checkRangeNumbers(
	from: Int,
	toExclusive: Int,
	offset: Int,
	step: Int
) = checkRangeNumbers(from, toExclusive, offset, step, zero = 0)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
fun checkRangeNumbers(
	from: Long,
	toExclusive: Long,
	offset: Long,
	step: Long
) = checkRangeNumbers(from, toExclusive, offset, step, zero = 0)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
fun checkRangeNumbers(
	from: BigInt,
	toExclusive: BigInt,
	offset: BigInt,
	step: BigInt
) = checkRangeNumbers(from, toExclusive, offset, step, zero = BigInt.ZERO)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
@Suppress("NOTHING_TO_INLINE")
private inline fun <NumberT> checkRangeNumbers(
	from: NumberT,
	toExclusive: NumberT,
	offset: NumberT,
	step: NumberT,
	zero: NumberT
) where NumberT : Number, NumberT : Comparable<NumberT> {
	check(from < toExclusive) {
		// we know, we expose an internal name to the user
		"RepeatingIterator only supports that startIndex ($from) is less than endIndexExclusive ($toExclusive) - feel free to open a feature request ${FEATURE_REQUEST_URL}&title=RepeatingIterator%20downwards"
	}
	check(step > zero) {
		"RepeatingIterator only supports a positive step (given $step) - feel free to open a feature request ${FEATURE_REQUEST_URL}&title=RepeatingIterator%20downwards"
	}
	check(offset >= zero) {
		"RepeatingIterator only supports an offset greater than or equal to 0 (given $offset)"
	}
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
fun checkRequestedMinArgsMaxArgs(
	requestedMinArgs: Int?,
	maxArgs: Int?
) {
	requestedMinArgs?.also { checkIsPositive(it, "requestedMinArgs") }
	maxArgs?.also { checkIsPositive(it, "maxArgs") }
	check(requestedMinArgs == null || maxArgs == null || requestedMinArgs <= maxArgs) {
		"requestedMinArgs ($requestedMinArgs) must be less than or equal to maxArgs ($maxArgs)"
	}
}
