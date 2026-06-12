package com.tegonal.variist.generators.impl

import com.tegonal.variist.config.ComponentFactoryContainer
import com.tegonal.variist.config._components
import com.tegonal.variist.generators.ArbArgsGenerator
import com.tegonal.variist.generators.CoreSemiOrderedArgsGenerator

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.3.0
 */
class ArbArgsGeneratorAsSemiOrderedGenerator<A1>(
	private val arbArgsGenerator: ArbArgsGenerator<A1>,
) : CoreSemiOrderedArgsGenerator<A1> {
	/**
	 * Size = 1 because the only constant part of this SemiOrderedArgsGenerator is that it always yields
	 * values from [arbArgsGenerator].
	 */
	override val size: Int = 1
	override val componentFactoryContainer: ComponentFactoryContainer get() = arbArgsGenerator._components

	override fun generateOne(offset: Int): A1 = generateOne(offset, seedOffset = 0)
	override fun generate(offset: Int): Sequence<A1> = generate(offset, seedOffset = 0)
	override fun generateOne(offset: Int, seedOffset: Int): A1 = arbArgsGenerator.generateOne(seedOffset)
	override fun generate(offset: Int, seedOffset: Int): Sequence<A1> = arbArgsGenerator.generate(seedOffset)
}
