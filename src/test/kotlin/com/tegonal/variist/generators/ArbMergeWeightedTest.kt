package com.tegonal.variist.generators

import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.atrium.api.verbs.expectGrouped
import ch.tutteli.kbox.append
import ch.tutteli.kbox.toList
import ch.tutteli.kbox.toVararg
import com.tegonal.variist.config._components
import com.tegonal.variist.providers.ArgsSource
import com.tegonal.variist.testutils.PseudoArbArgsGenerator
import com.tegonal.variist.testutils.withMockedRandom
import com.tegonal.variist.utils.createVariistRandom
import com.tegonal.variist.utils.repeatForever
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest

class ArbMergeWeightedTest {

	@ParameterizedTest
	@ArgsSource("arbWeightsInTotalAlways100")
	fun `check weights are correct`(weights: List<Int>) {
		val g1 = PseudoArbArgsGenerator(
			(0..9).toList(),
			seedBaseOffset = 0,
			arb._components.withMockedRandom(ints = (1..100).toList())
		)
		val (secondWithWeights, othersWithWeights) = weights.drop(1).mapIndexed { index, weight ->
			weight to PseudoArbArgsGenerator(
				(0..9).asSequence().map { it + (index + 1) * 10 },
				// we know that mergeWeighted uses an index based offset, we revert this to get exactly the above list
				// of values
				seedBaseOffset = -index - 1
			)
		}.toVararg()

		val merged = arb.mergeWeighted(weights.first() to g1, secondWithWeights, *othersWithWeights)

		val l = merged.generate().take(100).toList()

		// The following depends heavily on implementation details: we know that we use Random inside for a
		// uniform distribution and we know that weights keep the given order, i.e. if g1 has weight 10 then if
		// Random.next(1,100) yields a value between 1 and 10 g1 is picked to contribute a value
		val expected = weights.flatMapIndexed { index, weight ->
			val s = repeatForever().flatMap { (0..9).asSequence().map { it + index * 10 } }
			s.take(weight)
		}.toList()

		expect(l).toContainExactlyElementsOf(expected)
	}

	@Test
	fun `check same generator merged twice generates different values`() {
		val numOfValues = 10
		// The following depends heavily on implementation details: we know that we use Random inside for a
		// uniform distribution, so if we use equal weights and a mocked random which picks alternating the first or
		// the second, we can check if mergeWeighted uses a seedOffset if we use a PseudoArbArgsGenerator with a
		// known order
		val alternate = (1..50).asSequence().zip((51..100).asSequence()) { a, b -> listOf(a, b) }
			.flatten().take(numOfValues).toList()
		val g = PseudoArbArgsGenerator(
			(0..20).toList(),
			componentFactoryContainer = arb._components.withMockedRandom(ints = alternate)
		)

		val merged = arb.mergeWeighted(50 to g, 50 to g)
		val l = merged.generate().take(numOfValues).toList()
		val (firstWithIndex, secondWithIndex) = l.withIndex().partition { it.index % 2 == 0 }
		// if they are exactly the same then most likely no seedOffset was used
		expect(firstWithIndex.map { it.value }).notToEqual(secondWithIndex.map { it.value })
	}


	@Test
	fun `check same generator merged four times generates different values`() {
		val numOfValues = 12
		// The following depends heavily on implementation details: we know that we use Random inside for a
		// uniform distribution, so if we use equal weights and a mocked random which picks alternating the first to
		// fourth generator, we can check if mergeWeighted uses a seedOffset if we use a PseudoArbArgsGenerator with
		// a known order
		val alternate = (1..25).asSequence()
			.zip((26..50).asSequence())
			.zip((51..75).asSequence(), transform = { p, b -> p.append(b) })
			.zip((76..100).asSequence(), transform = { t, b -> t.append(b).toList() })
			.flatten().take(numOfValues).toList()

		val g = PseudoArbArgsGenerator(
			(0..20).toList(),
			componentFactoryContainer = arb._components.withMockedRandom(ints = alternate)
		)
		val merged = arb.mergeWeighted(25 to g, 25 to g, 25 to g, 25 to g)
		val l = merged.generate(0).take(numOfValues).toList()
		val (firstAndThird, secondAndFourth) = l.withIndex().partition { it.index % 2 == 0 }
		val (firstWithIndex, thirdWithIndex) = firstAndThird.map { it.value }.withIndex()
			.partition { it.index % 2 == 0 }
		val (secondWithIndex, fourthWithIndex) = secondAndFourth.map { it.value }.withIndex()
			.partition { it.index % 2 == 0 }

		val first = firstWithIndex.map { it.value }
		val second = secondWithIndex.map { it.value }
		val third = thirdWithIndex.map { it.value }
		val fourth = fourthWithIndex.map { it.value }

		// if they are exactly the same then most likely no seedOffset was used
		expectGrouped {
			group("first != second") {
				expect(first).notToEqual(second)
			}
			group("first != third") {
				expect(first).notToEqual(third)
			}
			group("first != fourth") {
				expect(first).notToEqual(fourth)
			}

			group("second != third") {
				expect(second).notToEqual(third)
			}
			group("second != fourth") {
				expect(second).notToEqual(fourth)
			}

			group("third != fourth") {
				expect(third).notToEqual(fourth)
			}
		}
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
	fun `invalid total weights in case of 2`(weight1: Int, weight2: Int) {
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
	fun `invalid total weights in case of 3`(weight1: Int, weight2: Int, weight3: Int) {
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
			//TODO 2.1.0 introduce the concept of edge cases, here we would like to be sure that 0 is invalid as well
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
