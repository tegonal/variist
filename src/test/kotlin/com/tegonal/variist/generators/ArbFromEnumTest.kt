package com.tegonal.variist.generators

import ch.tutteli.kbox.Tuple
import com.tegonal.variist.testutils.AbcdEnum

class ArbFromEnumTest : AbstractArbArgsGeneratorTest<AbcdEnum>() {

	override fun createGenerators(modifiedArb: ArbExtensionPoint) = sequenceOf(
		Tuple("fromEnum", modifiedArb.fromEnum<AbcdEnum>(), AbcdEnum.entries),
		Tuple("fromEnumWeighted", modifiedArb.fromEnumWeighted<AbcdEnum> {
			when (it) {
				AbcdEnum.A -> 1
				AbcdEnum.B -> 2
				AbcdEnum.C -> 10
				AbcdEnum.D -> 5
			}
		}, AbcdEnum.entries)
	)
}
