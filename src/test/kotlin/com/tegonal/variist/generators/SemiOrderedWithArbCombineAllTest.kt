package com.tegonal.variist.generators

import ch.tutteli.kbox.Tuple
import ch.tutteli.kbox.mapSecond
import com.tegonal.variist.config.ordered
import com.tegonal.variist.testutils.PseudoArbArgsGenerator
import org.junit.jupiter.api.TestFactory

class SemiOrderedWithArbCombineAllTest : AbstractOrderedArgsGeneratorWithoutAnnotationsTest() {
	val a1s = listOf(1, 2)
	val a2s = listOf('a', 'b', 'c')
	val generator: SemiOrderedArgsGenerator<Int> = customComponentFactoryContainer.ordered.fromList(a1s)
	val randomGenerator = PseudoArbArgsGenerator(a2s)

	fun createGenerators(): OrderedArgsTestFactoryResult<Pair<Int, Any>> = sequenceOf(
		Tuple(
			"combine with 1 random",
			Tuple(generator, randomGenerator).combineAll(),
			a1s.zip(a2s)
		),
		Tuple(
			"combine with 2 random",
			Tuple(generator, randomGenerator, randomGenerator).combineAll()
				.map { (a1, a2, a3) -> a1 to (a2 to a3) },
			a1s.zip(a2s.map { it to it })
		),
		Tuple(
			"combine with 3 random",
			Tuple(generator, randomGenerator, randomGenerator, randomGenerator).combineAll()
				.map { (a1, a2, a3, a4) ->
					a1 to Triple(a2, a3, a4)
				},
			a1s.zip(a2s.map { Triple(it, it, it) })
		)
	)

	fun createGeneratorsAllPossibleCombinations() =
		sequenceOf(
			Tuple(
				"combine with 1 random",
				Tuple(generator, randomGenerator).combineAll(),
				a1s.flatMap { a1 -> a2s.map { a2 -> a1 to a2 } }
			),
			Tuple(
				"combine with 2 random",
				Tuple(generator, randomGenerator, randomGenerator).combineAll()
					.map { (a1, a2, a3) -> a1 to (a2 to a3) },
				a1s.flatMap { a1 -> a2s.map { a2 -> a1 to (a2 to a2) } }
			),
			Tuple(
				"combine with 3 random",
				Tuple(generator, randomGenerator, randomGenerator, randomGenerator).combineAll()
					.map { (a1, a2, a3, a4) ->
						a1 to Triple(a2, a3, a4)
					},
				a1s.flatMap { a1 -> a2s.map { a2 -> a1 to Triple(a2, a2, a2) } }
			)
		)

	private fun createGeneratorsUseOnlyFirstValue(): Sequence<Triple<String, SemiOrderedArgsGenerator<Int>, List<Pair<Int, Any>>>> =
		createGenerators().map { triple ->
			triple.mapSecond { semiOrderedArgsGenerator ->
				semiOrderedArgsGenerator.map { it.first }
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
}

