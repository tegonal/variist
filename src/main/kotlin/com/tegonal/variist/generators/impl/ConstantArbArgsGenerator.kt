package com.tegonal.variist.generators.impl

import com.tegonal.variist.config.ComponentFactoryContainer
import com.tegonal.variist.utils.repeatForever

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class ConstantArbArgsGenerator<T>(
	componentFactoryContainer: ComponentFactoryContainer,
	private val constant: T,
) : BaseArbArgsGenerator<T>(componentFactoryContainer) {
	val sequence = repeatForever(constant)

	override fun generateOne(seedOffset: Int): T = constant
	override fun generate(seedOffset: Int): Sequence<T> = sequence
}
