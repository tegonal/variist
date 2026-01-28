package com.tegonal.variist.generators

import ch.tutteli.kbox.Tuple
import com.tegonal.variist.config.build
import com.tegonal.variist.providers.ArgsRangeDecider
import com.tegonal.variist.testutils.anyToList
import com.tegonal.variist.testutils.getTestValue

@Suppress("UNCHECKED_CAST")
class OrderedConcatenateTest : AbstractOrderedConcatenateTest() {

	override fun createGenerators(modifiedOrdered: OrderedExtensionPoint): OrderedArgsTestFactoryResult<Any> {
		val g1Variants = variants(modifiedOrdered, 0)
		val g2Variants = variants(modifiedOrdered, 1)

		val concatenated = g1Variants.cartesian(g2Variants) { (name1, g1), (name2, g2) ->
			Tuple("$name1 + $name2", g1 + g2, anyToList(getTestValue(name1, 0)) + anyToList(getTestValue(name2, 1)))
		}
		val argsRange = customComponentFactoryContainer.build<ArgsRangeDecider>().decide(concatenated)
		// not using generateAndTake here is it would be based on modifiedOrdered skip
		return concatenated.generate(argsRange.offset).take(argsRange.take)
	}
}
