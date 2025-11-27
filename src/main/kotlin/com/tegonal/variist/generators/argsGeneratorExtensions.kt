package com.tegonal.variist.generators

import com.tegonal.variist.config._components
import com.tegonal.variist.config.build
import com.tegonal.variist.providers.AnnotationData
import com.tegonal.variist.providers.ArgsRange
import com.tegonal.variist.providers.ArgsRangeDecider

/**
 * Returns a finite sequence of values based on the [ArgsRange] that the configured [ArgsRangeDecider] will chose.
 *
 * @since 2.0.0
 */
fun <T> SemiOrderedArgsGenerator<T>.generateAndTakeBasedOnDecider(annotationData: AnnotationData? = null): Sequence<T> =
	_components.build<ArgsRangeDecider>().decide(this, annotationData).let(::generateAndTake)

/**
 * Returns a finite sequence of values based on the given [argsRange].
 *
 * @since 2.0.0
 */
fun <T> SemiOrderedArgsGenerator<T>.generateAndTake(argsRange: ArgsRange): Sequence<T> =
	generate(argsRange.offset).take(argsRange.take)

/**
 * Returns a finite sequence of values based on the [ArgsRange] that the configured [ArgsRangeDecider] will chose.
 * decide on.
 *
 * @since 2.0.0
 */
fun <T> ArbArgsGenerator<T>.generateAndTakeBasedOnDecider(annotationData: AnnotationData? = null): Sequence<T> =
	_components.build<ArgsRangeDecider>().decide(this, annotationData).let { this.generateAndTake(it.take) }

/**
 * Returns a finite sequence of values of size [take].
 *
 * @since 2.0.0
 */
fun <T> ArbArgsGenerator<T>.generateAndTake(take: Int): Sequence<T> =
	generate().take(take)

/**
 * Returns one value based on the [ArgsRange.offset] that the configured [ArgsRangeDecider] will chose.
 *
 * @since 2.0.0
 */
fun <T> SemiOrderedArgsGenerator<T>.generateOneBasedOnDecider(annotationData: AnnotationData? = null): T =
	_components.build<ArgsRangeDecider>().decide(this, annotationData).let { generateOne(it.offset) }
