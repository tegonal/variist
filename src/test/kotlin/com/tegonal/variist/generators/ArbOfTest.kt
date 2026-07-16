package com.tegonal.variist.generators

import ch.tutteli.kbox.Tuple

class ArbOfTest : AbstractArbArgsGeneratorTest<Any>() {

	override fun createGenerators(modifiedArb: ArbExtensionPoint): ArbArgsTestFactoryResult<Any> = sequenceOf(
		Tuple(
			"of(1)",
			modifiedArb.of(1),
			listOf(1)
		),
		Tuple(
			"of(1, 5)",
			modifiedArb.of(1, 5),
			listOf(1, 5)
		),
		listOf("a", "b", "c").let {
			Tuple(
				"ofWeighted",
				modifiedArb.ofWeighted(10 to it[0], 10 to it[2], *it.map { e -> 10 to e }.toTypedArray()),
				it
			)
		}
	)
}
