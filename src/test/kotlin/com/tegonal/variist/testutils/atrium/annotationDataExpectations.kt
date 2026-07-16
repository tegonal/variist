package com.tegonal.variist.testutils.atrium

import ch.tutteli.atrium.api.fluent.en_GB.feature
import ch.tutteli.atrium.creating.Expect
import com.tegonal.variist.providers.AnnotationData

val Expect<AnnotationData>.argsRangeOptions get() = feature(AnnotationData::argsRangeOptions)
val Expect<AnnotationData>.seedOffset get() = feature(AnnotationData::offset)
val Expect<AnnotationData>.extensionData get() = feature(AnnotationData::extensionData)
