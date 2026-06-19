package com.tegonal.variist.generators

import com.tegonal.variist.config._components
import com.tegonal.variist.generators.impl.*
import java.time.*
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit

/**
 * Returns an [ArbArgsGenerator] which generates [LocalTime]s ranging from [LocalTime.MIN] to inclusive [LocalTime.MAX]
 * where [temporalUnit] defines the steps which defaults to [ChronoUnit.SECONDS].
 *
 * @since 2.3.0
 */
fun ArbExtensionPoint.localTime(temporalUnit: TemporalUnit = ChronoUnit.SECONDS) =
	localTimeFromTo(LocalTime.MIN, LocalTime.MAX, temporalUnit)

/**
 * Returns an [ArbArgsGenerator] which generates [LocalTime]s ranging [from] (inclusive) to [toExclusive]
 * where [temporalUnit] defines the steps which defaults to [ChronoUnit.SECONDS].
 *
 * @since 2.1.0
 */
fun ArbExtensionPoint.localTimeFromUntil(
	from: LocalTime,
	toExclusive: LocalTime,
	temporalUnit: TemporalUnit = ChronoUnit.SECONDS,
): ArbArgsGenerator<LocalTime> =
	LocalTimeFromUntilArbArgsGenerator(_components, from, toExclusive, temporalUnit)

/**
 * Returns an [ArbArgsGenerator] which generates [LocalDate]s ranging [from] (inclusive) to [toExclusive]
 * where [temporalUnit] defines the steps which defaults to [ChronoUnit.DAYS].
 *
 * @throws java.time.temporal.UnsupportedTemporalTypeException In case you choose a [TemporalUnit] which is smaller
 *   than days.
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.localDateFromUntil(
	from: LocalDate,
	toExclusive: LocalDate,
	temporalUnit: TemporalUnit = ChronoUnit.DAYS,
): ArbArgsGenerator<LocalDate> =
	LocalDateFromUntilArbArgsGenerator(_components, from, toExclusive, temporalUnit)

/**
 * Returns an [ArbArgsGenerator] which generates [LocalDateTime]s ranging [from] (inclusive) to [toExclusive]
 * where [temporalUnit] defines the steps.
 *
 * @throws ArithmeticException in case the difference between [from] and [toExclusive] is too big.
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.localDateTimeFromUntil(
	from: LocalDateTime,
	toExclusive: LocalDateTime,
	temporalUnit: TemporalUnit,
): ArbArgsGenerator<LocalDateTime> =
	LocalDateTimeFromUntilArbArgsGenerator(_components, from, toExclusive, temporalUnit)

/**
 * Returns an [ArbArgsGenerator] which generates [ZonedDateTime]s ranging [from] (inclusive) to [toExclusive]
 * where [temporalUnit] defines the steps.
 *
 * @throws ArithmeticException in case the difference between [from] and [toExclusive] is too big.
 *
 * @since 2.0.0
 */
//TODO 3.0.0 also define a parameter to steer timezone
fun ArbExtensionPoint.zonedDateTimeFromUntil(
	from: ZonedDateTime,
	toExclusive: ZonedDateTime,
	temporalUnit: TemporalUnit,
): ArbArgsGenerator<ZonedDateTime> =
	ZonedDateTimeFromUntilArbArgsGenerator(_components, from, toExclusive, temporalUnit)

/**
 * Returns an [ArbArgsGenerator] which generates [OffsetDateTime]s ranging [from] (inclusive) to [toExclusive]
 * where [temporalUnit] defines the steps.
 *
 * @throws ArithmeticException in case the difference between [from] and [toExclusive] is too big.
 *
 * @since 2.0.0
 */
//TODO 3.0.0 also define a parameter to steer the offset
fun ArbExtensionPoint.offsetDateTimeFromUntil(
	from: OffsetDateTime,
	toExclusive: OffsetDateTime,
	temporalUnit: TemporalUnit,
): ArbArgsGenerator<OffsetDateTime> =
	OffsetDateTimeFromUntilArbArgsGenerator(_components, from, toExclusive, temporalUnit)

/**
 * Returns an [ArbArgsGenerator] which generates [LocalTime]s ranging [from] (inclusive) to [toInclusive]
 * where [temporalUnit] defines the steps which defaults to [ChronoUnit.SECONDS].
 *
 * @since 2.1.0
 */
fun ArbExtensionPoint.localTimeFromTo(
	from: LocalTime,
	toInclusive: LocalTime,
	temporalUnit: TemporalUnit = ChronoUnit.SECONDS,
): ArbArgsGenerator<LocalTime> =
	LocalTimeFromToArbArgsGenerator(_components, from, toInclusive, temporalUnit)

/**
 * Returns an [ArbArgsGenerator] which generates [LocalDate]s ranging [from] (inclusive) to [toInclusive]
 * where [temporalUnit] defines the steps which defaults to [ChronoUnit.DAYS].
 *
 * @throws java.time.temporal.UnsupportedTemporalTypeException In case you choose a [TemporalUnit] which is smaller
 *   than days.
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.localDateFromTo(
	from: LocalDate,
	toInclusive: LocalDate,
	temporalUnit: TemporalUnit = ChronoUnit.DAYS,
): ArbArgsGenerator<LocalDate> =
	LocalDateFromToArbArgsGenerator(_components, from, toInclusive, temporalUnit)

/**
 * Returns an [ArbArgsGenerator] which generates [LocalDateTime]s ranging [from] (inclusive) to [toInclusive]
 * where [temporalUnit] defines the steps.
 *
 * @throws ArithmeticException in case the difference between [from] and [toInclusive] is too big.
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.localDateTimeFromTo(
	from: LocalDateTime,
	toInclusive: LocalDateTime,
	temporalUnit: TemporalUnit
): ArbArgsGenerator<LocalDateTime> =
	LocalDateTimeFromToArbArgsGenerator(_components, from, toInclusive, temporalUnit)

/**
 * Returns an [ArbArgsGenerator] which generates [ZonedDateTime]s ranging [from] (inclusive) to [toInclusive]
 * where [temporalUnit] defines the steps.
 *
 * @throws ArithmeticException in case the difference between [from] and [toInclusive] is too big.
 *
 * @since 2.0.0
 */
//TODO 3.0.0 also define a parameter to steer timezone
fun ArbExtensionPoint.zonedDateTimeFromTo(
	from: ZonedDateTime,
	toInclusive: ZonedDateTime,
	temporalUnit: TemporalUnit
): ArbArgsGenerator<ZonedDateTime> =
	ZonedDateTimeFromToArbArgsGenerator(_components, from, toInclusive, temporalUnit)

/**
 * Returns an [ArbArgsGenerator] which generates [OffsetDateTime]s ranging [from] (inclusive) to [toInclusive]
 * where [temporalUnit] defines the steps.
 *
 * @throws ArithmeticException in case the difference between [from] and [toInclusive] is too big.
 *
 * @since 2.0.0
 */
//TODO 3.0.0 also define a parameter to steer the offset
fun ArbExtensionPoint.offsetDateTimeFromTo(
	from: OffsetDateTime,
	toInclusive: OffsetDateTime,
	temporalUnit: TemporalUnit
): ArbArgsGenerator<OffsetDateTime> =
	OffsetDateTimeFromToArbArgsGenerator(_components, from, toInclusive, temporalUnit)

//TODO 3.1.0 add
