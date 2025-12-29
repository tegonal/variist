package com.tegonal.variist.generators

/**
 * Returns an [ArbArgsGenerator] which generates [Char]s ranging from
 * [Char.MIN_VALUE] (inclusive) to [Char.MAX_VALUE] (inclusive).
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.char(): ArbArgsGenerator<Char> =
	charFromTo(Char.MIN_VALUE, Char.MAX_VALUE)

/**
 * Returns an [ArbArgsGenerator] which generates [Char]s ranging [from] (inclusive) to [toInclusive].
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.charFromTo(from: Char, toInclusive: Char): ArbArgsGenerator<Char> =
	// safe to use toInclusive.code + 1 as Char.MAX_VALUE.code + 1 is still < Int.MAX_VALUE
	intFromUntil(from.code, toInclusive.code + 1).map(Int::toChar)

