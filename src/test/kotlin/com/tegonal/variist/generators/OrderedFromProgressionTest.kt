package com.tegonal.variist.generators

import ch.tutteli.atrium.api.fluent.en_GB.messageToContain
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.kbox.Tuple
import com.tegonal.variist.providers.ArgsRange
import com.tegonal.variist.providers.ArgsSource
import com.tegonal.variist.providers.ArgsSourceOptions
import com.tegonal.variist.utils.repeatForever
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest

class OrderedFromProgressionTest : AbstractOrderedArgsGeneratorTest<Any>() {

	override fun createGenerators() = sequenceOf(
		Tuple("fromCharProgression", modifiedOrdered.fromProgression('a'..'d' step 2), listOf('a', 'c')),
		Tuple("fromIntProgression", modifiedOrdered.fromProgression(1..5 step 2), listOf(1, 3, 5)),
		Tuple("fromLongProgression", modifiedOrdered.fromProgression(1L..3L step 1), listOf(1L, 2L, 3L)),
	)

	@ParameterizedTest
	@ArgsSource("steps")
	fun charDifferentSteps(step: Int) {
		val argRange = ArgsRange(offset = 0, take = 4)
		val l1 = ordered.fromProgression('a'..'d' step step).generateAndTake(argRange).toList()
		expect(l1).toEqual(
			repeatForever().flatMap {
				listOf('a', 'b', 'c', 'd').withIndex().filter { (index, _) -> index % step == 0 }.map { (_, it) -> it }
			}.take(4).toList()
		)
		val l2 = ordered.fromProgression('d' downTo 'a' step step).generateAndTake(argRange).toList()
		expect(l2).toEqual(
			repeatForever().flatMap {
				listOf('d', 'c', 'b', 'a').withIndex().filter { (index, _) -> index % step == 0 }.map { (_, it) -> it }
			}.take(4).toList()
		)
	}

	@ParameterizedTest
	@ArgsSource("arbIntNegative")
	@ArgsSourceOptions(maxArgs = 1)
	fun intProgression_numberOfSteps_overflow_Int__throws(from: Int) {
		expect {
			ordered.fromProgression(from..Int.MAX_VALUE step 1)
		}.toThrow<IllegalStateException> {
			messageToContain("OrderedArgsGenerator.size only supports Int")
		}
	}

	@ParameterizedTest
	@ArgsSource("arbIntNegative")
	@ArgsSourceOptions(maxArgs = 1)
	fun longProgression_numberOfSteps_overflow_Int__throws(from: Int) {
		expect {
			ordered.fromProgression(from.toLong()..Int.MAX_VALUE.toLong() step 1)
		}.toThrow<IllegalStateException> {
			messageToContain("OrderedArgsGenerator.size only supports Int")
		}
	}

	@ParameterizedTest
	@ArgsSource("arbLongNegative")
	@ArgsSourceOptions(maxArgs = 1)
	fun longProgression_numberOfSteps_overflow_Long__throws(from: Long) {
		expect {
			ordered.fromProgression(from..Long.MAX_VALUE step 1)
		}.toThrow<IllegalStateException> {
			messageToContain("OrderedArgsGenerator.size only supports Int")
		}
	}

	companion object {
		@JvmStatic
		fun steps() = ordered.intFromUntil(1, 6)
	}
}
