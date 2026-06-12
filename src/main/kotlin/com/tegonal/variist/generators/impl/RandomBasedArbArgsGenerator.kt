package com.tegonal.variist.generators.impl

import com.tegonal.variist.config.ComponentFactoryContainer
import com.tegonal.variist.utils.repeatForever
import kotlin.random.Random

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
abstract class RandomBasedArbArgsGenerator<T>(
	componentFactoryContainer: ComponentFactoryContainer,
) : BaseArbArgsGenerator<T>(componentFactoryContainer) {

	override fun generateOne(seedOffset: Int): T =
		// Random is not thread safe, add synchronisation in case we run into issues
		createVariistRandom(seedOffset).nextElement()

	override fun generate(seedOffset: Int): Sequence<T> = createVariistRandom(seedOffset).let { random ->
		repeatForever().map {
			// Random is not thread safe, add synchronisation in case we run into issues
			random.nextElement()
		}
	}

	protected abstract fun Random.nextElement(): T
}

