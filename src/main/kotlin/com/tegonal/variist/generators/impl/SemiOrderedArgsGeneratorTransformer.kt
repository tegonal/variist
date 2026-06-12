package com.tegonal.variist.generators.impl

import com.tegonal.variist.generators.SemiOrderedArgsGenerator

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class SemiOrderedArgsGeneratorTransformer<T, R>(
	baseGenerator: SemiOrderedArgsGenerator<T>,
	transform: (Sequence<T>, seedOffset: Int) -> Sequence<R>
) : BaseSemiOrderedArgsGeneratorTransformer<T, R>(baseGenerator, transform)
