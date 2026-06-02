package com.tegonal.variist.generators.impl

import com.tegonal.variist.config.ComponentFactoryContainer
import com.tegonal.variist.generators.OrderedArgsGenerator
import com.tegonal.variist.utils.impl.determineStartingIndex
import com.tegonal.variist.utils.repeatForever

/**
 * Represents an [OrderedArgsGenerator] which is based on a given [Array].
 *
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class ArrayOrderedArgsGenerator<T>(
	componentFactoryContainer: ComponentFactoryContainer,
	seedBaseOffset: Int,
	private val values: Array<T>,
) : BaseSemiOrderedArgsGenerator<T>(componentFactoryContainer, seedBaseOffset, values.size), OrderedArgsGenerator<T> {

	override fun generateOneAfterChecks(offset: Int): T {
		val index = determineStartingIndex(0, size, offset, 1)
		return values[index]
	}

	override fun generateAfterChecks(offset: Int): Sequence<T> =
		repeatForever(values, offset)
}
