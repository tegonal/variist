package com.tegonal.variist.generators

import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.atrium.testfactories.TestFactoryBuilder
import ch.tutteli.kbox.Tuple
import ch.tutteli.kbox.toVararg
import com.tegonal.variist.config.ComponentFactoryContainer
import com.tegonal.variist.config._components
import com.tegonal.variist.config.config
import com.tegonal.variist.config.createBasedOnConfig
import com.tegonal.variist.testutils.BaseTest
import org.junit.jupiter.api.DynamicNode

typealias ArgsTestFactoryResult<T, ArgsGeneratorT> = Sequence<Triple<String, ArgsGeneratorT, List<T>>>

const val maxTakeInCanAlwaysTakeTheDesiredAmountTest = 200

abstract class AbstractArgsGeneratorTest : BaseTest() {
	protected val customComponentFactoryContainer =
		ComponentFactoryContainer.createBasedOnConfig(ordered._components.config)

	protected fun <T, ArgsGeneratorT : ArgsGenerator<T>, TestResultT : ArgsTestFactoryResult<T, ArgsGeneratorT>> usesGivenComponentContainerFactoryTest(
		factory: () -> TestResultT
	) = testFactory(factory) { generator, _, _ ->
		expect(generator._components).toBeTheInstance(customComponentFactoryContainer)
	}

	protected fun <T, ArgsGeneratorT : ArgsGenerator<T>, TestResultT : ArgsTestFactoryResult<T, ArgsGeneratorT>> canAlwaysTakeTheDesiredAmountTest(
		factory: () -> TestResultT,
		generate: (ArgsGeneratorT) -> Sequence<T>
	) = testFactory(factory, { testResult, givenTestFactory ->
		testResult.mapIndexed { index, (name, generator, expectedValues) ->
			{
				describe("[$index] $name") {
					arb.intFromTo(1, maxTakeInCanAlwaysTakeTheDesiredAmountTest).generate().take(10).forEach { take ->
						it("take $take") {
							givenTestFactory(generator, expectedValues, take)
						}
					}
				}
			}
		}
	}) { generator, expectedValues, takeAsAny ->
		val take = takeAsAny as Int
		var count = 0
		generate(generator).take(take).forEach {
			expect(expectedValues).toContain(it)
			++count
		}
		expect(count).toEqual(take)
	}

	protected fun <T, ArgsGeneratorT : ArgsGenerator<T>, TestResultT : ArgsTestFactoryResult<T, ArgsGeneratorT>> generateOneIsTheSameAsGenerateFirstTest(
		factory: () -> TestResultT,
		generateOne: (ArgsGeneratorT) -> T,
		generate: (ArgsGeneratorT) -> Sequence<T>
	) = testFactory(factory) { generator, _, _ ->
		expect(generateOne(generator)).toEqual(generate(generator).first())
	}

	protected fun <T, ArgsGeneratorT : ArgsGenerator<T>, TestResultT : ArgsTestFactoryResult<T, ArgsGeneratorT>> skipOneIsTheSameAsGenerateDropOneTest(
		factory: (ComponentFactoryContainer) -> TestResultT,
		generateAndTake: (ArgsGeneratorT, Int) -> Sequence<T>,
		generate: (ArgsGeneratorT) -> Sequence<T>
	) = factory(customComponentFactoryContainer).zip(
		factory(ComponentFactoryContainer.createBasedOnConfig(customComponentFactoryContainer.config.copy { skip = 1 }))
	) { firstTestResult, secondTestResult ->
		check(firstTestResult.first == secondTestResult.first) {
			"factory yielded not the same candidates on two subsequent calls, first was ${firstTestResult.first}, second was ${secondTestResult.first}"
		}
		Tuple(firstTestResult.first, firstTestResult.second, secondTestResult.second)
	}.mapIndexed { index, (name, generatorWithoutSkip, generatorWithSkip) ->
		val b: TestFactoryBuilder.() -> Unit = {
			it("[$index] $name") {
				val other = generate(generatorWithoutSkip).drop(1).take(2).toList()
				expect(other).toHaveSize(2)
				expect(generateAndTake(generatorWithSkip, 2).toList()).toContainExactly(other[0], other[1])
			}
		}
		b
	}.toVararg().let { (first, rest) ->
		// we know we cannot check it, the only reason why Atrium's testFactory does not return List<DynamicNode> has
		// to do with limitations imposed by Kotlin on typealiases in conjunction with actual/expected
		@Suppress("UNCHECKED_CAST")
		ch.tutteli.atrium.api.verbs.testFactory(first, *rest) as List<DynamicNode>
	}

	protected fun <T, ArgGeneratorT : ArgsGenerator<T>, TestResulT : ArgsTestFactoryResult<T, ArgGeneratorT>> testFactory(
		factory: () -> TestResulT,
		testFactoryDecorator: (TestResulT, TestFactoryBuilder.(generator: ArgGeneratorT, expectedValues: List<T>, decoratorValue: Any) -> Unit) -> Sequence<TestFactoryBuilder.() -> Unit> = { testResult, givenTestFactory ->
			testResult.mapIndexed { index, (name, generator, expectedValues) ->
				{
					it("[$index] $name") {
						givenTestFactory(generator, expectedValues, Unit)
					}
				}
			}
		},
		testFactory: TestFactoryBuilder.(generator: ArgGeneratorT, expectedValues: List<T>, decoratorValue: Any) -> Unit,
	): List<DynamicNode> = testFactoryDecorator(factory(), testFactory).toVararg().let { (first, rest) ->
		// we know we cannot check it, the only reason why Atrium's testFactory does not return List<DynamicNode> has
		// to do with limitations imposed by Kotlin on typealiases in conjunction with actual/expected
		@Suppress("UNCHECKED_CAST")
		ch.tutteli.atrium.api.verbs.testFactory(first, *rest) as List<DynamicNode>
	}
}
