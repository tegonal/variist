package com.tegonal.variist.generators

import com.tegonal.variist.config._components
import com.tegonal.variist.generators.impl.*
import java.time.*
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit

/**
 * Returns an [OrderedArgsGenerator] which generates [LocalTime]s ranging [from] (inclusive) to [toExclusive]
 * where [temporalUnit] defines the steps.
 *
 * @since 3.0.0
 */
fun OrderedExtensionPoint.localTimeFromUntil(
	from: LocalTime,
	toExclusive: LocalTime,
	temporalUnit: TemporalUnit,
): OrderedArgsGenerator<LocalTime> =
	LocalTimeFromUntilOrderedArgsGenerator(_components, from, toExclusive, temporalUnit)

/**
 * Returns an [OrderedArgsGenerator] which generates [LocalDate]s ranging [from] (inclusive) to [toExclusive]
 * where [temporalUnit] defines the steps which defaults to [ChronoUnit.DAYS].
 *
 * @throws java.time.temporal.UnsupportedTemporalTypeException In case you choose a [TemporalUnit] which is smaller
 *   than days.
 *
 * @since 3.0.0
 */
fun OrderedExtensionPoint.localDateFromUntil(
	from: LocalDate,
	toExclusive: LocalDate,
	temporalUnit: TemporalUnit = ChronoUnit.DAYS,
): OrderedArgsGenerator<LocalDate> =
	LocalDateFromUntilOrderedArgsGenerator(_components, from, toExclusive, temporalUnit)

/**
 * Returns an [OrderedArgsGenerator] which generates [LocalDateTime]s ranging [from] (inclusive) to [toExclusive]
 * where [temporalUnit] defines the steps.
 *
 * @throws ArithmeticException in case the difference between [from] and [toExclusive] is too big.
 *
 * @since 3.0.0
 */
fun OrderedExtensionPoint.localDateTimeFromUntil(
	from: LocalDateTime,
	toExclusive: LocalDateTime,
	temporalUnit: TemporalUnit,
): OrderedArgsGenerator<LocalDateTime> =
	LocalDateTimeFromUntilOrderedArgsGenerator(_components, from, toExclusive, temporalUnit)

/**
 * Returns an [OrderedArgsGenerator] which generates [ZonedDateTime]s ranging [from] (inclusive) to [toExclusive]
 * where [temporalUnit] defines the steps.
 *
 * @throws ArithmeticException in case the difference between [from] and [toExclusive] is too big.
 *
 * @since 3.0.0
 */
fun OrderedExtensionPoint.zonedDateTimeFromUntil(
	from: ZonedDateTime,
	toExclusive: ZonedDateTime,
	temporalUnit: TemporalUnit,
): OrderedArgsGenerator<ZonedDateTime> =
	ZonedDateTimeFromUntilOrderedArgsGenerator(_components, from, toExclusive, temporalUnit)

/**
 * Returns an [OrderedArgsGenerator] which generates [OffsetDateTime]s ranging [from] (inclusive) to [toExclusive]
 * where [temporalUnit] defines the steps.
 *
 * @throws ArithmeticException in case the difference between [from] and [toExclusive] is too big.
 *
 * @since 3.0.0
 */
fun OrderedExtensionPoint.offsetDateTimeFromUntil(
	from: OffsetDateTime,
	toExclusive: OffsetDateTime,
	temporalUnit: TemporalUnit,
): OrderedArgsGenerator<OffsetDateTime> =
	OffsetDateTimeFromUntilOrderedArgsGenerator(_components, from, toExclusive, temporalUnit)

/**
 * Returns an [OrderedArgsGenerator] which generates [LocalTime]s ranging [from] (inclusive) to [toInclusive]
 * where [temporalUnit] defines the steps which defaults to [ChronoUnit.SECONDS].
 *
 * @since 3.0.0
 */
fun OrderedExtensionPoint.localTimeFromTo(
	from: LocalTime,
	toInclusive: LocalTime,
	temporalUnit: TemporalUnit,
): OrderedArgsGenerator<LocalTime> =
	LocalTimeFromToOrderedArgsGenerator(_components, from, toInclusive, temporalUnit)

/**
 * Returns an [OrderedArgsGenerator] which generates [LocalDate]s ranging [from] (inclusive) to [toInclusive]
 * where [temporalUnit] defines the steps which defaults to [ChronoUnit.DAYS].
 *
 * @throws java.time.temporal.UnsupportedTemporalTypeException In case you choose a [TemporalUnit] which is smaller
 *   than days.
 *
 * @since 3.0.0
 */
fun OrderedExtensionPoint.localDateFromTo(
	from: LocalDate,
	toInclusive: LocalDate,
	temporalUnit: TemporalUnit = ChronoUnit.DAYS,
): OrderedArgsGenerator<LocalDate> =
	LocalDateFromToOrderedArgsGenerator(_components, from, toInclusive, temporalUnit)

/**
 * Returns an [OrderedArgsGenerator] which generates [LocalDateTime]s ranging [from] (inclusive) to [toInclusive]
 * where [temporalUnit] defines the steps.
 *
 * @throws ArithmeticException in case the difference between [from] and [toInclusive] is too big.
 *
 * @since 3.0.0
 */
fun OrderedExtensionPoint.localDateTimeFromTo(
	from: LocalDateTime,
	toInclusive: LocalDateTime,
	temporalUnit: TemporalUnit
): OrderedArgsGenerator<LocalDateTime> =
	LocalDateTimeFromToOrderedArgsGenerator(_components, from, toInclusive, temporalUnit)

/**
 * Returns an [OrderedArgsGenerator] which generates [ZonedDateTime]s ranging [from] (inclusive) to [toInclusive]
 * where [temporalUnit] defines the steps.
 *
 * @throws ArithmeticException in case the difference between [from] and [toInclusive] is too big.
 *
 * @since 3.0.0
 */
fun OrderedExtensionPoint.zonedDateTimeFromTo(
	from: ZonedDateTime,
	toInclusive: ZonedDateTime,
	temporalUnit: TemporalUnit,
): OrderedArgsGenerator<ZonedDateTime> =
	ZonedDateTimeFromToOrderedArgsGenerator(_components, from, toInclusive, temporalUnit)

/**
 * Returns an [OrderedArgsGenerator] which generates [OffsetDateTime]s ranging [from] (inclusive) to [toInclusive]
 * where [temporalUnit] defines the steps.
 *
 * @throws ArithmeticException in case the difference between [from] and [toInclusive] is too big.
 *
 * @since 3.0.0
 */
fun OrderedExtensionPoint.offsetDateTimeFromTo(
	from: OffsetDateTime,
	toInclusive: OffsetDateTime,
	temporalUnit: TemporalUnit,
): OrderedArgsGenerator<OffsetDateTime> =
	OffsetDateTimeFromToOrderedArgsGenerator(_components, from, toInclusive, temporalUnit)
