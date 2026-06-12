package com.tegonal.variist.generators.impl

import com.tegonal.variist.config._components
import com.tegonal.variist.generators.ArbArgsGenerator
import com.tegonal.variist.utils.deriveChildSeedOffset

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.1.0
 */
abstract class BaseMultiArbArgsMerger<T>(
	firstGenerator: ArbArgsGenerator<T>,
	secondGenerator: ArbArgsGenerator<T>,
	otherGenerators: Array<out ArbArgsGenerator<T>>,
) : BaseArbArgsGenerator<T>(
	// note, we don't (and cannot) check that a1Generator and a2Generator use the same ComponentContainer,
	// should you run into weird behaviour (such as one generator uses seed X and the other seed Y) then most likely
	// someone used two different initial factories
	firstGenerator._components,
) {
	protected val totalGenerators = otherGenerators.size + 2
	protected val generators = Array(totalGenerators) { index ->
		when (index) {
			0 -> firstGenerator
			1 -> secondGenerator
			else -> otherGenerators[index - 2]
		}
	}

	final override fun generateOne(seedOffset: Int): T {
		val index = getFirstIndex(seedOffset)
		return generators[index].generateOne(deriveChildSeedOffset(seedOffset, index + 1))
	}

	final override fun generate(seedOffset: Int): Sequence<T> = Sequence { iteratorFactory(seedOffset) }

	protected abstract fun getFirstIndex(seedOffset: Int): Int
	protected abstract fun iteratorFactory(seedOffset: Int): IteratorsWithOffset<T>

	protected abstract class IteratorsWithOffset<T>(
		generators: Array<ArbArgsGenerator<T>>,
		seedOffset: Int
	) : Iterator<T> {
		private val iterators = Array<Iterator<T>>(generators.size) { index ->
			generators[index].generate(deriveChildSeedOffset(seedOffset, index + 1)).iterator()
		}

		final override fun hasNext(): Boolean = true
		final override fun next(): T = iterators[nextIndex()].next()

		protected abstract fun nextIndex(): Int
	}
}
