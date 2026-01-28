package com.tegonal.variist.generators

import ch.tutteli.kbox.letIf
import com.tegonal.variist.config._components
import com.tegonal.variist.config.build
import com.tegonal.variist.config.config
import com.tegonal.variist.providers.AnnotationData
import com.tegonal.variist.providers.ArgsRange
import com.tegonal.variist.providers.ArgsRangeDecider
import com.tegonal.variist.config.VariistConfig

/**
 * Returns a finite [Sequence] of values based on [VariistConfig.skip] and the [ArgsRange] that the configured
 * [ArgsRangeDecider] will chose taking the given [annotationData] into account.
 *
 * @since 2.0.0
 */
fun <T> SemiOrderedArgsGenerator<T>.generateAndTakeBasedOnDecider(annotationData: AnnotationData? = null): Sequence<T> =
	decide(annotationData).let(::generateAndTake)

/**
 * Returns one value based on the [ArgsRange.offset] that the configured [ArgsRangeDecider] will chose.
 *
 * @since 2.0.0
 */
fun <T> SemiOrderedArgsGenerator<T>.generateOneBasedOnDecider(annotationData: AnnotationData? = null): T =
	decide(annotationData).offset.let(::generateOne)

/**
 * Returns a finite [Sequence] of values based [VariistConfig.skip] and thee [ArgsRange] that the configured
 * [ArgsRangeDecider] will chose taking the given [annotationData] into account.
 *
 * @since 2.0.0
 */
fun <T> ArbArgsGenerator<T>.generateAndTakeBasedOnDecider(annotationData: AnnotationData? = null): Sequence<T> =
	decide(annotationData).take.let(this::generateAndTake)

private fun <T> ArgsGenerator<T>.decide(annotationData: AnnotationData?): ArgsRange =
	_components.build<ArgsRangeDecider>().decide(this, annotationData)

/**
 * Returns a finite [Sequence] of values based [VariistConfig.skip] and the given [argsRange].
 *
 * @since 2.0.0
 */
fun <T> SemiOrderedArgsGenerator<T>.generateAndTake(argsRange: ArgsRange): Sequence<T> =
	skipByConfigAndTake(argsRange.take) { generate(argsRange.offset) }

/**
 * Returns a finite [Sequence] of values of size [take] respecting the configured [VariistConfig.skip].
 *
 * @since 2.0.0
 */
fun <T> ArbArgsGenerator<T>.generateAndTake(take: Int): Sequence<T> =
	skipByConfigAndTake(take) { generate() }

private inline fun <T> ArgsGenerator<T>.skipByConfigAndTake(take: Int, generate: () -> Sequence<T>): Sequence<T> =
	generate().skipAndTake(_components.config.skip, take)

private fun <T> Sequence<T>.skipAndTake(skip: Int?, take: Int): Sequence<T> =
	this.letIf(skip != null) { it.drop(skip!!) }
		.take(take)
