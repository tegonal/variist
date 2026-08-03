package com.tegonal.variist.generators

import ch.tutteli.kbox.Tuple
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class OrderedDateLikeTest : AbstractOrderedArgsGeneratorTest<Any>() {

	override fun createGenerators(modifiedOrdered: OrderedExtensionPoint) = run {
		val zoneOffset = ZoneOffset.ofHours(1)
		val nowZonedDateTime = ZonedDateTime.now().truncatedTo(ChronoUnit.HOURS)
		val nowLocalDateTime = nowZonedDateTime.toLocalDateTime()
		val nowLocalTime = nowZonedDateTime.toLocalTime().let {
			if (it.plusHours(2) >= LocalTime.of(0, 0)) LocalTime.of(21, 0) else it
		}
		val nowLocalDate = nowZonedDateTime.toLocalDate()
		val nowOffsetDateTime = nowZonedDateTime.toOffsetDateTime().withOffsetSameInstant(zoneOffset)
		sequenceOf(
			Tuple(
				"localTimeFromUntil",
				modifiedOrdered.localTimeFromUntil(nowLocalTime, nowLocalTime.plusHours(2), ChronoUnit.HOURS),
				listOf(nowLocalTime, nowLocalTime.plusHours(1))
			),
			Tuple(
				"localDateFromUntil",
				modifiedOrdered.localDateFromUntil(nowLocalDate, nowLocalDate.plusDays(2), ChronoUnit.DAYS),
				listOf(nowLocalDate, nowLocalDate.plusDays(1))
			),
			Tuple(
				"localDateTimeFromUntil",
				modifiedOrdered.localDateTimeFromUntil(
					nowLocalDateTime,
					nowLocalDateTime.plusDays(1),
					ChronoUnit.HOURS
				),
				(0L..23).map { nowLocalDateTime.plusHours(it) }
			),
			Tuple(
				"zonedDateTimeFromUntil",
				modifiedOrdered.zonedDateTimeFromUntil(
					nowZonedDateTime,
					nowZonedDateTime.plusHours(3),
					ChronoUnit.MINUTES,
				),
				(0L until 3 * 60).map { nowZonedDateTime.plusMinutes(it) }
			),

			Tuple(
				"offsetDateTimeFromUntil",
				modifiedOrdered.offsetDateTimeFromUntil(
					nowOffsetDateTime,
					nowOffsetDateTime.plusMinutes(2),
					ChronoUnit.SECONDS,
				),
				(0L until 2 * 60).map { nowOffsetDateTime.plusSeconds(it) }
			),
			Tuple(
				"localTimeFromTo",
				modifiedOrdered.localTimeFromTo(nowLocalTime, nowLocalTime.plusMinutes(2), ChronoUnit.MINUTES),
				listOf(nowLocalTime, nowLocalTime.plusMinutes(1), nowLocalTime.plusMinutes(2))
			),
			Tuple(
				"localDateFromTo",
				modifiedOrdered.localDateFromTo(nowLocalDate, nowLocalDate.plusDays(2), ChronoUnit.DAYS),
				listOf(nowLocalDate, nowLocalDate.plusDays(1), nowLocalDate.plusDays(2))
			),
			Tuple(
				"localDateTimeFromTo",
				modifiedOrdered.localDateTimeFromTo(
					nowLocalDateTime,
					nowLocalDateTime.plus(13, ChronoUnit.MILLIS),
					ChronoUnit.MILLIS
				),
				(0L..13).map { nowLocalDateTime.plus(it, ChronoUnit.MILLIS) }
			),
			Tuple(
				"zonedDateTimeFromTo",
				modifiedOrdered.zonedDateTimeFromTo(
					nowZonedDateTime,
					nowZonedDateTime.plus(11, ChronoUnit.MICROS),
					ChronoUnit.MICROS,
				),
				(0L..11).map { nowZonedDateTime.plus(it, ChronoUnit.MICROS) }
			),
			Tuple(
				"offsetDateTimeFromTo",
				modifiedOrdered.offsetDateTimeFromTo(
					nowOffsetDateTime,
					nowOffsetDateTime.plusMinutes(1),
					ChronoUnit.SECONDS,
				),
				(0L..1 * 60).map { nowOffsetDateTime.plusSeconds(it) }
			),
		)
	}
}
