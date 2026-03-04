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
	seedBaseOffset: Int,
	from: LocalTime,
	toExclusive: LocalTime,
	temporalUnit: TemporalUnit,
) : TemporalFromUntilArbArgsGenerator<LocalTime>(
	componentFactoryContainer, seedBaseOffset, from, toExclusive, temporalUnit, LocalTime::plus
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class LocalDateFromUntilArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	seedBaseOffset: Int,
	from: LocalDate,
	toExclusive: LocalDate,
	temporalUnit: TemporalUnit,
) : TemporalFromUntilArbArgsGenerator<LocalDate>(
	componentFactoryContainer, seedBaseOffset, from, toExclusive, temporalUnit, LocalDate::plus
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class LocalDateTimeFromUntilArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	seedBaseOffset: Int,
	from: LocalDateTime,
	toExclusive: LocalDateTime,
	temporalUnit: TemporalUnit = ChronoUnit.DAYS,
) : TemporalFromUntilArbArgsGenerator<LocalDateTime>(
	componentFactoryContainer, seedBaseOffset, from, toExclusive, temporalUnit, LocalDateTime::plus
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class ZonedDateTimeFromUntilArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	seedBaseOffset: Int,
	from: ZonedDateTime,
	toExclusive: ZonedDateTime,
	temporalUnit: TemporalUnit = ChronoUnit.DAYS,
) : TemporalFromUntilArbArgsGenerator<ZonedDateTime>(
	componentFactoryContainer, seedBaseOffset, from, toExclusive, temporalUnit, ZonedDateTime::plus
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class OffsetDateTimeFromUntilArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	seedBaseOffset: Int,
	from: OffsetDateTime,
	toExclusive: OffsetDateTime,
	temporalUnit: TemporalUnit = ChronoUnit.DAYS,
) : TemporalFromUntilArbArgsGenerator<OffsetDateTime>(
	componentFactoryContainer, seedBaseOffset, from, toExclusive, temporalUnit, OffsetDateTime::plus
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
	seedBaseOffset: Int,
	from: LocalTime,
	toInclusive: LocalTime,
	temporalUnit: TemporalUnit
) = if (from == toInclusive) {
	ConstantArbArgsGenerator(componentFactoryContainer, seedBaseOffset, from)
} else {
	InternalLocalTimeFromToArbArgsGenerator(componentFactoryContainer, seedBaseOffset, from, toInclusive, temporalUnit)
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.1.0
 */
private class InternalLocalTimeFromToArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	seedBaseOffset: Int,
	from: LocalTime,
	toInclusive: LocalTime,
	temporalUnit: TemporalUnit
) : TemporalFromToArbArgsGenerator<LocalTime>(
	componentFactoryContainer, seedBaseOffset, from, toInclusive, temporalUnit, LocalTime::plus
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
	seedBaseOffset: Int,
	from: LocalDate,
	toInclusive: LocalDate,
	temporalUnit: TemporalUnit = ChronoUnit.DAYS
) = if (from == toInclusive) {
	ConstantArbArgsGenerator(componentFactoryContainer, seedBaseOffset, from)
} else {
	InternalLocalDateFromToArbArgsGenerator(componentFactoryContainer, seedBaseOffset, from, toInclusive, temporalUnit)
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
private class InternalLocalDateFromToArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	seedBaseOffset: Int,
	from: LocalDate,
	toInclusive: LocalDate,
	temporalUnit: TemporalUnit
) : TemporalFromToArbArgsGenerator<LocalDate>(
	componentFactoryContainer, seedBaseOffset, from, toInclusive, temporalUnit, LocalDate::plus
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
	seedBaseOffset: Int,
	from: LocalDateTime,
	toInclusive: LocalDateTime,
	temporalUnit: TemporalUnit = ChronoUnit.DAYS
) = if (from == toInclusive) {
	ConstantArbArgsGenerator(componentFactoryContainer, seedBaseOffset, from)
} else {
	InternalLocalDateTimeFromToArbArgsGenerator(componentFactoryContainer, seedBaseOffset, from, toInclusive, temporalUnit)
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
private class InternalLocalDateTimeFromToArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	seedBaseOffset: Int,
	from: LocalDateTime,
	toInclusive: LocalDateTime,
	temporalUnit: TemporalUnit
) : TemporalFromToArbArgsGenerator<LocalDateTime>(
	componentFactoryContainer, seedBaseOffset, from, toInclusive, temporalUnit, LocalDateTime::plus
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
	seedBaseOffset: Int,
	from: ZonedDateTime,
	toInclusive: ZonedDateTime,
	temporalUnit: TemporalUnit = ChronoUnit.DAYS
) = if (from == toInclusive) {
	ConstantArbArgsGenerator(componentFactoryContainer, seedBaseOffset, from)
} else {
	InternalZonedDateTimeFromToArbArgsGenerator(componentFactoryContainer, seedBaseOffset, from, toInclusive, temporalUnit)
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
private class InternalZonedDateTimeFromToArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	seedBaseOffset: Int,
	from: ZonedDateTime,
	toInclusive: ZonedDateTime,
	temporalUnit: TemporalUnit
) : TemporalFromToArbArgsGenerator<ZonedDateTime>(
	componentFactoryContainer, seedBaseOffset, from, toInclusive, temporalUnit, ZonedDateTime::plus
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
	seedBaseOffset: Int,
	from: OffsetDateTime,
	toInclusive: OffsetDateTime,
	temporalUnit: TemporalUnit = ChronoUnit.DAYS
) = if (from == toInclusive) {
	ConstantArbArgsGenerator(componentFactoryContainer, seedBaseOffset, from)
} else {
	InternalOffsetDateTimeFromToArbArgsGenerator(componentFactoryContainer, seedBaseOffset, from, toInclusive, temporalUnit)
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
private class InternalOffsetDateTimeFromToArbArgsGenerator(
	componentFactoryContainer: ComponentFactoryContainer,
	seedBaseOffset: Int,
	from: OffsetDateTime,
	toInclusive: OffsetDateTime,
	temporalUnit: TemporalUnit
) : TemporalFromToArbArgsGenerator<OffsetDateTime>(
	componentFactoryContainer, seedBaseOffset, from, toInclusive, temporalUnit, OffsetDateTime::plus
)


/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
abstract class TemporalFromUntilArbArgsGenerator<T>(
	componentFactoryContainer: ComponentFactoryContainer,
	seedBaseOffset: Int,
	from: T,
	toExclusive: T,
	private val temporalUnit: TemporalUnit = ChronoUnit.DAYS,
	private val plusTyped: T.(Long, TemporalUnit) -> T,
) : OpenEndRangeBasedArbArgsGenerator<T>(
	componentFactoryContainer,
	seedBaseOffset,
	from,
	toExclusive,
) where T : Temporal, T : Comparable<T> {
	//TODO 2.1.0 between can overflow (just use a small enough TemporalUnit) -- which results in an
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
	seedBaseOffset: Int,
	from: T,
	toInclusive: T,
	private val temporalUnit: TemporalUnit = ChronoUnit.DAYS,
	private val plusTyped: T.(Long, TemporalUnit) -> T,
) : ClosedRangeBasedArbArgsGenerator<T>(
	componentFactoryContainer,
	seedBaseOffset,
	from,
	toInclusive,
) where T : Temporal, T : Comparable<T> {
	//TODO 2.1.0 between (and addExact) can overflow (just use a small enough TemporalUnit) -- which results in an
	// ArithmeticOverflowException. Use BigInt in such cases?
	private val diffPlusOneInLong = Math.addExact(temporalUnit.between(this.from, this.toInclusive), 1)
	final override fun nextElementInRange(random: Random): T =
		from.plusTyped(random.nextLong(0, diffPlusOneInLong), temporalUnit)
}
