package com.tegonal.variist.generators

import ch.tutteli.atrium.testfactories.TestFactory
import ch.tutteli.kbox.Tuple
import com.tegonal.variist.config.ordered

class OrderedAsSemiOrderedTransformationTest : AbstractOrderedArgsGeneratorTest<Int>() {

	// see SemiOrderedCombineTest for tests about combine

	override fun createGenerators(modifiedOrdered: OrderedExtensionPoint) =
		listOf(1, 2, 3, 4).let { l ->
			val mapFun: (Int) -> Int = { it + 1 }
			val generator = modifiedOrdered.fromList(l) as SemiOrderedArgsGenerator<Int>

			sequenceOf(
				Tuple("map", generator.map(mapFun), l.map(mapFun)),
			)
		}

	@TestFactory
	override fun offsetPlusXReturnsTheSameAsOffsetXMinus1JustShifted() =
		offsetPlusXReturnsTheSameAsOffsetXMinus1JustShiftedTest {
			// this "law" does not hold for mapIndexed
			createGenerators(customComponentFactoryContainer.ordered).filter { it.first != "mapIndexed" }
		}
}
