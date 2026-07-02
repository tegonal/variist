package com.tegonal.variist.generators.impl

import com.tegonal.variist.config.ComponentFactoryContainer
import com.tegonal.variist.generators.SemiOrderedArgsGenerator

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
abstract class BaseSemiOrderedArgsGenerator<T>(
	componentFactoryContainer: ComponentFactoryContainer,
	size: Int,
) : BaseSemiOrderedLikeArgsGenerator<T>(componentFactoryContainer, size),
	SemiOrderedArgsGenerator<T> {

	constructor(
		componentFactoryContainer: ComponentFactoryContainer,
		size: Long
	) : this(componentFactoryContainer, sizeLongToInt(size))
}
