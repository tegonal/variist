package com.tegonal.variist.generators

import com.tegonal.variist.generators.impl.SemiOrderedArgsGeneratorConcatenator

/**
 * Concatenates all given [SemiOrderedArgsGenerator] resulting in an [SemiOrderedArgsGenerator] which yields the values of
 * the first [SemiOrderedArgsGenerator], then the second generator ... up to the values of the last generator and then
 * starting over.
 *
 * I.e. the resulting [SemiOrderedArgsGenerator] generates the sum of [SemiOrderedArgsGenerator.size] values before repeating.
 *
 * @return The resulting [SemiOrderedArgsGenerator] after concatenation.
 *
 * @since 2.0.0
 */
fun <T> Sequence<SemiOrderedArgsGenerator<T>>.concatAll(): SemiOrderedArgsGenerator<T> = concatAll(iterator())

/**
 * Concatenates all given [SemiOrderedArgsGenerator] resulting in an [SemiOrderedArgsGenerator] which yields the values of
 * the first [SemiOrderedArgsGenerator], then the second generator ... up to the values of the last generator and then
 * starting over.
 *
 * I.e. the resulting [SemiOrderedArgsGenerator] generates the sum of [SemiOrderedArgsGenerator.size] values before repeating.
 *
 * @return The resulting [SemiOrderedArgsGenerator] after concatenation.
 *
 * @since 2.0.0
 */
fun <T> Iterable<SemiOrderedArgsGenerator<T>>.concatAll(): SemiOrderedArgsGenerator<T> = concatAll(iterator())

private fun <T> concatAll(iterator: Iterator<SemiOrderedArgsGenerator<T>>): SemiOrderedArgsGenerator<T> {
	check(iterator.hasNext()) { "cannot concatenate an empty Iterator of SemiOrderedArgsGenerator" }
	val first = iterator.next()
	var result = first
	while (iterator.hasNext()) {
		//TODO 2.1.0 would it be worth to introduce a Concatenator which takes n SemiOrderedArgsGenerator instead of just 2?
		result += iterator.next()
	}
	return result
}

/**
 * Merges `this` [SemiOrderedArgsGenerator] with the given [other]&nbsp;[SemiOrderedArgsGenerator] resulting in an
 * [SemiOrderedArgsGenerator] which yields the values of `this` [SemiOrderedArgsGenerator] and then of the [other]
 * before repeating.
 *
 * The resulting [SemiOrderedArgsGenerator] generates
 * [this.size][SemiOrderedArgsGenerator.size] + [other.size][SemiOrderedArgsGenerator.size] values before repeating.
 *
 * @param other The other [SemiOrderedArgsGenerator].
 *
 * @return The resulting [SemiOrderedArgsGenerator] after concatenation.
 *
 * @since 2.0.0
 */
operator fun <T> SemiOrderedArgsGenerator<T>.plus(
	other: SemiOrderedArgsGenerator<T>,
): SemiOrderedArgsGenerator<T> = SemiOrderedArgsGeneratorConcatenator(this, other)
