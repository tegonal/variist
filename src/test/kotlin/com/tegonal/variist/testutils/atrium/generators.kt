package com.tegonal.variist.testutils.atrium

import ch.tutteli.atrium.api.fluent.en_GB.feature
import ch.tutteli.atrium.creating.Expect
import com.tegonal.variist.generators.SemiOrderedLikeArgsGenerator

val <T, G : SemiOrderedLikeArgsGenerator<T>> Expect<G>.size get() = feature(SemiOrderedLikeArgsGenerator<T>::size)
