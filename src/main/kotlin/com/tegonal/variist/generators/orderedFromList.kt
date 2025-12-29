package com.tegonal.variist.generators

import com.tegonal.variist.config._components
import com.tegonal.variist.generators.impl.ListOrderedArgsGenerator

/**
 * Returns an [OrderedArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun <T> OrderedExtensionPoint.fromList(args: List<T>): OrderedArgsGenerator<T> =
	ListOrderedArgsGenerator(_components, args)
