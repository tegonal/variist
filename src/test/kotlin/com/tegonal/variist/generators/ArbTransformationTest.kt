package com.tegonal.variist.generators

import ch.tutteli.atrium.api.fluent.en_GB.toContainExactly
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.kbox.Tuple
import com.tegonal.variist.generators.impl.flatMapIndexedInternal
import com.tegonal.variist.generators.impl.mapIndexedInternal
import com.tegonal.variist.testutils.RepeatGivenListArbArgsGenerator
import com.tegonal.variist.testutils.firstDerivedChildFromSeed0
import org.junit.jupiter.api.Test

class ArbTransformationTest : AbstractArbArgsGeneratorTest<Any>() {

	// see ArbPseudoCombinatorTest for tests about zip/zipDependent etc.

	override fun createGenerators(modifiedArb: ArbExtensionPoint) =
		listOf(1, 2, 3, 4).let { l ->
			val mapFun: (Int) -> Int = { it + 1 }
			val generator = modifiedArb.fromList(l)
			sequenceOf(
				Tuple("map", generator.map(mapFun), l.map(mapFun)),
				Tuple(
					"filter", generator.filter { it % 2 == 0 },
					listOf(2, 4)
				),
				Tuple(
					"filterNot", generator.filterNot { it % 2 == 0 },
					listOf(1, 3)
				),
				Tuple(
					"chunked", generator.chunked(2),
					(1..4).flatMap { first -> (1..4).map { second -> listOf(first, second) } }
				),
				Tuple(
					"chunked with transform", generator.chunked(2) { it.toSet() },
					(1..4).flatMap { first -> (1..4).map { second -> setOf(first, second) } }
				),
				Tuple(
					"transform - flatMap",
					generator.transform { s -> s.flatMap { sequenceOf(it, it + 10) } },
					listOf(1, 11, 2, 12, 3, 13, 4, 14)
				),
				Tuple(
					"transform - zip",
					generator.transform { s -> s.zip(s) { a1, a2 -> a1 + a2 } },
					listOf(
						/* 1+1 = */ 2,
						/* 1+2 = */ 3,
						/* 1+3 = */ 4,
						/* 1+4 = */ 5,
						/* 2+4 = */ 6,
						/* 3+4 = */ 7,
						/* 4+4 = */ 8
					)
				),

				// internal functions --------------------------------------------------------------------------------
				Tuple(
					"mapIndexedInternal",
					generator.mapIndexedInternal { index, it, _ -> index to mapFun(it) },
					(0..maxTakeInCanAlwaysTakeTheDesiredAmountTest).flatMap { index ->
						l.map { index to mapFun(it) }
					}
				),
				Tuple(
					"flatMapIndexedInternal",
					generator.flatMapIndexedInternal { index, it, _ -> sequenceOf(index to mapFun(it)) },
					(0..maxTakeInCanAlwaysTakeTheDesiredAmountTest).flatMap { index ->
						l.map { index to mapFun(it) }
					}
				),
			)
		}

	@Test
	fun `check transform passes different seedOffset`() {
		val g = RepeatGivenListArbArgsGenerator(listOf(1, 2))
		g.transform { int -> int + 10 }.generate(seedOffset = 0).take(2).count()
		expect(g.seedOffsets).toContainExactly(firstDerivedChildFromSeed0)
	}

}
