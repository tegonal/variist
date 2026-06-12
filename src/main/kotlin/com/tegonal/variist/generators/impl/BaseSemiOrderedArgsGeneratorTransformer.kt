package com.tegonal.variist.generators.impl

import com.tegonal.variist.config._components
import com.tegonal.variist.generators.SemiOrderedArgsGenerator
import com.tegonal.variist.generators._core
import com.tegonal.variist.utils.deriveChildSeedOffset

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
abstract class BaseSemiOrderedArgsGeneratorTransformer<T, R>(
	baseGenerator: SemiOrderedArgsGenerator<T>,
	private val transform: (Sequence<T>, seedOffset: Int) -> Sequence<R>
) : BaseSemiOrderedArgsGenerator<R>(baseGenerator._core, baseGenerator.size) {

	private val baseGenerator = baseGenerator._core

	override fun generateAfterChecks(offset: Int, seedOffset: Int): Sequence<R> {
		val seq = baseGenerator.generate(offset, seedOffset)
		return transform(seq, deriveChildSeedOffset(seedOffset, 1))
	}
}
