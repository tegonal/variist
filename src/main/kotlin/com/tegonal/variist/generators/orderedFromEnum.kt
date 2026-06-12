package com.tegonal.variist.generators

import com.tegonal.variist.config._components
import com.tegonal.variist.generators.impl.ArrayOrderedArgsGenerator

/**
 * Returns an [OrderedArgsGenerator] for the given Enum of type [E].
 *
 * @param E the Enum type which shall be transformed into an [OrderedArgsGenerator].
 *
 * @since 2.0.0
 */
inline fun <reified E : Enum<E>> OrderedExtensionPoint.fromEnum(): OrderedArgsGenerator<E> =
	ArrayOrderedArgsGenerator(_components, enumValues<E>())
