package com.tegonal.variist.generators

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.kbox.Tuple
import com.tegonal.variist.config._components
import com.tegonal.variist.config.arb
import com.tegonal.variist.testutils.orderedWithSeed0
import com.tegonal.variist.testutils.withMockedRandom
import org.junit.jupiter.api.Test

class ArbSeedOffsetTest {

	@Test
	fun combineIncrementsSeedOffset() {
		val arb = orderedWithSeed0._components.withMockedRandom { seed ->
			Tuple((0..9).map { it + seed * 10 }, emptyList(), emptyList())
		}.arb

		val listOfPairs = arb.int().zip(arb.int()).generateAndTake(10).toList()
		val a1s = listOfPairs.map { it.first }
		val a2s = listOfPairs.map { it.second }
		expect(a1s).toEqual((0..9).toList())
		expect(a2s).toEqual((10..19).toList())
	}

	@Test
	fun combineTakesGivenSeedOffsetIntoAccount() {
		val arb = orderedWithSeed0._components.withMockedRandom { seed ->
			Tuple((0..9).map { it + seed * 10 }, emptyList(), emptyList())
		}.arb

		val listOfPairs = arb.int().zip(arb.int()).generate(seedOffset = 2).take(10).toList()
		val a1s = listOfPairs.map { it.first }
		val a2s = listOfPairs.map { it.second }
		expect(a1s).toEqual((20..29).toList())
		expect(a2s).toEqual((30..39).toList())
	}

	@Test
	fun zipDependentUsingArbInsideIncrementsBaseSeedOffset() {
		val arb1 = orderedWithSeed0._components.withMockedRandom { seed ->
			Tuple((0..9).map { it + seed * 10 }, emptyList(), emptyList())
		}.arb

		val listOfPairs = arb1.int().zipDependent { arb.int() }.generateAndTake(10).toList()
		val a1s = listOfPairs.map { it.first }
		val a2s = listOfPairs.map { it.second }
		expect(a1s).toEqual((0..9).toList())
		expect(a2s).toEqual((10..100 step 10).toList())
	}

	@Test
	fun zipDependentTakesGivenSeedOffsetIntoAccount() {
		val arb1 = orderedWithSeed0._components.withMockedRandom { seed ->
			Tuple((0..9).map { it + seed * 10 }, emptyList(), emptyList())
		}.arb

		val listOfPairs = arb1.int().zipDependent { arb.int() }.generate(seedOffset = 3).take(10).toList()
		val a1s = listOfPairs.map { it.first }
		val a2s = listOfPairs.map { it.second }
		expect(a1s).toEqual((30..39).toList())
		expect(a2s).toEqual((40..130 step 10).toList())
	}

	@Test
	fun usingArbInAnArbExtensionReceiverIncrementsBaseSeedOffset() {
		val arb1 = orderedWithSeed0._components.withMockedRandom { seed ->
			Tuple((0..9).map { it + seed * 10 }, emptyList(), emptyList())
		}.arb
		with(arb1) {
			val arb2 = arb
			val a1s = arb1.int().generateAndTake(10).toList()
			val a2s = arb2.int().generateAndTake(10).toList()
			expect(a1s).toEqual((0..9).toList())
			expect(a2s).toEqual((10..19).toList())
		}
	}
}
