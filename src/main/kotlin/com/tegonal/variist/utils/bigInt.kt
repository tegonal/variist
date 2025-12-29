package com.tegonal.variist.utils

import com.tegonal.variist.utils.impl.checkIsPositive
import com.tegonal.variist.utils.impl.requireFromLessThanToExclusive
import java.math.BigInteger
import kotlin.random.Random
import kotlin.random.asJavaRandom

/**
 * @since 2.0.0
 */
typealias BigInt = BigInteger

/**
 * Converts `this` [Int] to a [BigInt].
 *
 * @since 2.0.0
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Int.toBigInt() = this.toBigInteger()

/**
 * Converts `this` [Long] to a [BigInt].
 *
 * @since 2.0.0
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Long.toBigInt() = this.toBigInteger()

/**
 * Generates a random [BigInt] uniformly distributed between [from] (inclusive) and the specified [toExclusive] bound.
 *
 * @param from defines the lower bound (inclusive), must be less than [toExclusive].
 * @param toExclusive defines the exclusive upper bound.
 *
 * @throws IllegalArgumentException if [from] is not less than [toExclusive]
 *
 * @since 2.0.0
 */
fun Random.nextBigInt(from: BigInt, toExclusive: BigInt): BigInt {
	requireFromLessThanToExclusive(from, toExclusive)
	val range = toExclusive - from
	val result = nextBigInt(range)
	return result + from
}

/**
 * Generates a random [BigInt] uniformly distributed between `0` (inclusive) and the specified [toExclusive] bound.
 *
 * @param toExclusive defines the exclusive upper bound and must be positive.
 *
 * @throws IllegalStateException if [toExclusive] is negative or zero.
 *
 * @since 2.0.0
 */
fun Random.nextBigInt(toExclusive: BigInt): BigInt {
	checkIsPositive(toExclusive, "toExclusive")
	val bitLength = toExclusive.bitLength()
	when {
		bitLength <= 31 -> return nextInt(toExclusive.toInt()).toBigInt()
		bitLength <= 63 -> return nextLong(toExclusive.toLong()).toBigInt()
		else -> {
			val javaRandom = this.asJavaRandom()
			var count = 0
			val maxTries = 50
			while (true) {
				val candidate = BigInteger(bitLength, javaRandom)
				// rejection sampling: as we generate 0..2^bitLength - 1 which might be greater than or equal to
				// toExclusive, we only accept a candidate which fits into the requested range and retry otherwise.
				// Chances to pick a wrong candidate is in the worst case (toExclusive = 2^x+1) at nearly 50%, i.e.
				// we should quickly get a candidate in most cases. To prevent that we run into a bug in case of a buggy
				// random we stop after 50 tries (in theory, 50 times a wrong candidate in a row could happen even for
				// a non-buggy random but the chances are (1/2)^50, so very unlikely and a buggy random more likely).
				if (candidate < toExclusive) return candidate
				else if (count >= maxTries) error("looks like we deal with a non uniform-random ($this), could not find a candidate after $count tries -- are you mocking Random? If so, adjust your mocking logic, if not, then try to re-run your test and report the buggy random in case this error re-appears")
				else ++count
			}
		}
	}
}
