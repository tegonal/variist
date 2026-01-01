package com.tegonal.variist.testutils

import ch.tutteli.kbox.Tuple
import com.tegonal.variist.generators.*
import com.tegonal.variist.providers.PredefinedBoundProviders

fun intBoundError(lowerBoundName: String, upperBoundName: String) =
	boundErrors(
		lowerBoundName, upperBoundName,
		PredefinedBoundProviders.arbIntPositiveBoundsMinSize2(),
		arb.intNegativeAndZero(),
		arb.intPositive(),
		fromUntilZero = { arb.intFromUntil(it, 0) }
	)

fun longBoundError(lowerBoundName: String, upperBoundName: String) =
	boundErrors(
		lowerBoundName, upperBoundName,
		PredefinedBoundProviders.arbLongPositiveBoundsMinSize2(),
		arb.longNegativeAndZero(),
		arb.longPositive(),
		fromUntilZero = { arb.longFromUntil(it, 0) }
	)

private fun <T : Number> boundErrors(
	lowerBoundName: String, upperBoundName: String,
	positiveBoundsMinSize2: ArbArgsGenerator<Pair<T, T>>,
	negativeAndZero: ArbArgsGenerator<T>,
	positive: ArbArgsGenerator<T>,
	fromUntilZero: (T) -> ArbArgsGenerator<T>,
) = semiOrdered.fromArbs(
	positiveBoundsMinSize2.map { (max, min) ->
		Tuple(min, max, "$lowerBoundName ($min) must be less than or equal to $upperBoundName ($max)")
	},
	negativeAndZero.zipDependent({ positive }) { l, u ->
		Tuple(l, u, "$l is not a valid $lowerBoundName, must be greater than 0")
	},
	negativeAndZero.zipDependent({ fromUntilZero(it) }) { min, max ->
		// we know lower bound is validated first, hence this fails first (and not upper must be positive),
		// the important bit is that it still fails even if min <= max
		Tuple(min, max, "$min is not a valid $lowerBoundName, must be greater than 0")
	},
	positive.zipDependent({ negativeAndZero }) { l, u ->
		Tuple(l, u, "$u is not a valid $upperBoundName, must be greater than 0")
	},
)
