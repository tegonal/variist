package com.tegonal.variist.generators

import com.tegonal.variist.config._components
import com.tegonal.variist.config.ordered
import com.tegonal.variist.generators.impl.ArbArgsGeneratorAsSemiOrderedGenerator

/**
 * Returns a [SemiOrderedArgsGenerator] based on the given [ArbArgsGenerator]s where the fixed part of it is the number
 * of [generators], the values as such are arbitrary in respect to the given generators.
 *
 * @return a [SemiOrderedArgsGenerator] based on the given [generators] where they are used in round-robin fashion
 *   to generate values.
 *
 * @since 2.0.0
 */
fun <T> SemiOrderedExtensionPoint.fromArbs(vararg generators: ArbArgsGenerator<T>): SemiOrderedArgsGenerator<T> =
	when (val size = generators.size) {
		1 -> ArbArgsGeneratorAsSemiOrderedGenerator(generators[0])
		else -> _components.ordered.intFromUntil(0, size).zipDependent({ useCase ->
			generators[useCase]
		}) { _, arb -> arb }
	}
