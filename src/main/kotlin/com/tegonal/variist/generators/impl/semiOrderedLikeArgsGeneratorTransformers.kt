package com.tegonal.variist.generators.impl

import com.tegonal.variist.config._components
import com.tegonal.variist.generators.OrderedArgsGenerator
import com.tegonal.variist.generators.SemiOrderedArgsGenerator
import com.tegonal.variist.generators.SemiOrderedLikeArgsGenerator
import com.tegonal.variist.utils.deriveChildSeedOffset

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class OrderedArgsGeneratorTransformer<T, R>(
	baseGenerator: OrderedArgsGenerator<T>,
	transform: (Sequence<T>, seedOffset: Int) -> Sequence<R>
) : BaseSemiOrderedLikeArgsGeneratorTransformer<T, R>(baseGenerator, transform),
	OrderedArgsGenerator<R>

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class SemiOrderedArgsGeneratorTransformer<T, R>(
	baseGenerator: SemiOrderedLikeArgsGenerator<T>,
	transform: (Sequence<T>, seedOffset: Int) -> Sequence<R>
) : BaseSemiOrderedLikeArgsGeneratorTransformer<T, R>(baseGenerator, transform),
	SemiOrderedArgsGenerator<R>


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
