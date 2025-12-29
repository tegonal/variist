package com.tegonal.variist.generators

import com.tegonal.variist.generators.impl.OrderedArgsGeneratorConcatenator

/**
 * Concatenates all given [OrderedArgsGenerator] resulting in an [OrderedArgsGenerator] which yields the values of
 * the first [OrderedArgsGenerator], then the second generator ... up to the values of the last generator and then
 * starting over.
 *
 * I.e. the resulting [OrderedArgsGenerator] generates the sum of [OrderedArgsGenerator.size] values before repeating.
 *
 * @return The resulting [OrderedArgsGenerator] after concatenation.
 *
 * @since 2.0.0
 */
fun <T> Sequence<OrderedArgsGenerator<T>>.concatAll(): OrderedArgsGenerator<T> = concatAll(iterator())

/**
 * Concatenates all given [OrderedArgsGenerator] resulting in an [OrderedArgsGenerator] which yields the values of
 * the first [OrderedArgsGenerator], then the second generator ... up to the values of the last generator and then
 * starting over.
 *
 * I.e. the resulting [OrderedArgsGenerator] generates the sum of [OrderedArgsGenerator.size] values before repeating.
 *
 * @return The resulting [OrderedArgsGenerator] after concatenation.
 *
 * @since 2.0.0
 */
fun <T> Iterable<OrderedArgsGenerator<T>>.concatAll(): OrderedArgsGenerator<T> = concatAll(iterator())

private fun <T> concatAll(iterator: Iterator<OrderedArgsGenerator<T>>): OrderedArgsGenerator<T> {
	check(iterator.hasNext()) { "cannot concatenate an empty Iterator of OrderedArgsGenerator" }
	val first = iterator.next()
	var result = first
	while (iterator.hasNext()) {
		//TODO 2.1.0 would it be worth to introduce a Concatenator which takes n OrderedArgsGenerator instead of just 2?
		result += iterator.next()
	}
	return result
}

/**
 * Concatenates `this` [OrderedArgsGenerator] with the given [other]&nbsp;[OrderedArgsGenerator] resulting in an
 * [OrderedArgsGenerator] which yields the values of `this` [OrderedArgsGenerator], then of the [other] and then
 * starting over.
 *
 * I.e. the resulting [OrderedArgsGenerator] generates
 * [this.size][OrderedArgsGenerator.size] + [other.size][OrderedArgsGenerator.size] values before repeating.
 *
 * @param other The other [OrderedArgsGenerator].
 *
 * @return The resulting [OrderedArgsGenerator] after concatenation.
 *
 * @since 2.0.0
 */
operator fun <T> OrderedArgsGenerator<T>.plus(
	other: OrderedArgsGenerator<T>,
): OrderedArgsGenerator<T> =
	//TODO 2.1.0 bench when it makes sense to materialise instead of concatenation, for small sizes it could be faster
	OrderedArgsGeneratorConcatenator(this, other)

