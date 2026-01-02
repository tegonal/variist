package com.tegonal.variist.generators

import ch.tutteli.atrium.api.fluent.en_GB.toBeGreaterThanOrEqualTo
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.kbox.Tuple
import com.tegonal.variist.providers.ArgsSource
import com.tegonal.variist.providers.ArgsSourceOptions
import org.junit.jupiter.params.ParameterizedTest

class ArbFromProgressionTest : AbstractArbArgsGeneratorTest<Any>() {

	override fun createGenerators(modifiedArb: ArbExtensionPoint) = sequenceOf(
		Tuple("fromCharProgression", modifiedArb.fromProgression('a'..'d' step 2), listOf('a', 'c')),
		Tuple("fromIntProgression", modifiedArb.fromProgression(1..5 step 2), listOf(1, 3, 5)),
		Tuple("fromLongProgression", modifiedArb.fromProgression(1L..3L step 1), listOf(1L, 2L, 3L)),
	)

	@ParameterizedTest
	@ArgsSource("arbIntNegative")
	@ArgsSourceOptions(maxArgs = 1)
	fun intProgression_numberOfSteps_overflow_Int__still_works(from: Int) {
		expect(arb.fromProgression(from..Int.MAX_VALUE step 1).generate().first())
			.toBeGreaterThanOrEqualTo(from)
	}

	@ParameterizedTest
	@ArgsSource("arbLongNegative")
	@ArgsSourceOptions(maxArgs = 1)
	fun longProgression_numberOfSteps_overflow_Long__still_works(from: Long) {
		expect(arb.fromProgression(from..Long.MAX_VALUE step 1).generate().first())
			.toBeGreaterThanOrEqualTo(from)
	}
}
