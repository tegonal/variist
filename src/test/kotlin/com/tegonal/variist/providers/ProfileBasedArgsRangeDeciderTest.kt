package com.tegonal.variist.providers

import ch.tutteli.atrium.api.fluent.en_GB.notToThrow
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.kbox.Tuple
import com.tegonal.variist.config.*
import com.tegonal.variist.generators.*
import com.tegonal.variist.generators.impl.DefaultArbExtensionPoint
import com.tegonal.variist.providers.impl.ProfileBasedArgsRangeDecider
import com.tegonal.variist.testutils.BaseTest
import com.tegonal.variist.testutils.atrium.offset
import com.tegonal.variist.testutils.atrium.take
import com.tegonal.variist.testutils.createArbWithCustomConfig
import com.tegonal.variist.testutils.createOrderedWithCustomConfig
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class ProfileBasedArgsRangeDeciderTest : BaseTest() {

	@Test
	fun seed_0_and_skip_max__offset_max() {
		val ordered = createOrderedWithCustomConfig(
			ordered._components.config.copy { seed = 0; skip = Int.MAX_VALUE }
		).ordered
		val argsRange = ProfileBasedArgsRangeDecider().decide(ordered.of(1, 2, 3, 4))
		expect(argsRange) {
			offset.toEqual(Int.MAX_VALUE)
		}
	}

	@Test
	fun seed_max_skip_not_defined__offset_max() {
		val ordered = createOrderedWithCustomConfig(
			ordered._components.config.copy { seed = Int.MAX_VALUE; skip = null }
		).ordered
		val argsRange = ProfileBasedArgsRangeDecider().decide(ordered.of(1, 2, 3, 4))
		expect(argsRange) {
			offset.toEqual(Int.MAX_VALUE)
		}
	}

	@ParameterizedTest
	@ValueSource(ints = [1, 2, 3, 4, 5])
	fun seed_plus_skip_max_overflows__offset_in_range_of_ordered_plus_skip(seed: Int) {
		val ordered = createOrderedWithCustomConfig(
			ordered._components.config.copy { this.seed = seed; this.skip = Int.MAX_VALUE }
		)
		val argsRange = ProfileBasedArgsRangeDecider().decide(ordered.of(1, 2, 3, 4))

		expect(argsRange) {
			// Int.MAX_VALUE + 1 % 4 == 0
			offset.toEqual((seed - 1) % 4)
		}
	}

	@ParameterizedTest
	@ValueSource(ints = [1, 2, 3, 4, 5])
	fun seed_max_plus_skip_overflows__offset_in_range_of_ordered_plus_skip(skip: Int) {
		val ordered = createOrderedWithCustomConfig(
			ordered._components.config.copy { this.seed = Int.MAX_VALUE; this.skip = skip }
		)
		val argsRange = ProfileBasedArgsRangeDecider().decide(ordered.of(1, 2, 3, 4))

		expect(argsRange) {
			// Int.MAX_VALUE + 1 % 4 == 0
			offset.toEqual((skip - 1) % 4)
		}
	}

	@ParameterizedTest
	@ValueSource(ints = [Int.MAX_VALUE, Int.MIN_VALUE])
	fun canCopeWithALargeSeedOffset(offset: Int) {
		expect {
			DefaultArbExtensionPoint(arb._components, offset).arb.int().generateAndTakeBasedOnDecider().count()
		}.notToThrow()
	}

	@Test
	fun canCopeWithALargeSkip() {
		expect {
			val ordered = createOrderedWithCustomConfig(
				ordered._components.config.copy { skip = Int.MAX_VALUE }
			)
			ordered.of(1, 2, 3, 4).generateAndTakeBasedOnDecider().count()
		}.notToThrow()
	}

	@ParameterizedTest
	@ValueSource(ints = [Int.MAX_VALUE, Int.MIN_VALUE])
	fun canCopeWithALargeSeed(offset: Int) {
		expect {
			val ordered = createOrderedWithCustomConfig(
				ordered._components.config.copy { seed = offset }
			)
			ordered.of(1, 2, 3, 4).generateAndTakeBasedOnDecider().count()
		}.notToThrow()
	}

	val ownTestProfiles = TestProfiles.create(
		TestType.entries.associate { testType ->
			testType.name to Env.entries.associate {
				it.name to TestConfig(
					maxArgs = when (it) {
						Env.Local -> 2
						Env.Push -> 3
						Env.PR -> 4
						Env.Main -> 5
						Env.DeployTest -> 7
						Env.DeployInt -> 8
						Env.NightlyTest -> 9
						Env.NightlyInt -> 10
						Env.HotfixPR -> 4
						Env.Hotfix -> 6
						Env.Release -> 6
					}
				)
			}
		}
	)

	@ParameterizedTest
	@ArgsSource("testTypeEnvAndGeneratorSize")
	fun ordered_takeIsMaxArgsOfTestTypeEnvCombinationUnlessGeneratorSizeIsSmaller(
		testType: TestType,
		env: Env,
		argsGeneratorSize: Int
	) {
		val customConfig = VariistConfig().copy {
			defaultProfile = testType.name
			activeEnv = env.name
			testProfiles = ownTestProfiles.toMutableList()
		}
		val ordered = createOrderedWithCustomConfig(customConfig)

		val argsGenerator = ordered.fromRange(0 until argsGeneratorSize)
		val config = argsGenerator._components.config
		expect(config.seed).toEqual(customConfig.seed)

		val argsRange = ProfileBasedArgsRangeDecider().decide(argsGenerator)

		expect(argsRange) {
			offset.toEqual(config.seed.toOffset())
			take.toEqual(
				minOf(
					argsGeneratorSize,
					ownTestProfiles.get(testType.name, env.name).maxArgs
				)
			)
		}
	}

	@Test
	fun ordered_maxArgsInConfigTakesPrecedenceOverRequestedMinArgsInAnnotation() {
		val orderedMaxArgs5 = createOrderedWithCustomConfig(
			ordered._components.config.copy { maxArgs = 5 }
		)
		val argsRange = ProfileBasedArgsRangeDecider().decide(
			orderedMaxArgs5.intFromUntil(1, 100),
			AnnotationData(ArgsRangeOptions(requestedMinArgs = 10))
		)
		expect(argsRange) {
			take.toEqual(5)
			offset.toEqual(orderedMaxArgs5._components.config.seed.toOffset())
		}
	}

	@Test
	fun semiOrdered_maxArgsInConfigTakesPrecedenceOverRequestedMinArgsInAnnotation() {
		val orderedMaxArgs5 = createOrderedWithCustomConfig(
			ordered._components.config.copy { maxArgs = 5 }
		).ordered
		val argsRange = ProfileBasedArgsRangeDecider().decide(
			orderedMaxArgs5.intFromUntil(1, 100).zip(arb.intPositive()),
			AnnotationData(ArgsRangeOptions(requestedMinArgs = 10))
		)
		expect(argsRange) {
			take.toEqual(5)
			offset.toEqual(orderedMaxArgs5._components.config.seed.toOffset())
		}
	}

	@Test
	fun arb_maxArgsInConfigTakesPrecedenceOverRequestedMinArgsInAnnotation() {
		val arbWithMaxArgs5 = createArbWithCustomConfig(
			arb._components.config.copy { maxArgs = 5 }
		).arb
		val argsRange = ProfileBasedArgsRangeDecider().decide(
			arbWithMaxArgs5.intFromUntil(1, 100),
			AnnotationData(ArgsRangeOptions(requestedMinArgs = 10))
		)
		expect(argsRange) {
			take.toEqual(5)
			offset.toEqual(arbWithMaxArgs5._components.config.seed.toOffset())
		}
	}

	@Test
	fun ordered_maxArgsInConfigDoesNotTakePrecedenceOverProfile() {
		val orderedWithMaxArgs10Profile2 = createOrderedWithCustomConfig(
			ordered._components.config.copy {
				testProfiles = ownTestProfiles.toMutableList()
				activeEnv = Env.Local.name
				maxArgs = 10
			}
		).ordered
		val argsRange = ProfileBasedArgsRangeDecider().decide(orderedWithMaxArgs10Profile2.intFromUntil(1, 20))
		expect(argsRange) {
			take.toEqual(2)
			offset.toEqual(orderedWithMaxArgs10Profile2._components.config.seed.toOffset())
		}
	}

	@Test
	fun semiOrdered_maxArgsInConfigDoesNotTakePrecedenceOverProfile() {
		val orderedWithMaxArgs10Profile2 = createOrderedWithCustomConfig(
			ordered._components.config.copy {
				testProfiles = ownTestProfiles.toMutableList()
				activeEnv = Env.Local.name
				maxArgs = 10
			}
		).ordered
		val argsRange = ProfileBasedArgsRangeDecider().decide(
			orderedWithMaxArgs10Profile2.intFromUntil(1, 10).zip(arb.intPositive())
		)
		expect(argsRange) {
			take.toEqual(2)
			offset.toEqual(orderedWithMaxArgs10Profile2._components.config.seed.toOffset())
		}
	}


	@Test
	fun arb_maxArgsInConfigDoesNotTakePrecedenceOverProfile() {
		val arbWithMaxArgs10Profile2 = createArbWithCustomConfig(
			arb._components.config.copy {
				testProfiles = ownTestProfiles.toMutableList()
				activeEnv = Env.Local.name
				maxArgs = 10
			}
		).arb
		val argsRange = ProfileBasedArgsRangeDecider().decide(arbWithMaxArgs10Profile2.intFromUntil(1, 10))
		expect(argsRange) {
			take.toEqual(2)
			offset.toEqual(arbWithMaxArgs10Profile2._components.config.seed.toOffset())
		}
	}


	@Test
	fun ordered_sizeTakesPrecedenceOverMaxArgsInConfigAndAnnotation() {
		val orderedMaxArgs5 = createOrderedWithCustomConfig(
			ordered._components.config.copy { maxArgs = 50 }
		).ordered
		val argsRange = ProfileBasedArgsRangeDecider().decide(
			orderedMaxArgs5.intFromUntil(1, 10),
			AnnotationData(ArgsRangeOptions(requestedMinArgs = 10))
		)
		expect(argsRange) {
			take.toEqual(9)
			offset.toEqual(orderedMaxArgs5._components.config.seed.toOffset())
		}
	}

	@Test
	fun semiOrdered_sizeTakesPrecedenceOverMaxArgsInConfigAndAnnotation() {
		val orderedMaxArgs5 = createOrderedWithCustomConfig(
			ordered._components.config.copy { maxArgs = 50 }
		).ordered
		val argsRange = ProfileBasedArgsRangeDecider().decide(
			orderedMaxArgs5.intFromUntil(1, 10).zip(arb.intPositive()),
			AnnotationData(ArgsRangeOptions(maxArgs = 20))
		)
		expect(argsRange) {
			take.toEqual(9)
			offset.toEqual(orderedMaxArgs5._components.config.seed.toOffset())
		}
	}


	@Test
	fun ordered_requestedMinArgsInConfigTakesPrecedenceOverAnnotation() {
		val orderedRequestedMinArgs1000 = createOrderedWithCustomConfig(
			ordered._components.config.copy { requestedMinArgs = 1000 }
		).ordered
		val argsRange = ProfileBasedArgsRangeDecider().decide(
			orderedRequestedMinArgs1000.intFromUntil(1, 10000),
			AnnotationData(ArgsRangeOptions(requestedMinArgs = 2000))
		)
		expect(argsRange) {
			take.toEqual(1000)
			offset.toEqual(orderedRequestedMinArgs1000._components.config.seed.toOffset())
		}
	}

	@Test
	fun semiOrdered_requestedMinArgsInConfigTakesPrecedenceOverAnnotation() {
		val orderedRequestedMinArgs1000 = createOrderedWithCustomConfig(
			ordered._components.config.copy { requestedMinArgs = 1000 }
		).ordered
		val argsRange = ProfileBasedArgsRangeDecider().decide(
			orderedRequestedMinArgs1000.intFromUntil(1, 10000).zip(arb.intPositive()),
			AnnotationData(ArgsRangeOptions(requestedMinArgs = 2000))
		)
		expect(argsRange) {
			take.toEqual(1000)
			offset.toEqual(orderedRequestedMinArgs1000._components.config.seed.toOffset())
		}
	}

	@Test
	fun arb_requestedMinArgsInConfigTakesPrecedenceOverAnnotation() {
		val arbWithRequestedMinArgs1000 = createArbWithCustomConfig(
			arb._components.config.copy { requestedMinArgs = 1000 }
		).arb
		val argsRange = ProfileBasedArgsRangeDecider().decide(
			arbWithRequestedMinArgs1000.intFromUntil(1, 10000),
			AnnotationData(ArgsRangeOptions(requestedMinArgs = 2000))
		)
		expect(argsRange) {
			take.toEqual(1000)
			offset.toEqual(arbWithRequestedMinArgs1000._components.config.seed.toOffset())
		}
	}

	@Test
	fun ordered_requestedMinArgsInConfigTakesPrecedenceOverMaxArgsInAnnotation() {
		val orderedWithRequestedMinArgs1000 = createOrderedWithCustomConfig(
			ordered._components.config.copy { requestedMinArgs = 1000 }
		).ordered
		val argsRange = ProfileBasedArgsRangeDecider().decide(
			orderedWithRequestedMinArgs1000.intFromUntil(1, 10000),
			AnnotationData(ArgsRangeOptions(maxArgs = 999))
		)
		expect(argsRange) {
			take.toEqual(1000)
			offset.toEqual(orderedWithRequestedMinArgs1000._components.config.seed.toOffset())
		}
	}

	@Test
	fun semiOrdered_requestedMinArgsInConfigTakesPrecedenceOverMaxArgsInAnnotation() {
		val orderedWithRequestedMinArgs1000 = createOrderedWithCustomConfig(
			ordered._components.config.copy { requestedMinArgs = 1000 }
		).ordered
		val argsRange = ProfileBasedArgsRangeDecider().decide(
			orderedWithRequestedMinArgs1000.intFromUntil(1, 10000).zip(arb.intPositive()),
			AnnotationData(ArgsRangeOptions(maxArgs = 999))
		)
		expect(argsRange) {
			take.toEqual(1000)
			offset.toEqual(orderedWithRequestedMinArgs1000._components.config.seed.toOffset())
		}
	}

	@Test
	fun arb_requestedMinArgsInConfigTakesPrecedenceOverMaxArgsInAnnotation() {
		val arbWithRequestedMinArgs1000 = createArbWithCustomConfig(
			arb._components.config.copy { requestedMinArgs = 1000 }
		).arb
		val argsRange = ProfileBasedArgsRangeDecider().decide(
			arbWithRequestedMinArgs1000.intFromUntil(1, 100),
			AnnotationData(ArgsRangeOptions(maxArgs = 999))
		)
		expect(argsRange) {
			take.toEqual(1000)
			offset.toEqual(arbWithRequestedMinArgs1000._components.config.seed.toOffset())
		}
	}


	@Test
	fun ordered_requiredMinIgnoredIfOrderedSizeIsSmaller() {
		val argsRange = ProfileBasedArgsRangeDecider().decide(
			ordered.of(1, 2),
			AnnotationData(ArgsRangeOptions(requestedMinArgs = 10))
		)
		expect(argsRange) {
			take.toEqual(2)
			offset.toEqual(ordered._components.config.seed.toOffset())
		}
	}

	@Test
	fun semiOrdered_requiredMinTakenIntoAccountEvenIfSizeOfSemiOrderedIsSmaller() {
		val argsRange = ProfileBasedArgsRangeDecider().decide(
			ordered.of(1, 2).zip(arb.of('A')),
			AnnotationData(ArgsRangeOptions(requestedMinArgs = 10))
		)
		expect(argsRange) {
			take.toEqual(10)
			offset.toEqual(ordered._components.config.seed.toOffset())
		}
	}

	companion object {

		@JvmStatic
		fun testTypeEnvAndGeneratorSize() = Tuple(
			ordered.fromEnum<TestType>(),
			ordered.fromEnum<Env>(),
			ordered.fromRange(1..11),
		)

	}
}
