package com.tegonal.variist.generators

/**
 * Returns an [OrderedArgsGenerator] for the given Enum of type [E].
 *
 * @param E the Enum type which shall be transformed into an [OrderedArgsGenerator].
 *
 * @since 2.0.0
 */
inline fun <reified E : Enum<E>> ArbExtensionPoint.fromEnum(): ArbArgsGenerator<E> =
	fromArray(enumValues<E>())
