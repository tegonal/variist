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
		is ArbArgsGenerator<*> -> {
			val initial = firstArgsGenerator.map { mutableListOf(it) }
			restMaybeArgGenerators.foldIndexed(initial) { index, generator, next ->
				when (next) {
					is ArbArgsGenerator<*> -> generator.zip(next) { list, aNext ->
						list.also { it.add(aNext) }
					}

					is SemiOrderedArgsGenerator<*> -> {
						val argsGenerator = ArgsGenerator::class.simpleName
						val arbArgsGenerator = ArbArgsGenerator::class.simpleName
						error("Wrong ordering of ${argsGenerator}s, first $argsGenerator was an $arbArgsGenerator which means only $arbArgsGenerator are allowed but found $next at position ${index + 1}. Make sure it comes first (or any other (Semi)OrderedArgsGenerators.")
					}
					is ArgsGenerator<*> -> throwUnsupportedArgsGenerator(next)
					else -> throwDontKnowHowToConvertToArgsGenerator(next)
				}
			}
		}

		is SemiOrderedArgsGenerator<*> -> {
			val first = firstArgsGenerator.map { mutableListOf(it) }
			restMaybeArgGenerators.fold(first) { generator, next ->
				when (next) {
					is ArgsGenerator<*> -> generator.combine(next) { list, aNext ->
						list.also { it.add(aNext) }
					}

					else -> throwDontKnowHowToConvertToArgsGenerator(next)
				}
			}
		}

		else -> throwUnsupportedArgsGenerator(firstArgsGenerator)
	}
}
