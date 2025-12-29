package com.tegonal.variist.generators

import com.tegonal.variist.config._components
import com.tegonal.variist.generators.impl.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit

/**
 * Returns an [ArbArgsGenerator] which generates [LocalDate]s ranging [from] (inclusive) to [toExclusive]
 * where [temporalUnit] defines the steps.
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.localDateFromUntil(
	from: LocalDate,
	toExclusive: LocalDate,
	temporalUnit: TemporalUnit = ChronoUnit.DAYS,
): ArbArgsGenerator<LocalDate> =
	LocalDateFromUntilArbArgsGenerator(_components, seedBaseOffset, from, toExclusive, temporalUnit)

/**
 * Returns an [ArbArgsGenerator] which generates [LocalDateTime]s ranging [from] (inclusive) to [toExclusive]
 * where [temporalUnit] defines the steps.
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.localDateTimeFromUntil(
	from: LocalDateTime,
	toExclusive: LocalDateTime,
	temporalUnit: TemporalUnit,
): ArbArgsGenerator<LocalDateTime> =
	LocalDateTimeFromUntilArbArgsGenerator(_components, seedBaseOffset, from, toExclusive, temporalUnit)

/**
 * Returns an [ArbArgsGenerator] which generates [ZonedDateTime]s ranging [from] (inclusive) to [toExclusive]
 * where [temporalUnit] defines the steps.
 *
 * @since 2.0.0
 */
//TODO 2.1.0 also define a parameter to steer timezone
fun ArbExtensionPoint.zonedDateTimeFromUntil(
	from: ZonedDateTime,
	toExclusive: ZonedDateTime,
	temporalUnit: TemporalUnit,
): ArbArgsGenerator<ZonedDateTime> =
	ZonedDateTimeFromUntilArbArgsGenerator(_components, seedBaseOffset, from, toExclusive, temporalUnit)

/**
 * Returns an [ArbArgsGenerator] which generates [OffsetDateTime]s ranging [from] (inclusive) to [toExclusive]
 * where [temporalUnit] defines the steps.
 *
 * @since 2.0.0
 */
//TODO 2.1.0 also define a parameter to steer the offset
fun ArbExtensionPoint.offsetDateTimeFromUntil(
	from: OffsetDateTime,
	toExclusive: OffsetDateTime,
	temporalUnit: TemporalUnit,
): ArbArgsGenerator<OffsetDateTime> =
	OffsetDateTimeFromUntilArbArgsGenerator(_components, seedBaseOffset, from, toExclusive, temporalUnit)

/**
 * Returns an [ArbArgsGenerator] which generates [LocalDate]s ranging [from] (inclusive) to [toExclusive]
 * where [temporalUnit] defines the steps.
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.localDateFromTo(
	from: LocalDate,
	toExclusive: LocalDate,
	temporalUnit: TemporalUnit = ChronoUnit.DAYS,
): ArbArgsGenerator<LocalDate> =
	LocalDateFromToArbArgsGenerator(_components, seedBaseOffset, from, toExclusive, temporalUnit)

/**
 * Returns an [ArbArgsGenerator] which generates [LocalDateTime]s ranging [from] (inclusive) to [toExclusive]
 * where [temporalUnit] defines the steps.
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.localDateTimeFromTo(
	from: LocalDateTime,
	toExclusive: LocalDateTime,
	temporalUnit: TemporalUnit
): ArbArgsGenerator<LocalDateTime> =
	LocalDateTimeFromToArbArgsGenerator(_components, seedBaseOffset, from, toExclusive, temporalUnit)

/**
 * Returns an [ArbArgsGenerator] which generates [ZonedDateTime]s ranging [from] (inclusive) to [toExclusive]
 * where [temporalUnit] defines the steps.
 *
 * @since 2.0.0
 */
//TODO 2.1.0 also define a parameter to steer timezone
fun ArbExtensionPoint.zonedDateTimeFromTo(
	from: ZonedDateTime,
	toExclusive: ZonedDateTime,
	temporalUnit: TemporalUnit
): ArbArgsGenerator<ZonedDateTime> =
	ZonedDateTimeFromToArbArgsGenerator(_components, seedBaseOffset, from, toExclusive, temporalUnit)


/**
 * Returns an [ArbArgsGenerator] which generates [OffsetDateTime]s ranging [from] (inclusive) to [toExclusive]
 * where [temporalUnit] defines the steps.
 *
 * @since 2.0.0
 */
//TODO 2.1.0 also define a parameter to steer the offset
fun ArbExtensionPoint.offsetDateTimeFromTo(
	from: OffsetDateTime,
	toExclusive: OffsetDateTime,
	temporalUnit: TemporalUnit
): ArbArgsGenerator<OffsetDateTime> =
	OffsetDateTimeFromToArbArgsGenerator(_components, seedBaseOffset, from, toExclusive, temporalUnit)

//TODO 2.1.0 localDateBounds and co.
