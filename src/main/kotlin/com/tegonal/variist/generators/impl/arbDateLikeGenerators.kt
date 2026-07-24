package com.tegonal.variist.generators.impl

import com.tegonal.variist.config.ComponentFactoryContainer
import com.tegonal.variist.generators.ArbArgsGenerator
import java.time.*
import java.time.temporal.Temporal
import java.time.temporal.TemporalUnit
import kotlin.random.Random

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.1.0
 */
class LocalTimeFromUntilArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: LocalTime,
	toExclusive: LocalTime,
	temporalUnit: TemporalUnit,
) : TemporalFromUntilArbArgsGenerator<LocalTime>(
	componentFactoryContainer, from, toExclusive, temporalUnit, LocalTime::plus
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class LocalDateFromUntilArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: LocalDate,
	toExclusive: LocalDate,
	temporalUnit: TemporalUnit,
) : TemporalFromUntilArbArgsGenerator<LocalDate>(
	componentFactoryContainer, from, toExclusive, temporalUnit, LocalDate::plus
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class LocalDateTimeFromUntilArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: LocalDateTime,
	toExclusive: LocalDateTime,
	temporalUnit: TemporalUnit,
) : TemporalFromUntilArbArgsGenerator<LocalDateTime>(
	componentFactoryContainer, from, toExclusive, temporalUnit, LocalDateTime::plus
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class ZonedDateTimeFromUntilArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: ZonedDateTime,
	toExclusive: ZonedDateTime,
	temporalUnit: TemporalUnit,
) : TemporalFromUntilArbArgsGenerator<ZonedDateTime>(
	componentFactoryContainer, from, toExclusive, temporalUnit, ZonedDateTime::plus
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class OffsetDateTimeFromUntilArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: OffsetDateTime,
	toExclusive: OffsetDateTime,
	temporalUnit: TemporalUnit,
) : TemporalFromUntilArbArgsGenerator<OffsetDateTime>(
	componentFactoryContainer, from, toExclusive, temporalUnit, OffsetDateTime::plus
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.1.0
 */
@Suppress("FunctionName")
fun LocalTimeFromToArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: LocalTime,
	toInclusive: LocalTime,
	temporalUnit: TemporalUnit
) = constantIfFromIsToInclusiveOtherwise(
	componentFactoryContainer, from, toInclusive, temporalUnit,
	::InternalLocalTimeFromToArbArgsGenerator
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.1.0
 */
private class InternalLocalTimeFromToArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: LocalTime,
	toInclusive: LocalTime,
	temporalUnit: TemporalUnit
) : TemporalFromToArbArgsGenerator<LocalTime>(
	componentFactoryContainer, from, toInclusive, temporalUnit, LocalTime::plus
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
@Suppress("FunctionName")
fun LocalDateFromToArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: LocalDate,
	toInclusive: LocalDate,
	temporalUnit: TemporalUnit
) = constantIfFromIsToInclusiveOtherwise(
	componentFactoryContainer, from, toInclusive, temporalUnit,
	::InternalLocalDateFromToArbArgsGenerator
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
private class InternalLocalDateFromToArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: LocalDate,
	toInclusive: LocalDate,
	temporalUnit: TemporalUnit
) : TemporalFromToArbArgsGenerator<LocalDate>(
	componentFactoryContainer, from, toInclusive, temporalUnit, LocalDate::plus
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
@Suppress("FunctionName")
fun LocalDateTimeFromToArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: LocalDateTime,
	toInclusive: LocalDateTime,
	temporalUnit: TemporalUnit
) = constantIfFromIsToInclusiveOtherwise(
	componentFactoryContainer, from, toInclusive, temporalUnit,
	::InternalLocalDateTimeFromToArbArgsGenerator
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
private class InternalLocalDateTimeFromToArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: LocalDateTime,
	toInclusive: LocalDateTime,
	temporalUnit: TemporalUnit
) : TemporalFromToArbArgsGenerator<LocalDateTime>(
	componentFactoryContainer, from, toInclusive, temporalUnit, LocalDateTime::plus
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
@Suppress("FunctionName")
fun ZonedDateTimeFromToArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: ZonedDateTime,
	toInclusive: ZonedDateTime,
	temporalUnit: TemporalUnit
) = constantIfPredicateHoldsOtherwiseFactory(
	componentFactoryContainer, from, toInclusive, temporalUnit,
	::InternalZonedDateTimeFromToArbArgsGenerator
) { from.isEqual(toInclusive) }

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
private class InternalZonedDateTimeFromToArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: ZonedDateTime,
	toInclusive: ZonedDateTime,
	temporalUnit: TemporalUnit
) : TemporalFromToArbArgsGenerator<ZonedDateTime>(
	componentFactoryContainer, from, toInclusive, temporalUnit, ZonedDateTime::plus
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
@Suppress("FunctionName")
fun OffsetDateTimeFromToArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: OffsetDateTime,
	toInclusive: OffsetDateTime,
	temporalUnit: TemporalUnit
) = constantIfPredicateHoldsOtherwiseFactory(
	componentFactoryContainer, from, toInclusive, temporalUnit,
	::InternalOffsetDateTimeFromToArbArgsGenerator
) { from.isEqual(toInclusive) }

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
private class InternalOffsetDateTimeFromToArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	from: OffsetDateTime,
	toInclusive: OffsetDateTime,
	temporalUnit: TemporalUnit
) : TemporalFromToArbArgsGenerator<OffsetDateTime>(
	componentFactoryContainer, from, toInclusive, temporalUnit, OffsetDateTime::plus
)


/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
abstract class TemporalFromUntilArbArgsGenerator<T>(
	componentFactoryContainer: ComponentFactoryContainer,
	from: T,
	toExclusive: T,
	private val temporalUnit: TemporalUnit,
	private val plusTyped: T.(Long, TemporalUnit) -> T,
) : OpenEndRangeBasedArbArgsGenerator<T>(
	componentFactoryContainer,
	from,
	toExclusive,
) where T : Temporal, T : Comparable<T> {
	private val diffInLong = temporalUnit.between(this.from, this.toExclusive)
	final override fun nextElementInRange(random: Random): T =
		from.plusTyped(random.nextLong(0, diffInLong), temporalUnit)
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
abstract class TemporalFromToArbArgsGenerator<T>(
	componentFactoryContainer: ComponentFactoryContainer,
	from: T,
	toInclusive: T,
	private val temporalUnit: TemporalUnit,
	private val plusTyped: T.(Long, TemporalUnit) -> T,
) : ClosedRangeBasedArbArgsGenerator<T>(
	componentFactoryContainer,
	from,
	toInclusive,
) where T : Temporal, T : Comparable<T> {
	private val diffPlusOneInLong = Math.addExact(temporalUnit.between(this.from, this.toInclusive), 1)
	final override fun nextElementInRange(random: Random): T =
		from.plusTyped(random.nextLong(0, diffPlusOneInLong), temporalUnit)
}

private inline fun <T> constantIfFromIsToInclusiveOtherwise(
	componentFactoryContainer: ComponentFactoryContainer,
	from: T,
	toInclusive: T,
	temporalUnit: TemporalUnit,
	factory: (ComponentFactoryContainer, from: T, toInclusive: T, TemporalUnit) -> ArbArgsGenerator<T>
): ArbArgsGenerator<T> =
	constantIfPredicateHoldsOtherwiseFactory(
		componentFactoryContainer, from, toInclusive, temporalUnit, factory
	) { from == toInclusive }

private inline fun <T> constantIfPredicateHoldsOtherwiseFactory(
	componentFactoryContainer: ComponentFactoryContainer,
	from: T,
	toInclusive: T,
	temporalUnit: TemporalUnit,
	factory: (ComponentFactoryContainer, from: T, toInclusive: T, TemporalUnit) -> ArbArgsGenerator<T>,
	predicate: () -> Boolean
): ArbArgsGenerator<T> =
	if (predicate()) {
		ConstantArbArgsGenerator(componentFactoryContainer, from)
	} else {
		factory(componentFactoryContainer, from, toInclusive, temporalUnit)
	}
