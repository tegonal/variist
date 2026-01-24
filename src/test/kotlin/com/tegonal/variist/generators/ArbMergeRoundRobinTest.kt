package com.tegonal.variist.generators

import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.atrium.logic.utils.expectLambda
import ch.tutteli.kbox.toVararg
import com.tegonal.variist.config._components
import com.tegonal.variist.config.config
import com.tegonal.variist.config.toOffset
import com.tegonal.variist.providers.ArgsSource
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest

class ArbMergeRoundRobinTest {

	@ParameterizedTest
	@ArgsSource("arbInt2To10")
	fun `check generators are equally picked`(numOfGenerators: Int) {
		val g1 = arb.intFromUntil(0, 10)
		val g2 = arb.intFromUntil(10, 20)
		val gOthers = if (numOfGenerators > 2) {
			Array(numOfGenerators - 2) {
				val fromValue = it * 10 + 20
				arb.intFromUntil(fromValue, fromValue + 10)
			}
		} else emptyArray()

		val numOfValues = 20
		val merged = arb.mergeRoundRobin(g1, g2, others = gOthers)
		val start = arb._components.config.seed.toOffset() % numOfGenerators
		val l = merged.generate(start).take(numOfValues).toList()

		// The following depends heavily on implementation details: we know that the first generator which contributes
		// a value is picked by the seedOffset defined above for generate
		val (first, rest) = Array(numOfValues) { index ->
			expectLambda {
				val fromValue = (start + index) % numOfGenerators * 10
				toBeGreaterThanOrEqualTo(fromValue)
				toBeLessThan(fromValue + 10)
			}
		}.toVararg()

		expect(l).toContain.inOrder.only.entries(first, *rest)
	}

	@Test
	fun `check same generator generates different values`() {
		val g = arb.intFromUntil(0, 10)
		val merged = arb.mergeRoundRobin(g, g)
		val l = merged.generate(0).take(10).toList()
		val (firstWithIndex, secondWithIndex) = l.withIndex().partition { it.index % 2 == 0 }
		// if they are exactly the same then most likely no seedOffset was used
		expect(firstWithIndex.map { it.value }).notToEqual(secondWithIndex.map { it.value })
	}

	companion object {
		@JvmStatic
		fun arbInt2To10() = arb.intFromTo(2, 10)
	}
}
