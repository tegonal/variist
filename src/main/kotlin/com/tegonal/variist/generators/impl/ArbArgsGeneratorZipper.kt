package com.tegonal.variist.generators.impl

import com.tegonal.variist.config._components
import com.tegonal.variist.generators.ArbArgsGenerator
import com.tegonal.variist.utils.deriveTwoChildSeedOffsets

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.3.0
 */
class ArbArgsGeneratorZipper<A1, A2, R>(
	private val a1ArbArgsGenerator: ArbArgsGenerator<A1>,
	private val a2ArbArgsGenerator: ArbArgsGenerator<A2>,
	private val transform: (A1, A2) -> R
) : BaseArbArgsGenerator<R>(a1ArbArgsGenerator._components) {

	override fun generate(seedOffset: Int): Sequence<R> {
		val (seedOffsetA1, seedOffsetA2) = deriveTwoChildSeedOffsets(seedOffset)
		return zipForever(
			a1ArbArgsGenerator.generate(seedOffsetA1),
			a2ArbArgsGenerator.generate(seedOffsetA2),
			transform
		)
	}
}
