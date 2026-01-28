package com.tegonal.variist.generators

import ch.tutteli.kbox.Tuple
import com.tegonal.variist.testutils.AbcdEnum

class OrderedFromEnumTest : AbstractOrderedArgsGeneratorTest<AbcdEnum>() {

	override fun createGenerators(modifiedOrdered: OrderedExtensionPoint) = sequenceOf(
		Tuple("fromEnum", modifiedOrdered.fromEnum<AbcdEnum>(), AbcdEnum.entries)
	)
}
