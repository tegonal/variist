package com.tegonal.variist.generators

/**
 * Returns a [SemiOrderedArgsGenerator] based on the given [ArbArgsGenerator]s where the fixed part of it is the number
 * of [generators], which one is next to contribute a value, the values as such are arbitrary in respect
 * to the given generators.
 *
 * @return an [SemiOrderedArgsGenerator] based on the given [generators] where they are used in round-robin fashion
 *   to generate values.
 *
 * @since 2.0.0
 */
fun <T> OrderedExtensionPoint.fromArbs(vararg generators: ArbArgsGenerator<T>): SemiOrderedArgsGenerator<T> =
	intFromUntil(0, generators.size).zipDependent({ useCase ->
		generators[useCase]
	}) { _, arb -> arb }

