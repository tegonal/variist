package com.tegonal.variist.generators.impl

import com.tegonal.variist.generators.ArbArgsGenerator

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.1.0
 */
class MultiArbArgsGeneratorRoundRobinMerger<T>(
	firstGenerator: ArbArgsGenerator<T>,
	secondGenerator: ArbArgsGenerator<T>,
	otherGenerators: Array<out ArbArgsGenerator<T>>,
) : BaseMultiArbArgsMerger<T>(firstGenerator, secondGenerator, otherGenerators) {

	override fun getFirstIndex(seedOffset: Int) = seedOffset % totalGenerators

	override fun iteratorFactory(seedOffset: Int): IteratorsWithOffset<T> =
		object : IteratorsWithOffset<T>(generators, seedOffset) {
			private var index = getFirstIndex(seedOffset)

			override fun nextIndex(): Int = index.also {
				++index
				if (index >= totalGenerators) index = 0
			}
		}
}
