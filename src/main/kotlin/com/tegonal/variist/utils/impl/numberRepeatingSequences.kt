package com.tegonal.variist.utils.impl

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
open class IntRepeatingSequence<T>(
	private val from: Int,
	private val toExclusive: Int,
	private val offset: Int,
	private val argsProvider: (Int) -> T,
	private val step: Int = 1,
) : Sequence<T> {

	// we don't check the invariants here, they should be checked in the ArgsGenerator and is again checked in the
	// Iterator (as safety net), checking it a third time seems unnecessary

	override fun iterator(): Iterator<T> {
		return object : BaseIntFromUntilRepeatingIterator<T>(from, toExclusive, offset, step) {
			override fun getElementAt(index: Int): T = argsProvider(index)
		}
	}
}
