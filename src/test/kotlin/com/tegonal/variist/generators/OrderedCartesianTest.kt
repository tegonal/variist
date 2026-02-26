package com.tegonal.variist.generators

import ch.tutteli.kbox.Tuple
import com.tegonal.variist.providers.ArgsSource
import org.junit.jupiter.params.ParameterizedTest

class OrderedCartesianTest : AbstractOrderedCombinerTest() {

	@ParameterizedTest
	@ArgsSource("arbNumOfIntAndChars")
	fun cartesian2(numOfInts: Int, numOfChars: Int) {
		val a1s = (1..numOfInts).toList()
		val a2s = (1..numOfChars).map { 'A' - 1 + it }
		val a1Generator = ordered.fromList(a1s)
		val a2Generator = ordered.fromList(a2s)
		val generator: OrderedArgsGenerator<Pair<Int, Char>> = a1Generator.cartesian(a2Generator)

		validateGeneration(generator.map { pair -> pair.toList() }, listOf(a1s, a2s))
	}

	@ParameterizedTest
	@ArgsSource("arbNumOfIntAndChars")
	fun combineAll2(numOfInts: Int, numOfChars: Int) {
		val a1s = (1..numOfInts).toList()
		val a2s = (1..numOfChars).map { 'A' + it }
		val a1Generator = ordered.fromList(a1s)
		val a2Generator = ordered.fromList(a2s)
		val generator: OrderedArgsGenerator<Pair<Int, Char>> = Tuple(a1Generator, a2Generator).combineAll()

		validateGeneration(generator.map { pair -> pair.toList() }, listOf(a1s, a2s))
	}

	@ParameterizedTest
	@ArgsSource("arbNumOfIntCharsAndStrings")
	fun cartesian_twice(numOfInts: Int, numOfChars: Int, numOfStrings: Int) {
		val a1s = (1..numOfInts).toList()
		val a2s = (1..numOfChars).map { 'A' - 1 + it }
		val a3s = (1..numOfStrings).map { ('a' - 1 + it).toString() }
		val a1Generator = ordered.fromList(a1s)
		val a2Generator = ordered.fromList(a2s)
		val a3Generator = ordered.fromList(a3s)
		val generator: OrderedArgsGenerator<Triple<Int, Char, String>> =
			a1Generator.cartesian(a2Generator).cartesian(a3Generator)

		validateGeneration(generator.map { triple -> triple.toList() }, listOf(a1s, a2s, a3s))
	}

	@ParameterizedTest
	@ArgsSource("arbNumOfIntCharsAndStrings")
	fun combineAll3(numOfInts: Int, numOfChars: Int, numOfStrings: Int) {
		val a1s = (1..numOfInts).toList()
		val a2s = (1..numOfChars).map { 'A' + it }
		val a3s = (1..numOfStrings).map { ('a' + it).toString() }
		val a1Generator = ordered.fromList(a1s)
		val a2Generator = ordered.fromList(a2s)
		val a3Generator = ordered.fromList(a3s)
		val generator: OrderedArgsGenerator<Triple<Int, Char, String>> =
			Tuple(a1Generator, a2Generator, a3Generator).combineAll()

		validateGeneration(generator.map { triple -> triple.toList() }, listOf(a1s, a2s, a3s))
	}

	@ParameterizedTest
	@ArgsSource("arbNumOfGeneratorsAndValues")
	fun cartesianDynamic(numOfValuesPerGenerator: List<Int>) {
		val values: List<List<Any>> = numOfValuesPerGenerator.mapIndexed { index, numOfValues ->
			when (index % 3) {
				0 -> (0 until numOfValues).toList()
				1 -> (0 until numOfValues).map { 'A' - 1 + it }
				2 -> (0 until numOfValues).map { ('a' - 1 + it).toString() }
				else -> error("modulo 3 does not work?")
			}
		}
		val generators = values.map { ordered.fromList(it) }
		val generator: OrderedArgsGenerator<List<Any>> =
			generators.drop(1).fold(generators.first().map { mutableListOf(it) }) { generator, other ->
				generator.cartesian(other) { l, a2 ->
					l.also { it.add(a2) }
				}
			}

		validateGeneration(generator, values)
	}
}
