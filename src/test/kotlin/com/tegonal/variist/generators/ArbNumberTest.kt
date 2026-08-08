package com.tegonal.variist.generators

import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.kbox.Tuple
import ch.tutteli.kbox.append
import com.tegonal.variist.config._components
import com.tegonal.variist.config.arb
import com.tegonal.variist.providers.ArgsSource
import com.tegonal.variist.testutils.withMockedRandom
import com.tegonal.variist.utils.BigInt
import com.tegonal.variist.utils.toBigInt
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import kotlin.math.max

class ArbNumberTest : AbstractArbArgsGeneratorTest<Any>() {

	override fun createGenerators(modifiedArb: ArbExtensionPoint) = sequenceOf(
		// we skip testing int(), long(), double(), intPositive, longPositive here, range is too big (see tests below)

		Tuple("intFromUntil", modifiedArb.intFromUntil(1, 5), listOf(1, 2, 3, 4)),
		Tuple("longFromUntil", modifiedArb.longFromUntil(1L, 3L), listOf(1L, 2L)),
		Tuple(
			"bigIntFromUntil",
			modifiedArb.bigIntFromUntil(BigInt.ONE, 3L.toBigInt()),
			listOf(BigInt.ONE, BigInt.TWO)
		),
		// we cannot test doubleFromUntil as the result range is infinite, see test below

		Tuple("intFromTo", modifiedArb.intFromTo(1, 5), listOf(1, 2, 3, 4, 5)),
		Tuple("longFromTo", modifiedArb.longFromTo(1, 5), listOf(1L, 2L, 3L, 4L, 5L)),
		Tuple(
			"bigIntFromTo",
			modifiedArb.bigIntFromTo(BigInt.ONE, 3L.toBigInt()),
			listOf(BigInt.ONE, BigInt.TWO, 3.toBigInt())
		),
		Tuple(
			"intComposition", modifiedArb.intComposition(5, 2, 2), listOf(
				listOf(2, 3), listOf(3, 2)
			)
		)
	)

	@Test
	fun int() {
		val ints = (0..10).shuffled()
		val arb = arb._components.withMockedRandom(ints = ints).arb
		expect(arb.int().generateAndTake(11).toList()).toEqual(ints)
	}

	@Test
	fun long() {
		val longs = (0L..10L).shuffled()
		val arb = arb._components.withMockedRandom(longs = longs).arb
		expect(arb.long().generateAndTake(11).toList()).toEqual(longs)
	}

	@Test
	fun double() {
		val doubles = (0..10L).shuffled().map { it.toDouble() }
		val arb = arb._components.withMockedRandom(doubles = doubles).arb
		expect(arb.double().generateAndTake(11).toList()).toEqual(doubles)
	}

	@Test
	fun intPositive() {
		arb.intPositive().generateAndTakeBasedOnDecider().forEach {
			expect(it).toBeGreaterThan(0)
		}
	}

	@Test
	fun longPositive() {
		arb.longPositive().generateAndTakeBasedOnDecider().forEach {
			expect(it).toBeGreaterThan(0)
		}
	}

	@Test
	fun intPositiveAndZero() {
		arb.intPositiveAndZero().generateAndTakeBasedOnDecider().forEach {
			expect(it).toBeGreaterThanOrEqualTo(0)
		}
	}

	@Test
	fun longPositiveAndZero() {
		arb.longPositiveAndZero().generateAndTakeBasedOnDecider().forEach {
			expect(it).toBeGreaterThanOrEqualTo(0)
		}
	}

	@Test
	fun intNegative() {
		arb.intNegative().generateAndTakeBasedOnDecider().forEach {
			expect(it).toBeLessThan(0)
		}
	}

	@Test
	fun longNegative() {
		arb.longNegative().generateAndTakeBasedOnDecider().forEach {
			expect(it).toBeLessThan(0)
		}
	}

	@Test
	fun intNegativeAndZero() {
		arb.intNegativeAndZero().generateAndTakeBasedOnDecider().forEach {
			expect(it).toBeLessThanOrEqualTo(0)
		}
	}

	@Test
	fun longNegativeAndZero() {
		arb.longNegativeAndZero().generateAndTakeBasedOnDecider().forEach {
			expect(it).toBeLessThanOrEqualTo(0)
		}
	}

	@ParameterizedTest
	@ArgsSource("arbIntBoundsMinSize2")
	fun intFromUntil(from: Int, until: Int) {
		arb.intFromUntil(from, until).generateAndTake(3).forEach {
			expect(it).toBeGreaterThanOrEqualTo(from).toBeLessThan(until)
		}
	}

	@ParameterizedTest
	@ArgsSource("arbLongBoundsMinSize2")
	fun longFromUntil(from: Long, until: Long) {
		arb.longFromUntil(from, until).generateAndTake(3).forEach {
			expect(it).toBeGreaterThanOrEqualTo(from).toBeLessThan(until)
		}
	}

	@ParameterizedTest
	@ArgsSource("arbLongBoundsMinSize2")
	fun doubleFromUntil(from: Double, until: Double) {
		arb.doubleFromUntil(from, until).generateAndTake(3).forEach {
			expect(it).toBeGreaterThanOrEqualTo(from).toBeLessThan(until)
		}
	}

	@ParameterizedTest
	@ArgsSource("arbLongBoundsMinSize2")
	fun bigIntFromUntil(fromLong: Long, untilLong: Long) {
		val from = fromLong.toBigInt()
		val until = untilLong.toBigInt()
		arb.bigIntFromUntil(from, until).generateAndTake(3).forEach {
			expect(it).toBeGreaterThanOrEqualTo(from).toBeLessThan(until)
		}
	}

	@ParameterizedTest
	@ArgsSource("arbIntBoundsMinSize2")
	fun intFromTo(from: Int, until: Int) {
		arb.intFromTo(from, until).generateAndTake(3).forEach {
			expect(it).toBeGreaterThanOrEqualTo(from).toBeLessThanOrEqualTo(until)
		}
	}

	@ParameterizedTest
	@ArgsSource("arbLongBoundsMinSize2")
	fun longFromTo(from: Long, until: Long) {
		arb.longFromTo(from, until).generateAndTake(3).forEach {
			expect(it).toBeGreaterThanOrEqualTo(from).toBeLessThanOrEqualTo(until)
		}
	}

	@ParameterizedTest
	@ArgsSource("arbLongBoundsMinSize2")
	fun bigIntFromTo(fromLong: Long, untilLong: Long) {
		val from = fromLong.toBigInt()
		val until = untilLong.toBigInt()
		arb.bigIntFromTo(from, until).generateAndTake(3).forEach {
			expect(it).toBeGreaterThanOrEqualTo(from).toBeLessThanOrEqualTo(until)
		}
	}

	@ParameterizedTest
	@ArgsSource("arbTotalPartsAndMinPart")
	fun intComposition(total: Int, numberOfParts: Int, minPart: Int) {
		val parts = arb.intComposition(total, numberOfParts, minPart).generateOne()
		expect(parts) {
			toHaveSize(numberOfParts)
			feature("sum") { sum() }.toEqual(total)
			toHaveElementsAndAll { toBeGreaterThanOrEqualTo(minPart) }
		}
	}

	@Test
	fun intComposition_total4_parts4_everyPart1() {
		val parts = arb.intComposition(total = 4, numberOfParts = 4).generateAndTake(5).toList()
		expect(parts).toHaveElementsAndAll { toContainExactly(1, 1, 1, 1) }
	}

	@Test
	fun intComposition_total8_parts4_minPart2_everyPart1() {
		val parts = arb.intComposition(total = 8, numberOfParts = 4, minPart = 2).generateAndTake(5).toList()
		expect(parts).toHaveElementsAndAll { toContainExactly(2, 2, 2, 2) }
	}

	@Test
	fun intComposition_total8_parts5_minPart2_throws() {
		expect {
			arb.intComposition(total = 8, numberOfParts = 5, minPart = 2)
		}.toThrow<IllegalArgumentException>()
			.messageToContain("numberOfParts (5) * minPart (2) must be less than or equal to total (8)")
	}

	companion object {
		@JvmStatic
		fun arbTotalPartsAndMinPart() = arb.intFromUntil(1, 100)
			.zipDependent { total -> arb.intFromTo(1, total) }
			.zipDependent({ (total, parts) ->
				arb.intFromTo(1, max(1, total / parts))
			}) { pair, minPart -> pair.append(minPart) }
	}
}
