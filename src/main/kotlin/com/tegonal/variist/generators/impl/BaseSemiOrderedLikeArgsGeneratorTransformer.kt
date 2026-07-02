//TODO 3.0.0 move to file semiOrderedLikeArgsGeneratorTransformers
package com.tegonal.variist.generators.impl

import com.tegonal.variist.config._components
import com.tegonal.variist.generators.SemiOrderedLikeArgsGenerator
import com.tegonal.variist.utils.deriveChildSeedOffset

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
abstract class BaseSemiOrderedLikeArgsGeneratorTransformer<T, R>(
	private val baseGenerator: SemiOrderedLikeArgsGenerator<T>,
	private val transform: (Sequence<T>, seedOffset: Int) -> Sequence<R>
) : BaseSemiOrderedLikeArgsGenerator<R>(baseGenerator._components, baseGenerator.size) {

	override fun generateAfterChecks(offset: Int, seedOffset: Int): Sequence<R> {
		val seq = baseGenerator.generate(offset, seedOffset)
		return transform(seq, deriveChildSeedOffset(seedOffset, 1))
	}
}
