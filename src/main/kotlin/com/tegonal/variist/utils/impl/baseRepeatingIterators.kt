package com.tegonal.variist.utils.impl

import com.tegonal.variist.utils.BigInt
import com.tegonal.variist.utils.toBigInt

/**
 *
 * Returns an [Iterator] which produces an infinite stream of [T]s based on an [Int] range [from] (inclusive) until
 * [toExclusive] considering the given [step] and [offset].
 *
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @param from The inclusive lower bound of the range (in case [offset] = 0 also the starting number) --
 *   has to be less than [toExclusive].
 *
 * @param toExclusive The non-inclusive upper bound of the range. Put differently. the number which is no longer
 *   included in the range, i.e. if the current number + [step] is greater than [toExclusive] then the next number
 *   is [from] again -- has to be greater than [from].
 *
 * @param offset Influences from which number the range starts where it defines an offset to the [from] of the
 *   range in terms of [step]s, taking [toExclusive] and the infinite character of this iterator into account.
 *   Following a few examples:
 *   - startIndex=1, endIndexExclusive=4, step=1, offset=1 => range starts at 2
 *   - startIndex=1, endIndexExclusive=4, step=2, offset=1 => range starts at 3
 *   - startIndex=1, endIndexExclusive=4, step=2, offset=2 => range starts at 1 (since 5 is already greater than 4 it starts over at 1)
 *   - startIndex=1, endIndexExclusive=4, step=2, offset=3 => range starts at 3
 *
 * @param step Defines the step from the current number to the next - must be greater than 0.
 *
 * @since 2.0.0
 */
abstract class BaseIntFromUntilRepeatingIterator<T>(
	private val from: Int,
	private val toExclusive: Int,
	offset: Int = 0,
	private val step: Int = 1,
) : Iterator<T> {

	private var index: Int

	init {
		checkRangeNumbers(from, toExclusive, offset, step)
		index = determineStartingIndex(from, toExclusive, offset, step)
	}

	override fun hasNext() = true
	override fun next(): T =
		getElementAt(index).also {
			if (index < toExclusive - step) {
				index += step
			} else {
				index = from
			}
		}

	abstract fun getElementAt(index: Int): T
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
abstract class BaseLongFromUntilRepeatingIterator<T>(
	private val from: Long,
	private val toExclusive: Long,
	offset: Long = 0,
	private val step: Long = 1,
) : Iterator<T> {

	private var index: Long

	init {
		checkRangeNumbers(from, toExclusive, offset, step)
		index = determineStartingIndex(
			from.toBigInt(), toExclusive.toBigInt(), offset.toBigInt(), step.toBigInt()
		).toLong()
	}

	override fun hasNext() = true
	override fun next(): T =
		getElementAt(index).also {
			index += step
			if (index >= toExclusive) {
				index = from
			}
		}

	abstract fun getElementAt(index: Long): T
}


/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
abstract class BaseBigIntFromUntilRepeatingIterator<T>(
	private val from: BigInt,
	private val toExclusive: BigInt,
	offset: BigInt = BigInt.ZERO,
	private val step: BigInt = BigInt.ONE,
) : Iterator<T> {

	private var index: BigInt

	init {
		checkRangeNumbers(from, toExclusive, offset, step)
		index = determineStartingIndex(from, toExclusive, offset, step)
	}

	override fun hasNext() = true
	override fun next(): T =
		getElementAt(index).also {
			index += step
			if (index >= toExclusive) {
				index = from
			}
		}

	abstract fun getElementAt(index: BigInt): T
}

/**
 * Convenience method which converts to [Long] and then calls determineStartingIndex and converts the result back to Int
 *
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
fun determineStartingIndex(
	from: Int,
	toExclusive: Int,
	offset: Int,
	step: Int
): Int = determineStartingIndex(from.toLong(), toExclusive.toLong(), offset.toLong(), step.toLong()).toInt()

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
fun determineStartingIndex(
	from: Long,
	toExclusive: Long,
	offset: Long,
	step: Long
) = determineStartingIndex(
	from,
	toExclusive,
	offset,
	step,
	Long::plus,
	Long::minus,
	Long::times,
	Long::div,
	Long::rem,
	1
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
fun determineStartingIndex(
	from: BigInt,
	toExclusive: BigInt,
	offset: BigInt,
	step: BigInt
) = determineStartingIndex(
	from,
	toExclusive,
	offset,
	step,
	BigInt::plus,
	BigInt::minus,
	BigInt::times,
	BigInt::div,
	BigInt::rem,
	BigInt.ONE
)

private inline fun <NumberT> determineStartingIndex(
	from: NumberT,
	toExclusive: NumberT,
	offset: NumberT,
	step: NumberT,
	crossinline plus: (NumberT, NumberT) -> NumberT,
	crossinline minus: (NumberT, NumberT) -> NumberT,
	crossinline times: (NumberT, NumberT) -> NumberT,
	crossinline divide: (NumberT, NumberT) -> NumberT,
	crossinline mod: (NumberT, NumberT) -> NumberT,
	one: NumberT,
): NumberT where NumberT : Number, NumberT : Comparable<NumberT> {
	val elementsInRange = minus(toExclusive, from)
	val maxNumberOfSteps = if (step == one) elementsInRange else divide(plus(elementsInRange, minus(step, one)), step)
	val offsetInRange = mod(offset, maxNumberOfSteps)
	val offsetInRangeInSteps = times(offsetInRange, step)
	return plus(from, offsetInRangeInSteps)
}
