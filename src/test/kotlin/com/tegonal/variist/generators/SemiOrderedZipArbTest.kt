package com.tegonal.variist.generators

import ch.tutteli.atrium.api.fluent.en_GB.toHaveSize
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.kbox.Tuple
import ch.tutteli.kbox.mapSecond
import com.tegonal.variist.config.ordered
import com.tegonal.variist.testutils.RepeatGivenListArbArgsGenerator
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class SemiOrderedZipArbTest : AbstractOrderedArgsGeneratorWithoutAnnotationsTest() {
	val a1s = listOf(1, 2)
	val a2s = listOf('a', 'b', 'c')

	val a1GeneratorOrdered = customComponentFactoryContainer.ordered.fromList(a1s)
	val a2sGenerator = RepeatGivenListArbArgsGenerator(a2s)

	fun createGenerators(): OrderedArgsTestFactoryResult<Pair<Int, Any>> = sequenceOf(
		Tuple(
			"zip with 1 random",
			a1GeneratorOrdered.zip(a2sGenerator),
			// zip is only correct because for most tests we don't take more than generator.size
			// see createGeneratorsAllPossibleCombinations where we need to use flatMap
			a1s.zip(a2s)
		),
		Tuple(
			"zip with 2 random",
			a1GeneratorOrdered.zip(a2sGenerator).zip(a2sGenerator) { pair, a3 ->
				pair.mapSecond { it to a3 }
			},
			a1s.zip(a2s.map { it to it })
		),
		Tuple(
			"zip with 3 random",
			a1GeneratorOrdered.zip(a2sGenerator).zip(a2sGenerator).zip(a2sGenerator) { (a1, a2, a3), a4 ->
				a1 to Triple(a2, a3, a4)
			},
			a1s.zip(a2s.map { Triple(it, it, it) })
		),
		Tuple(
			"zipDependent",
			a1GeneratorOrdered.zipDependent { _ -> a2sGenerator },
			a1s.map { it to a2s.first() }
		),
	)

	fun createGeneratorsAllPossibleCombinations() =
		sequenceOf(
			Tuple(
				"zip with 1 random",
				a1GeneratorOrdered.zip(a2sGenerator),
				a1s.flatMap { a1 -> a2s.map { a2 -> a1 to a2 } }
			),
			Tuple(
				"zip with 2 random",
				a1GeneratorOrdered.zip(a2sGenerator)
					.zip(a2sGenerator) { pair, a3 ->
						pair.mapSecond { it to a3 }
					},
				a1s.flatMap { a1 -> a2s.map { a2 -> a1 to (a2 to a2) } }
			),
			Tuple(
				"zip with 3 random",
				a1GeneratorOrdered.zip(a2sGenerator).zip(a2sGenerator)
					.zip(a2sGenerator) { (a1, a2, a3), a4 ->
						a1 to Triple(a2, a3, a4)
					},
				a1s.flatMap { a1 -> a2s.map { a2 -> a1 to Triple(a2, a2, a2) } }
			),
			Tuple(
				"zipDependent",
				a1GeneratorOrdered.zipDependent { _ -> a2sGenerator },
				a1s.flatMap { a1 -> a2s.map { a2 -> a1 to a2 } }
			),
		)

	private fun createGeneratorsUseOnlyFirstValue(): Sequence<Triple<String, SemiOrderedArgsGenerator<Int>, List<Pair<Int, Any>>>> =
		createGenerators().map { triple ->
			triple.mapSecond { semiOrderedArgsGenerator ->
				(semiOrderedArgsGenerator as SemiOrderedArgsGenerator<Pair<Int, Any>>).map { it.first }
			}
		}

	@TestFactory
	fun canAlwaysTakeTheDesiredAmount() = canAlwaysTakeTheDesiredAmountTest(::createGeneratorsAllPossibleCombinations)

	@TestFactory
	fun coversAllCases() = coversAllCasesTest(::createGenerators)

	@TestFactory
	fun minusOffsetThrows() = minusOffsetThrowsTest(::createGenerators)

	@TestFactory
	fun firstRepeatsAfterReachingSize() = returnsDifferentValuesUntilReachingSizeAndThenRepeatsTest {
		// we want to make sure the ordered part stays ordered
		createGeneratorsUseOnlyFirstValue()
	}

	@TestFactory
	fun offsetPlusXReturnsTheSameFirstAsOffsetXMinus1JustShifted() =
		offsetPlusXReturnsTheSameAsOffsetXMinus1JustShiftedTest {
			// we want to make sure the ordered part stays ordered
			createGeneratorsUseOnlyFirstValue()
		}

	@Test
	fun `check zip passes different seedOffsets`() {
		val g = RepeatGivenListArbArgsGenerator(listOf(1, 2))
		g.zip(g).zip(g).zip(g).generate(seedOffset = 0).take(2).count()
		expect(g.seedOffsets.toSet()).toHaveSize(4)
	}
}

