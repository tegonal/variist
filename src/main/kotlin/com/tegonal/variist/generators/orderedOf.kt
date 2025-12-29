package com.tegonal.variist.generators

import com.tegonal.variist.config._components
import com.tegonal.variist.generators.impl.ArrayOrderedArgsGenerator

/**
 * Returns an [OrderedArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun <T> OrderedExtensionPoint.of(vararg args: T): OrderedArgsGenerator<T> =
	ArrayOrderedArgsGenerator(_components, args)
