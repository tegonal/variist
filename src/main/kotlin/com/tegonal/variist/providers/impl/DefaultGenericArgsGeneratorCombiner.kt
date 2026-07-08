package com.tegonal.variist.providers.impl

import com.tegonal.variist.generators.*
import com.tegonal.variist.generators.impl.throwDontKnowHowToConvertToArgsGenerator
import com.tegonal.variist.generators.impl.throwUnsupportedArgsGenerator
import com.tegonal.variist.providers.GenericArgsGeneratorCombiner

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class DefaultGenericArgsGeneratorCombiner : GenericArgsGeneratorCombiner {

	override fun combineFirstWithRest(
		firstArgsGenerator: ArgsGenerator<*>,
		restMaybeArgGenerators: List<*>
	): ArgsGenerator<List<*>> = when (firstArgsGenerator) {
		is ArbArgsGenerator<*> ->
			combineWithArbArgGenerator(firstArgsGenerator, restMaybeArgGenerators)

		is SemiOrderedLikeArgsGenerator<*> ->
			combineWithSemiOrderedGenerator(firstArgsGenerator.map { mutableListOf(it) }, restMaybeArgGenerators)

		else -> throwUnsupportedArgsGenerator(firstArgsGenerator)
	}

	private fun combineWithArbArgGenerator(
		firstArgsGenerator: ArbArgsGenerator<*>,
		restMaybeArgGenerators: List<*>
	): ArgsGenerator<List<Any?>> {
		var acc = firstArgsGenerator.map { mutableListOf(it) }
		for (i in restMaybeArgGenerators.indices) {
			val next = restMaybeArgGenerators[i]
			acc = when (next) {
				is ArbArgsGenerator<*> -> acc.zip(next) { list, aNext ->
					list.also { it.add(aNext) }
				}

				is SemiOrderedLikeArgsGenerator<*> -> {
					// return early, i.e. exit the for loop and return the result of combineWithSemiOrderedGenerator
					return combineWithSemiOrderedGenerator(acc.toSemiOrdered(), restMaybeArgGenerators.drop(i))
				}

				else -> throwDontKnowHowToConvertToArgsGenerator(next)
			}
		}
		return acc
	}

	private fun combineWithSemiOrderedGenerator(
		first: SemiOrderedLikeArgsGenerator<MutableList<Any?>>,
		restMaybeArgGenerators: List<*>
	): SemiOrderedLikeArgsGenerator<List<Any?>> = restMaybeArgGenerators.fold(first) { generator, next ->
		when (next) {
			is ArgsGenerator<*> -> generator.combine(next) { list, aNext ->
				list.also { it.add(aNext) }
			}

			else -> throwDontKnowHowToConvertToArgsGenerator(next)
		}
	}
}
