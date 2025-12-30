package com.tegonal.variist.utils.impl

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class ForeverConstantSequence<T>(
	constant: T,
) : Sequence<T> {
	private val iterator = ForeverConstantIterator(constant)
	override fun iterator(): Iterator<T> = iterator
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class ForeverConstantIterator<T>(private val constant: T) : Iterator<T> {
	override fun hasNext(): Boolean = true
	override fun next(): T = constant
}

