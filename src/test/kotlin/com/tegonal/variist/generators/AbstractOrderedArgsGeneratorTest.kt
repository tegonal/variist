package com.tegonal.variist.generators

import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.atrium.api.verbs.expectGrouped
import ch.tutteli.atrium.creating.Expect
import ch.tutteli.atrium.testfactories.TestFactory
import com.tegonal.variist.config._components
import com.tegonal.variist.config.config
import com.tegonal.variist.config.ordered
import com.tegonal.variist.config.toOffset
import com.tegonal.variist.providers.ArgsRange
import com.tegonal.variist.utils.createVariistRandom

typealias OrderedArgsTestFactoryResult<T> = ArgsTestFactoryResult<T, SemiOrderedArgsGenerator<T>>

abstract class AbstractOrderedArgsGeneratorWithoutAnnotationsTest : AbstractArgsGeneratorTest() {

	protected fun <T> canAlwaysTakeTheDesiredAmountTest(factory: () -> OrderedArgsTestFactoryResult<T>) =
		super.canAlwaysTakeTheDesiredAmountTest(factory) { it.generate(offset = 0) }

	protected fun <T> coversAllCasesTest(factory: () -> OrderedArgsTestFactoryResult<T>) =
		testFactory(factory) { generator, expectedValues, _ ->
			expectGrouped {
				expect(generator.size).toEqual(expectedValues.size)

				(1..expectedValues.size).forEach { take ->
					group("take $take") {
						val l = generator.generate(offset = 0).take(take).toList()
						@Suppress("UNCHECKED_CAST")
						(expect(l) as Expect<List<*>>).toContainExactlyElementsOf(expectedValues.take(take))
					}
				}
			}
		}

	protected fun <T> minusOffsetThrowsTest(factory: () -> OrderedArgsTestFactoryResult<T>) =
		createVariistRandom().let { variistRandom ->
			testFactory(factory) { generator, _, _ ->
				expect {
					generator.generate(variistRandom.nextInt(Int.MIN_VALUE, -1))
				}.toThrow<IllegalStateException>()
			}
		}


	protected fun <T> returnsDifferentValuesUntilReachingSizeAndThenRepeatsTest(
		factory: () -> OrderedArgsTestFactoryResult<T>
	) = testFactory(factory) { generator, _, _ ->
		val generatorSize = generator.size
		val doubleSize = generatorSize * 2
		val list = generator.generate(offset = 0).take(doubleSize).toList()
		expect(list) {
			size.toEqual(doubleSize)
			feature("toSet", { toSet() }) {
				size.toEqual(generatorSize)
			}
			(0 until generatorSize).forEach {
				get(it).toEqual(list[it + generatorSize])
			}
		}
	}

	protected fun <T> offsetPlusXReturnsTheSameAsOffsetXMinus1JustShiftedTest(
		factory: () -> OrderedArgsTestFactoryResult<T>
	) = testFactory(factory) { generator, expectedValues, _ ->
		val take = expectedValues.size
		val list = generator.generate(offset = 0).take(take).toList()
		expectGrouped {
			(1 until take).forEach { offset ->
				generator.generate(offset).take(take).forEachIndexed { index, value ->
					val listIndex = (index + offset) % take
					group("offset $offset, index $index same as offset ${offset - 1}, index $listIndex") {
						expect(value).toEqual(list[listIndex])
					}
				}
			}
		}
	}

}

abstract class AbstractOrderedArgsGeneratorTest<T> : AbstractOrderedArgsGeneratorWithoutAnnotationsTest() {

	abstract fun createGenerators(modifiedOrdered: OrderedExtensionPoint): OrderedArgsTestFactoryResult<T>

	@TestFactory
	fun usesGivenComponentContainerFactory() =
		usesGivenComponentContainerFactoryTest { createGenerators(customComponentFactoryContainer.ordered) }

	@TestFactory
	fun canAlwaysTakeTheDesiredAmount() =
		canAlwaysTakeTheDesiredAmountTest { createGenerators(customComponentFactoryContainer.ordered) }

	@TestFactory
	fun generateOneIsTheSameAsGenerateFirst() = generateOneIsTheSameAsGenerateFirstTest(
		factory = { createGenerators(customComponentFactoryContainer.ordered) },
		generateOne = { it.generateOne(customComponentFactoryContainer.config.seed.toOffset()) },
		generate = { it.generate(customComponentFactoryContainer.config.seed.toOffset()) }
	)

	@TestFactory
	fun coversAllCases() = coversAllCasesTest { createGenerators(customComponentFactoryContainer.ordered) }

	@TestFactory
	fun minusOffsetThrows() = minusOffsetThrowsTest { createGenerators(customComponentFactoryContainer.ordered) }

	@TestFactory
	fun returnsDifferentValuesUntilReachingSizeAndThenRepeats() =
		returnsDifferentValuesUntilReachingSizeAndThenRepeatsTest { createGenerators(customComponentFactoryContainer.ordered) }

	@TestFactory
	open fun offsetPlusXReturnsTheSameAsOffsetXMinus1JustShifted() =
		offsetPlusXReturnsTheSameAsOffsetXMinus1JustShiftedTest { createGenerators(customComponentFactoryContainer.ordered) }

	@TestFactory
	fun skipOneIsTheSameAsGenerateDropOne() = skipOneIsTheSameAsGenerateDropOneTest(
		factory = { createGenerators(it.ordered) },
		generateAndTake = { generator, num ->
			generator.generateAndTake(ArgsRange(generator._components.config.seed.toOffset(), num))
		},
		generate = { generator -> generator.generate(generator._components.config.seed.toOffset()) }
	)
}
