package com.tegonal.variist.generators.impl

import com.tegonal.variist.config.ComponentFactoryContainer
import com.tegonal.variist.config.ComponentFactoryContainerProvider
import com.tegonal.variist.generators.OrderedArgsGenerator
import com.tegonal.variist.utils.repeatForever

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 3.0.0
 */
class ConstantOrderedArgsGenerator<T>(
	override val componentFactoryContainer: ComponentFactoryContainer,
	private val constant: T,
) : OrderedArgsGenerator<T>, ComponentFactoryContainerProvider {
	private val sequence = repeatForever(constant)

	override val size: Int = 1

	@Deprecated("Use generateOne without seedOffset because seedOffset is ignored. This method mainly exists so that you can abstract over SemiOrderedLikeArgsGenerator")
	override fun generateOne(offset: Int, seedOffset: Int): T = constant

	@Deprecated("Use generate without seedOffset because seedOffset is ignored. This method mainly exists so that you can abstract over SemiOrderedLikeArgsGenerator")
	override fun generate(offset: Int, seedOffset: Int): Sequence<T> = sequence
}
