package com.tegonal.variist.generators

import ch.tutteli.kbox.Tuple
import com.tegonal.variist.utils.BigInt
import com.tegonal.variist.utils.toBigInt

class OrderedNumberTest : AbstractOrderedArgsGeneratorTest<Any>() {

	override fun createGenerators(modifiedOrdered: OrderedExtensionPoint) = sequenceOf(
		Tuple("intFromUntil", modifiedOrdered.intFromUntil(1, 4), listOf(1, 2, 3)),
		Tuple("longFromUntil", modifiedOrdered.longFromUntil(1L, 5L), listOf(1L, 2L, 3L, 4L)),
		Tuple(
			"bigIntFromUntil",
			modifiedOrdered.bigIntFromUntil(BigInt.ZERO, 3.toBigInt()),
			listOf(BigInt.ZERO, BigInt.ONE, BigInt.TWO)
		),
		Tuple("intFromTo", modifiedOrdered.intFromTo(1, 4), listOf(1, 2, 3, 4)),
		Tuple("longFromTo", modifiedOrdered.longFromTo(1L, 5L), listOf(1L, 2L, 3L, 4L, 5)),
		Tuple(
			"bigIntFromTo",
			modifiedOrdered.bigIntFromTo(BigInt.ZERO, 3.toBigInt()),
			listOf(BigInt.ZERO, BigInt.ONE, BigInt.TWO, 3.toBigInt())
		),
	)
}
