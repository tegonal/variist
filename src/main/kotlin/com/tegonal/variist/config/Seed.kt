package com.tegonal.variist.config

import com.tegonal.variist.generators.SemiOrderedLikeArgsGenerator
import com.tegonal.variist.utils.seedToOffset

/**
 * Represents the Variist seed, typically used for [kotlin.random.Random] and
 * as offset in [SemiOrderedLikeArgsGenerator.generate].
 *
 * Use [value] to retrieve the seed as such (e.g. for [kotlin.random.Random]) and
 * [toOffset] in case it shall be used as random offset.
 *
 * @since 2.0.0
 */
@JvmInline
value class Seed(val value: Int) {
	override fun toString(): String = value.toString()
}

/**
 * Turns this seed into a positive [Int] so that it can be used as offset in an [SemiOrderedLikeArgsGenerator].
 */
fun Seed.toOffset(): Int = seedToOffset(value)
