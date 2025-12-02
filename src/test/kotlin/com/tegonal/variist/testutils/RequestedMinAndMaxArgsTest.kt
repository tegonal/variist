package com.tegonal.variist.testutils

import ch.tutteli.atrium.api.fluent.en_GB.messageToContain
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.kbox.Tuple
import com.tegonal.variist.generators.*
import com.tegonal.variist.providers.ArgsSource
import org.junit.jupiter.params.ParameterizedTest

interface RequestedMinAndMaxArgsTest {

	fun setupRequestedMinArgsMaxArgs(requestedMinArgs: Int?, maxArgs: Int?)

	@ParameterizedTest
	@ArgsSource("requestedMinArgsMaxArgsHappyCases")
	fun requestedMinArgs_maxArgs_happy_cases(requestedMinArgs: Int?, maxArgs: Int?) {
		setupRequestedMinArgsMaxArgs(requestedMinArgs, maxArgs)
	}

	@ParameterizedTest
	@ArgsSource("requestedMinArgsMaxArgsFailureCases")
	fun requestedMinArgs_maxArgs_failure_cases(requestedMinArgs: Int?, maxArgs: Int?, exceptionMessageContains: String) {
		expect {
			setupRequestedMinArgsMaxArgs(requestedMinArgs, maxArgs)
		}.toThrow<IllegalStateException> {
			messageToContain(exceptionMessageContains)
		}
	}

	companion object {
		@JvmStatic
		fun requestedMinArgsMaxArgsHappyCases() = ordered.fromArbs(
			arb.of(Tuple(null, null)),
			arb.intPositive().map { Tuple(it, null) },
			arb.intPositive().map { Tuple(null, it) },
			arb.intPositive().map { Tuple(it, it) },
			arb.intBounds(minInclusive = 1, minSize = 2)
		)

		@JvmStatic
		fun requestedMinArgsMaxArgsFailureCases() = ordered.fromArbs(
			arb.intFromUntil(10, Int.MAX_VALUE).zipDependent({ arb.intFromTo(1, it - 1) }) { min, max ->
				Tuple(min, max, "requestedMinArgs ($min) must be less than or equal to maxArgs ($max)")
			},

			arb.intNegativeAndZero().zipDependent({ arb.intPositive() }) { min, max ->
				Tuple(min, max, "$min is not a valid requestedMinArgs, must be greater than 0")
			},

			arb.intPositive().zipDependent({ arb.intNegativeAndZero() }) { min, max ->
				Tuple(min, max, "$max is not a valid maxArgs, must be greater than 0")
			},

			arb.intNegativeAndZero().zipDependent({ arb.intFromUntil(it, 0) }) { min, max ->
				// we know requestedMinArgs is validated first, the important bit is that it still fails even if
				// min <= max
				Tuple(min, max, "$min is not a valid requestedMinArgs, must be greater than 0")
			},
		)
	}
}
