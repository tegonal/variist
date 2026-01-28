package com.tegonal.variist.generators

import com.tegonal.variist.testutils.AbcdEnum
import com.tegonal.variist.testutils.XyzEnum
import com.tegonal.variist.testutils.getTestValue
import com.tegonal.variist.utils.BigInt

abstract class AbstractOrderedConcatenateTest : AbstractOrderedArgsGeneratorTest<Any>() {

	@Suppress("UNCHECKED_CAST")
	fun variants(modifiedOrdered: OrderedExtensionPoint, index: Int) =
		modifiedOrdered.of(
			"of".let { it to modifiedOrdered.of(*getTestValue(it, index) as Array<Int>) },
			"fromList".let { it to modifiedOrdered.fromList(getTestValue(it, index) as List<Any>) },
			"fromByteArray".let { it to modifiedOrdered.fromArray(getTestValue(it, index) as ByteArray) },
			"fromCharArray".let { it to modifiedOrdered.fromArray(getTestValue(it, index) as CharArray) },
			"fromShortArray".let { it to modifiedOrdered.fromArray(getTestValue(it, index) as ShortArray) },
			"fromIntArray".let { it to modifiedOrdered.fromArray(getTestValue(it, index) as IntArray) },
			"fromLongArray".let { it to modifiedOrdered.fromArray(getTestValue(it, index) as LongArray) },
			"fromFloatArray".let { it to modifiedOrdered.fromArray(getTestValue(it, index) as FloatArray) },
			"fromDoubleArray".let { it to modifiedOrdered.fromArray(getTestValue(it, index) as DoubleArray) },
			"fromBooleanArray".let { it to modifiedOrdered.fromArray(getTestValue(it, index) as BooleanArray) },
			"fromArray".let { it to modifiedOrdered.fromArray(getTestValue(it, index) as Array<Any>) },
			"fromEnum" to if (index == 0) modifiedOrdered.fromEnum<AbcdEnum>() else modifiedOrdered.fromEnum<XyzEnum>(),
			"fromCharRange".let { it to modifiedOrdered.fromRange(getTestValue(it, index) as CharRange) },
			"fromIntRange".let { it to modifiedOrdered.fromRange(getTestValue(it, index) as IntRange) },
			"fromLongRange".let { it to modifiedOrdered.fromRange(getTestValue(it, index) as LongRange) },
			"fromCharProgression".let {
				it to modifiedOrdered.fromProgression(getTestValue(it, index) as CharProgression)
			},
			"fromIntProgression".let {
				it to modifiedOrdered.fromProgression(getTestValue(it, index) as IntProgression)
			},
			"fromLongProgression".let {
				it to modifiedOrdered.fromProgression(getTestValue(it, index) as LongProgression)
			},
			"intFromUntil".let {
				(getTestValue(it, index) as IntRange).let { range ->
					it to modifiedOrdered.intFromUntil(range.start, range.endInclusive + 1)
				}
			},
			"longFromUntil".let {
				(getTestValue(it, index) as LongRange).let { range ->
					it to modifiedOrdered.longFromUntil(range.start, range.endInclusive + 1)
				}
			},
			"bigIntFromUntil".let {
				(getTestValue(it, index) as List<BigInt>).let { list ->
					it to modifiedOrdered.bigIntFromUntil(list.first(), list.last() + BigInt.ONE)
				}
			},
			"intFromTo".let {
				(getTestValue(it, index) as IntRange).let { range ->
					it to modifiedOrdered.intFromTo(range.start, range.endInclusive)
				}
			},
			"longFromTo".let {
				(getTestValue(it, index) as LongRange).let { range ->
					it to modifiedOrdered.longFromTo(range.start, range.endInclusive)
				}
			},
			"bigIntFromTo".let {
				(getTestValue(it, index) as List<BigInt>).let { list ->
					it to modifiedOrdered.bigIntFromTo(list.first(), list.last())
				}
			},
		)
}
