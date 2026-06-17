package com.tegonal.variist.providers

import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.kbox.Tuple
import ch.tutteli.kbox.flatten
import com.tegonal.variist.config.*
import com.tegonal.variist.config.impl.createVia
import com.tegonal.variist.generators.*
import com.tegonal.variist.testutils.Tuple2LikeStructure
import com.tegonal.variist.testutils.Tuple4LikeStructure
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

class ArgsProviderTest {

	@ParameterizedTest
	@ArgsSource("rawValuesInList")
	fun rawValues_inList(value: Long) {
		val range = rawValuesInRange()
		expect(value).toBeGreaterThanOrEqualTo(range.first).toBeLessThanOrEqualTo(range.last)
	}

	@ParameterizedTest
	@ArgsSource("rawValuesInSet")
	fun rawValues_inSet(value: Long) {
		val range = rawValuesInRange()
		expect(value).toBeGreaterThanOrEqualTo(range.first).toBeLessThanOrEqualTo(range.last)
	}

	@ParameterizedTest
	@ArgsSource("rawValuesInIterable")
	fun rawValues_inIterable(value: Long) {
		val range = rawValuesInRange()
		expect(value).toBeGreaterThanOrEqualTo(range.first).toBeLessThanOrEqualTo(range.last)
	}

	@ParameterizedTest
	@ArgsSource("rawValuesInSequence")
	fun rawValues_inSequence(value: Long) {
		val range = rawValuesInRange()
		expect(value).toBeGreaterThanOrEqualTo(range.first).toBeLessThanOrEqualTo(range.last)
	}

	@ParameterizedTest
	@ArgsSource("rawValuesInRange")
	fun rawValues_inRange(value: Long) {
		val range = rawValuesInRange()
		expect(value).toBeGreaterThanOrEqualTo(range.first).toBeLessThanOrEqualTo(range.last)
	}

	@ParameterizedTest
	@ArgsSource("rawArgs")
	fun rawArgs_isSplit(index: Int, value: Long) {
		val (expectedIndex, expectValue) = rawArgs()[index].get()
		expect(value).toEqual(expectValue as Long)
		expect(index).toEqual(expectedIndex as Int)
	}

	@ParameterizedTest
	@ArgsSource("orderedArgs")
	fun orderedArgs_isSplit(index: Int, value: Long) {
		val (expectedIndex, expectValue) = rawArgs()[index].get()
		expect(value).toEqual(expectValue as Long)
		expect(index).toEqual(expectedIndex as Int)
	}

	@ParameterizedTest
	@ArgsSource("arbArgs")
	fun arbArgs_isSplit(index: Int, value: Long) {
		val (expectedIndex, expectValue) = rawArgs()[index].get()
		expect(value).toEqual(expectValue as Long)
		expect(index).toEqual(expectedIndex as Int)
	}

	@ParameterizedTest
	@ArgsSource("rawPairs")
	fun rawPair_isSplit(index: Int, value: Long) {
		val (expectedIndex, expectValue) = rawPairs()[index]
		expect(value).toEqual(expectValue)
		expect(index).toEqual(expectedIndex)
	}

	@ParameterizedTest
	@ArgsSource("orderedPairs")
	fun orderedPair_isSplit(index: Int, value: Long) {
		val (expectedIndex, expectValue) = rawPairs()[index]
		expect(value).toEqual(expectValue)
		expect(index).toEqual(expectedIndex)
	}

	@ParameterizedTest
	@ArgsSource("arbPairs")
	fun arbPair_isSplit(index: Int, value: Long) {
		val (expectedIndex, expectValue) = rawPairs()[index]
		expect(value).toEqual(expectValue)
		expect(index).toEqual(expectedIndex)
	}

	@ParameterizedTest
	@ArgsSource("rawPairsInList")
	fun rawPairInList_isNotSplit(p: List<Pair<Int, Long>>) {
		expect(p).toHaveSize(1)
		val (index, value) = p.first()
		val (expectedIndex, expectValue) = rawPairs()[index]
		expect(value).toEqual(expectValue)
		expect(index).toEqual(expectedIndex)
	}


	@ParameterizedTest
	@ArgsSource("rawTupleLikeInList")
	fun rawTupleLike_isNotSplit(tupleLike: Tuple4LikeStructure<Int, Long, Double, Float>) {
		// TODO would be nicer if we take the index from ParameterizedTest, could be possible with junit 5.4/6
		val expectedTupleLike = rawTupleLikeInList()[tupleLike.a1]
		expect(tupleLike).toEqual(expectedTupleLike)
	}

	@ParameterizedTest
	@ArgsSource("orderedTupleLike")
	fun orderedTupleLike_isNotSplit(tupleLike: Tuple4LikeStructure<Int, Long, Double, Float>) {
		// TODO would be nicer if we take the index from ParameterizedTest, could be possible with junit 5.4/6
		val expectedTupleLike = rawTupleLikeInList()[tupleLike.a1]
		expect(tupleLike).toEqual(expectedTupleLike)
	}

	@ParameterizedTest
	@ArgsSource("arbTupleLike")
	fun arbTupleLike_isNotSplit(tupleLike: Tuple4LikeStructure<Int, Long, Double, Float>) {
		// TODO would be nicer if we take the index from ParameterizedTest, could be possible with junit 5.4/6
		val expectedTupleLike = rawTupleLikeInList()[tupleLike.a1]
		expect(tupleLike).toEqual(expectedTupleLike)
	}


	@ParameterizedTest
	@ArgsSource("rawNestedTuples")
	fun rawNestedTuples_areFlattenedAndSplit(i: Int, l: Long, d: Double) {
		expect(i.toDouble() + l.toDouble()).toEqual(d)
	}

	@ParameterizedTest
	@ArgsSource("orderedNestedTuples")
	fun orderedNestedTuples_areFlattenedAndSplit(i: Int, l: Long, d: Double) {
		expect(i.toDouble() + l.toDouble()).toEqual(d)
	}

	@ParameterizedTest
	@ArgsSource("arbNestedTuples")
	fun arbNestedTuples_areFlattenedAndSplit(i: Int, l: Long, d: Double) {
		expect(i.toDouble() + l.toDouble()).toEqual(d)
	}

	@ParameterizedTest
	@ArgsSource("rawNestedTupleLike")
	fun rawNestedTupleLike_onlyTupleAreFlattened(
		@Suppress("unused") c: Char,
		@Suppress("unused") s: String,
		t: Tuple4LikeStructure<Int, Long, Double, Float>
	) {
		expect(t.a1.toDouble() + t.a2.toDouble()).toEqual(t.a3)
	}

	@ParameterizedTest
	@ArgsSource("tupleOfOrdered")
	fun tupleOfOrdered_areSplit(i: Int, c: Char, l: Long) {
		expect(i.toLong() + c.code).toEqual(l)
	}

	@ParameterizedTest
	@ArgsSource("tupleLikeOfOrdered")
	fun tupleLikeOfOrdered_areSplit(i: Int, c: Char, l: Long, result: Long) {
		expect(i.toLong() + c.code + l).toEqual(result)
	}

	@ParameterizedTest
	@ArgsSource("tupleOfArb")
	fun tupleOfArb_areSplit(i: Int, c: Char, l: Long) {
		expect(i.toLong() + c.code).toEqual(l)
	}

	@ParameterizedTest
	@ArgsSource("tupleLikeOfArb")
	fun tupleLikeOfArb_areSplit(i: Int, c: Char, l: Long, result: Long) {
		expect(i.toLong() + c.code + l).toEqual(result)
	}

	@ParameterizedTest
	@ArgsSource("tupleOfOrderedReturningTuples")
	fun tupleOfOrderedReturningTuples_areFlattenedAndSplit(i1: Int, i2: Int, c1: Char, c2: Char, l: Long) {
		expect(i1.toLong() + i2 + c1.code + c2.code).toEqual(l)
	}

	@ParameterizedTest
	@ArgsSource("tupleOfArbReturningTuples")
	fun tupleOfArbReturningTuples_areFlattenedAndSplit(i1: Int, i2: Int, c1: Char, c2: Char, l: Long) {
		expect(i1.toLong() + i2 + c1.code + c2.code).toEqual(l)
	}

	@ParameterizedTest
	@ArgsSource("tupleOfOrderedAndArbReturningTuples")
	fun tupleOfOrderedAndArbReturningTuples_areFlattenedAndSplit(i1: Int, i2: Int, c1: Char, c2: Char, l: Long) {
		expect(i1.toLong() + i2 + c1.code + c2.code).toEqual(l)
	}

	@ParameterizedTest
	@ArgsSource("nestedTupleOfOrdered")
	fun nestedTupleOfOrdered_areFlattenedAndSplit(i: Int, c: Char, result: Long) {
		expect(i.toLong() + c.code).toEqual(result)
	}

	@Suppress("unused")
	@ParameterizedTest
	@ArgsSource("nestedTupleLikeOfOrdered")
	fun nestedTupleLikeOfOrdered_areFlattenedAndSplit(
		ignoredChar: Char,
		ignoredString: String,
		i: Int,
		c: Char,
		l: Long,
		result: Long
	) {
		expect(i.toLong() + c.code + l).toEqual(result)
	}

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


	@ParameterizedTest
	@ArgsSource("orderedWithArbSuffixArgsGenerator")
	fun argsGeneratorSuffixDeciderAddsArbLongToOrdered(i: Int, l: Long) {
		expect(i.toLong()).toBeLessThan(l)
	}

	@ParameterizedTest
	@ArgsSource("arbWithArbSuffixArgsGenerator")
	fun argsGeneratorSuffixDeciderAddsArbLongToArb(i: Int, l: Long) {
		expect(i.toLong()).toBeLessThan(l)
	}


	@ParameterizedTest
	@ArgsSource("orderedWithOrderedSuffixArgsGenerator")
	fun argsGeneratorSuffixDeciderAddsOrderedLongToOrdered(i: Int, l: Long) {
		expect(i.toLong()).toBeLessThan(l)
	}

	@ParameterizedTest
	@ArgsSource("arbWithOrderedSuffixArgsGenerator")
	fun argsGeneratorSuffixDeciderAddsOrderedLongToArb(i: Int, l: Long) {
		expect(i.toLong()).toBeLessThan(l)
	}

	@ParameterizedTest
	@ArgsSource("tupleOfArbOrderedAndSemiOrdered")
	fun canCombineArbOrderedAndSemiOrdered(a: Int, b: Char, c: Int) {
		expect(a + b.code).toEqual(c)
	}

	@ParameterizedTest
	@ArgsSource("tupleOfArbAndOrdered")
	fun canCombineArbAndOrdered(i: Int, l: Long) {
		expect(i.toLong()).toEqual(l)
	}

	companion object {
		@JvmStatic
		fun rawValuesInList() = rawValuesInRange().toList()

		@JvmStatic
		fun rawValuesInSet() = rawValuesInRange().toSet()

		@JvmStatic
		fun rawValuesInIterable() = rawValuesInSequence().asIterable()

		@JvmStatic
		fun rawValuesInSequence() = rawValuesInRange().asSequence()

		@JvmStatic
		fun rawValuesInRange() = (20L..22L)


		@JvmStatic
		fun rawArgs() = rawValuesInRange().mapIndexed { index, it -> Arguments.of(index, it) }

		@JvmStatic
		fun orderedArgs() = ordered.fromList(rawPairs())

		@JvmStatic
		fun arbArgs() = arb.fromList(rawPairs())


		@JvmStatic
		fun rawPairs() = rawValuesInRange().mapIndexed { index, it -> index to it }

		@JvmStatic
		fun orderedPairs() = ordered.fromList(rawPairs())

		@JvmStatic
		fun arbPairs() = arb.fromList(rawPairs())

		@JvmStatic
		fun rawPairsInList() = rawValuesInRange().mapIndexed { index, it -> listOf(index to it) }

		@JvmStatic
		fun rawTupleLikeInList() =
			listOf(Tuple4LikeStructure(0, 2L, 3.0, 4.0f), Tuple4LikeStructure(1, 20L, 30.0, 40.0f))

		@JvmStatic
		fun orderedTupleLike() = ordered.fromList(rawTupleLikeInList())

		@JvmStatic
		fun arbTupleLike() = arb.fromList(rawTupleLikeInList())

		@JvmStatic
		fun rawNestedTuples() = listOf(Tuple(Tuple(1, Tuple(2L, 3.0)), Tuple(Tuple(2, 1L), 3.0)))

		@JvmStatic
		fun rawNestedTupleLike() = listOf(
			Tuple('a', "b", Tuple4LikeStructure(0, 2L, 2.0, 4.0f)),
			Tuple('b', "c", Tuple4LikeStructure(1, 3L, 4.0, 5.0f))
		)

		@JvmStatic
		fun orderedNestedTuples() = ordered.fromList(rawNestedTuples())

		@JvmStatic
		fun arbNestedTuples() = arb.fromList(rawNestedTuples())

		@JvmStatic
		fun orderedWithArbSuffixArgsGenerator() = withArbSuffixGenerator.ordered.of(1, 2, 3)

		@JvmStatic
		fun arbWithArbSuffixArgsGenerator() = withArbSuffixGenerator.arb.of(1, 2, 3)

		private val withArbSuffixGenerator = ordered._components.merge(
			ComponentFactoryContainer.create(
				mapOf(
					SuffixArgsGeneratorDecider::class createVia { _ ->
						object : SuffixArgsGeneratorDecider {
							override fun computeSuffixArgsGenerator(annotationData: AnnotationData): ArgsGenerator<*> =
								arb.of(4L, 5L, 6L)
						}
					}
				))
		)

		@JvmStatic
		fun orderedWithOrderedSuffixArgsGenerator() = withOrderedSuffixGenerator.ordered.of(1, 2, 3)

		@JvmStatic
		fun arbWithOrderedSuffixArgsGenerator() = withOrderedSuffixGenerator.arb.of(1, 2, 3)

		private val withOrderedSuffixGenerator = ordered._components.merge(
			ComponentFactoryContainer.create(
				mapOf(
					SuffixArgsGeneratorDecider::class createVia { _ ->
						object : SuffixArgsGeneratorDecider {
							override fun computeSuffixArgsGenerator(annotationData: AnnotationData): ArgsGenerator<*> =
								arb.of(4L, 5L, 6L)
						}
					}
				))
		)

		@JvmStatic
		fun tupleOfOrdered() = Tuple(ordered.of(1), ordered.of('a'), ordered.of(1 + 'a'.code.toLong()))

		@JvmStatic
		fun tupleLikeOfOrdered() =
			Tuple4LikeStructure(ordered.of(1), ordered.of('a'), ordered.of(2L), ordered.of(1 + 'a'.code.toLong() + 2L))

		@JvmStatic
		fun tupleOfArb() = Tuple(arb.of(1), arb.of('a'), arb.of(1 + 'a'.code.toLong()))

		@JvmStatic
		fun tupleLikeOfArb() =
			Tuple4LikeStructure(arb.of(1), arb.of('a'), arb.of(2L), arb.of(1 + 'a'.code.toLong() + 2L))

		@JvmStatic
		fun tupleOfOrderedReturningTuples() =
			Tuple(ordered.of(1 to 2), ordered.of('a' to 'b'), ordered.of(1 + 2 + 'a'.code.toLong() + 'b'.code))

		@JvmStatic
		fun tupleOfArbReturningTuples() =
			Tuple(arb.of(1 to 2), arb.of('a' to 'b'), arb.of(1 + 2 + 'a'.code.toLong() + 'b'.code))

		@JvmStatic
		fun tupleOfOrderedAndArbReturningTuples() =
			Tuple(ordered.of(1 to 2), arb.of('a' to 'b'), arb.of(1 + 2 + 'a'.code.toLong() + 'b'.code))

		@JvmStatic
		fun nestedTupleOfOrdered() = Tuple(Tuple(ordered.of(1), ordered.of('a')), ordered.of(1 + 'a'.code.toLong()))


		@JvmStatic
		fun nestedTupleLikeOfOrdered() = Tuple(
			ordered.of('a'),
			Tuple4LikeStructure(
				Tuple2LikeStructure(ordered.of("b"), ordered.of(1)),
				ordered.of('a'),
				ordered.of(2),
				ordered.of(1 + 'a'.code.toLong() + 2L)
			)
		)

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

		@JvmStatic
		fun tupleOfArbOrderedAndSemiOrdered() =
			Tuple(arb.of(1), ordered.of('a'), semiOrdered.fromArbs(arb.of(1 + 'a'.code)))

		@JvmStatic
		fun tupleOfArbAndOrdered() =
			Tuple(arb.of(1), ordered.of(1L))
	}
}
