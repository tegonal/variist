package com.tegonal.variist

import ch.tutteli.kbox.Tuple3
import com.tegonal.variist.config._components
import com.tegonal.variist.generators.*
import com.tegonal.variist.generators.impl.BaseSemiOrderedArgsGenerator
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Fork(3)
@State(Scope.Benchmark)
open class Cartesian3Bench {

	@Param("10")//, "25", "50", "75", "99")//@Param("0", "25", "40", "50", "75", "90", "100")
	var offsetPercentage: Int = 0
	var offset = 0

	@Param("1", "25")//"50", "75", "100")//@Param("1", "25", "40", "50", "75", "90", "100")
	var takePercentage: Int = 0
	var take = 0


	@Param("12", "30", "120", "300", "500")
	var numOfInts = 1
	lateinit var intGenerator: OrderedArgsGenerator<Int>
	lateinit var charGenerator: OrderedArgsGenerator<Char>
	lateinit var longGenerator: OrderedArgsGenerator<Long>

	@Setup
	fun setup() {
		val combinations = numOfInts * numOfInts / 3 * numOfInts / 2
		offset = combinations * offsetPercentage / 100
		take = (combinations * takePercentage / 100)
		if (take <= 0) take = 1

		intGenerator = ordered.intFromUntil(0, numOfInts + 1)
		charGenerator = ordered.intFromUntil(0, numOfInts / 3).map { (it + 65).toChar() }
		longGenerator = ordered.longFromUntil(0L, numOfInts / 2L)
	}

	// to my surprise, twoCartesian is between 43 - 60% faster -- average was 58% (max vs. int) and
	// also uses 55 - 70% -- average 58% less memory. So it is definitely not worth it to introduce a specialised version

	@Benchmark
	fun twoCartesian() =
		intGenerator.cartesian(charGenerator).cartesian(longGenerator).generate(offset).take(take).count()

	@Benchmark
	fun cartesianProductArgsGenerator() =
		OrderedCartesianProduct3ArgsGenerator(intGenerator, charGenerator, longGenerator, ::Tuple3).generate(offset)
			.take(take).count()
}


class OrderedCartesianProduct3ArgsGenerator<A1, A2, A3, R>(
	a1Generator: OrderedArgsGenerator<A1>,
	a2Generator: OrderedArgsGenerator<A2>,
	a3Generator: OrderedArgsGenerator<A3>,
	transform: (A1, A2, A3) -> R
) : CartesianProductMultiArgsGenerator<R>(listOf(a1Generator, a2Generator, a3Generator), { args ->
	@Suppress("UNCHECKED_CAST") // that's actually true, we accept this due to performance reasons
	transform(args[0] as A1, args[1] as A2, args[2] as A3)
}), OrderedArgsGenerator<R>


/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.1.0
 */
abstract class CartesianProductMultiArgsGenerator<R>(
	private val generators: List<SemiOrderedArgsGenerator<*>>,
	private val transform: (Array<*>) -> R
) : BaseSemiOrderedArgsGenerator<R>(
	// note, we don't (and cannot) check that a1Generator and a2Generator use the same ComponentContainer,
	// should you run into weird behaviour (such as one generator uses seed X and the other seed Y) then most likely
	// someone used two different initial factories
	generators.first()._components,
	generators.fold(1L) { acc, gen -> acc * gen.size }
) {
	private val numOfGenerators = generators.size
	private val sizes: IntArray = IntArray(numOfGenerators) { generators[it].size }
	private val order: Array<Int> = Array(numOfGenerators) { it }.also { arr ->
		arr.sortBy { sizes[it] }
	}
	private val sizeOfBiggest: Int = sizes[order.last()]


	override fun generateAfterChecks(offset: Int): Sequence<R> {
		// we generate in chunks of sizeOfBiggest
		val offsetInFirstChunk = offset % sizeOfBiggest
		val offsetsInChunk = Array(numOfGenerators - 1) {
			// would need to be fixed if offset != 0
			0
		}

		return Sequence {

			object : Iterator<R> {
				private val iterators = Array(numOfGenerators) {
					generators[it].generate(offsetInFirstChunk).iterator()
				}

				// in the first chunk we might have an offset and if so will produce fewer values
				private var count = 0

				override fun hasNext(): Boolean = true
				override fun next(): R = transform(Array(numOfGenerators) { iterators[it].next() }).also {
					++count
					// we generate in chunks of sizeOfBiggest...
					if (count >= sizeOfBiggest) {
						count = 0
						// ... each time the end of a chunk is reached ...
						// we set this to true so that we take the if-branch below and then ...
						var hasOffsetReachedSize = true
						var offsetInChunk: Int

						for (generatorBySizeIndex in 0 until numOfGenerators - 1) {
							val generatorIndex = order[generatorBySizeIndex]
							if (hasOffsetReachedSize) {
								// ... we increase the offsetInChunk starting with the smallest generator,
								// increasing the offset of the next bigger generator only if...
								offsetInChunk = ++offsetsInChunk[generatorBySizeIndex]
								hasOffsetReachedSize = offsetInChunk >= sizes[generatorIndex]
								// ... the current (for the first loop, the smallest), has reached its size...
								if (hasOffsetReachedSize) {
									// ... in which case we reset the offset to 0 for the current...
									offsetInChunk = 0
									offsetsInChunk[generatorBySizeIndex] = 0
								}
							} else {
								// ... (if the previous has not reached its size then we use the current offset) ...
								offsetInChunk = offsetsInChunk[generatorBySizeIndex]
							}
							// ... and for all generators except for the biggest we create a new iterator with the
							// calculated offset
							iterators[generatorIndex] = generators[generatorIndex].generate(offsetInChunk).iterator()
						}
					}
				}
			}
		}
	}
}
