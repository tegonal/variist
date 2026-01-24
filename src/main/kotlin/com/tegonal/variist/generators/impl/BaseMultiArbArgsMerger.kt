package com.tegonal.variist.generators.impl

import com.tegonal.variist.config._components
import com.tegonal.variist.generators.ArbArgsGenerator

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
	seedBaseOffset: Int,
) : BaseArbArgsGenerator<T>(
	// note, we don't (and cannot) check that a1Generator and a2Generator use the same ComponentContainer,
	// should you run into weird behaviour (such as one generator uses seed X and the other seed Y) then most likely
	// someone used two different initial factories
	firstGenerator._components,
	seedBaseOffset
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
		return generators[index].generateOne(
			seedOffset +
				// + index since we do the same in generate (and generateOne should be the same as generate().first())
				// to be more precise, we do the same in IteratorsWithOffset when creating the iterators, see
				// explanation there
				index
		)
	}

	final override fun generate(seedOffset: Int): Sequence<T> = Sequence { iteratorFactory(seedOffset) }

	protected abstract fun getFirstIndex(seedOffset: Int): Int
	protected abstract fun iteratorFactory(seedOffset: Int): IteratorsWithOffset<T>

	protected abstract class IteratorsWithOffset<T>(
		generators: Array<ArbArgsGenerator<T>>,
		seedOffset: Int
	) : Iterator<T> {
		private val iterators = Array<Iterator<T>>(generators.size) { index ->
			generators[index].generate(
				// we use `+ index` in order that we use different seedOffsets. This way, even if one e.g. merges the
				// same generator multiple times they will get different results. Without this, if one specified
				// the same generator say 3 times, always get 3 times the same value, then 3 times again the next
				// value etc. because each sequence would start with the same random seed. The same applies for overlapping
				// generators such as arb.intFromUntil(1, 10) and arb.intFromUntil(1,20). Each time Random generators a
				// number % 20 < 10, then both generators would yield the same value.
				seedOffset + index
			).iterator()
		}
		final override fun hasNext(): Boolean = true
		final override fun next(): T = iterators[nextIndex()].next()

		protected abstract fun nextIndex(): Int
	}
}
