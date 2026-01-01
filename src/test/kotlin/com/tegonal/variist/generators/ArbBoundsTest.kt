package com.tegonal.variist.generators

import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.kbox.Tuple
import ch.tutteli.kbox.Tuple2
import ch.tutteli.kbox.Tuple3
import ch.tutteli.kbox.mapFirst
import com.tegonal.variist.generators.impl.createBoundsArbGenerator
import com.tegonal.variist.generators.impl.createIntDomainBasedBoundsArbGenerator
import com.tegonal.variist.generators.impl.possibleMaxSizeSafeInIntDomain
import com.tegonal.variist.generators.impl.possibleMaxSizeSafeInLongDomain
import com.tegonal.variist.providers.ArgsSource
import com.tegonal.variist.testutils.intBoundError
import com.tegonal.variist.testutils.longBoundError
import com.tegonal.variist.utils.BigInt
import com.tegonal.variist.utils.toBigInt
import org.junit.jupiter.api.Named
import org.junit.jupiter.params.ParameterizedTest

class ArbBoundsTest : AbstractArbArgsGeneratorTest<Any>() {

	override fun createGenerators(modifiedArb: ArbExtensionPoint): ArbArgsTestFactoryResult<Any> {
		val minSize2MaxSize2 = listOf('a' to 'b', 'b' to 'c', 'c' to 'd')
		val minSize1MaxSize2 = listOf('a' to 'a', 'b' to 'b', 'c' to 'c', 'd' to 'd') + minSize2MaxSize2
		return sequenceOf(
			Tuple(
				"charBounds minSize=1, maxSize=2",
				modifiedArb.charBounds('a', 'd', minSize = 1, maxSize = 2),
				minSize1MaxSize2
			),
			Tuple(
				"intBounds minSize=1, maxSize=2",
				modifiedArb.intBounds(1, 4, minSize = 1, maxSize = 2),
				minSize1MaxSize2.map { it.first.code - 'a'.code + 1 to it.second.code - 'a'.code + 1 }
			),
			Tuple(
				"longBounds minSize=1, maxSize=2",
				modifiedArb.longBounds(1, 4, minSize = 1, maxSize = 2),
				minSize1MaxSize2.map { (it.first.code - 'a'.code + 1).toLong() to it.second.code - 'a'.code + 1.toLong() }
			),
		)
	}

	@ParameterizedTest
	@ArgsSource("arbIntSafeMinMaxAndMinSize")
	fun createIntMaxInIntDomain(minInclusive: Int, maxInclusive: Int, minSize: Int) {
		arb.createIntDomainBasedBoundsArbGenerator(
			minInclusive = minInclusive, maxInclusive = maxInclusive, minSize = minSize, maxSize = null
		) { s, l -> s to l }.generateAndTake(3).forEachIndexed { index, pair ->
			expect(index to pair) {
				second {
					first.toBeGreaterThanOrEqualTo(minInclusive).toBeLessThanOrEqualTo(maxInclusive)
					second.toBeGreaterThanOrEqualTo(pair.first).toBeLessThanOrEqualTo(maxInclusive)
				}
			}
		}
	}

	@ParameterizedTest
	@ArgsSource("arbIntSafeMinMaxAndMinSize")
	fun createMaxInIntDomain(minInclusive: Int, maxInclusive: Int, minSize: Int) {
		val minInclusiveL = minInclusive.toLong()
		val maxInclusiveL = maxInclusive.toLong()
		arb.createBoundsArbGenerator(
			minInclusive = minInclusiveL, maxInclusive = maxInclusiveL, minSize = minSize.toLong(), maxSize = null
		) { s, l -> s to l }.generateAndTake(3).forEachIndexed { index, pair ->
			expect(index to pair) {
				second {
					first.toBeGreaterThanOrEqualTo(minInclusiveL).toBeLessThanOrEqualTo(maxInclusiveL)
					second.toBeGreaterThanOrEqualTo(pair.first).toBeLessThanOrEqualTo(maxInclusiveL)
				}
			}
		}
	}

	@ParameterizedTest
	@ArgsSource("arbIntSafeMinMaxAndMinSize")
	fun createMaxInIntDomainButShifted(minInclusive: Int, maxInclusive: Int, minSize: Int) {
		val intMaxAsLong = Int.MAX_VALUE.toLong()
		val minInclusiveShifted = intMaxAsLong + minInclusive
		val maxInclusiveShifted = intMaxAsLong + maxInclusive
		arb.createBoundsArbGenerator(
			minInclusive = minInclusiveShifted,
			maxInclusive = maxInclusiveShifted,
			minSize = minSize.toLong(),
			maxSize = null
		) { s, l -> s to l }.generateAndTake(3).forEachIndexed { index, pair ->
			expect(index to pair) {
				second {
					first.toBeGreaterThanOrEqualTo(minInclusiveShifted).toBeLessThanOrEqualTo(maxInclusiveShifted)
					second.toBeGreaterThanOrEqualTo(pair.first).toBeLessThanOrEqualTo(maxInclusiveShifted)
				}
			}
		}
	}

	@ParameterizedTest
	@ArgsSource("arbLongSafeMinMaxAndMinSize")
	fun createMaxInLongDomain(minInclusive: Long, maxInclusive: Long, minSize: Long) {
		arb.createBoundsArbGenerator(
			minInclusive = minInclusive, maxInclusive = maxInclusive, minSize = minSize, maxSize = null
		) { s, l -> s to l }.generateAndTake(3).forEachIndexed { index, pair ->
			expect(index to pair) {
				second {
					first.toBeGreaterThanOrEqualTo(minInclusive).toBeLessThanOrEqualTo(maxInclusive)
					second.toBeGreaterThanOrEqualTo(pair.first).toBeLessThanOrEqualTo(maxInclusive)
				}
			}
		}
	}

	@ParameterizedTest
	@ArgsSource("arbLongUnsafeMinMaxAndSize")
	fun createMaxOutsideLongDomain(minInclusive: Long, maxInclusive: Long, minSize: Long) {
		arb.createBoundsArbGenerator(
			minInclusive = minInclusive, maxInclusive = maxInclusive, minSize = minSize, maxSize = null
		) { s, l -> s to l }.generateAndTake(3).forEachIndexed { index, pair ->
			expect(index to pair) {
				second {
					first.toBeGreaterThanOrEqualTo(minInclusive).toBeLessThanOrEqualTo(maxInclusive)
					second.toBeGreaterThanOrEqualTo(pair.first).toBeLessThanOrEqualTo(maxInclusive)
				}
			}
		}
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
		fun arbIntSafeMinMaxAndMinSize() =
			arbIntSafeMinMax().zipDependent {
				arb.intFromTo(1, it.second - it.first + 1)
			}

		@JvmStatic
		fun arbIntSafeMinMax() =
			// we are not using arb.intRange here on purpose as we would use the function under test in the test-setup
			arb.intFromUntil(Int.MIN_VALUE, Int.MAX_VALUE - possibleMaxSizeSafeInIntDomain).zipDependent {
				arb.intFromUntil(it, it + possibleMaxSizeSafeInIntDomain)
			}

		@JvmStatic
		fun arbLongSafeMinMaxAndMinSize() =
			arbLongSafeMinMax().zipDependent {
				arb.longFromTo(1, it.second - it.first + 1)
			}

		@JvmStatic
		fun arbLongSafeMinMax() =
			// we are not using arb.longRange here on purpose as we would use the function under test in the test-setup
			arb.longFromUntil(Long.MIN_VALUE, Long.MAX_VALUE - possibleMaxSizeSafeInLongDomain).zipDependent {
				// + possibleMaxSizeSafeInIntDomain as we could otherwise land in the Int-Domain
				arb.longFromUntil(it + possibleMaxSizeSafeInIntDomain, it + possibleMaxSizeSafeInLongDomain)
			}

		@JvmStatic
		fun arbLongUnsafeMinMaxAndSize() =
			arb.longFromUntil(Long.MIN_VALUE, Long.MAX_VALUE - possibleMaxSizeSafeInLongDomain - 10).zipDependent {
				arb.longFromUntil(it + possibleMaxSizeSafeInLongDomain, Long.MAX_VALUE)
			}.zipDependent {
				arb.longFromTo(
					1,
					(it.second.toBigInt() - it.first.toBigInt() + BigInt.ONE).min(Long.MAX_VALUE.toBigInt()).toLong()
				)
			}

		@JvmStatic
		fun validationErrors() = run {
			fun <T : Number> SemiOrderedArgsGenerator<Tuple3<T, T, String>>.case(
				description: String,
				factory: (T, T) -> () -> ArbArgsGenerator<*>
			) = map { (lower, upper, errMsg) -> Tuple(description, errMsg, Named.of("f", factory(lower, upper))) }

			listOf(
				intBoundError("minSize", "maxSize").case("charBounds") { l, u ->
					{ arb.charBounds(minSize = l, maxSize = u) }
				},
				intBoundError("minSize", "maxSize").case("charBoundsBased") { l, u ->
					{ arb.charBoundsBased(minSize = l, maxSize = u, factory = ::Tuple2) }
				},
				intBoundError("minSize", "maxSize").case("intBounds") { l, u ->
					{ arb.intBounds(minSize = l, maxSize = u) }
				},
				intBoundError("minSize", "maxSize").case("intBoundsBased") { l, u ->
					{ arb.intBoundsBased(minSize = l, maxSize = u, factory = ::Tuple2) }
				},
				longBoundError("minSize", "maxSize").case("longBounds") { l, u ->
					{ arb.longBounds(minSize = l, maxSize = u) }
				},
				longBoundError("minSize", "maxSize").case("longBoundsBased") { l, u ->
					{ arb.longBoundsBased(minSize = l, maxSize = u, factory = ::Tuple2) }
				},
			).concatAll()
		} + run {
			fun errMsg(lowerBound: Any, upperBound: Any, minSize: Any) =
				"minInclusive ($upperBound) must be less than or equal to `maxInclusive ($lowerBound) - minSize ($minSize) + 1`"

			fun <T : Any> ArbArgsGenerator<Tuple3<T, T, T>>.case(
				description: String,
				factory: (T, T, T) -> () -> ArbArgsGenerator<*>
			) = map { (lower, upper, minSize) ->
				Tuple(description, errMsg(lower, upper, minSize), Named.of("f", factory(lower, upper, minSize)))
			}

			val charBounds = arb.charBounds().zip(arb.intPositive())
			val intBounds = arb.intBounds().zip(arb.intPositive())
			val longBounds = arb.longBounds().zip(arb.longPositive())

			semiOrdered.fromArbs(
				charBounds.map { (l, u, minSize) ->
					Tuple("charBounds", errMsg(l.code, u.code, minSize), Named.of("f") {
						arb.charBounds(minInclusive = u, maxInclusive = l, minSize = minSize)
					})
				},
				charBounds.map { (l, u, minSize) ->
					Tuple("charBoundsBased", errMsg(l.code, u.code, minSize = minSize), Named.of("f") {
						arb.charBoundsBased(minInclusive = u, maxInclusive = l, minSize = minSize, factory = ::Tuple2)
					})
				},
				intBounds.case("intBounds") { l, u, minSize ->
					{ arb.intBounds(minInclusive = u, maxInclusive = l, minSize = minSize) }
				},
				intBounds.case("intBoundsBased") { l, u, minSize ->
					{ arb.intBoundsBased(minInclusive = u, maxInclusive = l, minSize = minSize, factory = ::Tuple2) }
				},
				longBounds.case("longBounds") { l, u, minSize ->
					{ arb.longBounds(minInclusive = u, maxInclusive = l, minSize = minSize) }
				},
				longBounds.case("longBoundsBased") { l, u, minSize ->
					{ arb.longBoundsBased(minInclusive = u, maxInclusive = l, minSize = minSize, factory = ::Tuple2) }
				}
			)

		}.map { p -> p.mapFirst { "$it minInclusive > maxInclusive - minSize + 1" } }
	}
}
