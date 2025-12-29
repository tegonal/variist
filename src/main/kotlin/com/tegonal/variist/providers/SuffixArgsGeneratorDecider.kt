package com.tegonal.variist.providers

import com.tegonal.variist.generators.ArgsGenerator

/**
 * Allows to define that a certain [ArgsGenerator] shall be combined as last generator.
 *
 *
 * @since 2.0.0
 */
interface SuffixArgsGeneratorDecider {

	/**
	 * Returns the corresponding [ArgsGenerator] which shall be used as last generator in
	 * [GenericArgsGeneratorCombiner.combineFirstWithRest], `null` in case no [ArgsGenerator] shall
	 * be combined additionally.
	 */
	fun computeSuffixArgsGenerator(annotationData: AnnotationData): ArgsGenerator<*>?
}
