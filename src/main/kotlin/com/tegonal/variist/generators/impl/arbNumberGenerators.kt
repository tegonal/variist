package com.tegonal.variist.generators.impl

import com.tegonal.variist.config.ComponentFactoryContainer
import com.tegonal.variist.generators.ArbArgsGenerator
import com.tegonal.variist.generators.map
import com.tegonal.variist.utils.BigInt
import com.tegonal.variist.utils.nextBigInt
import com.tegonal.variist.utils.toBigInt
import kotlin.random.Random

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class ArbIntArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
) : RandomBasedArbArgsGenerator<Int>(componentFactoryContainer) {

	override fun Random.nextElement(): Int = nextInt()
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class ArbLongArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
) : RandomBasedArbArgsGenerator<Long>(componentFactoryContainer) {

	override fun Random.nextElement(): Long = nextLong()
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class ArbDoubleArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
) : RandomBasedArbArgsGenerator<Double>(componentFactoryContainer) {

	override fun Random.nextElement(): Double = nextDouble()
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class IntFromUntilArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: Int,
	toExclusive: Int
) : OpenEndRangeBasedArbArgsGenerator<Int>(componentFactoryContainer, from, toExclusive) {

	override fun nextElementInRange(random: Random): Int = random.nextInt(from, toExclusive)
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
@Suppress("FunctionName")
fun IntFromToArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: Int,
	toInclusive: Int,
): ArbArgsGenerator<Int> =
	if (from == toInclusive) {
		ConstantArbArgsGenerator(componentFactoryContainer, from)
	} else if (toInclusive == Int.MAX_VALUE) {
		if (from == Int.MIN_VALUE) ArbIntArgsGenerator(componentFactoryContainer)
		else {
			//TODO 3.5.0 bench what is better (speed vs. memory), this approach or if we would shift the range
			LongFromUntilArbArgsGenerator(
				componentFactoryContainer,
				from.toLong(),
				toInclusive.toLong() + 1
			).map { it.toInt() }
		}
	} else IntFromUntilArbArgsGenerator(componentFactoryContainer, from, toInclusive + 1)


/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
@Suppress("FunctionName")
fun LongFromToArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: Long,
	toInclusive: Long,
): ArbArgsGenerator<Long> =
	if (from == toInclusive) {
		ConstantArbArgsGenerator(componentFactoryContainer, from)
	} else if (toInclusive == Long.MAX_VALUE) {
		if (from == Long.MIN_VALUE) {
			ArbLongArgsGenerator(componentFactoryContainer)
		} else {
			BigIntFromUntilArbArgsGenerator(
				componentFactoryContainer,
				from.toBigInt(),
				toInclusive.toBigInt() + BigInt.ONE
			).map { it.toLong() }
		}
	} else LongFromUntilArbArgsGenerator(componentFactoryContainer, from, toInclusive + 1)


/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class LongFromUntilArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: Long,
	toExclusive: Long,
) : OpenEndRangeBasedArbArgsGenerator<Long>(componentFactoryContainer, from, toExclusive) {
	override fun nextElementInRange(random: Random): Long = random.nextLong(from, toExclusive)
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class DoubleFromUntilArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: Double,
	toExclusive: Double,
) : OpenEndRangeBasedArbArgsGenerator<Double>(componentFactoryContainer, from, toExclusive) {
	override fun nextElementInRange(random: Random): Double = random.nextDouble(from, toExclusive)
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class BigIntFromUntilArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: BigInt,
	toExclusive: BigInt,
) : OpenEndRangeBasedArbArgsGenerator<BigInt>(componentFactoryContainer, from, toExclusive) {
	override fun nextElementInRange(random: Random): BigInt = random.nextBigInt(from, toExclusive)
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
@Suppress("FunctionName")
fun BigIntFromToArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: BigInt,
	toInclusive: BigInt,
): ArbArgsGenerator<BigInt> =
	if (from == toInclusive) {
		ConstantArbArgsGenerator(componentFactoryContainer, from)
	} else {
		BigIntFromUntilArbArgsGenerator(componentFactoryContainer, from, toInclusive + BigInt.ONE)
	}
