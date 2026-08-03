package com.tegonal.variist.generators.impl

import com.tegonal.variist.config.ComponentFactoryContainer
import com.tegonal.variist.config.ordered
import com.tegonal.variist.generators.OrderedArgsGenerator
import com.tegonal.variist.generators.impl.BaseSemiOrderedLikeArgsGenerator.Companion.sizeLongToInt
import com.tegonal.variist.generators.intFromUntil
import com.tegonal.variist.utils.impl.requireFromLessThanOrEqualToExclusive
import com.tegonal.variist.utils.impl.requireFromLessThanToExclusive
import java.time.*
import java.time.temporal.ChronoUnit
import java.time.temporal.Temporal
import java.time.temporal.TemporalUnit

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 3.0.0
 */
class LocalTimeFromUntilOrderedArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: LocalTime,
	toExclusive: LocalTime,
	temporalUnit: TemporalUnit,
) : TemporalFromUntilOrderedArgsGenerator<LocalTime>(
	componentFactoryContainer, from, toExclusive, temporalUnit, LocalTime::plus
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 3.0.0
 */
class LocalDateFromUntilOrderedArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: LocalDate,
	toExclusive: LocalDate,
	temporalUnit: TemporalUnit,
) : TemporalFromUntilOrderedArgsGenerator<LocalDate>(
	componentFactoryContainer, from, toExclusive, temporalUnit, LocalDate::plus
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 3.0.0
 */
class LocalDateTimeFromUntilOrderedArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: LocalDateTime,
	toExclusive: LocalDateTime,
	temporalUnit: TemporalUnit,
) : TemporalFromUntilOrderedArgsGenerator<LocalDateTime>(
	componentFactoryContainer, from, toExclusive, temporalUnit, LocalDateTime::plus
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 3.0.0
 */
class ZonedDateTimeFromUntilOrderedArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: ZonedDateTime,
	toExclusive: ZonedDateTime,
	temporalUnit: TemporalUnit,
) : TemporalFromUntilOrderedArgsGenerator<ZonedDateTime>(
	componentFactoryContainer, from, toExclusive, temporalUnit, ZonedDateTime::plus
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 3.0.0
 */
class OffsetDateTimeFromUntilOrderedArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: OffsetDateTime,
	toExclusive: OffsetDateTime,
	temporalUnit: TemporalUnit,
) : TemporalFromUntilOrderedArgsGenerator<OffsetDateTime>(
	componentFactoryContainer, from, toExclusive, temporalUnit, OffsetDateTime::plus
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 3.0.0
 */
@Suppress("FunctionName")
fun LocalTimeFromToOrderedArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: LocalTime,
	toInclusive: LocalTime,
	temporalUnit: TemporalUnit
) = constantIfFromIsToInclusiveOtherwise(
	componentFactoryContainer, from, toInclusive, temporalUnit,
	::InternalLocalTimeFromToOrderedArgsGenerator
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 3.0.0
 */
private class InternalLocalTimeFromToOrderedArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: LocalTime,
	toInclusive: LocalTime,
	temporalUnit: TemporalUnit
) : TemporalFromToOrderedArgsGenerator<LocalTime>(
	componentFactoryContainer, from, toInclusive, temporalUnit, LocalTime::plus
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 3.0.0
 */
@Suppress("FunctionName")
fun LocalDateFromToOrderedArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: LocalDate,
	toInclusive: LocalDate,
	temporalUnit: TemporalUnit = ChronoUnit.DAYS
) = constantIfFromIsToInclusiveOtherwise(
	componentFactoryContainer, from, toInclusive, temporalUnit,
	::InternalLocalDateFromToOrderedArgsGenerator
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 3.0.0
 */
private class InternalLocalDateFromToOrderedArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: LocalDate,
	toInclusive: LocalDate,
	temporalUnit: TemporalUnit
) : TemporalFromToOrderedArgsGenerator<LocalDate>(
	componentFactoryContainer, from, toInclusive, temporalUnit, LocalDate::plus
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 3.0.0
 */
@Suppress("FunctionName")
fun LocalDateTimeFromToOrderedArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: LocalDateTime,
	toInclusive: LocalDateTime,
	temporalUnit: TemporalUnit = ChronoUnit.DAYS
) = constantIfFromIsToInclusiveOtherwise(
	componentFactoryContainer, from, toInclusive, temporalUnit,
	::InternalLocalDateTimeFromToOrderedArgsGenerator
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 3.0.0
 */
private class InternalLocalDateTimeFromToOrderedArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: LocalDateTime,
	toInclusive: LocalDateTime,
	temporalUnit: TemporalUnit
) : TemporalFromToOrderedArgsGenerator<LocalDateTime>(
	componentFactoryContainer, from, toInclusive, temporalUnit, LocalDateTime::plus
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 3.0.0
 */
@Suppress("FunctionName")
fun ZonedDateTimeFromToOrderedArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: ZonedDateTime,
	toInclusive: ZonedDateTime,
	temporalUnit: TemporalUnit = ChronoUnit.DAYS
) = constantIfPredicateHoldsOtherwiseFactory(
	componentFactoryContainer, from, toInclusive, temporalUnit,
	::InternalZonedDateTimeFromToOrderedArgsGenerator
) { from.isEqual(toInclusive) }

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 3.0.0
 */
private class InternalZonedDateTimeFromToOrderedArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: ZonedDateTime,
	toInclusive: ZonedDateTime,
	temporalUnit: TemporalUnit
) : TemporalFromToOrderedArgsGenerator<ZonedDateTime>(
	componentFactoryContainer, from, toInclusive, temporalUnit, ZonedDateTime::plus
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 3.0.0
 */
@Suppress("FunctionName")
fun OffsetDateTimeFromToOrderedArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: OffsetDateTime,
	toInclusive: OffsetDateTime,
	temporalUnit: TemporalUnit = ChronoUnit.DAYS
) = constantIfPredicateHoldsOtherwiseFactory(
	componentFactoryContainer, from, toInclusive, temporalUnit,
	::InternalOffsetDateTimeFromToOrderedArgsGenerator
) { from.isEqual(toInclusive) }

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 3.0.0
 */
private class InternalOffsetDateTimeFromToOrderedArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: OffsetDateTime,
	toInclusive: OffsetDateTime,
	temporalUnit: TemporalUnit
) : TemporalFromToOrderedArgsGenerator<OffsetDateTime>(
	componentFactoryContainer, from, toInclusive, temporalUnit, OffsetDateTime::plus
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 3.0.0
 */
abstract class TemporalFromUntilOrderedArgsGenerator<T>(
	componentFactoryContainer: ComponentFactoryContainer,
	from: T,
	toExclusive: T,
	protected val temporalUnit: TemporalUnit = ChronoUnit.DAYS,
	private val plusTyped: T.(Long, TemporalUnit) -> T,
) : OrderedArgsGeneratorMapper<Int, T>(
	run {
		val size = temporalUnit.between(from, toExclusive)
		componentFactoryContainer.ordered.intFromUntil(0, sizeLongToInt(size))
	},
	{ step -> from.plusTyped(step.toLong(), temporalUnit) }
) where T : Temporal, T : Comparable<T> {
	init {
		requireFromLessThanToExclusive(from, toExclusive)
	}
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 3.0.0
 */
abstract class TemporalFromToOrderedArgsGenerator<T>(
	componentFactoryContainer: ComponentFactoryContainer,
	from: T,
	toInclusive: T,
	protected val temporalUnit: TemporalUnit = ChronoUnit.DAYS,
	private val plusTyped: T.(Long, TemporalUnit) -> T,
) : OrderedArgsGeneratorMapper<Int, T>(
	run {
		val size = Math.addExact(temporalUnit.between(from, toInclusive), 1)
		componentFactoryContainer.ordered.intFromUntil(0, sizeLongToInt(size))
	},
	{ step -> from.plusTyped(step.toLong(), temporalUnit) }
) where T : Temporal, T : Comparable<T> {
	init {
		requireFromLessThanOrEqualToExclusive(from, toInclusive)
	}
}

private inline fun <T> constantIfFromIsToInclusiveOtherwise(
	componentFactoryContainer: ComponentFactoryContainer,
	from: T,
	toInclusive: T,
	temporalUnit: TemporalUnit,
	factory: (ComponentFactoryContainer, from: T, toInclusive: T, TemporalUnit) -> OrderedArgsGenerator<T>
): OrderedArgsGenerator<T> =
	constantIfPredicateHoldsOtherwiseFactory(componentFactoryContainer, from, toInclusive, temporalUnit, factory) {
		from == toInclusive
	}

private inline fun <T> constantIfPredicateHoldsOtherwiseFactory(
	componentFactoryContainer: ComponentFactoryContainer,
	from: T,
	toInclusive: T,
	temporalUnit: TemporalUnit,
	factory: (ComponentFactoryContainer, from: T, toInclusive: T, TemporalUnit) -> OrderedArgsGenerator<T>,
	predicate: () -> Boolean
): OrderedArgsGenerator<T> =
	if (predicate()) {
		ConstantOrderedArgsGenerator(componentFactoryContainer, from)
	} else {
		factory(componentFactoryContainer, from, toInclusive, temporalUnit)
	}
