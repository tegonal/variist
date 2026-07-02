//TODO 3.0.0 rename file to semiOrderedLikeCartesianProductArgsGenerators
package com.tegonal.variist.generators.impl

import com.tegonal.variist.config._components
import com.tegonal.variist.generators.OrderedArgsGenerator
import com.tegonal.variist.generators.SemiOrderedArgsGenerator
import com.tegonal.variist.generators.SemiOrderedLikeArgsGenerator
import com.tegonal.variist.utils.deriveChildSeedOffset
import com.tegonal.variist.utils.deriveTwoChildSeedOffsets

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class OrderedCartesianProductArgsGenerator<A1, A2, R>(
	a1Generator: OrderedArgsGenerator<A1>,
	a2Generator: OrderedArgsGenerator<A2>,
	transform: (A1, A2) -> R
) : CartesianProductArgsGenerator<A1, A2, R>(a1Generator, a2Generator, transform),
	OrderedArgsGenerator<R>

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class SemiOrderedCartesianProductArgsGenerator<A1, A2, R>(
	a1Generator: SemiOrderedLikeArgsGenerator<A1>,
	a2Generator: SemiOrderedLikeArgsGenerator<A2>,
	transform: (A1, A2) -> R
) : CartesianProductArgsGenerator<A1, A2, R>(a1Generator, a2Generator, transform),
	SemiOrderedArgsGenerator<R>

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.1.0
 */
abstract class CartesianProductArgsGenerator<A1, A2, R>(
	private val a1Generator: SemiOrderedLikeArgsGenerator<A1>,
	private val a2Generator: SemiOrderedLikeArgsGenerator<A2>,
	private val transform: (A1, A2) -> R
) : BaseSemiOrderedLikeArgsGenerator<R>(
	// note, we don't (and cannot) check that a1Generator and a2Generator use the same ComponentContainer,
	// should you run into weird behaviour (such as one generator uses seed X and the other seed Y) then most likely
	// someone used two different initial factories
	a1Generator._components,
	a1Generator.size.toLong() * a2Generator.size.toLong()
) {
	// Note calculating the lcm doesn't bring much in speed: up to -10% if less than 3 values, up to 13% for > 12 values
	// comparing max time for lcm / min for previous approach: overall an improvement around 4%.
	// But is uses about 10% less memory in case of Int/Char values (less Iterators are created).
	private val leastCommonMultiple: Int
	private val repeatsAfterNumOfChunks: Int
	private val noShiftsNeeded: Boolean

	init {
		val size = this.size
		leastCommonMultiple = size / greatestCommonDivisor(a1Generator.size, a2Generator.size)
		repeatsAfterNumOfChunks = size / leastCommonMultiple

		// if the least common multiple is equal to size, then we don't need to shift the offset of a1 in each chunk,
		// a1 will naturally shift and be in sync again when reaching `size` where it shall repeat.
		noShiftsNeeded = leastCommonMultiple == size
	}

	private fun greatestCommonDivisor(a1Size: Int, a2Size: Int): Int {
		var a = a1Size
		var b = a2Size
		while (a != 0) {
			val temp = a
			a = b % a
			b = temp
		}
		return b
	}

	override fun generateOneAfterChecks(offset: Int, seedOffset: Int): R {
		val offsetInFirstChunk = calculateOffsetInFirstChunk(offset)

		val a2Offset = if (noShiftsNeeded) {
			offsetInFirstChunk
		} else {
			val numOfChunksSkipped = calculateNumberOfSkippedChunks(offset)
			calculateA2Offset(offsetInFirstChunk, numOfChunksSkipped)
		}
		val (a1SeedOffset, a2SeedOffset) = deriveTwoChildSeedOffsets(seedOffset)

		return transform(
			a1Generator.generateOne(offsetInFirstChunk, a1SeedOffset),
			a2Generator.generateOne(a2Offset, a2SeedOffset)
		)
	}

	override fun generateAfterChecks(offset: Int, seedOffset: Int): Sequence<R> {
		val (a1SeedOffset, a2SeedOffset) = deriveTwoChildSeedOffsets(seedOffset)
		val offsetInFirstChunk = calculateOffsetInFirstChunk(offset)

		return if (noShiftsNeeded) {
			Sequence {
				object : Iterator<R> {
					private val a1Iterator = a1Generator.generate(offsetInFirstChunk, a1SeedOffset).iterator()
					private val a2Iterator = a2Generator.generate(offsetInFirstChunk, a2SeedOffset).iterator()

					override fun hasNext(): Boolean = true
					override fun next(): R = transform(a1Iterator.next(), a2Iterator.next())
				}
			}
		} else {
			val numOfChunksSkipped = calculateNumberOfSkippedChunks(offset)
			// we shift the starting offset of a2Generator each chunk by one
			val a2Offset = calculateA2Offset(offsetInFirstChunk, numOfChunksSkipped)
			Sequence {
				// not thread-safe
				object : Iterator<R> {
					private var inChunkNumber = numOfChunksSkipped

					private val a1Iterator = a1Generator.generate(offsetInFirstChunk, a1SeedOffset).iterator()
					private var a2Iterator = a2Generator.generate(a2Offset, a2SeedOffset).iterator()

					// in the first chunk we might have an offset and if so will produce fewer values
					private var countInChunk = offsetInFirstChunk
					private var childSeedIndex = 1

					override fun hasNext(): Boolean = true
					override fun next(): R =
						transform(a1Iterator.next(), a2Iterator.next()).also {
							++countInChunk

							// we generate in chunks of leastCommonMultiple...
							if (countInChunk >= leastCommonMultiple) {
								countInChunk = 0
								++inChunkNumber
								++childSeedIndex

								if (inChunkNumber >= repeatsAfterNumOfChunks) {
									inChunkNumber = 0
								}
								// We could also drop 1 -- i.e. a1Iterator.next() -- instead of creating a new Iterator
								// if inChunkNumber != 0. But since we don't know how costly it is to create a value,
								// we use an iterator to be on the safer side.
								// For small values it would be beneficial (e.g. for Int we would use around 10% less
								// memory)
								a2Iterator = a2Generator.generate(
									inChunkNumber,
									deriveChildSeedOffset(a2SeedOffset, childSeedIndex)
								).iterator()
							}
						}
				}
			}
		}
	}

	private fun calculateOffsetInFirstChunk(offset: Int): Int =
		offset % leastCommonMultiple

	private fun calculateNumberOfSkippedChunks(offset: Int): Int =
		(offset / leastCommonMultiple) % repeatsAfterNumOfChunks

	private fun calculateA2Offset(offsetInFirstChunk: Int, numOfChunksSkipped: Int): Int =
		offsetInFirstChunk + numOfChunksSkipped
}

