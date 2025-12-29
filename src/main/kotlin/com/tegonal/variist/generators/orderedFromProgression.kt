package com.tegonal.variist.generators

import com.tegonal.variist.config._components
import com.tegonal.variist.generators.impl.IntFromToOrderedArgsGenerator
import com.tegonal.variist.generators.impl.LongFromToOrderedArgsGenerator

/**
 * Returns an [OrderedArgsGenerator] generating [Char]s based on the given [progression].
 *
 * @since 2.0.0
 */
fun OrderedExtensionPoint.fromProgression(progression: CharProgression): OrderedArgsGenerator<Char> =
	if (progression.step > 0) {
		IntFromToOrderedArgsGenerator(_components, progression.first.code, progression.last.code, progression.step)
			.map { it.toChar() }
	}
	//TODO 2.1.0 no longer needed once we support minus steps in the iterator
	else fromList(progression.toList())

/**
 * Returns an [OrderedArgsGenerator] generating [Int]s based on the given [progression].
 *
 * @since 2.0.0
 */
fun OrderedExtensionPoint.fromProgression(progression: IntProgression): OrderedArgsGenerator<Int> =
	if (progression.step > 0) {
		IntFromToOrderedArgsGenerator(_components, progression.first, progression.last, progression.step)
	}
	//TODO 2.1.0 no longer needed once we support minus steps in the iterator
	else fromList(progression.toList())

/**
 * Returns an [OrderedArgsGenerator] generating [Long]s based on the given [progression].
 *
 * @since 2.0.0
 */
fun OrderedExtensionPoint.fromProgression(progression: LongProgression): OrderedArgsGenerator<Long> =
	if (progression.step > 0) {
		LongFromToOrderedArgsGenerator(_components, progression.first, progression.last, progression.step)
	}
	//TODO 2.1.0 no longer needed once we support minus steps in the iterator
	else fromList(progression.toList())
