package com.tegonal.variist.generators.impl

import com.tegonal.variist.config.ComponentFactoryContainer
import com.tegonal.variist.generators.OrderedArgsGenerator
import com.tegonal.variist.generators.map
import com.tegonal.variist.utils.BigInt
import com.tegonal.variist.utils.impl.BigIntFromUntilRepeatingIterator
import com.tegonal.variist.utils.impl.IntFromUntilRepeatingIterator
import com.tegonal.variist.utils.impl.LongFromUntilRepeatingIterator
import com.tegonal.variist.utils.impl.checkRangeNumbers
import com.tegonal.variist.utils.impl.determineStartingIndex
import com.tegonal.variist.utils.toBigInt

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class IntFromUntilOrderedArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	private val from: Int,
	private val toExclusive: Int,
	private val step: Int,
) : BaseSemiOrderedArgsGenerator<Int>(
	componentFactoryContainer,
	run {
		// we first check the numbers before calculating the size as the size would be wrong
		// if the invariants are not given
		checkRangeNumbers(from, toExclusive, offset = 0, step = step)
		// range size could be bigger than Int.MAX_VALUE, hence we use toLong
		calculatedRangeSizeToArgsGeneratorSize(from.toLong(), toExclusive.toLong(), step.toLong())
	}
), OrderedArgsGenerator<Int> {

	override fun generateOneAfterChecks(offset: Int): Int {
		// index = value so we can directly return it
		return determineStartingIndex(from, toExclusive, offset, step)
	}

	override fun generateAfterChecks(offset: Int): Sequence<Int> = Sequence {
		IntFromUntilRepeatingIterator(from, toExclusive, offset = offset, step = step)
	}

	//TODO 2.5.0 bench if overriding toArbArgsGenerator with delegating to arb.fromProgression would be faster than the
	// default implementation.
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class LongFromUntilOrderedArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	private val from: Long,
	private val toExclusive: Long,
	private val step: Long,
) : BaseSemiOrderedArgsGenerator<Long>(
	componentFactoryContainer,
	run {
		// we first check the numbers before calculating the size as the size would be wrong
		// if the invariants are not given
		checkRangeNumbers(from, toExclusive, offset = 0, step = step)
		// range size could be bigger than Int.MAX_VALUE, hence we use toBigInt
		calculatedRangeSizeToArgsGeneratorSize(from.toBigInt(), toExclusive.toBigInt(), step.toBigInt())
	}
), OrderedArgsGenerator<Long> {

	override fun generateOneAfterChecks(offset: Int): Long {
		// index = value so we can directly return it
		return determineStartingIndex(
			// toExclusive - from could overflow, so we use BigInt
			from.toBigInt(), toExclusive.toBigInt(), offset.toBigInt(), step.toBigInt()
		).toLong()
	}

	override fun generateAfterChecks(offset: Int): Sequence<Long> = Sequence {
		LongFromUntilRepeatingIterator(from, toExclusive, offset = offset.toLong(), step = step)
	}
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class BigIntFromUntilOrderedArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	private val from: BigInt,
	private val toExclusive: BigInt,
	private val step: BigInt,
) : BaseSemiOrderedArgsGenerator<BigInt>(
	componentFactoryContainer,
	run {
		// we first check the numbers before calculating the size as the size would be wrong
		// if the invariants are not given
		checkRangeNumbers(from, toExclusive, offset = BigInt.ZERO, step = step)
		calculatedRangeSizeToArgsGeneratorSize(from, toExclusive, step)
	}
), OrderedArgsGenerator<BigInt> {

	override fun generateOneAfterChecks(offset: Int): BigInt {
		// index = value so we can directly return it
		return determineStartingIndex(from, toExclusive, offset.toBigInt(), step)
	}

	override fun generateAfterChecks(offset: Int): Sequence<BigInt> = Sequence {
		BigIntFromUntilRepeatingIterator(from, toExclusive, offset = offset.toBigInt(), step = step)
	}
}

private fun calculatedRangeSizeToArgsGeneratorSize(
	from: Long,
	toExclusive: Long,
	step: Long,
) = calculatedRangeSizeToArgsGeneratorSize(from, toExclusive, step, Long::minus, Long::plus, Long::div, one = 1)

private fun calculatedRangeSizeToArgsGeneratorSize(
	from: BigInt,
	toExclusive: BigInt,
	step: BigInt,
) = calculatedRangeSizeToArgsGeneratorSize(
	from,
	toExclusive,
	step,
	BigInt::minus,
	BigInt::plus,
	BigInt::div,
	one = BigInt.ONE
)


private inline fun <NumberT> calculatedRangeSizeToArgsGeneratorSize(
	from: NumberT,
	toExclusive: NumberT,
	step: NumberT,
	crossinline minus: (NumberT, NumberT) -> NumberT,
	crossinline plus: (NumberT, NumberT) -> NumberT,
	crossinline divide: (NumberT, NumberT) -> NumberT,
	one: NumberT,
): NumberT where NumberT : Number, NumberT : Comparable<NumberT> {
	val elementsInRange = minus(toExclusive, from)
	val roundUpTrick = plus(elementsInRange, minus(step, one))
	val numberOfSteps = divide(roundUpTrick, step)
	// if step is greater than the range size we get 0 as a result but the progression's size is 1 in such a case
	return maxOf(one, numberOfSteps)
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
@Suppress("FunctionName")
fun IntFromToOrderedArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: Int,
	toInclusive: Int,
	step: Int,
): OrderedArgsGenerator<Int> =
	if (toInclusive == Int.MAX_VALUE) {
		//TODO 2.5.0 bench what is better (speed vs. memory), this approach or if we would shift the range if possible?
		LongFromUntilOrderedArgsGenerator(
			componentFactoryContainer,
			from.toLong(),
			toInclusive.toLong() + 1,
			step.toLong()
		).map { it.toInt() }
	} else IntFromUntilOrderedArgsGenerator(componentFactoryContainer, from, toInclusive + 1, step)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
@Suppress("FunctionName")
fun LongFromToOrderedArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: Long,
	toInclusive: Long,
	step: Long,
): OrderedArgsGenerator<Long> =
	if (toInclusive == Long.MAX_VALUE) {
		//TODO 2.5.0 bench what is better (speed vs. memory), this approach or if we would shift the range
		BigIntFromUntilOrderedArgsGenerator(
			componentFactoryContainer,
			from.toBigInt(),
			toInclusive.toBigInt() + BigInt.ONE,
			step.toBigInt()
		).map { it.toLong() }
	} else LongFromUntilOrderedArgsGenerator(componentFactoryContainer, from, toInclusive + 1, step)
