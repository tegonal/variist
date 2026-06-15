package com.tegonal.variist.generators

import ch.tutteli.atrium.testfactories.TestFactory
import com.tegonal.variist.config.arb
import com.tegonal.variist.config.config
import com.tegonal.variist.config.toOffset

typealias ArbArgsTestFactoryResult<T> = ArgsTestFactoryResult<T, ArbArgsGenerator<T>>

abstract class AbstractArbArgsGeneratorWithoutAnnotationsTest : AbstractArgsGeneratorTest()

abstract class AbstractArbArgsGeneratorTest<T> : AbstractArbArgsGeneratorWithoutAnnotationsTest() {

	//TODO 2.2.0 add a test case where we use a mocked Random so that we can be sure
	// it yields all values in case of an ArbSizeAware or the like
	abstract fun createGenerators(modifiedArb: ArbExtensionPoint): ArbArgsTestFactoryResult<T>

	@TestFactory
	fun usesGivenComponentContainerFactory() =
		usesGivenComponentContainerFactoryTest { createGenerators(customComponentFactoryContainer.arb) }

	@TestFactory
	fun canAlwaysTakeTheDesiredAmount() =
		canAlwaysTakeTheDesiredAmountTest({ createGenerators(customComponentFactoryContainer.arb) }) { it.generate() }

	@TestFactory
	fun generateOneIsTheSameAsGenerateFirst() = generateOneIsTheSameAsGenerateFirstTest(
		factory = { createGenerators(customComponentFactoryContainer.arb) },
		generateOne = { it.generateOne(customComponentFactoryContainer.config.seed.toOffset()) },
		generate = { it.generate(customComponentFactoryContainer.config.seed.toOffset()) }
	)

	@TestFactory
	fun skipOneIsTheSameAsGenerateDrop1() = skipOneIsTheSameAsGenerateDropOneTest(
		factory = { componentFactoryContainer -> createGenerators(componentFactoryContainer.arb) },
		generateAndTake = { generator, num -> generator.generateAndTake(num) },
		generate = { generator -> generator.generate() }
	)
}
