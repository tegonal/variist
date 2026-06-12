package com.tegonal.variist.generators.impl

import com.tegonal.variist.config.ComponentFactoryContainer
import com.tegonal.variist.utils.impl.requireFromLessThanOrEqualToExclusive
import kotlin.random.Random

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
abstract class ClosedRangeBasedArbArgsGenerator<T : Comparable<T>>(
	componentFactoryContainer: ComponentFactoryContainer,
	protected val from: T,
	protected val toInclusive: T,
) : RandomBasedArbArgsGenerator<T>(componentFactoryContainer) {
	init {
		requireFromLessThanOrEqualToExclusive(from, toInclusive)
	}

	final override fun Random.nextElement(): T = nextElementInRange(this)

	protected abstract fun nextElementInRange(random: Random): T
}

