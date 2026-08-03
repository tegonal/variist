package com.tegonal.variist.generators.impl

import com.tegonal.variist.config.ComponentFactoryContainer
import com.tegonal.variist.config.ComponentFactoryContainerProvider
import com.tegonal.variist.config._components
import com.tegonal.variist.generators.OrderedArgsGenerator

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 3.0.0
 */
class OrderedArgsGeneratorMapper<T, R>(
	private val baseGenerator: OrderedArgsGenerator<T>,
	private val transform: (T) -> R
) : OrderedArgsGenerator<R>, ComponentFactoryContainerProvider {
	override val size: Int get() = baseGenerator.size
	override val componentFactoryContainer: ComponentFactoryContainer get() = baseGenerator._components

	@Deprecated("Use generateOne without seedOffset because seedOffset is ignored. This method mainly exists so that you can abstract over SemiOrderedLikeArgsGenerator")
	override fun generateOne(offset: Int, seedOffset: Int): R {
		val value = baseGenerator.generateOne(offset)
		return transform(value)
	}

	@Deprecated("Use generate without seedOffset because seedOffset is ignored. This method mainly exists so that you can abstract over SemiOrderedLikeArgsGenerator")
	override fun generate(offset: Int, seedOffset: Int): Sequence<R> =
		baseGenerator.generate(offset).map(transform)
}
