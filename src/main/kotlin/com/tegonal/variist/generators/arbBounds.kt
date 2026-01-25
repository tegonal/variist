package com.tegonal.variist.generators

import ch.tutteli.kbox.Tuple2
import com.tegonal.variist.generators.impl.createBoundsArbGenerator
import com.tegonal.variist.generators.impl.createIntDomainBasedBoundsArbGenerator
import com.tegonal.variist.generators.impl.possibleMaxSizeSafeInIntDomain

/**
 * Returns an [ArbArgsGenerator] which generates [Tuple2] representing a lower and upper bound where the bounds
 * range from [minInclusive] to [maxInclusive] respecting the given [minSize] as well as
 * [maxSize] if defined.
 *
 * @param minSize must be greater than or equal to 1
 * @param maxSize must be greater than or equal to [minSize] and less than the possible max size given
 *   by [minInclusive], [maxInclusive]
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.charBounds(
	minInclusive: Char = Char.MIN_VALUE,
	maxInclusive: Char = Char.MAX_VALUE,
	minSize: Int = 1,
	maxSize: Int? = null,
): ArbArgsGenerator<Tuple2<Char, Char>> =
	charBoundsBasedInternal(minInclusive, maxInclusive, minSize, maxSize, ::Tuple2)

/**
 * Returns an [ArbArgsGenerator] which generates a [T] based on generated lower and upper bounds and the given [factory]
 * where the bounds range from [minInclusive] to [maxInclusive] respecting the given [minSize] as well as
 * [maxSize] if defined.
 *
 * If [minSize] = 0, then it generates cases where `lower > upper`.
 * Set [minSize] to 1 or greater if you do not want to include them.
 *
 * @param minSize must be greater than or equal to 0
 * @param maxSize must be greater than or equal to [minSize] and less than the possible max size given
 *   by [minInclusive], [maxInclusive]
 *
 * @since 2.0.0
 */
fun <T> ArbExtensionPoint.charBoundsBased(
	minInclusive: Char = Char.MIN_VALUE,
	maxInclusive: Char = Char.MAX_VALUE,
	minSize: Int,
	maxSize: Int? = null,
	factory: (lowerBound: Char, upperBound: Char) -> T
): ArbArgsGenerator<T> = boundsGeneratorTakingMinSize0IntoAccount(
	minInclusive, maxInclusive, minSize, maxSize, ::charBoundsBasedInternal, factory, zero = 0, one = 1
)

private fun <T> ArbExtensionPoint.charBoundsBasedInternal(
	minInclusive: Char = Char.MIN_VALUE,
	maxInclusive: Char = Char.MAX_VALUE,
	minSize: Int,
	maxSize: Int?,
	factory: (lowerBound: Char, upperBound: Char) -> T
) = if (minInclusive != Char.MIN_VALUE || maxInclusive != Char.MAX_VALUE) {
	// less than 65'535 elements, we can use the Int domain based implementation
	createIntDomainBasedBoundsArbGenerator(
		minInclusive = minInclusive.code,
		maxInclusive = maxInclusive.code,
		minSize = minSize,
		maxSize = maxSize
	) { lowerBound, upperBound ->
		factory(lowerBound.toChar(), upperBound.toChar())
	}
} else {
	createBoundsArbGenerator(
		minInclusive = minInclusive.code.toLong(),
		maxInclusive = maxInclusive.code.toLong(),
		minSize = minSize.toLong(),
		maxSize = maxSize?.toLong()
	) { lowerBound, upperBound ->
		factory(lowerBound.toInt().toChar(), upperBound.toInt().toChar())
	}
}

/**
 * Returns an [ArbArgsGenerator] which generates [Tuple2] representing a lower and upper bound where the bounds
 * range from [minInclusive] to [maxInclusive] respecting the given [minSize] as well as
 * [maxSize] if defined.
 *
 * @param minSize must be greater than or equal to 1
 * @param maxSize must be greater than or equal to [minSize] and less than the possible max size given
 *   by [minInclusive], [maxInclusive]
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.intBounds(
	minInclusive: Int = Int.MIN_VALUE,
	maxInclusive: Int = Int.MAX_VALUE,
	minSize: Int = 1,
	maxSize: Int? = null,
): ArbArgsGenerator<Tuple2<Int, Int>> = intBoundsBasedInternal(minInclusive, maxInclusive, minSize, maxSize, ::Tuple2)

/**
 * Returns an [ArbArgsGenerator] which generates a [T] based on generated lower and upper bounds and the given [factory]
 * where the bounds range from [minInclusive] to [maxInclusive] respecting the given [minSize] as well as
 * [maxSize] if defined.
 *
 * If [minSize] = 0, then it generates cases where `lower > upper`.
 * Set [minSize] to 1 or greater if you do not want to include them.
 *
 * @param minSize must be greater than or equal to 0
 * @param maxSize must be greater than or equal to [minSize] and less than the possible max size given
 *   by [minInclusive], [maxInclusive]
 *
 * @since 2.0.0
 */
fun <T> ArbExtensionPoint.intBoundsBased(
	minInclusive: Int = Int.MIN_VALUE,
	maxInclusive: Int = Int.MAX_VALUE,
	minSize: Int,
	maxSize: Int? = null,
	factory: (lowerBound: Int, upperBound: Int) -> T
): ArbArgsGenerator<T> = boundsGeneratorTakingMinSize0IntoAccount(
	minInclusive, maxInclusive, minSize, maxSize, ::intBoundsBasedInternal, factory, zero = 0, one = 1
)

private fun <T> ArbExtensionPoint.intBoundsBasedInternal(
	minInclusive: Int = Int.MIN_VALUE,
	maxInclusive: Int = Int.MAX_VALUE,
	minSize: Int,
	maxSize: Int?,
	factory: (lowerBound: Int, upperBound: Int) -> T
): ArbArgsGenerator<T> {
	val possibleMaxSize = maxInclusive.toLong() - minInclusive + 1
	// it is beneficial if we can stay in the int domain (memory wise), hence we have this check here.
	// The check as such makes it a tiny bit slower if it does not stay in the Int domain but we guess in
	// most cases the requirements don't require large ranges and we jump into the if branch (i.e. no need to convert
	// to long)
	return if (possibleMaxSize <= possibleMaxSizeSafeInIntDomain) {
		createIntDomainBasedBoundsArbGenerator(
			minInclusive = minInclusive,
			maxInclusive = maxInclusive,
			minSize = minSize,
			maxSize = maxSize,
			factory = factory
		)
	} else {
		createBoundsArbGenerator(
			minInclusive = minInclusive.toLong(),
			maxInclusive = maxInclusive.toLong(),
			minSize = minSize.toLong(),
			maxSize = maxSize?.toLong()
		) { lowerBound, upperBound ->
			factory(lowerBound.toInt(), upperBound.toInt())
		}
	}
}

/**
 * Returns an [ArbArgsGenerator] which generates [Tuple2] representing a lower and upper bound where the bounds
 * range from [minInclusive] to [maxInclusive] respecting the given [minSize] as well as
 * [maxSize] if defined.
 *
 * @param minSize must be greater than or equal to 1
 * @param maxSize must be greater than or equal to [minSize] and less than the possible max size given
 *   by [minInclusive], [maxInclusive]
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.longBounds(
	minInclusive: Long = Long.MIN_VALUE,
	maxInclusive: Long = Long.MAX_VALUE,
	minSize: Long = 1,
	maxSize: Long? = null,
): ArbArgsGenerator<Tuple2<Long, Long>> =
	longBoundsBasedInternal(minInclusive, maxInclusive, minSize, maxSize, ::Tuple2)

/**
 * Returns an [ArbArgsGenerator] which generates a [T] based on generated lower and upper bounds and the given [factory]
 * where the bounds range from [minInclusive] to [maxInclusive] respecting the given [minSize] as well as
 * [maxSize] if defined.
 *
 * If [minSize] = 0, then it generates cases where `lower > upper`.
 * Set [minSize] to 1 or greater if you do not want to include them.
 *
 * @param minSize must be greater than or equal to 1
 * @param maxSize must be greater than or equal to [minSize] and less than the possible max size given
 *   by [minInclusive], [maxInclusive]
 *
 * @since 2.0.0
 */
fun <T> ArbExtensionPoint.longBoundsBased(
	minInclusive: Long = Long.MIN_VALUE,
	maxInclusive: Long = Long.MAX_VALUE,
	minSize: Long,
	maxSize: Long? = null,
	factory: (lowerBound: Long, upperBound: Long) -> T
): ArbArgsGenerator<T> = boundsGeneratorTakingMinSize0IntoAccount(
	minInclusive, maxInclusive, minSize, maxSize, ::longBoundsBasedInternal, factory, zero = 0, one = 1
)

private fun <T> ArbExtensionPoint.longBoundsBasedInternal(
	minInclusive: Long = Long.MIN_VALUE,
	maxInclusive: Long = Long.MAX_VALUE,
	minSize: Long,
	maxSize: Long?,
	factory: (lowerBound: Long, upperBound: Long) -> T
): ArbArgsGenerator<T> = createBoundsArbGenerator(
	minInclusive = minInclusive,
	maxInclusive = maxInclusive,
	minSize = minSize,
	maxSize = maxSize,
	factory = factory
)


private fun <T, E, NumberT> ArbExtensionPoint.boundsGeneratorTakingMinSize0IntoAccount(
	minInclusive: T,
	maxInclusive: T,
	minSize: NumberT,
	maxSize: NumberT?,
	boundGenerator: (T, T, NumberT, NumberT?, (T, T) -> E) -> ArbArgsGenerator<E>,
	factory: (T, T) -> E,
	zero: NumberT,
	one: NumberT
): ArbArgsGenerator<E> where NumberT : Number, NumberT : Comparable<NumberT> {
	val arbRange = boundGenerator(minInclusive, maxInclusive, if (minSize == zero) one else minSize, maxSize, factory)
	return includeEmptyRangeIfMinSizeIs0(minInclusive, maxInclusive, minSize, arbRange, factory, zero)
}

private fun <T, E, NumberT> ArbExtensionPoint.includeEmptyRangeIfMinSizeIs0(
	minInclusive: T,
	maxInclusive: T,
	minSize: NumberT,
	arbRange: ArbArgsGenerator<E>,
	factory: (T, T) -> E,
	zero: NumberT
): ArbArgsGenerator<E> = if (minSize == zero) {
	mergeWeighted(
		//TODO 2.1.0 make this configurable once we introduce the concept of edge cases, for now we generate an
		// empty range in 5% of the cases --
		//
		// I don't think that we gain something if we vary the bounds of an empty range
		5 to arb.of(factory(maxInclusive, minInclusive)),
		95 to arbRange
	)
} else arbRange
