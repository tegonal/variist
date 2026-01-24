package com.tegonal.variist.generators

import ch.tutteli.kbox.Tuple
import com.tegonal.variist.testutils.anyToList
import com.tegonal.variist.testutils.getTestValue

class ArbMergeRoundRobinThreeTest : AbstractArbMergeTwoTest() {

	override fun createGenerators(modifiedArb: ArbExtensionPoint): ArbArgsTestFactoryResult<Any> {
		val g1Variants = variants(modifiedArb, 0)
		val g2Variants = variants(modifiedArb, 1)

		val combined = g1Variants.cartesian(g2Variants) { (name1, g1), (name2, g2) ->
			val l = listOf(888_888, 999_999)
			Tuple(
				"$name1, $name2, fromList",
				modifiedArb.mergeRoundRobin(g1, g2, modifiedArb.fromList(l)),
				anyToList(getTestValue(name1, 0)) + anyToList(getTestValue(name2, 1)) + l
			)
		}

		return combined.generateAndTakeBasedOnDecider()
	}
}
