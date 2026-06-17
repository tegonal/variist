package com.tegonal.variist.testutils

import com.tegonal.variist.utils.toBigInt
import java.time.LocalDate
import java.time.LocalDateTime

private val testValues: Map<String, List<Any>> = mapOf(
	"of" to listOf(arrayOf(1, 2, 3), arrayOf(10, 11, 12, 13)),
	"fromList" to listOf(listOf(4, 5, 6), listOf(14, 15)),
	"fromByteArray" to listOf(byteArrayOf(7, 8, 9), byteArrayOf(70)),
	"fromCharArray" to listOf(charArrayOf('a', 'b', 'c'), charArrayOf('A', 'B', 'C', 'D')),
	"fromShortArray" to listOf(shortArrayOf(10, 11, 12), shortArrayOf(100, 110)),
	"fromIntArray" to listOf(intArrayOf(16, 17, 18), intArrayOf(19)),
	"fromLongArray" to listOf(longArrayOf(20, 21, 22, 23), longArrayOf(24, 25, 26)),
	"fromFloatArray" to listOf(floatArrayOf(1.2f, 2.5f), floatArrayOf(10.2f, 20.5f, 78.2f)),
	"fromDoubleArray" to listOf(doubleArrayOf(12.3, 17.4, 16.0, 17.89), doubleArrayOf(120.3, 170.4)),
	"fromBooleanArray" to listOf(booleanArrayOf(true), booleanArrayOf(false)),
	"fromArray" to listOf(arrayOf<Any>(1000, 2.0, 'z'), arrayOf<Any>(10_000, 'Z')),
	"fromEnum" to listOf(AbcdEnum.entries.toList(), XyzEnum.entries.toList()),
	"fromCharRange" to listOf('f'..'i', 'F'..'H'),
	"fromIntRange" to listOf(100..104, 105..107),
	"fromLongRange" to listOf(110L..113L, 114L..119L),
	"fromCharProgression" to listOf('k'..'p' step 2, 'K'..'O' step 3),
	"fromIntProgression" to listOf(120..130 step 2, 150 downTo 140 step 2),
	"fromLongProgression" to listOf(160L..170L step 3, 190L downTo 171L step 5),
	"intFromUntil" to listOf(200..205, 206..210),
	"longFromUntil" to listOf(211L..213L, 214L..219L),
	"bigIntFromUntil" to listOf((220L..224L).map { it.toBigInt() }, (225L..228L).map { it.toBigInt() }),
	"intFromTo" to listOf(230..233, 234..238),
	"longFromTo" to listOf(240L..242L, 243L..247L),
	"bigIntFromTo" to listOf((250L..255L).map { it.toBigInt() }, (256L..259L).map { it.toBigInt() }),
	"localDateFromUntil" to LocalDate.now().let { now ->
		listOf(
			arrayOf(now, now.plusDays(1)),
			arrayOf(now.plusDays(3), now.plusDays(4), now.plusDays(5))
		)
	},
	"localDateTimeFromUntil" to LocalDateTime.now().let { now ->
		listOf(
			arrayOf(now, now.plusDays(1)),
			arrayOf(now.plusDays(3), now.plusDays(4), now.plusDays(5))
		)
	}
	//TODO 3.1.0 add missing functions
)

fun getTestValue(key: String, index: Int) = (testValues[key]!!).let { it[index] }

@Suppress("UNCHECKED_CAST")
fun anyToList(v: Any): List<Any> = when (v) {
	is List<*> -> v as List<Any>
	is Iterable<*> -> v.toList() as List<Any>
	is Array<*> -> v.toList() as List<Any>
	is ByteArray -> v.toList()
	is CharArray -> v.toList()
	is ShortArray -> v.toList()
	is IntArray -> v.toList()
	is LongArray -> v.toList()
	is FloatArray -> v.toList()
	is DoubleArray -> v.toList()
	is BooleanArray -> v.toList()
	else -> error("a type which we did not expect ${v::class.qualifiedName}")
}
