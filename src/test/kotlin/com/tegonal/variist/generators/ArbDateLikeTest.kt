package com.tegonal.variist.generators

import ch.tutteli.kbox.Tuple
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class ArbDateLikeTest : AbstractArbArgsGeneratorTest<Any>() {

	override fun createGenerators(modifiedArb: ArbExtensionPoint) =
		ZonedDateTime.now().truncatedTo(ChronoUnit.HOURS).let { nowZonedDateTime ->
			val nowLocalDateTime = nowZonedDateTime.toLocalDateTime()
			val nowLocalTime = nowZonedDateTime.toLocalTime()
			val nowLocalDate = nowZonedDateTime.toLocalDate()
			val nowOffsetDateTime = nowZonedDateTime.toOffsetDateTime()
			sequenceOf(
				Tuple(
					"localTimeFromUntil",
					modifiedArb.localTimeFromUntil(nowLocalTime, nowLocalTime.plusHours(2), ChronoUnit.HOURS),
					listOf(nowLocalTime, nowLocalTime.plusHours(1))
				),
				Tuple(
					"localDateFromUntil",
					modifiedArb.localDateFromUntil(nowLocalDate, nowLocalDate.plusDays(2), ChronoUnit.DAYS),
					listOf(nowLocalDate, nowLocalDate.plusDays(1))
				),
				Tuple(
					"localDateTimeFromUntil",
					modifiedArb.localDateTimeFromUntil(
						nowLocalDateTime,
						nowLocalDateTime.plusDays(1),
						ChronoUnit.HOURS
					),
					(0L..23).map { nowLocalDateTime.plusHours(it) }
				),
				Tuple(
					"zonedDateTimeFromUntil",
					modifiedArb.zonedDateTimeFromUntil(
						nowZonedDateTime,
						nowZonedDateTime.plusHours(3),
						ChronoUnit.MINUTES
					),
					(0L until 3 * 60).map { nowZonedDateTime.plusMinutes(it) }
				),
				Tuple(
					"offsetDateTimeFromUntil",
					modifiedArb.offsetDateTimeFromUntil(
						nowOffsetDateTime,
						nowOffsetDateTime.plusMinutes(2),
						ChronoUnit.SECONDS
					),
					(0L until 2 * 60).map { nowOffsetDateTime.plusSeconds(it) }
				),
				Tuple(
					"localTimeFromTo",
					modifiedArb.localTimeFromTo(nowLocalTime, nowLocalTime.plusMinutes(2), ChronoUnit.MINUTES),
					listOf(nowLocalTime, nowLocalTime.plusMinutes(1), nowLocalTime.plusMinutes(2))
				),
				Tuple(
					"localDateFromTo",
					modifiedArb.localDateFromTo(nowLocalDate, nowLocalDate.plusDays(2), ChronoUnit.DAYS),
					listOf(nowLocalDate, nowLocalDate.plusDays(1), nowLocalDate.plusDays(2))
				),
				Tuple(
					"localDateTimeFromTo",
					modifiedArb.localDateTimeFromTo(
						nowLocalDateTime,
						nowLocalDateTime.plus(13, ChronoUnit.MILLIS),
						ChronoUnit.MILLIS
					),
					(0L..13).map { nowLocalDateTime.plus(it, ChronoUnit.MILLIS) }
				),
				Tuple(
					"zonedDateTimeFromTo",
					modifiedArb.zonedDateTimeFromTo(
						nowZonedDateTime,
						nowZonedDateTime.plus(11, ChronoUnit.MICROS),
						ChronoUnit.MICROS
					),
					(0L..11).map { nowZonedDateTime.plus(it, ChronoUnit.MICROS) }
				),
				Tuple(
					"offsetDateTimeFromTo",
					modifiedArb.offsetDateTimeFromTo(
						nowOffsetDateTime,
						nowOffsetDateTime.plusMinutes(1),
						ChronoUnit.SECONDS
					),
					(0L..1 * 60).map { nowOffsetDateTime.plusSeconds(it) }
				),
			)
		}
}
