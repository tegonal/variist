package com.tegonal.variist.providers

import ch.tutteli.atrium.api.fluent.en_GB.notToEqualNull
import ch.tutteli.atrium.api.fluent.en_GB.toContainOnlyEntriesOf
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import com.tegonal.variist.config.ArgsRangeOptions
import com.tegonal.variist.testutils.atrium.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class AnnotationDataMergeTest {

	@Test
	fun merge_with_null_overrides_nothing() {
		val data = filledAnnotationData()
		val other = AnnotationData(null, offset = null, emptyMap())

		val result = data.merge(other)

		expect(result) {
			argsRangeOptions.notToEqualNull {
				profile.toEqual("default")
				requestedMinArgs.toEqual(1)
				minArgsOverridesSizeLimit.toEqual(true)
				maxArgs.toEqual(10)
			}
			seedOffset.toEqual(1)
			extensionData.toContainOnlyEntriesOf(mapOf("key1" to "value1"))
		}
	}

	@Test
	fun merge_with_empty_overrides_nothing() {
		val data = filledAnnotationData()
		val other = AnnotationData(ArgsRangeOptions(), offset = null, emptyMap())

		val result = data.merge(other)

		expect(result) {
			argsRangeOptions.notToEqualNull {
				profile.toEqual("default")
				requestedMinArgs.toEqual(1)
				minArgsOverridesSizeLimit.toEqual(true)
				maxArgs.toEqual(10)
			}
			seedOffset.toEqual(1)
			extensionData.toContainOnlyEntriesOf(mapOf("key1" to "value1"))
		}
	}

	@Test
	fun merge_with_other_having_profile_overrides_this_profile() {
		val data = filledAnnotationData()
		val other = AnnotationData(
			ArgsRangeOptions(profile = "override"),
			offset = null,
			emptyMap()
		)

		val result = data.merge(other)

		expect(result) {
			argsRangeOptions.notToEqualNull {
				profile.toEqual("override")
				requestedMinArgs.toEqual(1)
				minArgsOverridesSizeLimit.toEqual(true)
				maxArgs.toEqual(10)
			}
			seedOffset.toEqual(1)
			extensionData.toContainOnlyEntriesOf(mapOf("key1" to "value1"))
		}
	}

	@Test
	fun merge_with_other_having_requestedMinArgs_overrides_this_requestedMinArgs() {
		val data = filledAnnotationData()
		val other = AnnotationData(
			ArgsRangeOptions(requestedMinArgs = 5),
			offset = null,
			emptyMap()
		)

		val result = data.merge(other)

		expect(result) {
			argsRangeOptions.notToEqualNull {
				profile.toEqual("default")
				requestedMinArgs.toEqual(5)
				minArgsOverridesSizeLimit.toEqual(true)
				maxArgs.toEqual(10)
			}
			seedOffset.toEqual(1)
			extensionData.toContainOnlyEntriesOf(mapOf("key1" to "value1"))
		}
	}

	@Test
	fun merge_with_other_having_maxArgs_overrides_this_maxArgs() {
		val data = filledAnnotationData()
		val other = AnnotationData(
			ArgsRangeOptions(maxArgs = 20),
			offset = null,
			emptyMap()
		)

		val result = data.merge(other)

		expect(result) {
			argsRangeOptions.notToEqualNull {
				profile.toEqual("default")
				requestedMinArgs.toEqual(1)
				minArgsOverridesSizeLimit.toEqual(true)
				maxArgs.toEqual(20)
			}
			seedOffset.toEqual(1)
			extensionData.toContainOnlyEntriesOf(mapOf("key1" to "value1"))
		}
	}

	@ParameterizedTest
	@ValueSource(booleans = [true, false])
	fun merge_with_other_having_minArgsOverridesSizeLimit_overrides_this(value: Boolean) {
		val data = AnnotationData(
			ArgsRangeOptions(minArgsOverridesSizeLimit = !value),
			offset = 1,
			emptyMap()
		)
		val other = AnnotationData(
			ArgsRangeOptions(minArgsOverridesSizeLimit = value),
			offset = null,
			emptyMap()
		)

		val result = data.merge(other)

		expect(result.argsRangeOptions).notToEqualNull {
			minArgsOverridesSizeLimit.toEqual(value)
		}
	}

	@Test
	fun merge_with_other_having_seedOffset_overrides_this() {
		val data = filledAnnotationData()
		val other = AnnotationData(
			argsRangeOptions = null,
			offset = 2,
			emptyMap()
		)

		val result = data.merge(other)

		expect(result) {
			argsRangeOptions.notToEqualNull {
				profile.toEqual("default")
				requestedMinArgs.toEqual(1)
				minArgsOverridesSizeLimit.toEqual(true)
				maxArgs.toEqual(10)
			}
			seedOffset.toEqual(2)
			extensionData.toContainOnlyEntriesOf(mapOf("key1" to "value1"))
		}
	}

	@Test
	fun merge_combines_extensionData_with_other_overriding_existing_keys() {
		val data = AnnotationData(
			filledArgsRangeOptions(),
			offset = 1,
			mapOf("key1" to "value1", "key2" to "value2")
		)
		val other = AnnotationData(
			argsRangeOptions = null,
			offset = null,
			mapOf("key2" to "overridden", "key3" to "value3")
		)

		val result = data.merge(other)

		expect(result) {
			argsRangeOptions.notToEqualNull {
				profile.toEqual("default")
				requestedMinArgs.toEqual(1)
				minArgsOverridesSizeLimit.toEqual(true)
				maxArgs.toEqual(10)
			}
			seedOffset.toEqual(1)
			extensionData.toContainOnlyEntriesOf(mapOf("key1" to "value1", "key2" to "overridden", "key3" to "value3"))
		}
	}

	private fun filledAnnotationData() = AnnotationData(
		filledArgsRangeOptions(),
		offset = 1,
		mapOf("key1" to "value1")
	)

	private fun filledArgsRangeOptions() =
		ArgsRangeOptions(profile = "default", requestedMinArgs = 1, maxArgs = 10, minArgsOverridesSizeLimit = true)

}
