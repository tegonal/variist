package com.tegonal.variist.generators

import com.tegonal.variist.config._components
import com.tegonal.variist.generators.impl.*
import java.time.*
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import kotlin.math.sign

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
fun ArbExtensionPoint.zonedDateTimeFromUntil(
	from: ZonedDateTime,
	toExclusive: ZonedDateTime,
	temporalUnit: TemporalUnit,
	arbZoneId: ArbArgsGenerator<ZoneId> = zoneId(),
): ArbArgsGenerator<ZonedDateTime> =
	ZonedDateTimeFromUntilArbArgsGenerator(_components, from, toExclusive, temporalUnit)
		.withZoneSameInstant(arbZoneId)

/**
 * Returns an [ArbArgsGenerator] which generates [OffsetDateTime]s ranging [from] (inclusive) to [toExclusive]
 * where [temporalUnit] defines the steps.
 *
 * @throws ArithmeticException in case the difference between [from] and [toExclusive] is too big.
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.offsetDateTimeFromUntil(
	from: OffsetDateTime,
	toExclusive: OffsetDateTime,
	temporalUnit: TemporalUnit,
	arbZoneOffset: ArbArgsGenerator<ZoneOffset> = zoneOffset()
): ArbArgsGenerator<OffsetDateTime> =
	OffsetDateTimeFromUntilArbArgsGenerator(_components, from, toExclusive, temporalUnit)
		.withOffsetSameInstant(arbZoneOffset)

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
fun ArbExtensionPoint.zonedDateTimeFromTo(
	from: ZonedDateTime,
	toInclusive: ZonedDateTime,
	temporalUnit: TemporalUnit,
	arbZoneId: ArbArgsGenerator<ZoneId> = zoneId()
): ArbArgsGenerator<ZonedDateTime> =
	ZonedDateTimeFromToArbArgsGenerator(_components, from, toInclusive, temporalUnit)
		.withZoneSameInstant(arbZoneId)

/**
 * Returns an [ArbArgsGenerator] which generates [OffsetDateTime]s ranging [from] (inclusive) to [toInclusive]
 * where [temporalUnit] defines the steps.
 *
 * @throws ArithmeticException in case the difference between [from] and [toInclusive] is too big.
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.offsetDateTimeFromTo(
	from: OffsetDateTime,
	toInclusive: OffsetDateTime,
	temporalUnit: TemporalUnit,
	arbZoneOffset: ArbArgsGenerator<ZoneOffset> = zoneOffset()
): ArbArgsGenerator<OffsetDateTime> =
	OffsetDateTimeFromToArbArgsGenerator(_components, from, toInclusive, temporalUnit)
		.withOffsetSameInstant(arbZoneOffset)

/**
 * Returns an [ArbArgsGenerator] which generates [ZoneId]s.
 *
 * @since 2.3.0
 */
fun ArbExtensionPoint.zoneId(): ArbArgsGenerator<ZoneId> = arb.fromList(zoneIds)

private val zoneIds by lazy { ZoneId.getAvailableZoneIds().map { ZoneId.of(it) } }

/**
 * Returns an [ArbArgsGenerator] which generates [ZoneId]s.
 *
 * @since 2.3.0
 */
fun ArbExtensionPoint.zoneOffset(): ArbArgsGenerator<ZoneOffset> =
	arb.intFromTo(-18, 18).zip(arb.intFromTo(0, 59)).zip(arb.intFromTo(0, 59)) { (h, m), s ->
		if (h == 18 || h == -18) ZoneOffset.ofHours(h)
		else ZoneOffset.ofHoursMinutesSeconds(h, m * h.sign, s * h.sign)
	}

/**
 * Adjust the [ZoneId] of the generated [ZonedDateTime] based on the given [ArbArgsGenerator].
 *
 * @since 2.3.0
 */
fun ArbArgsGenerator<ZonedDateTime>.withZoneSameInstant(
	arbZoneId: ArbArgsGenerator<ZoneId>
): ArbArgsGenerator<ZonedDateTime> = zip(arbZoneId) { date, zoneId ->
	date.withZoneSameInstant(zoneId)
}

/**
 * Adjust the [ZoneOffset] of the generated [OffsetDateTime] based on the given [ArbArgsGenerator].
 *
 * @since 2.3.0
 */
fun ArbArgsGenerator<OffsetDateTime>.withOffsetSameInstant(
	arbZoneOffset: ArbArgsGenerator<ZoneOffset>
): ArbArgsGenerator<OffsetDateTime> = zip(arbZoneOffset) { date, zoneOffset ->
	date.withOffsetSameInstant(zoneOffset)
}
