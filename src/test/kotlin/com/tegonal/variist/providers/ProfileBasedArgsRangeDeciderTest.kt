package com.tegonal.variist.providers

import ch.tutteli.atrium.api.fluent.en_GB.group
import ch.tutteli.atrium.api.fluent.en_GB.notToThrow
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.atrium.api.verbs.expectGrouped
import ch.tutteli.kbox.Tuple
import com.tegonal.variist.config.*
import com.tegonal.variist.generators.*
import com.tegonal.variist.generators.impl.DefaultArbExtensionPoint
import com.tegonal.variist.providers.impl.ProfileBasedArgsRangeDecider
import com.tegonal.variist.testutils.BaseTest
import com.tegonal.variist.testutils.atrium.offset
import com.tegonal.variist.testutils.atrium.take
import com.tegonal.variist.testutils.createOrderedWithCustomConfig
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class ProfileBasedArgsRangeDeciderTest : BaseTest() {

	@Test
	fun seed_0_and_skip_max__offset_0() {
		val ordered = createOrderedWithCustomConfig(
			VariistConfig().copy { seed = 0; skip = Int.MAX_VALUE }
		).ordered
		val argsRange = ProfileBasedArgsRangeDecider().decide(ordered.of(1, 2, 3, 4))
		expect(argsRange) {
			offset.toEqual(0)
		}
	}

	@Test
	fun seed_max_skip_not_defined__offset_max() {
		val ordered = createOrderedWithCustomConfig(
			VariistConfig().copy { seed = Int.MAX_VALUE; skip = null }
		).ordered
		val argsRange = ProfileBasedArgsRangeDecider().decide(ordered.of(1, 2, 3, 4))
		expect(argsRange) {
			offset.toEqual(Int.MAX_VALUE)
		}
	}

	@ParameterizedTest
	@ValueSource(ints = [1, 2, 3, 4, 5])
	fun seed_plus_skip_max_overflows__offset_is_seed(seed: Int) {
		val ordered = createOrderedWithCustomConfig(
			VariistConfig().copy { this.seed = seed; this.skip = Int.MAX_VALUE }
		)
		val argsRange = ProfileBasedArgsRangeDecider().decide(ordered.of(1, 2, 3, 4))

		expect(argsRange) {
			offset.toEqual(seed)
		}
	}

	@ParameterizedTest
	@ValueSource(ints = [1, 2, 3, 4, 5])
	fun seed_max_plus_skip_overflows__offset_is_max(skip: Int) {
		val ordered = createOrderedWithCustomConfig(
			VariistConfig().copy { this.seed = Int.MAX_VALUE; this.skip = skip }
		)
		val argsRange = ProfileBasedArgsRangeDecider().decide(ordered.of(1, 2, 3, 4))

		expect(argsRange) {
			offset.toEqual(Int.MAX_VALUE)
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
				VariistConfig().copy { skip = Int.MAX_VALUE }
			)
			ordered.of(1, 2, 3, 4).generateAndTakeBasedOnDecider().count()
		}.notToThrow()
	}

	@ParameterizedTest
	@ValueSource(ints = [Int.MAX_VALUE, Int.MIN_VALUE])
	fun canCopeWithALargeSeed(offset: Int) {
		expect {
			val ordered = createOrderedWithCustomConfig(
				VariistConfig().copy { seed = offset }
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
						Env.Release -> 500
					}
				)
			}
		}
	)
	val configWithProfile500 = ordered._components.config.copy {
		maxArgs = null
		requestedMinArgs = null
		testProfiles = ownTestProfiles.toMutableMap()
		activeEnv = Env.Release.name
	}

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
			testProfiles = ownTestProfiles.toMutableMap()
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

	@ParameterizedTest
	@ArgsSource("requestedMinArgsMaxArgsTestsForProfile500")
	fun requestedMinArgsMaxArgsTests(sample: Sample) {
		val customOrdered = createOrderedWithCustomConfig(configWithProfile500.copy {
			requestedMinArgs = sample.configRequestedMinArgs
			maxArgs = sample.configMaxArgs
		})
		val annotationData = AnnotationData(
			ArgsRangeOptions(
				requestedMinArgs = sample.annotationRequestedMinArgs,
				maxArgs = sample.annotationMaxArgs,
				minArgsOverridesSizeLimit = sample.minArgsOverridesSizeLimit
			)
		)

		expectGrouped {
			group("ordered") {
				val range = ProfileBasedArgsRangeDecider().decide(
					customOrdered.intFromUntil(0, sample.generatorSize),
					annotationData
				)
				expect(range) {
					take.toEqual(sample.expectedOrderedTake)
					offset.toEqual(customOrdered._components.config.seed.toOffset())
				}
			}
			group("semiOrdered") {
				val range = ProfileBasedArgsRangeDecider().decide(
					customOrdered.intFromUntil(0, sample.generatorSize).zip(arb.of('a')),
					annotationData
				)
				expect(range) {
					take.toEqual(sample.expectedSemiOrderedTake)
					offset.toEqual(customOrdered._components.config.seed.toOffset())
				}
			}
			group("arb") {
				val range = ProfileBasedArgsRangeDecider().decide(
					customOrdered.arb.intFromUntil(0, sample.generatorSize),
					annotationData
				)
				expect(range) {
					take.toEqual(sample.expectedArbTake)
					offset.toEqual(customOrdered._components.config.seed.toOffset())
				}
			}
		}
	}

	companion object {

		@JvmStatic
		fun testTypeEnvAndGeneratorSize() = Tuple(
			ordered.fromEnum<TestType>(),
			ordered.fromEnum<Env>(),
			ordered.fromRange(1..11),
		)

		@JvmStatic
		fun requestedMinArgsMaxArgsTestsForProfile500() = run {
			val intBounds1To20 = arb.intBounds(minInclusive = 1, maxInclusive = 20, minSize = 2)
			val intBounds501To520 = arb.intBounds(minInclusive = 501, maxInclusive = 520, minSize = 2)
			semiOrdered.fromArbs(
				arb.intFromUntil(501, 550).map { maxArgs ->
					Sample(
						"profile < config.maxArgs < size => profile",
						configRequestedMinArgs = null,
						configMaxArgs = maxArgs,
						annotationRequestedMinArgs = null,
						annotationMaxArgs = null,
						minArgsOverridesSizeLimit = true,
						generatorSize = 1000,
						expectedOrderedTake = 500,
						expectedSemiOrderedTake = 500,
						expectedArbTake = 500,
					)
				},
				arb.intFromUntil(501, 550).map { maxArgs ->
					Sample(
						"profile < annotation.maxArgs  < size => profile",
						configRequestedMinArgs = null,
						configMaxArgs = null,
						annotationRequestedMinArgs = null,
						annotationMaxArgs = maxArgs,
						minArgsOverridesSizeLimit = true,
						generatorSize = 1000,
						expectedOrderedTake = 500,
						expectedSemiOrderedTake = 500,
						expectedArbTake = 500,
					)
				},
				intBounds1To20.map { (lower, upper) ->
					Sample(
						"config.maxArgs < annotation.maxArgs < profile < size => config",
						configRequestedMinArgs = null,
						configMaxArgs = lower,
						annotationRequestedMinArgs = null,
						annotationMaxArgs = upper,
						minArgsOverridesSizeLimit = true,
						generatorSize = 1000,
						expectedOrderedTake = lower,
						expectedSemiOrderedTake = lower,
						expectedArbTake = lower,
					)
				},
				intBounds1To20.map { (lower, upper) ->
					Sample(
						"config.maxArgs < annotation.requestedMinArgs < profile < size => config",
						configRequestedMinArgs = null,
						configMaxArgs = lower,
						annotationRequestedMinArgs = upper,
						annotationMaxArgs = null,
						minArgsOverridesSizeLimit = true,
						generatorSize = 1000,
						expectedOrderedTake = lower,
						expectedSemiOrderedTake = lower,
						expectedArbTake = lower,
					)
				},

				intBounds1To20.map { (lower, upper) ->
					Sample(
						"size < config.maxArgs < profile => size for (semi)ordered, maxArgs for arb",
						configRequestedMinArgs = null,
						configMaxArgs = upper,
						annotationRequestedMinArgs = null,
						annotationMaxArgs = null,
						minArgsOverridesSizeLimit = true,
						generatorSize = lower,
						expectedOrderedTake = lower,
						expectedSemiOrderedTake = lower,
						expectedArbTake = upper,
					)
				},
				intBounds1To20.map { (lower, upper) ->
					Sample(
						"size < annotation.maxArgs < profile => size for (semi)ordered, maxArgs for arb",
						configRequestedMinArgs = null,
						configMaxArgs = null,
						annotationRequestedMinArgs = null,
						annotationMaxArgs = upper,
						minArgsOverridesSizeLimit = true,
						generatorSize = lower,
						expectedOrderedTake = lower,
						expectedSemiOrderedTake = lower,
						expectedArbTake = upper,
					)
				},

				intBounds501To520.map { (lower, upper) ->
					Sample(
						"profile < config.requestedMinArgs < annotation.requestedMinArgs < size => config takes precedence",
						configRequestedMinArgs = lower,
						configMaxArgs = null,
						annotationRequestedMinArgs = upper,
						annotationMaxArgs = null,
						minArgsOverridesSizeLimit = true,
						generatorSize = upper + 100,
						expectedOrderedTake = lower,
						expectedSemiOrderedTake = lower,
						expectedArbTake = lower,
					)
				},
				intBounds501To520.map { (lower, upper) ->
					Sample(
						"profile < annotation.maxArgs < config.requestedMinArgs < size => config takes precedence",
						configRequestedMinArgs = upper,
						configMaxArgs = null,
						annotationRequestedMinArgs = null,
						annotationMaxArgs = lower,
						minArgsOverridesSizeLimit = true,
						generatorSize = upper + 100,
						expectedOrderedTake = upper,
						expectedSemiOrderedTake = upper,
						expectedArbTake = upper,
					)
				},
				intBounds1To20.map { (lower, upper) ->
					Sample(
						"size < config.requestedMinArgs < profile, minArgsOverrides=false => size for (semi)ordered, profile for arb",
						configRequestedMinArgs = upper,
						configMaxArgs = null,
						annotationRequestedMinArgs = null,
						annotationMaxArgs = null,
						minArgsOverridesSizeLimit = false,
						generatorSize = lower,
						expectedOrderedTake = lower,
						expectedSemiOrderedTake = lower,
						expectedArbTake = 500,
					)
				},
				intBounds1To20.map { (lower, upper) ->
					Sample(
						"size < annotation.requestedMinArgs < profile, minArgsOverrides=false => size for (semi)ordered, profile for arb",
						configRequestedMinArgs = null,
						configMaxArgs = null,
						annotationRequestedMinArgs = upper,
						annotationMaxArgs = null,
						minArgsOverridesSizeLimit = false,
						generatorSize = lower,
						expectedOrderedTake = lower,
						expectedSemiOrderedTake = lower,
						expectedArbTake = 500,
					)
				},
				intBounds1To20.map { (lower, upper) ->
					Sample(
						"size < config.requestedMinArgs < profile, minArgsOverrides=true => requestedMinArgs for (semi)ordered, profile for arb",
						configRequestedMinArgs = upper,
						configMaxArgs = null,
						annotationRequestedMinArgs = null,
						annotationMaxArgs = null,
						minArgsOverridesSizeLimit = true,
						generatorSize = lower,
						expectedOrderedTake = upper,
						expectedSemiOrderedTake = upper,
						expectedArbTake = 500,
					)
				},
				intBounds1To20.map { (lower, upper) ->
					Sample(
						"size < annotation.requestedMinArgs < profile, minArgsOverrides=true => size for (semi)ordered, profile for arb",
						configRequestedMinArgs = null,
						configMaxArgs = null,
						annotationRequestedMinArgs = upper,
						annotationMaxArgs = null,
						minArgsOverridesSizeLimit = true,
						generatorSize = lower,
						expectedOrderedTake = upper,
						expectedSemiOrderedTake = upper,
						expectedArbTake = 500,
					)
				},
				intBounds501To520.map { (lower, upper) ->
					Sample(
						"profile < size < config.requestedMinArgs, minArgsOverrides=false => size for (semi)ordered, requestedMinArgs for arb",
						configRequestedMinArgs = upper,
						configMaxArgs = null,
						annotationRequestedMinArgs = null,
						annotationMaxArgs = null,
						minArgsOverridesSizeLimit = false,
						generatorSize = lower,
						expectedOrderedTake = lower,
						expectedSemiOrderedTake = lower,
						expectedArbTake = upper,
					)
				},
				intBounds501To520.map { (lower, upper) ->
					Sample(
						"profile < size < annotation.requestedMinArgs, minArgsOverrides=false => size for (semi)ordered, requestedMinArgs for arb",
						configRequestedMinArgs = null,
						configMaxArgs = null,
						annotationRequestedMinArgs = upper,
						annotationMaxArgs = null,
						minArgsOverridesSizeLimit = false,
						generatorSize = lower,
						expectedOrderedTake = lower,
						expectedSemiOrderedTake = lower,
						expectedArbTake = upper,
					)
				},
				intBounds501To520.map { (lower, upper) ->
					Sample(
						"profile < size < config.requestedMinArgs, minArgsOverrides=true => requestedMinArgs",
						configRequestedMinArgs = upper,
						configMaxArgs = null,
						annotationRequestedMinArgs = null,
						annotationMaxArgs = null,
						minArgsOverridesSizeLimit = true,
						generatorSize = lower,
						expectedOrderedTake = upper,
						expectedSemiOrderedTake = upper,
						expectedArbTake = upper,
					)
				},
				intBounds501To520.map { (lower, upper) ->
					Sample(
						"profile < size < annotation.requestedMinArgs, minArgsOverrides=true => requestedMinArgs",
						configRequestedMinArgs = null,
						configMaxArgs = null,
						annotationRequestedMinArgs = upper,
						annotationMaxArgs = null,
						minArgsOverridesSizeLimit = true,
						generatorSize = lower,
						expectedOrderedTake = upper,
						expectedSemiOrderedTake = upper,
						expectedArbTake = upper,
					)
				},
				arb.intBounds(minInclusive = 600, maxInclusive = 700, minSize = 10).zipDependent({ arb.intFromUntil(it.first, it.first + 9) }, {(lower, upper), middle ->
					Sample(
						"profile < config.requestedMinArgs < size < annotation.requestedMinArgs, minArgsOverrides=false => config.requestedMinArgs",
						configRequestedMinArgs = lower,
						configMaxArgs = null,
						annotationRequestedMinArgs = upper,
						annotationMaxArgs = null,
						minArgsOverridesSizeLimit = false,
						generatorSize = middle,
						expectedOrderedTake = lower,
						expectedSemiOrderedTake = lower,
						expectedArbTake = lower,
					)
				}),
				arb.intBounds(minInclusive = 600, maxInclusive = 700, minSize = 10).zipDependent({ arb.intFromUntil(it.first+1, it.first + 9) }, {(lower, upper), middle ->
					Sample(
						"profile < annotation.requestedMinArgs < size < config.requestedMinArgs, minArgsOverrides=false => size for semi(ordered), config.requestedMinArgs for arb",
						configRequestedMinArgs = upper,
						configMaxArgs = null,
						annotationRequestedMinArgs = lower,
						annotationMaxArgs = null,
						minArgsOverridesSizeLimit = false,
						generatorSize = middle,
						expectedOrderedTake = middle,
						expectedSemiOrderedTake = middle,
						expectedArbTake = upper,
					)
				}),
			)
		}
	}

	data class Sample(
		val description: String,
		val configRequestedMinArgs: Int?,
		val configMaxArgs: Int?,
		val annotationRequestedMinArgs: Int?,
		val annotationMaxArgs: Int?,
		val minArgsOverridesSizeLimit: Boolean,
		val generatorSize: Int,
		val expectedOrderedTake: Int,
		val expectedSemiOrderedTake: Int,
		val expectedArbTake: Int,
	) {
		override fun toString(): String =
			"$description: c.min=$configRequestedMinArgs, c.max=$configMaxArgs, a.min=$annotationRequestedMinArgs, a.max=$annotationMaxArgs, allow=$minArgsOverridesSizeLimit,s=$generatorSize,o=$expectedOrderedTake,so=$expectedSemiOrderedTake,a=$expectedArbTake"
	}
}
