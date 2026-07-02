//TODO 3.0.0 rename file to semiOrderedLike...
package com.tegonal.variist.generators

import com.tegonal.variist.generators.impl.throwUnsupportedArgsGenerator

/**
 * Combines `this` [SemiOrderedLikeArgsGenerator] with the given [other]&nbsp;[ArgsGenerator] and [transform]s the generated
 * values pairwise, returning a [SemiOrderedArgsGenerator] which generates values of type [R].
 *
 * It is not statically known what [SemiOrderedArgsGenerator.size] the resulting [SemiOrderedArgsGenerator] will have.
 *
 * How the [other]&nbsp;[ArgsGenerator] is combined depends on its type:
 *   - a [SemiOrderedLikeArgsGenerator] is combined using [cartesian]
 *   - an [ArbArgsGenerator] is combined using [zip]
 *
 * @return The resulting [SemiOrderedArgsGenerator] which generates values of type [R].
 * @throws UnsupportedOperationException in case the [other]&nbsp;[ArgsGenerator] is neither
 *   a [SemiOrderedLikeArgsGenerator] nor an [ArbArgsGenerator].
 *
 * @since 2.0.0
 */
fun <A1, A2, R> SemiOrderedLikeArgsGenerator<A1>.combine(
	other: ArgsGenerator<A2>,
	transform: (A1, A2) -> R
): SemiOrderedArgsGenerator<R> = when (other) {
	is SemiOrderedLikeArgsGenerator<A2> -> cartesian(other, transform)
	is ArbArgsGenerator<A2> -> zip(other, transform)
	else -> throwUnsupportedArgsGenerator(other)
}
