package com.tegonal.variist.generators.impl

import com.tegonal.variist.config._components
import com.tegonal.variist.config.arb
import com.tegonal.variist.generators.ArbArgsGenerator
import com.tegonal.variist.generators.ArbExtensionPoint
import com.tegonal.variist.generators.SemiOrderedLikeArgsGenerator

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class SemiOrderedFlatZipArbArgsGenerator<A1, A2, R>(
	private val baseGenerator: SemiOrderedLikeArgsGenerator<A1>,
	private val otherFactory: ArbExtensionPoint.(A1) -> ArbArgsGenerator<A2>,
	private val amount: Int,
	private val transform: (A1, A2) -> R
) : BaseSemiOrderedArgsGenerator<R>(baseGenerator._components, baseGenerator.size * amount) {

	override fun generateOneAfterChecks(offset: Int, seedOffset: Int): R {
		val orderedOffset = offset / amount
		val transformationOffset = offset % amount
		val a1 = baseGenerator.generateOne(orderedOffset, seedOffset)
		// Note, no need to do generate(offset + seedBaseOffset) here because semiOrderedArgsGenerator.arb already
		// passes the seedBaseOffset of this generator during the creation of ArbArgsGenerator
		val a2 = _components.arb.otherFactory(a1).generate(seedOffset = seedOffset)
			.drop(transformationOffset).first()
		return transform(a1, a2)
	}

	@OptIn(InternalDangerousApi::class)
	override fun generateAfterChecks(offset: Int, seedOffset: Int): Sequence<R> {
		val orderedOffset = offset / amount
		val transformationOffset = offset % amount
		return baseGenerator.flatMapIndexedInternal { index, a1, innerSeedOffset ->
			// Note, no need to do generate(offset + seedBaseOffset) here because semiOrderedArgsGenerator.arb
			// passes the seedBaseOffset of this generator during the creation of ArbArgsGenerator
			_components.arb.otherFactory(a1).generate(seedOffset = innerSeedOffset + index)
				.take(amount).drop(transformationOffset).map { a2 ->
					transform(a1, a2)
				}
		}.generate(orderedOffset, seedOffset)
	}
}

