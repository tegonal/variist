package com.tegonal.variist.providers

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
	fun merge_with_empty_overrides_minArgsOverridesSizeLimit() {
		val data = AnnotationData(
			ArgsRangeOptions(profile = "default", requestedMinArgs = 1, maxArgs = 10, minArgsOverridesSizeLimit = true),
			mapOf("key1" to "value1")
		)
		val other = AnnotationData(ArgsRangeOptions(), emptyMap())

		val result = data.merge(other)

		expect(result.argsRangeOptions) {
			profile.toEqual("default")
			requestedMinArgs.toEqual(1)
			minArgsOverridesSizeLimit.toEqual(false)
			maxArgs.toEqual(10)
		}
		expect(result.extensionData).toContainOnlyEntriesOf(data.extensionData)
	}

	@Test
	fun merge_with_other_having_profile_overrides_this_profile() {
		val data = AnnotationData(
			ArgsRangeOptions(profile = "default", requestedMinArgs = 1, maxArgs = 10),
			emptyMap()
		)
		val other = AnnotationData(
			ArgsRangeOptions(profile = "override"),
			emptyMap()
		)

		val result = data.merge(other)

		expect(result.argsRangeOptions) {
			profile.toEqual("override")
			requestedMinArgs.toEqual(1)
			maxArgs.toEqual(10)
		}
		expect(result.extensionData).toEqual(emptyMap())
	}

	@Test
	fun merge_with_other_having_requestedMinArgs_overrides_this_requestedMinArgs() {
		val data = AnnotationData(
			ArgsRangeOptions(requestedMinArgs = 1),
			emptyMap()
		)
		val other = AnnotationData(
			ArgsRangeOptions(requestedMinArgs = 5),
			emptyMap()
		)

		val result = data.merge(other)

		expect(result.argsRangeOptions) {
			requestedMinArgs.toEqual(5)
		}
	}

	@Test
	fun merge_with_other_having_maxArgs_overrides_this_maxArgs() {
		val data = AnnotationData(
			ArgsRangeOptions(maxArgs = 10),
			emptyMap()
		)
		val other = AnnotationData(
			ArgsRangeOptions(maxArgs = 20),
			emptyMap()
		)

		val result = data.merge(other)

		expect(result.argsRangeOptions) {
			maxArgs.toEqual(20)
		}
	}

	@Test
	fun merge_combines_extensionData_with_other_overriding_existing_keys() {
		val data = AnnotationData(
			ArgsRangeOptions(),
			mapOf("key1" to "value1", "key2" to "value2")
		)
		val other = AnnotationData(
			ArgsRangeOptions(),
			mapOf("key2" to "overridden", "key3" to "value3")
		)

		val result = data.merge(other)

		expect(result.extensionData).toEqual(
			mapOf("key1" to "value1", "key2" to "overridden", "key3" to "value3")
		)
	}

	@ParameterizedTest
	@ValueSource(booleans = [true, false])
	fun merge_with_other_having_minArgsOverridesSizeLimit_overrides_this(value: Boolean) {
		val data = AnnotationData(
			ArgsRangeOptions(minArgsOverridesSizeLimit = !value),
			emptyMap()
		)
		val other = AnnotationData(
			ArgsRangeOptions(minArgsOverridesSizeLimit = value),
			emptyMap()
		)

		val result = data.merge(other)

		expect(result.argsRangeOptions) {
			minArgsOverridesSizeLimit.toEqual(value)
		}
	}
}
