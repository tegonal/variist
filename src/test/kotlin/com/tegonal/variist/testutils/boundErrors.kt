package com.tegonal.variist.testutils

import ch.tutteli.kbox.Tuple
import ch.tutteli.kbox.Tuple3
import com.tegonal.variist.generators.*
import com.tegonal.variist.providers.PredefinedBoundProviders
import org.junit.jupiter.api.Named

fun intMinSizeMaxSizeError(description: String, factory: (Int, Int) -> () -> ArbArgsGenerator<*>) =
	intBoundError("minSize", "maxSize").lowerUpperBoundCase(description, factory)

fun intBoundError(lowerBoundName: String, upperBoundName: String) =
	boundErrors(
		lowerBoundName, upperBoundName,
		PredefinedBoundProviders.arbIntPositiveBoundsMinSize2(),
		arb.intNegativeAndZero(),
		arb.intPositive(),
		fromUntilZero = { arb.intFromUntil(it, 0) }
	)

fun longMinSizeMaxSizeError(description: String, factory: (Long, Long) -> () -> ArbArgsGenerator<*>) =
	longBoundError("minSize", "maxSize").lowerUpperBoundCase(description, factory)


fun longBoundError(lowerBoundName: String, upperBoundName: String) =
	boundErrors(
		lowerBoundName, upperBoundName,
		PredefinedBoundProviders.arbLongPositiveBoundsMinSize2(),
		arb.longNegativeAndZero(),
		arb.longPositive(),
		fromUntilZero = { arb.longFromUntil(it, 0) }
	)


fun <T : Number> SemiOrderedArgsGenerator<Tuple3<T, T, String>>.lowerUpperBoundCase(
	description: String,
	factory: (T, T) -> () -> ArbArgsGenerator<*>
) = map { (lower, upper, errMsg) -> Tuple(description, errMsg, Named.of("f", factory(lower, upper))) }

fun minInclusiveMustBeLessThanMaxInclusive(lowerBound: Any, upperBound: Any, minSize: Any) =
	"minInclusive ($upperBound) must be less than or equal to `maxInclusive ($lowerBound) - minSize ($minSize) + 1`"

fun <T : Any> ArbArgsGenerator<Tuple3<T, T, T>>.minMaxInclusiveCase(
	description: String,
	factory: (T, T, T) -> () -> ArbArgsGenerator<*>
) = map { (lower, upper, minSize) ->
	Tuple(
		description,
		minInclusiveMustBeLessThanMaxInclusive(lower, upper, minSize),
		Named.of("f", factory(lower, upper, minSize))
	)
}

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
