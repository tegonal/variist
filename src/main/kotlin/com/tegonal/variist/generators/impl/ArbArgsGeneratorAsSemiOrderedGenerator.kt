package com.tegonal.variist.generators.impl

import com.tegonal.variist.config.ComponentFactoryContainer
import com.tegonal.variist.config.ComponentFactoryContainerProvider
import com.tegonal.variist.config._components
import com.tegonal.variist.generators.ArbArgsGenerator
import com.tegonal.variist.generators.SemiOrderedArgsGenerator

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.3.0
 */
class ArbArgsGeneratorAsSemiOrderedGenerator<A1>(
	private val arbArgsGenerator: ArbArgsGenerator<A1>,
) : SemiOrderedArgsGenerator<A1>, ComponentFactoryContainerProvider {
	/**
	 * Size = 1 because the only constant part of this SemiOrderedArgsGenerator is that it always yields
	 * values from [arbArgsGenerator].
	 */
	override val size: Int = 1
	override val componentFactoryContainer: ComponentFactoryContainer get() = arbArgsGenerator._components

	override fun generateOne(offset: Int, seedOffset: Int): A1 = arbArgsGenerator.generateOne(seedOffset)
	override fun generate(offset: Int, seedOffset: Int): Sequence<A1> = arbArgsGenerator.generate(seedOffset)
}
