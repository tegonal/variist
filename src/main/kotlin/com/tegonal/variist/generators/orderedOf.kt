package com.tegonal.variist.generators

/**
 * Returns an [OrderedArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
// note: not `arg: T, vararg args: T` on purpose for performance reasons, we have a check on size
fun <T> OrderedExtensionPoint.of(vararg args: T): OrderedArgsGenerator<T> =
	fromArray(args)
