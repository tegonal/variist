package com.tegonal.variist.generators.impl

import com.tegonal.variist.generators.ArbArgsGenerator
import com.tegonal.variist.generators.SemiOrderedArgsGenerator
import com.tegonal.variist.generators._core

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class SemiOrderedZipArbArgsGenerator<A1, A2, R>(
	private val semiOrderedArgsGenerator: SemiOrderedArgsGenerator<A1>,
	private val arbArgsGenerator: ArbArgsGenerator<A2>,
	private val transform: (A1, A2) -> R
) : BaseSemiOrderedArgsGenerator<R>(semiOrderedArgsGenerator._core, semiOrderedArgsGenerator.size) {

	override fun generateAfterChecks(offset: Int): Sequence<R> =
		zipForever(
			semiOrderedArgsGenerator.generate(offset),
			// +seedBaseOffset because we want different values if a similar ArbArgsGenerator is used in multiple
			// SemiOrderedArgsGenerators (e.g. combined multiple times) -- for instance:
			// orderef.of(1).zip(arb.of('a','b')).zip(arb.of('a','b'))
			// the two arb should not generate exactly the same sequence otherwise we will see only the values:
			// (1, 'a','a'), (1, 'b','b')
			arbArgsGenerator.generate(offset + seedBaseOffset),
			transform
		)
}
