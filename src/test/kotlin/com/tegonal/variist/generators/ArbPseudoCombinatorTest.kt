package com.tegonal.variist.generators

import ch.tutteli.atrium.api.fluent.en_GB.elementsOf
import ch.tutteli.atrium.api.fluent.en_GB.notToContain
import ch.tutteli.atrium.api.fluent.en_GB.toContainExactlyElementsOf
import ch.tutteli.atrium.api.fluent.en_GB.toHaveSize
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.kbox.Tuple
import ch.tutteli.kbox.Tuple2
import com.tegonal.variist.testutils.RepeatGivenListArbArgsGenerator
import com.tegonal.variist.utils.repeatForever
import kotlin.test.Test

class ArbPseudoCombinatorTest {

	// Note, this test relies on implementation details and is thus fragile. E.g. it is undefined how two
	// ArbArgsGenerator are combined, since the result is random the combination is random. We use
	// PseudoArbArgsGenerator which does not pick randomly but just iterates the given sequence forever

	val a1s = sequenceOf(1, 2, 3, 4)
	val a2s = sequenceOf('a', 'b', 'c', 'd')

	@Test
	fun zip() {
		val a1Generator = RepeatGivenListArbArgsGenerator(a1s)
		val a2Generator = RepeatGivenListArbArgsGenerator(a2s)
		val generator = a1Generator.zip(a2Generator)
		val expected = a1s.zip(a2s)
		val oneCombined = expected.take(1).toList()
		val fourCombined = expected.take(4).toList()

		expect(generator.generateToList(1)).toContainExactlyElementsOf(oneCombined)
		expectOneSeedOffsetEachWhichIsNotTheSame(a1Generator, a2Generator)

		expect(generator.generateToList(2)).toContainExactlyElementsOf(expected.take(2).toList())
		expect(generator.generateToList(3)).toContainExactlyElementsOf(expected.take(3).toList())
		expect(generator.generateToList(4)).toContainExactlyElementsOf(fourCombined)
		expect(generator.generateToList(5)).toContainExactlyElementsOf(fourCombined + oneCombined)
	}

	@Test
	fun zipTransformed() {
		val f: (Int, Char) -> Tuple2<Int, Char> = { a1, a2 -> a1 to a2 }
		val a1Generator = RepeatGivenListArbArgsGenerator(a1s)
		val a2Generator = RepeatGivenListArbArgsGenerator(a2s)
		val generator = a1Generator.zip(a2Generator, f)
		val expected = a1s.zip(a2s, f)
		val oneCombined = expected.take(1).toList()
		val fourCombined = expected.take(4).toList()

		expect(generator.generateToList(1)).toContainExactlyElementsOf(oneCombined)
		expectOneSeedOffsetEachWhichIsNotTheSame(a1Generator, a2Generator)

		expect(generator.generateToList(2)).toContainExactlyElementsOf(expected.take(2).toList())
		expect(generator.generateToList(3)).toContainExactlyElementsOf(expected.take(3).toList())
		expect(generator.generateToList(4)).toContainExactlyElementsOf(fourCombined)
		expect(generator.generateToList(5)).toContainExactlyElementsOf(fourCombined + oneCombined)
	}

	@Test
	fun combineAll() {
		val a1Generator = RepeatGivenListArbArgsGenerator(a1s)
		val a2Generator = RepeatGivenListArbArgsGenerator(a2s)
		val generator = Tuple(a1Generator, a2Generator).combineAll()
		val expected = a1s.zip(a2s)
		val oneCombined = expected.take(1).toList()
		val fourCombined = expected.take(4).toList()

		expect(generator.generateToList(1)).toContainExactlyElementsOf(oneCombined)
		expectOneSeedOffsetEachWhichIsNotTheSame(a1Generator, a2Generator)
		expect(generator.generateToList(2)).toContainExactlyElementsOf(expected.take(2).toList())
		expect(generator.generateToList(3)).toContainExactlyElementsOf(expected.take(3).toList())
		expect(generator.generateToList(4)).toContainExactlyElementsOf(fourCombined)
		expect(generator.generateToList(5)).toContainExactlyElementsOf(fourCombined + oneCombined)
	}

	@Test
	fun zipDependent() {
		val a1Generator = RepeatGivenListArbArgsGenerator(a1s)
		val generator = a1Generator.zipDependent { int ->
			RepeatGivenListArbArgsGenerator(a2s.map { char -> char + int })
		}

		// note, our expectation is based on an implementation detail, we know that zipDependent just
		// picks the first generated value of the resulting ArbArgsGenerator and that RepeatGivenListArbArgsGenerator
		// just repeats the list regardless what seedOffset is passed, i.e. a2 is always 'a' + a1
		val expected = a1s.map { a1 -> a1 to 'a' + a1 }
		val oneCombined = expected.take(1).toList()
		val fourCombined = expected.take(4).toList()

		expect(generator.generateToList(1)).toContainExactlyElementsOf(oneCombined)
		expect(generator.generateToList(2)).toContainExactlyElementsOf(expected.take(2).toList())
		expect(generator.generateToList(3)).toContainExactlyElementsOf(expected.take(3).toList())
		expect(generator.generateToList(4)).toContainExactlyElementsOf(fourCombined)
		expect(generator.generateToList(5)).toContainExactlyElementsOf(fourCombined + oneCombined)
	}

	@Test
	fun zipDependent_doesUseDifferentSeedOffsets() {
		val g1 = RepeatGivenListArbArgsGenerator(listOf(1, 2))
		val g2 = g1 // just to make the comment below more understandable
		g1.zipDependent { g2 }.generateToList(2).count()
		// one for g1 and two for g2 because zipDependent just picks one value
		expect(g1.seedOffsets.toSet()).toHaveSize(3)
	}

	@Test
	fun flatZipDependent_amount1() {
		val a1Generator = RepeatGivenListArbArgsGenerator(a1s)
		val generator = a1Generator.flatZipDependent(amount = 1) { int ->
			RepeatGivenListArbArgsGenerator(a2s.map { char -> char + int })
		}

		// note, our expectation is based on an implementation detail, we know that flatZipDependent amount=1 just
		// picks the first generated value of the resulting ArbArgsGenerator and that RepeatGivenListArbArgsGenerator
		// just repeats the list regardless what seedOffset is passed
		val expected = a1s.flatMap { int -> a2s.take(1).map { char -> int to char + int } }
		val oneCombined = expected.take(1).toList()
		val fourCombined = expected.take(4).toList()

		expect(generator.generateToList(1)).toContainExactlyElementsOf(oneCombined)
		expect(generator.generateToList(2)).toContainExactlyElementsOf(expected.take(2).toList())
		expect(generator.generateToList(3)).toContainExactlyElementsOf(expected.take(3).toList())
		expect(generator.generateToList(4)).toContainExactlyElementsOf(fourCombined)
		expect(generator.generateToList(5)).toContainExactlyElementsOf(fourCombined + oneCombined)
	}

	@Test
	fun flatZipDependent_amount2() {
		val a1Generator = RepeatGivenListArbArgsGenerator(a1s)
		val generator = a1Generator.flatZipDependent(amount = 2) { int ->
			RepeatGivenListArbArgsGenerator(a2s.map { char -> char + int })
		}

		// note, our expectation is based on an implementation detail, we know that flatZipDependent amount=1 just
		// picks the first generated value of the resulting ArbArgsGenerator and that RepeatGivenListArbArgsGenerator
		// just repeats the list regardless what seedOffset is passed
		val expected = a1s.flatMap { int -> a2s.take(2).map { char -> int to char + int } }
		val oneCombined = expected.take(1).toList()
		val eightCombined = expected.take(8).toList()

		expect(generator.generateToList(1)).toContainExactlyElementsOf(oneCombined)
		expect(generator.generateToList(2)).toContainExactlyElementsOf(expected.take(2).toList())
		expect(generator.generateToList(3)).toContainExactlyElementsOf(expected.take(3).toList())
		expect(generator.generateToList(4)).toContainExactlyElementsOf(expected.take(4).toList())
		expect(generator.generateToList(5)).toContainExactlyElementsOf(expected.take(5).toList())
		expect(generator.generateToList(6)).toContainExactlyElementsOf(expected.take(6).toList())
		expect(generator.generateToList(7)).toContainExactlyElementsOf(expected.take(7).toList())
		expect(generator.generateToList(8)).toContainExactlyElementsOf(eightCombined)
		expect(generator.generateToList(9)).toContainExactlyElementsOf(eightCombined + oneCombined)
	}

	@Test
	fun flatZipDependent_amount3_transform() {
		val a1Generator = RepeatGivenListArbArgsGenerator(a1s)
		val generator = a1Generator.flatZipDependent(amount = 3) { int ->
			RepeatGivenListArbArgsGenerator(a2s.map { char -> char + int })
		}

		// note, our expectation is based on an implementation detail, we know that flatZipDependent amount=1 just
		// picks the first generated value of the resulting ArbArgsGenerator and that RepeatGivenListArbArgsGenerator
		// just repeats the list regardless what seedOffset is passed
		val expected = a1s.flatMap { int -> a2s.take(3).map { char -> int to char + int } }

		val oneCombined = expected.take(1).toList()
		val twelveCombined = expected.take(12).toList()

		expect(generator.generateToList(1)).toContainExactlyElementsOf(oneCombined)
		expect(generator.generateToList(2)).toContainExactlyElementsOf(expected.take(2).toList())
		expect(generator.generateToList(3)).toContainExactlyElementsOf(expected.take(3).toList())
		expect(generator.generateToList(4)).toContainExactlyElementsOf(expected.take(4).toList())
		expect(generator.generateToList(5)).toContainExactlyElementsOf(expected.take(5).toList())
		expect(generator.generateToList(6)).toContainExactlyElementsOf(expected.take(6).toList())
		expect(generator.generateToList(7)).toContainExactlyElementsOf(expected.take(7).toList())
		expect(generator.generateToList(8)).toContainExactlyElementsOf(expected.take(8).toList())
		expect(generator.generateToList(9)).toContainExactlyElementsOf(expected.take(9).toList())
		expect(generator.generateToList(10)).toContainExactlyElementsOf(expected.take(10).toList())
		expect(generator.generateToList(11)).toContainExactlyElementsOf(expected.take(11).toList())
		expect(generator.generateToList(12)).toContainExactlyElementsOf(twelveCombined)
		expect(generator.generateToList(13)).toContainExactlyElementsOf(twelveCombined + oneCombined)
	}

	@Test
	fun flatZipDependent_doesUseDifferentSeedOffsets() {
		val g1 = RepeatGivenListArbArgsGenerator(listOf(1, 2))
		val g2 = g1 // just to make the comment below more understandable
		g1.flatZipDependent(1) { g2 }.generateToList(2).count()
		// one for g1 and two for g2
		expect(g1.seedOffsets.toSet()).toHaveSize(3)
	}

	@Test
	fun map() {
		val f: (Int) -> Int = { it + 1 }
		val a1s = sequenceOf(1, 2, 3, 4)
		val a1Generator = RepeatGivenListArbArgsGenerator(a1s)
		val generator = a1Generator.map(f)

		val expected = a1s.map(f)
		val take1 = expected.take(1).toList()
		val take4 = expected.take(4).toList()

		expect(generator.generateToList(1)).toContainExactlyElementsOf(take1)
		expect(generator.generateToList(2)).toContainExactlyElementsOf(expected.take(2).toList())
		expect(generator.generateToList(3)).toContainExactlyElementsOf(expected.take(3).toList())
		expect(generator.generateToList(4)).toContainExactlyElementsOf(take4)
		expect(generator.generateToList(5)).toContainExactlyElementsOf(take4 + take1)
	}

	@Test
	fun filter() {
		val a1Generator = RepeatGivenListArbArgsGenerator(a1s)
		val generator = a1Generator.filter { it % 2 == 0 }

		val expected = repeatForever((2 until 5 step 2).toList(), 0)
		expect(generator.generateToList(1)).toContainExactlyElementsOf(expected.take(1).toList())
		expect(generator.generateToList(2)).toContainExactlyElementsOf(expected.take(2).toList())
		expect(generator.generateToList(3)).toContainExactlyElementsOf(expected.take(3).toList())
		expect(generator.generateToList(4)).toContainExactlyElementsOf(expected.take(4).toList())
		expect(generator.generateToList(5)).toContainExactlyElementsOf(expected.take(5).toList())
	}

	@Test
	fun filterNot() {
		val a1Generator = RepeatGivenListArbArgsGenerator(a1s)
		val generator = a1Generator.filterNot { it % 2 == 0 }

		val expected = repeatForever((1 until 5 step 2).toList(), 0)
		expect(generator.generateToList(1)).toContainExactlyElementsOf(expected.take(1).toList())
		expect(generator.generateToList(2)).toContainExactlyElementsOf(expected.take(2).toList())
		expect(generator.generateToList(3)).toContainExactlyElementsOf(expected.take(3).toList())
		expect(generator.generateToList(4)).toContainExactlyElementsOf(expected.take(4).toList())
		expect(generator.generateToList(5)).toContainExactlyElementsOf(expected.take(5).toList())
	}

	@Test
	fun transform() {
		val a1s = sequenceOf(1, 2)
		val a1Generator = RepeatGivenListArbArgsGenerator(a1s)
		val generator = a1Generator.transform { seq ->
			seq.flatMap { sequenceOf('a' + it, 'A' + it) }
		}

		val expected = repeatForever().flatMap { sequenceOf('b', 'B', 'c', 'C') }
		expect(generator.generateToList(1)).toContainExactlyElementsOf(expected.take(1).toList())
		expect(generator.generateToList(2)).toContainExactlyElementsOf(expected.take(2).toList())
		expect(generator.generateToList(3)).toContainExactlyElementsOf(expected.take(3).toList())
		expect(generator.generateToList(4)).toContainExactlyElementsOf(expected.take(4).toList())
		expect(generator.generateToList(5)).toContainExactlyElementsOf(expected.take(5).toList())
	}

	private fun <T> ArbArgsGenerator<T>.generateToList(amount: Int): List<T> = generate().take(amount).toList()

	private fun expectOneSeedOffsetEachWhichIsNotTheSame(
		a1Generator: RepeatGivenListArbArgsGenerator<Int>,
		a2Generator: RepeatGivenListArbArgsGenerator<Char>
	) {
		expect(a1Generator.seedOffsets) {
			toHaveSize(1)
			notToContain.elementsOf(a2Generator.seedOffsets)
		}
		expect(a2Generator.seedOffsets).toHaveSize(1)
	}
}
