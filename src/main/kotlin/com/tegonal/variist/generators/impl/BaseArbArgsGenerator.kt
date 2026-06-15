package com.tegonal.variist.generators.impl

import com.tegonal.variist.config.*
import kotlin.random.Random

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
@Suppress("DEPRECATION")
abstract class BaseArbArgsGenerator<T>(
	override val componentFactoryContainer: ComponentFactoryContainer,
) :
//TODO 3.0.0 replace with ArbArgsGenerator<T>
	com.tegonal.variist.generators.CoreArbArgsGenerator<T>, ComponentFactoryContainerProvider {

	protected val config get(): VariistConfig = componentFactoryContainer.config

	protected fun createVariistRandom(seedOffset: Int): Random =
		componentFactoryContainer.createVariistRandom(seedOffset)
}
