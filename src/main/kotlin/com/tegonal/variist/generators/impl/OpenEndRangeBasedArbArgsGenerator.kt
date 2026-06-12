package com.tegonal.variist.generators.impl

import com.tegonal.variist.config.ComponentFactoryContainer
import com.tegonal.variist.utils.impl.requireFromLessThanToExclusive
import kotlin.random.Random

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
abstract class OpenEndRangeBasedArbArgsGenerator<T : Comparable<T>>(
	componentFactoryContainer: ComponentFactoryContainer,
	protected val from: T,
	protected val toExclusive: T,
) : RandomBasedArbArgsGenerator<T>(componentFactoryContainer) {
	init {
		requireFromLessThanToExclusive(from, toExclusive)
	}

	final override fun Random.nextElement(): T = nextElementInRange(this)

	protected abstract fun nextElementInRange(random: Random): T
}

