package com.tegonal.variist.testutils.atrium

import ch.tutteli.atrium.api.fluent.en_GB.feature
import ch.tutteli.atrium.creating.Expect
import com.tegonal.variist.config.ArgsRangeOptions

val Expect<ArgsRangeOptions>.profile get() = feature(ArgsRangeOptions::profile)
val Expect<ArgsRangeOptions>.requestedMinArgs get() = feature(ArgsRangeOptions::requestedMinArgs)
val Expect<ArgsRangeOptions>.minArgsOverridesSizeLimit get() = feature(ArgsRangeOptions::minArgsOverridesSizeLimit)
val Expect<ArgsRangeOptions>.maxArgs get() = feature(ArgsRangeOptions::maxArgs)
