package com.tegonal.variist

import ch.tutteli.kbox.Tuple2
import com.tegonal.variist.config._components
import com.tegonal.variist.generators.*
import com.tegonal.variist.generators.impl.BaseSemiOrderedArgsGenerator
import com.tegonal.variist.generators.impl.OrderedCartesianProductArgsGenerator
import com.tegonal.variist.utils.repeatForever
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 300, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 300, timeUnit = TimeUnit.MILLISECONDS)
@Fork(3)
@State(Scope.Benchmark)
open class CartesianBench {

	@Param("50")//"10", "25", "50", "75", "99")//@Param("0", "25", "40", "50", "75", "90", "100")
	var offsetPercentage: Int = 0
	var offset = 0

	@Param("1", "25", "50", "75", "100")//@Param("1", "25", "40", "50", "75", "90", "100")
	var takePercentage: Int = 0
	var take = 0


	@Param("12", "30", "57", "110", "300", "500")
	var numOfInts = 1
	lateinit var intGenerator: OrderedArgsGenerator<Int>
	lateinit var charGenerator: OrderedArgsGenerator<Char>
	lateinit var ints: List<Int>
	lateinit var chars: List<Char>

	@Setup
	fun setup() {
		val combinations = numOfInts * numOfInts / 3
		offset = combinations * offsetPercentage / 100
		take = combinations * takePercentage / 100
		if (take <= 0) take = 1

		ints = (0..numOfInts).toList()
		chars = (0..numOfInts / 3).map { (it + 65).toChar() }
		intGenerator = ordered.intFromUntil(0, numOfInts)
		charGenerator = ordered.intFromUntil(0, numOfInts / 3).map { (it + 65).toChar() }
	}

//	@Benchmark
//	fun cartesianSequence() =
//		CartesianSequence(intGenerator, charGenerator, ::Tuple2, offset).take(take).count()

	// no significant difference between combineFun and cartesianProductArgsGenerator for generate
	// but a significant difference when using generateOne implemented in cartesianProductArgsGenerator:
	// it uses about 5.5 times less memory (max cartesian vs. min combineFun)
	// it is about 1.3 times faster (again max vs. min)

	//	@Benchmark
//	fun combineFun() =
//		combine(intGenerator, charGenerator, ::Tuple2, offset).take(take).count()

	@Benchmark
	fun normal() =
		OrderedCartesianProductArgsGenerator(intGenerator, charGenerator, ::Tuple2).generate(offset).take(take).count()

	// lcm based is about 4% faster tahn smaller/bigger (unless less than 3 values are generated, then the overhead of
	// calculating the lcm is bigger). But it uses about 10% less memory

	@Benchmark
	fun smallerBigger() =
		SmallerBiggerArgsGenerator(intGenerator, charGenerator, ::Tuple2).generate(offset).take(take).count()
//
//	@Benchmark
//	fun flatMap() = repeatForever().flatMap {
//		ints.asSequence().flatMap { a1 -> chars.asSequence().map { a2 -> a1 to a2 } }
//	}.drop(offset).take(take).count()
//
//	@Benchmark
//	fun radix(): Int =
//		Radix(intGenerator, charGenerator, ::Tuple2).generate(offset).take(take).count()
//
//	@Benchmark
//	fun lcm(): Int =
//		LcmBased(intGenerator, charGenerator, ::Tuple2).generate(offset).take(take).count()
}

private fun <A1, A2, R> combine(
	a1Generator: SemiOrderedArgsGenerator<A1>,
	a2Generator: SemiOrderedArgsGenerator<A2>,
	transform: (A1, A2) -> R,
	offset: Int
): Sequence<R> {
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

class Radix<A, B, R>(
	a: OrderedArgsGenerator<A>,
	private val b: OrderedArgsGenerator<B>,
	private val transform: (A, B) -> R
) : BaseSemiOrderedArgsGenerator<R>(a._components, a.size.toLong() * b.size.toLong()) {
	val aL = a.toList()
	val aSize = a.size
	val bSize = b.size

	override fun generateAfterChecks(offset: Int): Sequence<R> {
		var i = offset % size
		val bItr = b.generate(offset).iterator()
		return repeatForever().map {
			val aIndex = i / bSize % aSize
			++i
			if (i == size) i = 0
			transform(aL.get(aIndex), bItr.next())
		}
	}
}


class SmallerBiggerArgsGenerator<A1, A2, R>(
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
	private val a2Size: Int = a2Generator.size

	override fun generateOneAfterChecks(offset: Int): R = withOffsets(offset) { _, _, a1Offset, a2Offset ->
		val a1 = a1Generator.generateOne(a1Offset)
		val a2 = a2Generator.generateOne(a2Offset)
		transform(a1, a2)
	}

	// Some notes about performance (verified via jmh), we think performance matters here because it will be used heavily:
	// - using a custom tailored Iterator with a chunked based approach is:
	//   - 1.5 - 2 times faster than generateSequence + specialised zip function and uses only half of the memory
	//   - 2 - x times faster than generateSequence + flatMap on the underlying list/array where speed and memory
	//     in this case depends on the offset and how expensive the discarded values are to allocate/build
	// - multiple if(a1IsSmaller) rather than:
	//   - var and a single if because SSA (static single assignment) can usually be better optimised
	//     by an AOT compiler/JIT
	//   - we don't use tuple + destructuring because we would allocate memory unnecessarily (~50 bytes more per
	//     call) and this change was significant in the tests, hence also the use of this inline function to:
	//     a) not use a Tuple and b) still don't duplicate the logic
	private inline fun <T> withOffsets(
		offset: Int,
		action: (chunkOffset: Int, offsetInFirstChunk: Int, a1Offset: Int, a2Offset: Int) -> T
	): T {
		val offsetInFirstChunk = offset % a2Size
		val numOfSkippedChunks = offset / a2Size

		// we shift the starting offset of a1Generator each chunk by one
		val a1Offset = numOfSkippedChunks + offsetInFirstChunk

		return action(numOfSkippedChunks, offsetInFirstChunk, a1Offset, offsetInFirstChunk)
	}

	/**
	 * Combines two [SemiOrderedArgsGenerator] by letting the bigger
	 * (in terms of [SemiOrderedArgsGenerator.size] = maxSize) generate repeatedly values starting from the defined offset and by letting the smaller generate chunks of maxSize
	 * whereas the offset progresses from the given [offset] until its [SemiOrderedArgsGenerator.size].
	 *
	 * This approach allows to generate lazily combined values without the need to generate more data than needed.
	 */
	override fun generateAfterChecks(offset: Int): Sequence<R> = Sequence {
		withOffsets(offset) { chunkOffset, offsetInFirstChunk, a1Offset, a2Offset ->
			object : Iterator<R> {
				private var chunkIndex = chunkOffset
				private var a1Iterator = a1Generator.generate(a1Offset).iterator()
				private val a2Iterator = a2Generator.generate(a2Offset).iterator()

				// in the first chunk we might have an offset and if so will produce fewer values
				private var count = offsetInFirstChunk

				override fun hasNext(): Boolean = true
				override fun next(): R =
					transform(a1Iterator.next(), a2Iterator.next()).also {
						++count
						// we generate in chunks of sizeOfBigger...
						if (count >= a2Size) {
							count = 0
							// ... each time we reach the end of a chunk we increase the chunkIndex which we use
							// as starting offset of the smaller
							++chunkIndex

							// in v2.0.0 we used to have the following because it fits the mental model that we increase
							// the starting offset of the smaller until we reach the end in which case we reset. Yet,
							// this is unnecessary, as ArgsGenerators generate an infinite sequence.
							//if (chunkIndex >= sizeOfSmaller) {
							//	chunkIndex = 0
							//}

							// we only change the offset of the smaller iterator, the iterator of the bigger just repeats
							// we could also reset it but that would mean creating a new Iterator (and we prefer to avoid
							// this cost)
							a1Iterator = a1Generator.generate(chunkIndex).iterator()
						}
					}
			}
		}
	}
}
