package com.tegonal.variist.generators.impl

import com.tegonal.variist.config.ComponentFactoryContainer
import com.tegonal.variist.generators.OrderedArgsGenerator
import com.tegonal.variist.utils.impl.RepeatingRandomAccessSequence
import com.tegonal.variist.utils.impl.determineStartingIndex

/**
 * Represents a class for [OrderedArgsGenerator] which provide fast random access.
 *
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class RandomAccessOrderedArgsGenerator<T>(
	componentFactoryContainer: ComponentFactoryContainer,
	size: Int,
	private val elementAt: (index: Int) -> T
) : BaseSemiOrderedArgsGenerator<T>(componentFactoryContainer, size), OrderedArgsGenerator<T> {

	override fun generateOneAfterChecks(offset: Int, seedOffset: Int): T {
		val index = determineStartingIndex(0, size, offset, 1)
		return elementAt(index)
	}

	override fun generateAfterChecks(offset: Int, seedOffset: Int): Sequence<T> =
		RepeatingRandomAccessSequence(size, offset, elementAt)
}
