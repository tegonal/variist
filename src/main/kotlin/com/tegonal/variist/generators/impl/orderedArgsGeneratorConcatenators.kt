package com.tegonal.variist.generators.impl

import com.tegonal.variist.config._components
import com.tegonal.variist.generators.OrderedArgsGenerator
import com.tegonal.variist.generators.SemiOrderedArgsGenerator

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class OrderedArgsGeneratorConcatenator<T>(
	private val a1Generator: OrderedArgsGenerator<T>,
	private val a2Generator: OrderedArgsGenerator<T>,
) : BaseSemiOrderedArgsGenerator<T>(
	// note, we don't (and cannot) check that a1Generator and a2Generator use the same ComponentContainer,
	// should you run into weird behaviour (such as one generator uses seed X and the other seed Y) then most likely
	// someone used two different initial factories
	a1Generator._components,
	a1Generator.size.toLong() + a2Generator.size.toLong()
), OrderedArgsGenerator<T> {

	override fun generateOneAfterChecks(offset: Int): T {
		val offsetInRange = offset % size
		val a1Size = a1Generator.size
		return if (offsetInRange < a1Size) {
			a1Generator.generateOne(offsetInRange)
		} else {
			a2Generator.generateOne(offsetInRange - a1Size)
		}
	}

	override fun generateAfterChecks(offset: Int): Sequence<T> =
		concatenate(a1Generator, a2Generator, size, offset)
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class SemiOrderedArgsGeneratorConcatenator<T>(
	private val a1Generator: SemiOrderedArgsGenerator<T>,
	private val a2Generator: SemiOrderedArgsGenerator<T>,
) : BaseSemiOrderedArgsGenerator<T>(
	// note, we don't (and cannot) check that a1Generator and a2Generator use the same ComponentContainer,
	// should you run into weird behaviour (such as one generator uses seed X and the other seed Y) then most likely
	// someone used two different initial factories
	a1Generator._components,
	a1Generator.size.toLong() + a2Generator.size.toLong()
) {

	override fun generateAfterChecks(offset: Int): Sequence<T> =
		concatenate(a1Generator, a2Generator, size, offset)
}

private fun <T> concatenate(
	a1Generator: SemiOrderedArgsGenerator<T>,
	a2Generator: SemiOrderedArgsGenerator<T>,
	newSize: Int,
	offset: Int
): Sequence<T> {
	// TODO 2.1.0 no micro-benchmarking done yet, maybe we find a more efficient solution?

	val offsetInRange = offset % newSize
	val a1Size = a1Generator.size
	val a2Size = a2Generator.size
	val isOffsetSmallerThanA1Size = offsetInRange < a1Size

	val countA1 = if (isOffsetSmallerThanA1Size) offsetInRange else 0
	val countA2 = if (isOffsetSmallerThanA1Size) 0 else offsetInRange - a1Size

	return Sequence {
		object : Iterator<T> {
			var a1Iterator = a1Generator.generate(countA1).iterator()
			var a2Iterator = a2Generator.generate(countA2).iterator()
			var isA1IteratorInUse = isOffsetSmallerThanA1Size
			var count = if (isOffsetSmallerThanA1Size) countA1 else countA2

			override fun hasNext(): Boolean = true
			override fun next(): T {
				++count
				return if (isA1IteratorInUse) {
					a1Iterator.next().also {
						if (count >= a1Size) {
							count = 0
							isA1IteratorInUse = false
						}
					}
				} else {
					a2Iterator.next().also {
						if (count >= a2Size) {
							count = 0
							isA1IteratorInUse = true
						}
					}
				}
			}
		}
	}
}
