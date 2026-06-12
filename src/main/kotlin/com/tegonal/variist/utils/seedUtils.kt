package com.tegonal.variist.utils

import com.tegonal.variist.utils.impl.checkIsPositive

/**
 * Derives two different child seed offsets for the given [seedOffset].
 *
 * @since 2.3.0
 */
@Suppress("NOTHING_TO_INLINE")
inline fun deriveTwoChildSeedOffsets(seedOffset: Int): Pair<Int, Int> =
	deriveChildSeedOffset(seedOffset, 1) to deriveChildSeedOffset(seedOffset, 2)

/**
 * Derives the child seed offset for the child with [childNumber] based on the given [seedOffset].
 *
 * @param childNumber Identifying the child where it is recommended to start at 1 and increment by 1.
 *   In any case, the number needs to be positive.
 *
 * @since 2.3.0
 */
fun deriveChildSeedOffset(seedOffset: Int, childNumber: Int): Int {
	checkIsPositive(childNumber, "childIndex")
	return fmix32(seedOffset + childNumber * SEED_OFFSET_STEP)
}

/**
 * Golden ratio
 */
private const val SEED_OFFSET_STEP = 0x9E3779B9.toInt()

/**
 * MurmurHash3 finaliser mix invented by Austin Appleby - see for instance (the variant with uint32_t):
 * https://chromium.googlesource.com/external/smhasher/%2B/58dd8869da8c95f5c26ec70a6cdd243a7647c8fc/MurmurHash3.cpp
 * the code as such is public domain and is not licensed differently in this project
 *
 * @since 2.3.0
 */
fun fmix32(z: Int): Int {
	var x = z
	x = x xor (x ushr 16)
	x *= 0x85ebca6b.toInt()
	x = x xor (x ushr 13)
	x *= 0xc2b2ae35.toInt()
	x = x xor (x ushr 16)
	return x
}
