package com.tegonal.variist.generators

import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.kbox.Tuple
import com.tegonal.variist.config._components
import com.tegonal.variist.config.arb
import com.tegonal.variist.config.config
import com.tegonal.variist.providers.ArgsSource
import com.tegonal.variist.providers.ArgsSourceOptions
import com.tegonal.variist.testutils.createArbWithCustomConfig
import com.tegonal.variist.testutils.firstDerivedChildFromSeed0
import com.tegonal.variist.testutils.withMockedRandom
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.params.ParameterizedTest

class ArbStringTest : AbstractArbArgsGeneratorTest<Any>() {

	override fun createGenerators(modifiedArb: ArbExtensionPoint) = sequenceOf(
		// we skip the other predefined UnicodeRanges because they are quite big and we don't prove more
		// ASCII has one UnicodeRange, ISO_8859_1_PRINTABLE has two
		UnicodeRanges.ASCII.let {
			Tuple(
				"string ASCII",
				modifiedArb.string(maxLength = 1, allowedRanges = it.ranges),
				listOf("") + it.toStrings()
			)
		},
		UnicodeRanges.ISO_8859_1_PRINTABLE.let {
			Tuple(
				"string ISO_8859_1_PRINTABLE",
				modifiedArb.string(maxLength = 1, allowedRanges = it.ranges),
				listOf("") + it.toStrings()
			)
		}
	)

	private fun UnicodeRanges.toStrings() = ranges.flatMap { range ->
		(range.start..range.endInclusive).map {
			StringBuilder().appendCodePoint(it).toString()
		}
	}

	@ParameterizedTest
	@ArgsSource("arb1To20")
	@ArgsSourceOptions(maxArgs = 5)
	fun failsIMinLengthIsOddAndMultipleNonBmpRange(numberOfUnicodeRanges: Int) {
		expect {
			arb.string(
				minLength = 1, maxLength = 2,
				allowedRanges = (1..numberOfUnicodeRanges).map {
					UnicodeRange(
						UnicodeRange.NON_BMP_START + it * 10,
						UnicodeRange.NON_BMP_START + it * 10 + 6
					)
				}.toTypedArray()
			)
		}.toThrow<IllegalArgumentException> {
			messageToContain("minLength (1) is odd", "none of the allowedRanges is in the BPM domain")
		}
	}

	@Test
	fun minLength1CheckFallbackToBmpWorks() {
		val g = arb.string(
			minLength = 1,
			maxLength = 1,
			allowedRanges = arrayOf(
				// the chances that A get's picked are quite low, hence it should be enough to have just one Test
				UnicodeRange('A', 'A'),
				UnicodeRange(UnicodeRange.NON_BMP_START, UnicodeRange.MAX_CODE_POINT)
			)
		)
		expect(g.generateOne(seedOffset = 0)).toEqual("A")
	}

	@Test
	fun checkFallbackWorksIfTwoAreGenerated() {
		val modifiedArb =
			createArbWithCustomConfig(arb._components.config.copy { seed = 0 })._components.withMockedRandom { seed ->
				// seed = 0 is the length
				if (seed == firstDerivedChildFromSeed0) Tuple(listOf(3), emptyList(), emptyList())
				// everything else are the code points
				else Tuple(listOf(0xF602 + 1, 1), emptyList(), emptyList())
			}.arb


		val g = modifiedArb.string(
			minLength = 3,
			maxLength = 6,
			allowedRanges = arrayOf(
				UnicodeRange('A', 'A'),
				UnicodeRange(UnicodeRange.NON_BMP_START, UnicodeRange.MAX_CODE_POINT)
			)
		)
		expect(g.generateOne(seedOffset = 0)).toEqual("\uD83D\uDE02A")
	}

	@TestFactory
	fun checkGenerationForLength1To20Works() =
		ordered.intFromTo(1, 20).zipDependent { length ->
			arb.string(
				minLength = length,
				maxLength = length,
				allowedRanges = arrayOf(
					UnicodeRange('A', 'Z'),
					UnicodeRange(UnicodeRange.NON_BMP_START, UnicodeRange.MAX_CODE_POINT)
				)
			)
		}.generateAndTakeBasedOnDecider().map { (length, string) ->
			dynamicTest("length $length") {
				expect(string).feature("length") { this.length }.toEqual(length)
			}
		}

	@TestFactory
	fun checkGenerationWorksIfOnlyNonBmp() =
		ordered.fromProgression(2..20 step 2).zipDependent { length ->
			arb.string(
				minLength = length,
				maxLength = length + 6,
				allowedRanges = arrayOf(
					UnicodeRange(UnicodeRange.NON_BMP_START, UnicodeRange.MAX_CODE_POINT)
				)
			)
		}.generateAndTakeBasedOnDecider().map { (length, string) ->
			dynamicTest("length $length .. ${length + 6}") {
				expect(string).feature("length", { this.length }) {
					feature("to be even") { this % 2 == 0 }.toEqual(true)
					toBeGreaterThanOrEqualTo(length)
					toBeLessThanOrEqualTo(length + 6)
				}
			}
		}

	companion object {
		@JvmStatic
		fun arb1To20() = arb.intFromTo(1, 20)
	}
}
