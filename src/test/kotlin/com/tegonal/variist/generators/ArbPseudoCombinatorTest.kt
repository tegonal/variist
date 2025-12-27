package com.tegonal.variist.generators

import ch.tutteli.atrium.api.fluent.en_GB.toContainExactlyElementsOf
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.kbox.Tuple
import com.tegonal.variist.Args
import com.tegonal.variist.Args2
import com.tegonal.variist.testutils.PseudoArbArgsGenerator
import com.tegonal.variist.utils.repeatForever
import kotlin.test.Test

class ArbPseudoCombinatorTest {

	// Note, this test relies on implementation details and is thus fragile. E.g. it is undefined how two
	// ArbArgsGenerator are combined, since the result is random the combination is random. We use
	// PseudoArbArgsGenerator which does not pick randomly but just iterates the given sequence forever

	val a1s = sequenceOf(1, 2, 3, 4)
	val a2s = sequenceOf('a', 'b', 'c', 'd')
	val a2sAfterCombine = a2s.drop(1) + sequenceOf('a')

	@Test
	fun zip() {
		val a1generator = PseudoArbArgsGenerator(a1s)
		val generator = a1generator.zip(PseudoArbArgsGenerator(a2s))
		val expected = a1s.zip(a2sAfterCombine)
		val oneCombined = expected.take(1).toList()
		val fourCombined = expected.take(4).toList()

		// zip shouldn't change the seedBaseOffset
		expect(generator._core.seedBaseOffset).toEqual(a1generator.seedBaseOffset)

		expect(generator.generateToList(1)).toContainExactlyElementsOf(oneCombined)
		expect(generator.generateToList(2)).toContainExactlyElementsOf(expected.take(2).toList())
		expect(generator.generateToList(3)).toContainExactlyElementsOf(expected.take(3).toList())
		expect(generator.generateToList(4)).toContainExactlyElementsOf(fourCombined)
		expect(generator.generateToList(5)).toContainExactlyElementsOf(fourCombined + oneCombined)
	}

	@Test
	fun zipTransformed() {
		val f: (Int, Char) -> Args2<Int, Char> = { a1, a2 -> Args.of(a1, a2) }
		val a1generator = PseudoArbArgsGenerator(a1s)
		val generator = a1generator.zip(PseudoArbArgsGenerator(a2s), f)
		val expected = a1s.zip(a2sAfterCombine, f)
		val oneCombined = expected.take(1).toList()
		val fourCombined = expected.take(4).toList()

		// zip shouldn't change the seedBaseOffset
		expect(generator._core.seedBaseOffset).toEqual(a1generator.seedBaseOffset)

		expect(generator.generateToList(1)).toContainExactlyElementsOf(oneCombined)
		expect(generator.generateToList(2)).toContainExactlyElementsOf(expected.take(2).toList())
		expect(generator.generateToList(3)).toContainExactlyElementsOf(expected.take(3).toList())
		expect(generator.generateToList(4)).toContainExactlyElementsOf(fourCombined)
		expect(generator.generateToList(5)).toContainExactlyElementsOf(fourCombined + oneCombined)
	}

	@Test
	fun combineAll() {
		val a1generator = PseudoArbArgsGenerator(a1s)
		val generator = Tuple(a1generator, PseudoArbArgsGenerator(a2s)).combineAll()
		val expected = a1s.zip(a2sAfterCombine)
		val oneCombined = expected.take(1).toList()
		val fourCombined = expected.take(4).toList()

		// combineAll shouldn't change the seedBaseOffset
		expect(generator._core.seedBaseOffset).toEqual(a1generator.seedBaseOffset)

		expect(generator.generateToList(1)).toContainExactlyElementsOf(oneCombined)
		expect(generator.generateToList(2)).toContainExactlyElementsOf(expected.take(2).toList())
		expect(generator.generateToList(3)).toContainExactlyElementsOf(expected.take(3).toList())
		expect(generator.generateToList(4)).toContainExactlyElementsOf(fourCombined)
		expect(generator.generateToList(5)).toContainExactlyElementsOf(fourCombined + oneCombined)
	}

	@Test
	fun zipDependent() {
		val a1generator = PseudoArbArgsGenerator(a1s)
		val generator = a1generator.zipDependent { int ->
			PseudoArbArgsGenerator(a2s.map { char -> char + int })
		}

		// zipDependent shouldn't change the seedBaseOffset
		expect(generator._core.seedBaseOffset).toEqual(a1generator.seedBaseOffset)

		// note, our expectation is based on an implementation detail, we know that zipDependent just
		// picks the first generated value of the resulting ArbArgsGenerator and that PseudoArbArgsGenerator drops
		// according to seedOffset
		val a2sexpected = sequenceOf('b', 'd', 'f', 'h')
		val expected = a1s.zip(a2sexpected)
		val oneCombined = expected.take(1).toList()
		val fourCombined = expected.take(4).toList()

		expect(generator.generateToList(1)).toContainExactlyElementsOf(oneCombined)
		expect(generator.generateToList(2)).toContainExactlyElementsOf(expected.take(2).toList())
		expect(generator.generateToList(3)).toContainExactlyElementsOf(expected.take(3).toList())
		expect(generator.generateToList(4)).toContainExactlyElementsOf(fourCombined)
		expect(generator.generateToList(5)).toContainExactlyElementsOf(fourCombined + oneCombined)
	}

	@Test
	fun flatZipDependent_amount1() {
		val a1generator = PseudoArbArgsGenerator(a1s)
		val generator = a1generator.flatZipDependent(amount = 1) { int ->
			PseudoArbArgsGenerator(a2s.map { char -> char + int })
		}

		// flatZipDependent shouldn't change the seedBaseOffset
		expect(generator._core.seedBaseOffset).toEqual(a1generator.seedBaseOffset)

		// note, our expectation is based on an implementation detail, we know that flatZipDependent amount=1 just
		// picks the first generated value of the resulting ArbArgsGenerator and that PseudoArbArgsGenerator drops
		// according to seedOffset
		val a2sexpected = sequenceOf('b', 'd', 'f', 'h')
		val expected = a1s.zip(a2sexpected)
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
		val a1generator = PseudoArbArgsGenerator(a1s)
		val generator = a1generator.flatZipDependent(amount = 2) { int ->
			PseudoArbArgsGenerator(a2s.map { char -> char + int })
		}

		// flatZipDependent shouldn't change the seedBaseOffset
		expect(generator._core.seedBaseOffset).toEqual(a1generator.seedBaseOffset)

		val expected = listOf(
			// seedOffset=0, so does not influence drop
			1 to 'b', // a1=1, index = 0 => 'a' + 1, drop 0 = 'b'
			1 to 'c',
			2 to 'd', // a2=2, index = 1 => 'a' + 2, drop 1 = 'd'
			2 to 'e',
			3 to 'f', // a2=3, index = 2 => 'a' + 3, drop 2 = 'f', 'g
			3 to 'g',
			4 to 'h', // a2=4, index = 3 => 'a' + 4, drop 3 = 'h'
			4 to 'e', // start over at 'a'+4 i.e. 'e'
		)

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
		val a1generator = PseudoArbArgsGenerator(a1s)
		val generator = a1generator.flatZipDependent(amount = 3) { int ->
			PseudoArbArgsGenerator(a2s.map { char -> char + int })
		}

		// flatZipDependent shouldn't change the seedBaseOffset
		expect(generator._core.seedBaseOffset).toEqual(a1generator.seedBaseOffset)

		val expected = listOf(
			1 to 'b', // a1=1, index = 0 => 'a' + 1, drop 0 = 'b'
			1 to 'c',
			1 to 'd',
			2 to 'd', // a2=2, index = 1 => 'a' + 2, drop 1 = 'd'
			2 to 'e',
			2 to 'f',
			3 to 'f', // a2=3, index = 2 => 'a' + 3, drop 2 = 'f', 'g
			3 to 'g',
			3 to 'd', // start over at 'a' + 3 i.e. 'd'
			4 to 'h', // a2=4, index = 3 => 'a' + 4, drop 3 = 'h'
			4 to 'e', // start over at 'a' + 4 i.e. 'e'
			4 to 'f',
		)

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
	fun map() {
		val f: (Int) -> Int = { it + 1 }
		val a1s = sequenceOf(1, 2, 3, 4)
		val a1generator = PseudoArbArgsGenerator(a1s)
		val generator = a1generator.map(f)

		// map shouldn't change the seedBaseOffset
		expect(generator._core.seedBaseOffset).toEqual(a1generator.seedBaseOffset)

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
		val a1generator = PseudoArbArgsGenerator(a1s)
		val generator = a1generator.filter { it % 2 == 0 }

		// filter shouldn't change the seedBaseOffset
		expect(generator._core.seedBaseOffset).toEqual(a1generator.seedBaseOffset)

		val expected = repeatForever((2 until 5 step 2).toList(), 0)
		expect(generator.generateToList(1)).toContainExactlyElementsOf(expected.take(1).toList())
		expect(generator.generateToList(2)).toContainExactlyElementsOf(expected.take(2).toList())
		expect(generator.generateToList(3)).toContainExactlyElementsOf(expected.take(3).toList())
		expect(generator.generateToList(4)).toContainExactlyElementsOf(expected.take(4).toList())
		expect(generator.generateToList(5)).toContainExactlyElementsOf(expected.take(5).toList())
	}

	@Test
	fun filterNot() {
		val a1generator = PseudoArbArgsGenerator(a1s)
		val generator = a1generator.filterNot { it % 2 == 0 }

		// filterNot shouldn't change the seedBaseOffset
		expect(generator._core.seedBaseOffset).toEqual(a1generator.seedBaseOffset)

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
		val a1generator = PseudoArbArgsGenerator(a1s)
		val generator = a1generator.transform { seq ->
			seq.flatMap { sequenceOf('a' + it, 'A' + it) }
		}

		// transform shouldn't change the seedBaseOffset
		expect(generator._core.seedBaseOffset).toEqual(a1generator.seedBaseOffset)

		val expected = repeatForever().flatMap { sequenceOf('b', 'B', 'c', 'C') }
		expect(generator.generateToList(1)).toContainExactlyElementsOf(expected.take(1).toList())
		expect(generator.generateToList(2)).toContainExactlyElementsOf(expected.take(2).toList())
		expect(generator.generateToList(3)).toContainExactlyElementsOf(expected.take(3).toList())
		expect(generator.generateToList(4)).toContainExactlyElementsOf(expected.take(4).toList())
		expect(generator.generateToList(5)).toContainExactlyElementsOf(expected.take(5).toList())
	}

	fun <T> ArbArgsGenerator<T>.generateToList(amount: Int): List<T> = generate().take(amount).toList()
}
