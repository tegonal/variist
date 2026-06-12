package com.tegonal.variist.generators

import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.kbox.append
import ch.tutteli.kbox.toList
import ch.tutteli.kbox.toVararg
import com.tegonal.variist.config._components
import com.tegonal.variist.providers.ArgsSource
import com.tegonal.variist.testutils.RepeatGivenListArbArgsGenerator
import com.tegonal.variist.testutils.RepeatProvidedListArbArgsGenerator
import com.tegonal.variist.testutils.firstDerivedChildFromSeed0
import com.tegonal.variist.testutils.withMockedRandom
import com.tegonal.variist.utils.createVariistRandom
import com.tegonal.variist.utils.deriveChildSeedOffset
import com.tegonal.variist.utils.repeatForever
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest

class ArbMergeWeightedTest {

	@ParameterizedTest
	@ArgsSource("arbWeightsInTotalAlways100")
	fun `check weights are correct`(weights: List<Int>) {
		val g1 = RepeatGivenListArbArgsGenerator(
			(0..9).toList(),
			arb._components.withMockedRandom(ints = (1..100).toList())
		)
		val (secondWithWeights, othersWithWeights) = weights.drop(1).mapIndexed { index, weight ->
			weight to RepeatGivenListArbArgsGenerator(
				(0..9).asSequence().map { it + (index + 1) * 10 },
			)
		}.toVararg()

		val merged = arb.mergeWeighted(weights.first() to g1, secondWithWeights, *othersWithWeights)

		val l = merged.generate(seedOffset = 0).take(100).toList()

		// The following depends heavily on implementation details: we know that we use Random inside for a
		// uniform distribution and we know that weights keep the given order, i.e. if g1 has weight 10 then if
		// Random.next(1,100) yields a value between 1 and 10 g1 is picked to contribute a value
		val expected = weights.flatMapIndexed { index, weight ->
			repeatForever().flatMap {
				(0..9).asSequence().map { it + index * 10 }
			}.take(weight)
		}.toList()

		expect(l).toContainExactlyElementsOf(expected)
	}

	@Test
	fun `check same generator merged twice receives different seedOffsets`() {
		val numOfValues = 10
		// The following depends heavily on implementation details: we know that we use Random inside for a
		// uniform distribution, so if we use equal weights and a mocked random which picks alternating the first or
		// the second, we know what the result should look like
		val alternate = (1..50).asSequence().zip((51..100).asSequence()) { a, b -> listOf(a, b) }
			.flatten().take(numOfValues).toList()
		val g = RepeatProvidedListArbArgsGenerator(
			{ seedOffset ->
				if (seedOffset == firstDerivedChildFromSeed0) (0..20).toList()
				else (10..20) + (0..10)
			},
			componentFactoryContainer = arb._components.withMockedRandom(ints = alternate)
		)

		val merged = arb.mergeWeighted(50 to g, 50 to g)
		val l = merged.generate().take(numOfValues).toList()

		expect(g.seedOffsets.toSet()).toHaveSize(2)
		expect(l).toContainExactly(0, 10, 1, 11, 2, 12, 3, 13, 4, 14)
	}

	@Test
	fun `check same generator merged four times receives different seedOffsets`() {
		val numOfValues = 12

		// The following depends heavily on implementation details: we know that we use Random inside for a
		// uniform distribution, so if we use equal weights and a mocked random which picks alternating the first-to-
		// fourth generator, we know what the result should look like
		val alternate = (1..25).asSequence()
			.zip((26..50).asSequence())
			.zip((51..75).asSequence(), transform = { p, b -> p.append(b) })
			.zip((76..100).asSequence(), transform = { t, b -> t.append(b).toList() })
			.flatten().take(numOfValues).toList()

		val s2 = deriveChildSeedOffset(0, 2)
		val s3 = deriveChildSeedOffset(0, 3)

		val g = RepeatProvidedListArbArgsGenerator(
			{ seedOffset ->
				when (seedOffset) {
					firstDerivedChildFromSeed0 -> (0..20).toList()
					s2 -> (6..20) + (0..6)
					s3 -> (10..20) + (0..10)
					else -> (15..20) + (0..15)
				}
			},
			componentFactoryContainer = arb._components.withMockedRandom(ints = alternate)
		)
		val merged = arb.mergeWeighted(25 to g, 25 to g, 25 to g, 25 to g)
		val l = merged.generate(0).take(numOfValues).toList()

		expect(g.seedOffsets.toSet()).toHaveSize(4)
		expect(l).toContainExactly(0, 6, 10, 15, 1, 7, 11, 16, 2, 8, 12, 17)
	}


	@ParameterizedTest
	@ArgsSource("arbInvalidWeight")
	fun invalidWeights(weight: Int) {
		val g1 = arb.intFromUntil(1, 10)
		val g2 = arb.intFromUntil(20, 30)
		val g3 = arb.intFromUntil(40, 50)

		expect {
			arb.mergeWeighted(weight to g1, 50 to g2)
		}.toThrow<IllegalStateException> {
			messageToContain("$weight is not a valid 1st weight, must be greater than 0")
		}
		expect {
			arb.mergeWeighted(50 to g1, weight to g2)
		}.toThrow<IllegalStateException> {
			messageToContain("$weight is not a valid 2nd weight, must be greater than 0")
		}

		expect {
			arb.mergeWeighted(10 to g1, 50 to g2, weight to g3)
		}.toThrow<IllegalStateException> {
			messageToContain("$weight is not a valid 3rd weight, must be greater than 0")
		}

		expect {
			arb.mergeWeighted(10 to g1, 50 to g2, 20 to g3, weight to g1)
		}.toThrow<IllegalStateException> {
			messageToContain("$weight is not a valid 4th weight, must be greater than 0")
		}
	}

	@ParameterizedTest
	@ArgsSource("arbTwoWeightsInTotalIntMaxOrMore")
	fun `total weights overflow in case of 2, throws exception`(weight1: Int, weight2: Int) {
		val g1 = arb.intFromUntil(1, 10)
		val g2 = arb.intFromUntil(20, 30)

		expect {
			arb.mergeWeighted(weight1 to g1, weight2 to g2)
		}.toThrow<ArithmeticException> {
			messageToContain("integer overflow")
		}
	}

	@ParameterizedTest
	@ArgsSource("arbThreeWeightsInTotalIntMaxOrMore")
	fun `total weights overflow in case of 3, throws exception`(weight1: Int, weight2: Int, weight3: Int) {
		val g1 = arb.intFromUntil(1, 10)
		val g2 = arb.intFromUntil(20, 30)
		val g3 = arb.intFromUntil(40, 50)

		expect {
			arb.mergeWeighted(weight1 to g1, weight2 to g2, weight3 to g3)
		}.toThrow<ArithmeticException> {
			messageToContain("integer overflow")
		}
	}

	companion object {
		@JvmStatic
		fun arbWeightsInTotalAlways100() = createVariistRandom().let { variistRandom ->
			arb.intFromUntil(1, 10).map { numOfGenerators ->
				mutableListOf<Int>().also { weights ->
					val cumulativeWeight = (0 until numOfGenerators).fold(0) { cumulativeWeight, index ->
						val remainingWeight = 99 - numOfGenerators + index - cumulativeWeight
						val weight = if (remainingWeight <= 1) 1 else variistRandom.nextInt(1, remainingWeight)
						weights.add(weight)
						cumulativeWeight + weight
					}
					weights.add(100 - cumulativeWeight)
				}
			}
		}

		@JvmStatic
		fun arbInvalidWeight() =
			//TODO 2.2.0 introduce the concept of edge cases, here we would like to be sure that 0 is invalid as well
			arb.intFromTo(Int.MIN_VALUE, 0)

		@JvmStatic
		fun arbTwoWeightsInTotalIntMaxOrMore() =
			arb.intFromUntil(1, Int.MAX_VALUE).zipDependent {
				arb.intFromTo(Int.MAX_VALUE - it, Int.MAX_VALUE)
			}


		@JvmStatic
		fun arbThreeWeightsInTotalIntMaxOrMore() =
			arb.intFromUntil(1, Int.MAX_VALUE).zipDependent {
				arb.intFromUntil(1, Int.MAX_VALUE)
			}.zipDependent({ (a, b) ->
				val total = a.toLong() + b.toLong()
				if (total > Int.MAX_VALUE) arb.intFromUntil(1, Int.MAX_VALUE)
				else arb.intFromTo(Int.MAX_VALUE - total.toInt(), Int.MAX_VALUE)
			}) { p, a3 -> p.append(a3) }
	}
}
