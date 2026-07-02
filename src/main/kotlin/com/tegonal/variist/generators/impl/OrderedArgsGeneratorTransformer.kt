//TODO 3.0.0 move to file semiOrderedLikeArgsGeneratorTransformers
package com.tegonal.variist.generators.impl

import com.tegonal.variist.generators.OrderedArgsGenerator

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
