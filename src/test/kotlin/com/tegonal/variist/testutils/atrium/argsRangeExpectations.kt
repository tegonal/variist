package com.tegonal.variist.testutils.atrium

import ch.tutteli.atrium.api.fluent.en_GB.feature
import ch.tutteli.atrium.creating.Expect
import com.tegonal.variist.providers.ArgsRange

val Expect<ArgsRange>.offset get() = feature(ArgsRange::offset)
val Expect<ArgsRange>.take get() = feature(ArgsRange::take)
