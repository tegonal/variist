package com.tegonal.variist.providers

import ch.tutteli.atrium.api.fluent.en_GB.notToEqual
import ch.tutteli.atrium.api.fluent.en_GB.notToEqualOneOf
import ch.tutteli.atrium.api.fluent.en_GB.toHaveSize
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.kbox.Tuple
import ch.tutteli.kbox.flatten
import com.tegonal.variist.config.ArgsRangeOptions
import com.tegonal.variist.generators.*
import com.tegonal.variist.providers.impl.DefaultArgsGeneratorToArgumentsConverter
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest

class NotSameValuesTest {

	@ParameterizedTest
	@ArgsSource("pairOfSameSemiOrdered")
	fun pairOfSemiOrderedNotTheSameValues(s1: String, s2: String) {
		expect(s1).notToEqual(s2)
	}

	@ParameterizedTest
	@ArgsSource("tripleOfSameSemiOrdered")
	fun tripleOfSemiOrderedNotTheSameValues(s1: String, s2: String, s3: String) {
		expect(s1).notToEqualOneOf(s2, s3)
		expect(s2).notToEqual(s3)
	}

	@ParameterizedTest
	@ArgsSource("tuple4OfSameSemiOrdered")
	fun tuple4OfSemiOrderedNotTheSameValues(s1: String, s2: String, s3: String, s4: String) {
		expect(s1).notToEqualOneOf(s2, s3, s4)
		expect(s2).notToEqualOneOf(s3, s4)
		expect(s3).notToEqualOneOf(s4)
	}

	@ParameterizedTest
	@ArgsSource("tuple9OfSameSemiOrdered")
	fun tuple9OfSemiOrderedNotTheSameValues(
		s1: String,
		s2: String,
		s3: String,
		s4: String,
		s5: String,
		s6: String,
		s7: String,
		s8: String,
		s9: String
	) {
		expect(s1).notToEqualOneOf(s2, s3, s4, s5, s6, s7, s8, s9)
		expect(s2).notToEqualOneOf(s3, s4, s5, s6, s7, s8, s9)
		expect(s3).notToEqualOneOf(s4, s5, s6, s7, s8, s9)
		expect(s4).notToEqualOneOf(s5, s6, s7, s8, s9)
		expect(s5).notToEqualOneOf(s6, s7, s8, s9)
		expect(s6).notToEqualOneOf(s7, s8, s9)
		expect(s7).notToEqualOneOf(s8, s9)
		expect(s8).notToEqualOneOf(s9)
	}

	@Test
	fun tuple9SameSemiOrderedCombineAllDoesntGenerateSameValues() {
		val take = 10_000
		// note, the test tuple9OfSemiOrderedNotTheSameValues only not same values in a run i.e. within a tuple
		// we want to be sure we also don't get the same values over multiple runs
		val set = tuple9OfSameSemiOrdered().combineAll().generate(offset = 0).take(take).flatten().toSet()
		expect(set).toHaveSize(take * 9)
	}

	@Test
	fun tuple9SameArbCombineAllDoesntGenerateSameValues() {
		val take = 10_000
		val set = tuple9OfSameArb().combineAll().generate(seedOffset = 0).take(take).flatten().toSet()
		expect(set).toHaveSize(take * 9)
	}

	@Test
	fun sameArbNestedInTuplesAlsoDoesntGenerateSameValues() {
		val take = 10_000
		val g = arbStringWithLength10To20()
		val g2 = g.zip(g)
		val set = Tuple(g, g2, g, g).combineAll()
			.generate(seedOffset = 0).take(take).flatMap { listOf(it.a1) + it.a2.toList() + listOf(it.a3, it.a4) }
			.toSet()
		expect(set).toHaveSize(take * 5)
	}

	@Test
	fun tuple9ArbZipDependentAlsoDoesntGenerateSameValuesOverMultipleRuns() {
		val take = 10
		val g = arb.string(
			minLength = 10,
			maxLength = 10,
			allowedRanges = UnicodeRanges.ASCII_ALPHA_UPPER.ranges
		)
		val g9 = g.zipDependent { g }
			.zipDependent { g }
			.zipDependent { g }
			.zipDependent { g }
			.zipDependent { g }
			.zipDependent { g }
			.zipDependent { g }
			.zipDependent { g }
		val set = g9.generate(seedOffset = 0).take(take).flatten().toSet()
		expect(set).toHaveSize(take * 9)
	}

	@ParameterizedTest
	@ArgsSource("tupleOfOrderedWithTwoArb")
	@ArgsSourceOptions(minArgsOverridesSizeLimit = true, requestedMinArgs = 10)
	fun tupleOfOrderedWithTwoArbNotTheSameValues(@Suppress("unused", "UNUSED_PARAMETER") i: Int, a: String, b: String) {
		expect(a).notToEqual(b)
	}

	@Test
	fun orderedToArbArgsGeneratorDoesntGenerateSameValuesOverMultipleRuns() {
		val take = 10
		val g = ordered.intFromUntil(0, Int.MAX_VALUE).toArbArgsGenerator()
		val set = g.generate(seedOffset = 0).take(take).toSet()
		expect(set).toHaveSize(take)
	}

	@Test
	fun sameArbDifferentAnnotationDataOffset_generateAndTakeBasedOnDecider_differentValues() {
		val take = 10
		val g = arb.int()

		val set1 = g.generateAndTakeBasedOnDecider(
			AnnotationData(argsRangeOptions = ArgsRangeOptions(maxArgs = take), offset = 0)
		).toSet()
		val set2 = g.generateAndTakeBasedOnDecider(
			AnnotationData(argsRangeOptions = ArgsRangeOptions(maxArgs = take), offset = 1)
		).toSet()

		expect(set2 - set1).toHaveSize(take)
	}

	@Test
	fun sameArbDifferentAnnotationDataOffset_ArgsGeneratorToArgumentsConverter_differentValues() {
		val take = 10
		val g = arb.int()
		val converter = DefaultArgsGeneratorToArgumentsConverter()

		val set1 = converter.toArguments(
			"dummy",
			AnnotationData(argsRangeOptions = ArgsRangeOptions(maxArgs = take), offset = 0),
			g.map { listOf(it) }
		).flatMap { it.get().asSequence() }.toSet()

		val set2 = converter.toArguments(
			"dummy",
			AnnotationData(argsRangeOptions = ArgsRangeOptions(maxArgs = take), offset = 1),
			g.map { listOf(it) }
		).flatMap { it.get().asSequence() }.toSet()

		expect(set2 - set1).toHaveSize(take)
	}

	@Test
	fun sameSemiOrderedDifferentAnnotationDataOffset_generateAndTakeBasedOnDecider_differentValues() {
		val take = 10
		val g = ordered.intFromUntil(1, Int.MAX_VALUE).zip(arb.int())

		val set1 = g.generateAndTakeBasedOnDecider(
			AnnotationData(argsRangeOptions = ArgsRangeOptions(maxArgs = take), offset = 0)
		).toSet()
		val set2 = g.generateAndTakeBasedOnDecider(
			AnnotationData(argsRangeOptions = ArgsRangeOptions(maxArgs = take), offset = 1)
		).toSet()

		expect(set2 - set1).toHaveSize(take)
	}

	@Test
	fun sameSemiOrderedDifferentAnnotationDataOffset_ArgsGeneratorToArgumentsConverter_differentValues() {
		val take = 10
		val g = ordered.intFromUntil(1, Int.MAX_VALUE).zip(arb.int())
		val converter = DefaultArgsGeneratorToArgumentsConverter()

		val set1 = converter.toArguments(
			"dummy",
			AnnotationData(argsRangeOptions = ArgsRangeOptions(maxArgs = take), offset = 0),
			g.map { listOf(it) }
		).toSet()

		val set2 = converter.toArguments(
			"dummy",
			AnnotationData(argsRangeOptions = ArgsRangeOptions(maxArgs = take), offset = 1),
			g.map { listOf(it) }
		).toSet()

		expect(set2 - set1).toHaveSize(take)
		expect(set2.map { it.get()[0] } - set1.map { it.get()[0] }.toSet()).toHaveSize(take)
		expect(set2.map { it.get()[1] } - set1.map { it.get()[1] }.toSet()).toHaveSize(take)
	}

	@Test
	fun sameOrderedDifferentAnnotationDataOffset_generateAndTakeBasedOnDecider_differentValues() {
		val take = 10
		val g = ordered.intFromUntil(1, Int.MAX_VALUE)

		val set1 = g.generateAndTakeBasedOnDecider(
			AnnotationData(argsRangeOptions = ArgsRangeOptions(maxArgs = take), offset = 0)
		).toSet()
		val set2 = g.generateAndTakeBasedOnDecider(
			AnnotationData(argsRangeOptions = ArgsRangeOptions(maxArgs = take), offset = 1)
		).toSet()

		expect(set2 - set1).toHaveSize(take)
	}

	@Test
	fun sameOrderedDifferentAnnotationDataOffset_ArgsGeneratorToArgumentsConverter_differentValues() {
		val take = 10
		val g = ordered.intFromUntil(1, Int.MAX_VALUE)
		val converter = DefaultArgsGeneratorToArgumentsConverter()

		val set1 = converter.toArguments(
			"dummy",
			AnnotationData(argsRangeOptions = ArgsRangeOptions(maxArgs = take), offset = 0),
			g.map { listOf(it) }
		).flatMap { it.get().asSequence() }.toSet()

		val set2 = converter.toArguments(
			"dummy",
			AnnotationData(argsRangeOptions = ArgsRangeOptions(maxArgs = take), offset = 1),
			g.map { listOf(it) }
		).flatMap { it.get().asSequence() }.toSet()

		expect(set2 - set1).toHaveSize(take)
	}

	companion object {

		@JvmStatic
		fun pairOfSameSemiOrdered() = run {
			val g = ordered.intFromUntil(1, 11).zip(arb.string(minLength = 10, maxLength = 20)) { _, s -> s }
			Tuple(g, g)
		}

		@JvmStatic
		fun tripleOfSameSemiOrdered() = run {
			val g = ordered.intFromUntil(1, 11).zip(arb.string(minLength = 10, maxLength = 20)) { _, s -> s }
			Tuple(g, g, g)
		}

		@JvmStatic
		fun tuple4OfSameSemiOrdered() = run {
			val g = ordered.intFromUntil(1, 11).zip(arb.string(minLength = 10, maxLength = 20)) { _, s -> s }
			Tuple(g, g, g, g)
		}

		@JvmStatic
		fun tuple9OfSameSemiOrdered() = run {
			val g = ordered.intFromUntil(1, 11).zip(
				arb.string(
					minLength = 10,
					maxLength = 20,
					allowedRanges = UnicodeRanges.ASCII_ALPHA_UPPER.ranges
				)
			) { _, s -> s }
			Tuple(g, g, g, g, g, g, g, g, g)
		}

		@JvmStatic
		fun tuple9OfSameArb() = run {
			val g = arbStringWithLength10To20()
			Tuple(g, g, g, g, g, g, g, g, g)
		}

		fun arbStringWithLength10To20() = arb.string(
			minLength = 10,
			maxLength = 20,
			allowedRanges = UnicodeRanges.ASCII_ALPHA_UPPER.ranges
		)


		@JvmStatic
		fun tupleOfOrderedWithTwoArb() = run {
			val g = { arb.string(minLength = 10, maxLength = 20) }
			Tuple(ordered.of(1, 2), g(), g())
		}
	}
}
