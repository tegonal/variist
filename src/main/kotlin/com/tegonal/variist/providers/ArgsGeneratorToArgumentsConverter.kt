package com.tegonal.variist.providers

import ch.tutteli.kbox.Tuple4
import com.tegonal.variist.generators.ArgsGenerator
import org.junit.jupiter.params.provider.Arguments

/**
 * Responsible to generate values for a given [ArgsGenerator] and to transform them to [Arguments].
 *
 * @since 2.0.0
 */
interface ArgsGeneratorToArgumentsConverter {

	/**
	 * Uses the given [argsGenerator] to generate values and turning them into [Arguments].
	 *
	 * It typically uses an [ArgsRangeDecider] to decide what [ArgsRange] to generate.
	 *
	 * The [argsGenerator] generates [List]s where the elements represent individual generation results of combined
	 * [ArgsGenerator]. They correspond to the arguments when only raw values (non-[Arguments] and non-tuples)
	 * and are generated. [Arguments] and tuples ([Pair], [Triple], [Tuple4] etc.) are flattened.
	 *
	 * Some examples, if [argsGenerator] generates:
	 * - lists with two [Pair]s, then the resulting Sequence contains [Arguments] with 4 values.
	 * - lists with one [Triple] and one [Arguments] with 1 element,
	 *   then the resulting Sequence contains [Arguments] with 4 values.
	 * - lists with one [Arguments] with 2 and one [Arguments] with 4 elements,
	 *   then the resulting Sequence contains [Arguments] with 6 values.
	 *
	 * @param argsSourceId an identifier (e.q. fully qualified name) of the method which provided the [ArgsGenerator]s.
	 * @param annotationData
	 * @param argsGenerator A generator which generates [List]s where the elements represent individual generation
	 *   results of combined [ArgsGenerator]. See description how they are turned into [Arguments].
	 */
	fun toArguments(
		argsSourceId: String,
		annotationData: AnnotationData,
		argsGenerator: ArgsGenerator<List<*>>,
	): Sequence<Arguments>
}

