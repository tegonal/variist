package com.tegonal.variist.generators.impl

import com.tegonal.variist.config.ComponentFactoryContainer
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
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
	temporalUnit: TemporalUnit = ChronoUnit.DAYS,
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
	temporalUnit: TemporalUnit = ChronoUnit.DAYS,
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
	temporalUnit: TemporalUnit = ChronoUnit.DAYS,
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
) = if (from == toInclusive) {
	ConstantArbArgsGenerator(componentFactoryContainer, from)
} else {
	InternalLocalTimeFromToArbArgsGenerator(componentFactoryContainer, from, toInclusive, temporalUnit)
}

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
	temporalUnit: TemporalUnit = ChronoUnit.DAYS
) = if (from == toInclusive) {
	ConstantArbArgsGenerator(componentFactoryContainer, from)
} else {
	InternalLocalDateFromToArbArgsGenerator(componentFactoryContainer, from, toInclusive, temporalUnit)
}

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
	temporalUnit: TemporalUnit = ChronoUnit.DAYS
) = if (from == toInclusive) {
	ConstantArbArgsGenerator(componentFactoryContainer, from)
} else {
	InternalLocalDateTimeFromToArbArgsGenerator(componentFactoryContainer, from, toInclusive, temporalUnit)
}

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
	temporalUnit: TemporalUnit = ChronoUnit.DAYS
) = if (from == toInclusive) {
	ConstantArbArgsGenerator(componentFactoryContainer, from)
} else {
	InternalZonedDateTimeFromToArbArgsGenerator(componentFactoryContainer, from, toInclusive, temporalUnit)
}

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
	temporalUnit: TemporalUnit = ChronoUnit.DAYS
) = if (from == toInclusive) {
	ConstantArbArgsGenerator(componentFactoryContainer, from)
} else {
	InternalOffsetDateTimeFromToArbArgsGenerator(componentFactoryContainer, from, toInclusive, temporalUnit)
}

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
	private val temporalUnit: TemporalUnit = ChronoUnit.DAYS,
	private val plusTyped: T.(Long, TemporalUnit) -> T,
) : OpenEndRangeBasedArbArgsGenerator<T>(
	componentFactoryContainer,
	from,
	toExclusive,
) where T : Temporal, T : Comparable<T> {
	//TODO 2.2.0 between can overflow (just use a small enough TemporalUnit) -- which results in an
	// ArithmeticOverflowException. Use BigInt in such cases?
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
	private val temporalUnit: TemporalUnit = ChronoUnit.DAYS,
	private val plusTyped: T.(Long, TemporalUnit) -> T,
) : ClosedRangeBasedArbArgsGenerator<T>(
	componentFactoryContainer,
	from,
	toInclusive,
) where T : Temporal, T : Comparable<T> {
	//TODO 2.2.0 between (and addExact) can overflow (just use a small enough TemporalUnit) -- which results in an
	// ArithmeticOverflowException. Use BigInt in such cases?
	private val diffPlusOneInLong = Math.addExact(temporalUnit.between(this.from, this.toInclusive), 1)
	final override fun nextElementInRange(random: Random): T =
		from.plusTyped(random.nextLong(0, diffPlusOneInLong), temporalUnit)
}
