package com.tegonal.variist.providers

import com.tegonal.variist.config.VariistConfig
import com.tegonal.variist.generators.ArgsGenerator
import com.tegonal.variist.utils.impl.checkIsPositive
import com.tegonal.variist.utils.impl.failIfNegative

/**
 * Responsible to decide what range of arguments should be generated for a given [ArgsGenerator].
 *
 * @since 2.0.0
 */
interface ArgsRangeDecider {
	/**
	 * Returns an [ArgsRange] based on the given [argsGenerator] (and most likely the underlying [VariistConfig])
	 * and [annotationData] if given.
	 */
	fun decide(argsGenerator: ArgsGenerator<*>, annotationData: AnnotationData? = null): ArgsRange
}

/**
 * Represents a range which an [ArgsGenerator] should generate.
 *
 * @since 2.0.0
 */
data class ArgsRange(val offset: Int, val take: Int) {
	init {
		failIfNegative(offset, "offset")
		checkIsPositive(take, "take")
	}
}
