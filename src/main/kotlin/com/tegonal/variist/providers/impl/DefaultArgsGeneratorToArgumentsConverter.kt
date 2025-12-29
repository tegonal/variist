package com.tegonal.variist.providers.impl

import com.tegonal.variist.config._components
import com.tegonal.variist.config.build
import com.tegonal.variist.generators.ArbArgsGenerator
import com.tegonal.variist.generators.ArgsGenerator
import com.tegonal.variist.generators.SemiOrderedArgsGenerator
import com.tegonal.variist.generators.impl.throwUnsupportedArgsGenerator
import com.tegonal.variist.providers.AnnotationData
import com.tegonal.variist.providers.ArgsGeneratorToArgumentsConverter
import com.tegonal.variist.providers.ArgsRange
import com.tegonal.variist.providers.ArgsRangeDecider
import org.junit.jupiter.params.provider.Arguments

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class DefaultArgsGeneratorToArgumentsConverter : ArgsGeneratorToArgumentsConverter {

	override fun toArguments(
		argsSourceId: String,
		annotationData: AnnotationData,
		argsGenerator: ArgsGenerator<List<*>>,
	): Sequence<Arguments> {
		val argsRange = decideArgsRange(annotationData, argsGenerator)
		val sequenceOfList = when (argsGenerator) {
			is ArbArgsGenerator<List<*>> -> argsGenerator.generate().take(argsRange.take)
			is SemiOrderedArgsGenerator<List<*>> -> argsGenerator.generate(argsRange.offset).take(argsRange.take)
			else -> throwUnsupportedArgsGenerator(argsGenerator)
		}
		return sequenceOfList.map { generatorResults ->
			when (generatorResults.size) {
				0 -> error(
					"The ${ArgsGenerator::class.simpleName}(s) returned by $argsSourceId do not generate any value"
				)

				1 -> generatorResults.first().let { result ->
					// we don't split TupleLike (in contrast to GenericToArgsGeneratorConverter)
					tupleToList(result)?.let(::flattenTuplesAndPutIntoArguments)
						?: (result as? Arguments)
						?: Arguments.of(result)
				}

				else -> flattenTuplesAndPutIntoArguments(generatorResults)
			}
		}
	}

	private fun flattenTuplesAndPutIntoArguments(generatorResults: List<*>): Arguments {
		val flattenedArgs = flattenTuples(generatorResults)
		return Arguments.of(*flattenedArgs.toTypedArray())
	}

	//TODO 2.1.0 benchmark if we should use a mutable structure instead of flatMap, I guess in most cases we don't have
	// nesting at all, and sometimes we might have 1 or 2 levels
	private fun flattenTuples(generatorResults: List<*>): List<*> =
		generatorResults.flatMap { result ->
			tupleToList(result)?.let(::flattenTuples)
				?: when (result) {
					is Arguments -> result.get().toList()
					// assuming raw values
					else -> listOf(result)
				}
		}

	private fun decideArgsRange(
		annotationData: AnnotationData,
		argsGenerator: ArgsGenerator<*>,
	): ArgsRange {
		val componentFactoryContainer = argsGenerator._components
		val argsRangeDecider = componentFactoryContainer.build<ArgsRangeDecider>()
		return argsRangeDecider.decide(argsGenerator, annotationData)
	}
}
