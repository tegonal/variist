package com.tegonal.variist.generators.impl

import com.tegonal.variist.generators.*

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class SemiOrderedFlatZipArbArgsGenerator<A1, A2, R>(
	private val semiOrderedArgsGenerator: SemiOrderedArgsGenerator<A1>,
	private val otherFactory: ArbExtensionPoint.(A1) -> ArbArgsGenerator<A2>,
	private val amount: Int,
	private val transform: (A1, A2) -> R
) : BaseSemiOrderedArgsGenerator<R>(semiOrderedArgsGenerator._core, semiOrderedArgsGenerator.size * amount) {

	override fun generateOneAfterChecks(offset: Int): R {
		val orderedOffset = offset / amount
		val transformationOffset = offset % amount
		val a1 = semiOrderedArgsGenerator.generateOne(orderedOffset)
		// Note, no need to do generate(offset + seedBaseOffset) here because _core.arb already passes the
		// seedBaseOffset during the creation of ArbArgsGenerator
		val a2 = _core.arb.otherFactory(a1).generate(seedOffset = offset)
			.drop(transformationOffset).first()
		return transform(a1, a2)
	}

	@OptIn(InternalDangerousApi::class)
	override fun generateAfterChecks(offset: Int): Sequence<R> {
		val orderedOffset = offset / amount
		val transformationOffset = offset % amount
		return semiOrderedArgsGenerator.flatMapIndexedInternal { index, a1: A1 ->
			_core.arb.otherFactory(a1).generate(seedOffset = offset + index)
				.take(amount).drop(transformationOffset).map { a2 ->
					transform(a1, a2)
				}
		}.generate(orderedOffset)
	}
}

