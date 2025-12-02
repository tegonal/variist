package readme.examples

import ch.tutteli.atrium.api.fluent.en_GB.messageToContain
import ch.tutteli.atrium.api.fluent.en_GB.notToThrow
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.kbox.Tuple
import ch.tutteli.kbox.append
import com.tegonal.variist.generators.*
import com.tegonal.variist.providers.ArgsSource
import com.tegonal.variist.utils.impl.checkRequestedMinArgsMaxArgs
import org.junit.jupiter.api.Order
import org.junit.jupiter.params.ParameterizedTest
import kotlin.test.Test

@Order(1)
class FromArbsTest : PredefinedArgsProviders {

	//snippet-ordered-fromArbs-problem-start
	@Test
	fun null_null__no_error() {
		checkRequestedMinArgsMaxArgs(null, null)
	}

	@ParameterizedTest
	@ArgsSource("arbIntPositive")
	fun positiveInt_null__no_error(positiveInt: Int) {
		checkRequestedMinArgsMaxArgs(positiveInt, null)
	}

	@ParameterizedTest
	@ArgsSource("arbIntPositive")
	fun null_positiveInt__no_error(positiveInt: Int) {
		checkRequestedMinArgsMaxArgs(null, positiveInt)
	}

	@ParameterizedTest
	@ArgsSource("arbIntPositive")
	fun same_positiveInt__no_error(positiveInt: Int) {
		checkRequestedMinArgsMaxArgs(positiveInt, positiveInt)
	}
	//...
	//snippet-ordered-fromArbs-problem-end

	//snippet-ordered-fromArbs-bad-solution-start
	@ParameterizedTest
	@ArgsSource("arbIntPositive")
	fun checkRequestedMinArgsMaxArgs(positiveInt: Int) {
		checkRequestedMinArgsMaxArgs(null, null)
		checkRequestedMinArgsMaxArgs(positiveInt, null)
		checkRequestedMinArgsMaxArgs(null, positiveInt)
		checkRequestedMinArgsMaxArgs(positiveInt, positiveInt)
	}
	//snippet-ordered-fromArbs-bad-solution-end

	//snippet-ordered-fromArbs-start
	@ParameterizedTest
	@ArgsSource("requestedMinArgsMaxArgsHappyCases")
	fun requestedMinArgs_maxArgs_happy_cases(requestedMinArgs: Int?, maxArgs: Int?) {
		checkRequestedMinArgsMaxArgs(requestedMinArgs, maxArgs)
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
	}
	//snippet-ordered-fromArbs-end
}
