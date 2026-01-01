package com.tegonal.variist.utils

import ch.tutteli.atrium.api.fluent.en_GB.group
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.atrium.api.verbs.expectGrouped
import com.tegonal.variist.providers.ArgsSource
import com.tegonal.variist.testutils.BaseTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest

class SeedToOffsetTest : BaseTest() {

	@Test
	fun check_transitions() {
		expectGrouped {
			group("-1 == MAX_VALUE") {
				expect(seedToOffset(-1)).toEqual(seedToOffset(Int.MAX_VALUE))
			}
			group("0 == MIN_VALUE") {
				expect(seedToOffset(0)).toEqual(seedToOffset(Int.MIN_VALUE))
			}
		}
	}

	@ParameterizedTest
	@ArgsSource("arbIntPositive")
	fun check_continuity(i: Int) {
		// note for the reader, this is an implementation detail and we could as well change this
		expectGrouped {
			group("-1 - $i == MAX_VALUE - $") {
				expect(seedToOffset(-1 - i)).toEqual(seedToOffset(Int.MAX_VALUE - i))
			}
			group("-1 + $i == MAX_VALUE + $i") {
				expect(seedToOffset(-1 + i)).toEqual(seedToOffset(Int.MAX_VALUE + i))
			}
		}
	}
}
