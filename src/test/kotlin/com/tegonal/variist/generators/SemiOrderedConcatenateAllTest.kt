package com.tegonal.variist.generators

import ch.tutteli.atrium.api.fluent.en_GB.messageToContain
import ch.tutteli.atrium.api.fluent.en_GB.toContainExactly
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.kbox.Tuple
import com.tegonal.variist.config.build
import com.tegonal.variist.providers.ArgsRangeDecider
import com.tegonal.variist.testutils.anyToList
import com.tegonal.variist.testutils.getTestValue
import kotlin.test.Test

@Suppress("UNCHECKED_CAST")
class SemiOrderedConcatenateAllTest : AbstractOrderedConcatenateTest() {

	override fun createGenerators(modifiedOrdered: OrderedExtensionPoint): OrderedArgsTestFactoryResult<Any> {
		val g1Variants = variants(modifiedOrdered, 0)
		val g2Variants = variants(modifiedOrdered, 1)

		val concatenated = g1Variants.cartesian(g2Variants) { (name1, g1), (name2, g2) ->
			val semiG1: SemiOrderedArgsGenerator<Any> = g1
			val semiG2: SemiOrderedArgsGenerator<Any> = g2
			val l = listOf(888_888, 999_999)
			val semiG3: SemiOrderedArgsGenerator<Any> = ordered.fromList(l)

			Tuple(
				"$name1, $name2",
				listOf(semiG1, semiG3, semiG2).concatAll(),
				anyToList(getTestValue(name1, 0)) + l + anyToList(getTestValue(name2, 1))
			)
		}
		val argsRange = customComponentFactoryContainer.build<ArgsRangeDecider>().decide(concatenated)
		// not using generateAndTake here is it would be based on modifiedOrdered skip
		return concatenated.generate(argsRange.offset).take(argsRange.take)
	}

	@Test
	fun worksWithOneTimeConsumableSequence() {
		val semiG1: SemiOrderedArgsGenerator<Any> = ordered.intFromUntil(1, 5)
		val semiG2: SemiOrderedArgsGenerator<Any> = ordered.intFromUntil(10, 12)
		val seq = sequenceOf(semiG1, semiG2).constrainOnce()
		val concatenated = seq.concatAll()
		expect {
			seq.iterator()
		}.toThrow<IllegalStateException> { messageToContain("can be consumed only once") }
		expect(concatenated.generate(offset = 0).take(11).toList()).toContainExactly(1, 2, 3, 4, 10, 11, 1, 2, 3, 4, 10)
	}
}
