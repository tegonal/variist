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
	fun requestedMinArgs_maxArgs_failure_cases(
		requestedMinArgs: Int?,
		maxArgs: Int?,
		exceptionMessageContains: String
	) {
		expect {
			setupRequestedMinArgsMaxArgs(requestedMinArgs, maxArgs)
		}.toThrow<IllegalStateException> {
			messageToContain(exceptionMessageContains)
		}
	}

	companion object {
		@JvmStatic
		fun requestedMinArgsMaxArgsHappyCases() = semiOrdered.fromArbs(
			arb.of(Tuple(null, null)),
			arb.intPositive().map { Tuple(it, null) },
			arb.intPositive().map { Tuple(null, it) },
			arb.intPositive().map { Tuple(it, it) },
			arb.intBounds(minInclusive = 1, minSize = 2)
		)

		@JvmStatic
		fun requestedMinArgsMaxArgsFailureCases() = intBoundError("requestedMinArgs", "maxArgs")
	}
}
