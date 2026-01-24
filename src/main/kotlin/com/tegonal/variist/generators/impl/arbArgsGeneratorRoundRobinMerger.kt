package com.tegonal.variist.generators.impl

import com.tegonal.variist.config._components
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
	seedBaseOffset: Int,
) : BaseArbArgsGenerator<T>(
	// note, we don't (and cannot) check that a1Generator and a2Generator use the same ComponentContainer,
	// should you run into weird behaviour (such as one generator uses seed X and the other seed Y) then most likely
	// someone used two different initial factories
	firstGenerator._components,
	seedBaseOffset
), ArbArgsGenerator<T> {
	private val totalGenerators = otherGenerators.size + 2
	private val generators = Array(totalGenerators) { index ->
		when (index) {
			0 -> firstGenerator
			1 -> secondGenerator
			else -> otherGenerators[index - 2]
		}
	}

	override fun generateOne(seedOffset: Int): T {
		val index = (seedBaseOffset + seedOffset) % totalGenerators
		return generators[index].generateOne(seedOffset)
	}

	override fun generate(seedOffset: Int): Sequence<T> = Sequence {
		object : Iterator<T> {
			private val iterators = arbGeneratorsToIterators(generators, seedOffset)
			private var index = (seedBaseOffset + seedOffset) % totalGenerators

			override fun hasNext(): Boolean = true
			override fun next(): T = iterators[index].next().also {
				if (index + 1 < totalGenerators) ++index else index = 0
			}
		}
	}
}
