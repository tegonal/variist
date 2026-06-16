package com.tegonal.variist.generators.impl

import com.tegonal.variist.config._components
import com.tegonal.variist.generators.ArbArgsGenerator
import com.tegonal.variist.generators.SemiOrderedArgsGenerator
import com.tegonal.variist.utils.deriveTwoChildSeedOffsets

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class SemiOrderedZipArbArgsGenerator<A1, A2, R>(
	private val semiOrderedArgsGenerator: SemiOrderedArgsGenerator<A1>,
	private val arbArgsGenerator: ArbArgsGenerator<A2>,
	private val transform: (A1, A2) -> R
) : BaseSemiOrderedArgsGenerator<R>(semiOrderedArgsGenerator._components, semiOrderedArgsGenerator.size) {

	override fun generateAfterChecks(offset: Int, seedOffset: Int): Sequence<R> {
		val (seedOffset1, seedOffset2) = deriveTwoChildSeedOffsets(seedOffset)
		return zipForever(
			semiOrderedArgsGenerator.generate(offset, seedOffset1),
			arbArgsGenerator.generate(seedOffset2),
			transform
		)
	}
}
