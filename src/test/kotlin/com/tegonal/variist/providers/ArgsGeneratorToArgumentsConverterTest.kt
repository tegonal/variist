package com.tegonal.variist.providers

import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.atrium.logic.utils.expectLambda
import ch.tutteli.kbox.toVararg
import com.tegonal.variist.config.*
import com.tegonal.variist.generators.*
import com.tegonal.variist.providers.impl.DefaultArgsGeneratorToArgumentsConverter
import com.tegonal.variist.testutils.Tuple4LikeStructure
import com.tegonal.variist.testutils.createArbWithCustomConfig
import com.tegonal.variist.testutils.orderedWithSeed0
import com.tegonal.variist.testutils.withMockedArgsRange
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ValueSource
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import kotlin.test.Test

class ArgsGeneratorToArgumentsConverterTest {

	val testee = DefaultArgsGeneratorToArgumentsConverter()

	val requestedAtLeastArgs1000 = AnnotationData(
		ArgsRangeOptions(requestedMinArgs = 1000)
	)

	@Test
	fun `should not decide on ArgsRange`() {

		val orderedWithRange0To100 = ComponentFactoryContainer.createBasedOnConfig(
			// neither the config setting...
			VariistConfig().copy { maxArgs = 500 }
		).withMockedArgsRange(0, 1000).ordered

		// ... nor the AnnotationData
		val maxArgs100 = AnnotationData(ArgsRangeOptions(maxArgs = 100))
		val combinations = testee.toArguments(
			"id", maxArgs100, orderedWithRange0To100.of(1, 2, 3).map { listOf(it) }
		).map { it.get().asList() }.toList()

		expect(combinations).toHaveSize(1000)
	}

	@Test
	fun pairs_splitIntoArgs() {
		val combinations = testee.toArguments(
			"id", requestedAtLeastArgs1000, orderedWithSeed0.of(1 to 2, 3 to 4).map { listOf(it) }
		).map { it.get().asList() }.toList()

		expect(combinations) {
			toHaveSize(2)
			get(0).toContainExactly(1, 2)
			get(1).toContainExactly(3, 4)
		}
	}

	@Test
	fun multiplePairs_splitIntoArgs() {
		val combinations = testee.toArguments(
			"id", requestedAtLeastArgs1000, orderedWithSeed0.of(listOf(1 to 2, 3 to 4), listOf(5 to 6, 7 to 8))
		).map { it.get().asList() }.toList()
		expect(combinations) {
			toHaveSize(2)
			get(0).toContainExactly(1, 2, 3, 4)
			get(1).toContainExactly(5, 6, 7, 8)
		}
	}

	@Test
	fun tupleLike_notSplitIntoArgs() {
		val tupleLike = Tuple4LikeStructure(1, 2L, 3.0, 4f)
		val combinations = testee.toArguments(
			"id", requestedAtLeastArgs1000, orderedWithSeed0.of(tupleLike).map { listOf(it) }
		).map { it.get().asList() }.toList()
		expect(combinations) {
			toHaveSize(1)
			get(0).toContainExactly(tupleLike)
		}
	}

	@Test
	fun listOfTuples_notSplitIntoArgs() {
		val combinations = testee.toArguments(
			"id", requestedAtLeastArgs1000, orderedWithSeed0.of(listOf(listOf(1 to 2, 3 to 4)), listOf(5 to 6, 7 to 8))
		).map { it.get().asList() }.toList()
		expect(combinations) {
			toHaveSize(2)
			get(0).toContainExactly(listOf(1 to 2, 3 to 4))
			get(1).toContainExactly(5, 6, 7, 8)
		}
	}

	@Test
	fun oneArgs_keptAsIs() {
		val args1 = Arguments.of(1, 2)
		val args2 = Arguments.of(3, 4)
		val combinations = testee.toArguments(
			"id", requestedAtLeastArgs1000, orderedWithSeed0.of(args1, args2).map { listOf(it) }
		).toList()
		expect(combinations).toContainExactly(args1, args2)
	}

	@Test
	fun multipleArgs_flattened() {
		val combinations = testee.toArguments(
			"id", requestedAtLeastArgs1000, orderedWithSeed0.of(
				listOf(Arguments.of(1, 2), Arguments.of(3, 4)),
				listOf(Arguments.of(5, 6), Arguments.of(7, 8))
			)
		).map { it.get().asList() }.toList()
		expect(combinations) {
			toHaveSize(2)
			get(0).toContainExactly(1, 2, 3, 4)
			get(1).toContainExactly(5, 6, 7, 8)
		}
	}

	@ParameterizedTest
	@ValueSource(ints = [1, 2, 4, 9, 15])
	fun randomOnly(numOfGenerators: Int) {
		val firstList = createArbWithCustomConfig(VariistConfig()).fromRange(0 until 10).map { mutableListOf(it) }

		val combinations = testee.toArguments(
			"id", requestedAtLeastArgs1000,
			(1 until numOfGenerators).fold(firstList) { generator, from ->
				generator.zip(arb.fromRange(from * 10 until from * 10 + 10)) { list, num ->
					list.also { it.add(num) }
				}
			}
		).toList()
		expect(combinations) {
			toHaveSize(1000)
			//TODO use the following once https://github.com/robstoll/atrium/issues/1359 is fixed
//			toHaveElementsAndAll {
//				feature("get", { get().asList() }) {
//					... (see below)
//				}
//			}
			combinations.forEachIndexed { index, arguments ->
				feature("index $index", { arguments.get().asList() }) {
					val entries = (0 until numOfGenerators).map {
						expectLambda<Any> {
							toBeAnInstanceOf<Int> {
								toBeGreaterThanOrEqualTo(it * 10)
								toBeLessThan(it * 10 + 10)
							}
						}
					}
					val (first, rest) = entries.toVararg()
					toContainExactly(first, *rest)
				}
			}
		}
	}

	@Test
	fun randomOnlyDependent() {
		val arb = createArbWithCustomConfig(VariistConfig())
		val startDateMin = LocalDate.now()
		val lastDayOfYear = startDateMin.with(TemporalAdjusters.lastDayOfYear())
		val startDateUntil = if(startDateMin.isEqual(lastDayOfYear)) startDateMin.plusYears(1).minusDays(2) else lastDayOfYear
		val startDates = arb.localDateFromUntil(startDateMin, startDateUntil)

		val startAndEndDates = startDates.zipDependent { startDate ->
			arb.localDateFromUntil(startDate, startDate.plusYears(1))
		}.map { listOf(it) }

		val combinations = testee.toArguments("id", requestedAtLeastArgs1000, startAndEndDates).toList()

		expect(combinations) {
			toHaveSize(1000)
			combinations.forEachIndexed { index, arguments ->
				feature("index $index", { arguments.get().asList() }) {
					toHaveSize(2)
					extractSubject { l ->
						if (l.size == 2) {
							val (a1, a2) = l
							feature("a1") { a1 }.toBeAnInstanceOf<LocalDate> {
								toBeAfterOrTheSamePointInTimeAs(startDateMin)
								toBeBefore(startDateUntil)
							}
							val start = a1 as LocalDate
							feature("a2") { a2 }.toBeAnInstanceOf<LocalDate> {
								toBeAfterOrTheSamePointInTimeAs(start)
								toBeBefore(start.plusYears(1))
							}
						}
					}
				}
			}
		}
	}
}
