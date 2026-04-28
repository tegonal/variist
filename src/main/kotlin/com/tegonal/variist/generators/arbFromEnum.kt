package com.tegonal.variist.generators

/**
 * Returns an [ArbArgsGenerator] for the given [Enum] of type [E].
 *
 * @param E the [Enum] type which shall be transformed into an [ArbArgsGenerator].
 *
 * @since 2.0.0
 */
inline fun <reified E : Enum<E>> ArbExtensionPoint.fromEnum(): ArbArgsGenerator<E> =
	fromArray(enumValues<E>())

/**
 * Returns an [ArbArgsGenerator] for the given [Enum] of type [E] where the given [weightProvider] defines the
 * weight for each entry of the enum.
 *
 * See [mergeWeighted] for an explanation of the weights.
 * Note, if you want equal probability for each entry, then use [arb]`.`&nbsp;[fromEnum]`<T>()` instead.
 *
 * @param weightProvider A function which defines a weight for each entry of [E].
 * @param E the [Enum] type which shall be transformed into an [ArbArgsGenerator].
 *
 * @throws IllegalArgumentException in case a weight is wrong (is less than 1)
 * @throws ArithmeticException in case the weights sum up to [Int.MAX_VALUE] or more.
 *
 * @since 2.2.0
 */
inline fun <reified E : Enum<E>> ArbExtensionPoint.fromEnumWeighted(crossinline weightProvider: (E) -> Int): ArbArgsGenerator<E> =
	arb.fromListWeighted(enumValues<E>().map { weightProvider(it) to it })
