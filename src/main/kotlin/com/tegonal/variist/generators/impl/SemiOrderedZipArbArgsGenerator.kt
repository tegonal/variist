package com.tegonal.variist.generators.impl

import com.tegonal.variist.config._components
import com.tegonal.variist.generators.ArbArgsGenerator
import com.tegonal.variist.generators.SemiOrderedLikeArgsGenerator
import com.tegonal.variist.utils.deriveTwoChildSeedOffsets

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class SemiOrderedZipArbArgsGenerator<A1, A2, R>(
	private val baseGenerator: SemiOrderedLikeArgsGenerator<A1>,
	private val arbArgsGenerator: ArbArgsGenerator<A2>,
	private val transform: (A1, A2) -> R
) : BaseSemiOrderedArgsGenerator<R>(baseGenerator._components, baseGenerator.size) {

	override fun generateAfterChecks(offset: Int, seedOffset: Int): Sequence<R> {
		val (seedOffset1, seedOffset2) = deriveTwoChildSeedOffsets(seedOffset)
		return zipForever(
			baseGenerator.generate(offset, seedOffset1),
			arbArgsGenerator.generate(seedOffset2),
			transform
		)
	}
}
