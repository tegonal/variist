package com.tegonal.variist.generators.impl

import com.tegonal.variist.generators.OrderedArgsGenerator
import com.tegonal.variist.generators.SemiOrderedArgsGenerator
import com.tegonal.variist.config._components

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class OrderedCartesianProductArgsGenerator<A1, A2, R>(
	private val a1Generator: OrderedArgsGenerator<A1>,
	private val a2Generator: OrderedArgsGenerator<A2>,
	private val transform: (A1, A2) -> R
) : BaseSemiOrderedArgsGenerator<R>(
	// note, we don't (and cannot) check that a1Generator and a2Generator use the same ComponentContainer,
	// should you run into weird behaviour (such as one generator uses seed X and the other seed Y) then most likely
	// someone used two different initial factories
	a1Generator._components,
	a1Generator.size.toLong() * a2Generator.size.toLong()
), OrderedArgsGenerator<R> {

	//TODO 2.1.0 override generateOneAfterChecks?

	override fun generateAfterChecks(offset: Int): Sequence<R> =
		combine(a1Generator, a2Generator, transform, offset)
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class SemiOrderedCartesianProductArgsGenerator<A1, A2, R>(
	private val a1Generator: SemiOrderedArgsGenerator<A1>,
	private val a2Generator: SemiOrderedArgsGenerator<A2>,
	private val transform: (A1, A2) -> R
) : BaseSemiOrderedArgsGenerator<R>(
	// note, we don't (and cannot) check that a1Generator and a2Generator use the same ComponentContainer,
	// should you run into weird behaviour (such as one generator uses seed X and the other seed Y) then most likely
	// someone used two different initial factories
	a1Generator._components,
	a1Generator.size.toLong() * a2Generator.size.toLong()
) {

	override fun generateAfterChecks(offset: Int): Sequence<R> =
		combine(a1Generator, a2Generator, transform, offset)
}

/**
 * Combines two [SemiOrderedArgsGenerator] by letting the bigger
 * (in terms of [SemiOrderedArgsGenerator.size] = maxSize) generate repeatedly values starting from the defined offset and by letting the smaller generate chunks of maxSize
 * whereas the offset progresses from the given [offset] until its [SemiOrderedArgsGenerator.size].
 *
 * This approach allows to generate lazily combined values without the need to generate more data than needed.
 */
private fun <A1, A2, R> combine(
	a1Generator: SemiOrderedArgsGenerator<A1>,
	a2Generator: SemiOrderedArgsGenerator<A2>,
	transform: (A1, A2) -> R,
	offset: Int
): Sequence<R> {
	// Some notes about performance (verified via jmh), we think performance matters here because it will be used heavily:
	// - using a custom tailored Iterator with a chunked based approach is:
	//   - 1.5 - 2 times faster than generateSequence + specialised zip function and uses only half of the memory
	//   - 2 - x times faster than generateSequence + flatMap on the underlying list/array where speed and memory
	//     in this case depends on the offset and how expensive the discarded values are to allocate/build
	// - multiple if(a1IsSmaller) rather than:
	//   - var and a single if because SSA (static single assignment) can usually be better optimised
	//     by an AOT compiler/JIT
	//   - we don't use tuple + destructuring because we would allocate memory unnecessarily (~50 bytes more per
	//     call) and this change was significant in the tests

	val a1Size = a1Generator.size
	val a2Size = a2Generator.size
	val a1IsSmaller = a1Size < a2Size
	val sizeOfSmaller = if (a1IsSmaller) a1Size else a2Size
	val maxSize = if (a1IsSmaller) a2Size else a1Size

	// we generate in chunks of maxSize thus we can already fast-forward to the correct chunk ...
	val chunkOffset = (offset / maxSize) % sizeOfSmaller

	// ... within that chunk we might need to fast-forward elements to reach the desired offset
	val firstChunkOffset = offset % maxSize

	return Sequence {
		object : Iterator<R> {
			private var chunkIndex = chunkOffset

			private var a1Iterator =
				a1Generator.generate(firstChunkOffset + if (a1IsSmaller) chunkOffset else 0).iterator()

			private var a2Iterator =
				a2Generator.generate(firstChunkOffset + if (a1IsSmaller) 0 else chunkOffset).iterator()

			// in the first chunk we might have an offset and if so will produce fewer values
			private var count = firstChunkOffset

			override fun hasNext(): Boolean = true
			override fun next(): R =
				transform(a1Iterator.next(), a2Iterator.next()).also {
					++count
					if (count >= maxSize) {

						count = 0
						++chunkIndex
						if (chunkIndex >= sizeOfSmaller) {
							chunkIndex = 0
						}

						// we only change the offset of the smaller iterator, the iterator of the bigger just repeats
						// we could also reset it but that would mean creating a new Iterator (and we prefer to avoid
						// this cost)
						if (a1IsSmaller) {
							a1Iterator = a1Generator.generate(chunkIndex).iterator()
						} else {
							a2Iterator = a2Generator.generate(chunkIndex).iterator()
						}
					}
				}
		}
	}
}
