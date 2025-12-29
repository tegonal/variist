package com.tegonal.variist.generators

import com.tegonal.variist.config.*
import com.tegonal.variist.config.impl.VariistConfigViaPropertiesLoader

/**
 * Base class for extension points.
 *
 * @since 2.0.0
 */
interface GeneratorExtensionPoint : IsComponentFactoryContainerProvider {
	val arb: ArbExtensionPoint
	val ordered: OrderedExtensionPoint
}

/**
 * Extension point for factories which generate [OrderedArgsGenerator].
 *
 * @since 2.0.0
 */
interface OrderedExtensionPoint : GeneratorExtensionPoint

/**
 * Extension point for factories which generate [ArbArgsGenerator].
 *
 * @since 2.0.0
 */
interface ArbExtensionPoint : GeneratorExtensionPoint {
	/**
	 * Shall be used by [ArbArgsGenerator] as [CoreArbArgsGenerator.seedBaseOffset].
	 */
	val seedBaseOffset: Int
}

private val propertiesBasedComponentFactoryContainer: ComponentFactoryContainer = run {
	val config = VariistConfigViaPropertiesLoader().config
	ComponentFactoryContainer.createBasedOnConfig(config)
}

/**
 * Access to [OrderedArgsGenerator] factory methods like [ordered].[of][OrderedExtensionPoint.of].
 *
 * @since 2.0.0
 */
val ordered: OrderedExtensionPoint = propertiesBasedComponentFactoryContainer.ordered

/**
 * Access to [ArbArgsGenerator] factory methods like [arb].[of][ArbExtensionPoint.of].
 *
 * @since 2.0.0
 */
val arb: ArbExtensionPoint = propertiesBasedComponentFactoryContainer.arb
