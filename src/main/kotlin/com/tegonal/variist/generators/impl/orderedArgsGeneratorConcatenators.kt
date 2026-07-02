//TODO 3.0.0 rename file to semiOrderedLikeArgsGeneratorConcatenators
package com.tegonal.variist.generators.impl

import com.tegonal.variist.config._components
import com.tegonal.variist.generators.OrderedArgsGenerator
import com.tegonal.variist.generators.SemiOrderedArgsGenerator
import com.tegonal.variist.generators.SemiOrderedLikeArgsGenerator
import com.tegonal.variist.utils.deriveChildSeedOffset

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class OrderedArgsGeneratorConcatenator<T>(
	a1Generator: OrderedArgsGenerator<T>,
	a2Generator: OrderedArgsGenerator<T>,
) : SemiOrderedLikeArgsGeneratorConcatenator<T>(a1Generator, a2Generator),
	OrderedArgsGenerator<T>

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class SemiOrderedArgsGeneratorConcatenator<T>(
	a1Generator: SemiOrderedLikeArgsGenerator<T>,
	a2Generator: SemiOrderedLikeArgsGenerator<T>,
) : SemiOrderedLikeArgsGeneratorConcatenator<T>(a1Generator, a2Generator),
	SemiOrderedArgsGenerator<T>

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 3.0.0
 */
abstract class SemiOrderedLikeArgsGeneratorConcatenator<T>(
	private val a1Generator: SemiOrderedLikeArgsGenerator<T>,
	private val a2Generator: SemiOrderedLikeArgsGenerator<T>,
) : BaseSemiOrderedLikeArgsGenerator<T>(
	// note, we don't (and cannot) check that a1Generator and a2Generator use the same ComponentContainer,
	// should you run into weird behaviour (such as one generator uses seed X and the other seed Y) then most likely
	// someone used two different initial factories
	a1Generator._components,
	a1Generator.size.toLong() + a2Generator.size.toLong()
) {

	//TODO 3.1.0 implement generateOne

	override fun generateAfterChecks(offset: Int, seedOffset: Int): Sequence<T> {
		// TODO 3.5.0 no micro-benchmarking done yet, maybe we find a more efficient solution?

		val offsetInRange = offset % size
		val a1Size = a1Generator.size
		val a2Size = a2Generator.size
		val isOffsetSmallerThanA1Size = offsetInRange < a1Size

		val countA1 = if (isOffsetSmallerThanA1Size) offsetInRange else 0
		val countA2 = if (isOffsetSmallerThanA1Size) 0 else offsetInRange - a1Size

		return Sequence {
			object : Iterator<T> {
				val a1Iterator = a1Generator.generate(countA1, deriveChildSeedOffset(seedOffset, 1)).iterator()
				val a2Iterator = a2Generator.generate(countA2, deriveChildSeedOffset(seedOffset, 2)).iterator()
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
}
