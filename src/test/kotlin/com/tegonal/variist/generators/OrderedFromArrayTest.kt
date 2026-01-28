package com.tegonal.variist.generators

import ch.tutteli.kbox.Tuple

class OrderedFromArrayTest : AbstractOrderedArgsGeneratorTest<Any>() {

	override fun createGenerators(modifiedOrdered: OrderedExtensionPoint) = sequenceOf(
		byteArrayOf(1, 2, 3).let { Tuple("fromByteArray", modifiedOrdered.fromArray(it), it.toList()) },
		charArrayOf('a', 'b', 'c').let { Tuple("fromCharArray", modifiedOrdered.fromArray(it), it.toList()) },
		shortArrayOf(1, 2, 3).let { Tuple("fromShortArray", modifiedOrdered.fromArray(it), it.toList()) },
		intArrayOf(1, 2, 3).let { Tuple("fromIntArray", modifiedOrdered.fromArray(it), it.toList()) },
		longArrayOf(1, 2, 3).let { Tuple("fromLongArray", modifiedOrdered.fromArray(it), it.toList()) },
		floatArrayOf(1f, 2f, 3f).let { Tuple("fromFloatArray", modifiedOrdered.fromArray(it), it.toList()) },
		doubleArrayOf(1.0, 2.0, 3.0).let { Tuple("fromDoubleArray", modifiedOrdered.fromArray(it), it.toList()) },
		booleanArrayOf(true, false).let { Tuple("fromBooleanArray", modifiedOrdered.fromArray(it), it.toList()) },
		arrayOf<Any>(1, 2.0, 'a').let { Tuple("fromArray", modifiedOrdered.fromArray(it), it.toList()) },
	)
}
