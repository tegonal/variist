package com.tegonal.variist.generators

import ch.tutteli.atrium.api.fluent.en_GB.messageToContain
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.kbox.Tuple
import ch.tutteli.kbox.mapFirst
import com.tegonal.variist.providers.ArgsSource
import com.tegonal.variist.testutils.intMinSizeMaxSizeError
import com.tegonal.variist.testutils.longMinSizeMaxSizeError
import com.tegonal.variist.testutils.minInclusiveMustBeLessThanMaxInclusive
import com.tegonal.variist.testutils.minMaxInclusiveCase
import org.junit.jupiter.api.Named
import org.junit.jupiter.params.ParameterizedTest

class ArbRangeTest : AbstractArbArgsGeneratorTest<Any>() {

	override fun createGenerators(modifiedArb: ArbExtensionPoint): ArbArgsTestFactoryResult<Any> {
		val minSize2MaxSize2 = listOf('a'..'b', 'b'..'c', 'c'..'d')
		val minSize1MaxSize2 = listOf('a'..'a', 'b'..'b', 'c'..'c', 'd'..'d') + minSize2MaxSize2
		val minSize0MaxSize2 = listOf('d'..'a') + minSize1MaxSize2
		return sequenceOf(
			Tuple(
				"charRange minSize=0, maxSize=2",
				modifiedArb.charRange('a', 'd', maxSize = 2),
				minSize0MaxSize2
			),
			Tuple(
				"charRange minSize=1, maxSize=2",
				modifiedArb.charRange('a', 'd', minSize = 1, maxSize = 2),
				minSize1MaxSize2
			),
			Tuple(
				"intRange minSize=0, maxSize=2",
				modifiedArb.intRange(1, 4, maxSize = 2),
				minSize0MaxSize2.map { it.first.code - 'a'.code + 1..it.last.code - 'a'.code + 1 }
			),
			Tuple(
				"intRange minSize=1, maxSize=2",
				modifiedArb.intRange(1, 4, minSize = 1, maxSize = 2),
				minSize1MaxSize2.map { it.first.code - 'a'.code + 1..it.last.code - 'a'.code + 1 }
			),
			Tuple(
				"longRange minSize=0, maxSize=2",
				modifiedArb.longRange(1, 4, maxSize = 2),
				minSize0MaxSize2.map { (it.first.code - 'a'.code + 1).toLong()..it.last.code - 'a'.code + 1 }
			),
			Tuple(
				"longRange minSize=1, maxSize=2",
				modifiedArb.longRange(1, 4, minSize = 1, maxSize = 2),
				minSize1MaxSize2.map { (it.first.code - 'a'.code + 1).toLong()..it.last.code - 'a'.code + 1 }
			),
		)
	}

	@ParameterizedTest
	@ArgsSource("validationErrors")
	fun check_invariants(@Suppress("unused") what: String, errorMsg: String, factory: () -> ArbArgsGenerator<*>) {
		expect(factory).toThrow<IllegalStateException> {
			messageToContain(errorMsg)
		}
	}

	companion object {
		@JvmStatic
		fun validationErrors() = run {
			listOf(
				intMinSizeMaxSizeError("charRange") { l, u -> { arb.charRange(minSize = l, maxSize = u) } },
				intMinSizeMaxSizeError("intRange") { l, u -> { arb.intRange(minSize = l, maxSize = u) } },
				longMinSizeMaxSizeError("longRange") { l, u -> { arb.longRange(minSize = l, maxSize = u) } },
			).concatAll()
		} + run {
			val charBounds = arb.charBounds().zip(arb.intPositive())
			val intBounds = arb.intBounds().zip(arb.intPositive())
			val longBounds = arb.longBounds().zip(arb.longPositive())

			semiOrdered.fromArbs(
				charBounds.map { (l, u, minSize) ->
					Tuple("charRange", minInclusiveMustBeLessThanMaxInclusive(l.code, u.code, minSize), Named.of("f") {
						arb.charRange(minInclusive = u, maxInclusive = l, minSize = minSize)
					})
				},
				intBounds.minMaxInclusiveCase("intRange") { l, u, minSize ->
					{ arb.intRange(minInclusive = u, maxInclusive = l, minSize = minSize) }
				},
				longBounds.minMaxInclusiveCase("longRange") { l, u, minSize ->
					{ arb.longRange(minInclusive = u, maxInclusive = l, minSize = minSize) }
				},
			)
		}.map { p -> p.mapFirst { "$it minInclusive > maxInclusive - minSize + 1" } }
	}
}
