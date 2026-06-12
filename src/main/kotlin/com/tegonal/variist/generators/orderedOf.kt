package com.tegonal.variist.generators

import com.tegonal.variist.config._components
import com.tegonal.variist.generators.impl.ArrayOrderedArgsGenerator

/**
 * Returns an [OrderedArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
// note: not `arg: T, vararg args: T` on purpose for performance reasons, we have a check on size
fun <T> OrderedExtensionPoint.of(vararg args: T): OrderedArgsGenerator<T> =
	ArrayOrderedArgsGenerator(_components, args)
