package com.tegonal.variist.generators

import ch.tutteli.kbox.Tuple

class ArbFromListTest : AbstractArbArgsGeneratorTest<Any>() {

	override fun createGenerators(modifiedArb: ArbExtensionPoint) = sequenceOf(
		listOf(1, 2, 3).let { Tuple("fromList of Int", modifiedArb.fromList(it), it) },
		listOf('a', 'b', 'c').let { Tuple("fromList of Char", modifiedArb.fromList(it), it) },
		listOf<Any>(1, 2.0, 'a').let { Tuple("fromList of Any ", modifiedArb.fromList(it), it) },
		listOf("a", "b", "c").let {
			Tuple("fromListWeighted", modifiedArb.fromListWeighted(it.map { e -> 10 to e }), it)
		}
	)
}
