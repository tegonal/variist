package com.tegonal.variist.generators.impl

import com.tegonal.variist.generators.ArbArgsGenerator
import com.tegonal.variist.generators._core
import com.tegonal.variist.utils.deriveTwoChildSeedOffsets

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class ArbArgsGeneratorTransformer<T, R>(
	private val baseGenerator: ArbArgsGenerator<T>,
	private val transform: (Sequence<T>, seedOffset: Int) -> Sequence<R>
) : BaseArbArgsGenerator<R>(baseGenerator._core) {

	//TODO 2.3.0 implement generateOne

	override fun generate(seedOffset: Int): Sequence<R> {
		val (seedOffset1, seedOffset2) = deriveTwoChildSeedOffsets(seedOffset)
		val seq = baseGenerator.generate(seedOffset1)
		return transform(seq, seedOffset2)
	}
}
