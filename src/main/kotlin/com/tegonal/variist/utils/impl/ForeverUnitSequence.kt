package com.tegonal.variist.utils.impl

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
object ForeverUnitSequence : Sequence<Unit> {
	private val iterator = ForeverConstantIterator(Unit)
	override fun iterator(): Iterator<Unit> = iterator
}
