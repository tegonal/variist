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
	val semiOrdered: SemiOrderedExtensionPoint
}

/**
 * Extension point for factories which generate [OrderedArgsGenerator].
 *
 * @since 2.0.0
 */
interface OrderedExtensionPoint : GeneratorExtensionPoint

/**
 * Extension point for factories which generate [SemiOrderedArgsGenerator].
 *
 * @since 2.0.0
 */
interface SemiOrderedExtensionPoint : GeneratorExtensionPoint


/**
 * Extension point for factories which generate [ArbArgsGenerator].
 *
 * @since 2.0.0
 */
interface ArbExtensionPoint : GeneratorExtensionPoint {
	/**
	 * Is ignored since 2.3.0 and has a default of 0, will be removed with 3.0.0
	 */
	@Deprecated(
		"will be removed with 3.0.0, instead make sure you use the passed seedOffset and use deriveChildSeed when passing it to sub ArbArgsGnerators",
	)
	val seedBaseOffset: Int get() = 0
}

private val propertiesBasedComponentFactoryContainer: ComponentFactoryContainer = run {
	val config = VariistConfigViaPropertiesLoader().config
	ComponentFactoryContainer.createBasedOnConfig(config)
}

/**
 * Access to [OrderedArgsGenerator]-factory methods like [ordered].[of][OrderedExtensionPoint.of].
 *
 * @since 2.0.0
 */
val ordered: OrderedExtensionPoint = propertiesBasedComponentFactoryContainer.ordered

/**
 * Access to [SemiOrderedArgsGenerator]-factory methods like [semiOrdered].[fromArbs][SemiOrderedExtensionPoint.fromArbs].
 *
 * @since 2.0.0
 */
val semiOrdered: SemiOrderedExtensionPoint = propertiesBasedComponentFactoryContainer.semiOrdered

/**
 * Access to [ArbArgsGenerator]-factory methods like [arb].[of][ArbExtensionPoint.of].
 *
 * @since 2.0.0
 */
val arb: ArbExtensionPoint = propertiesBasedComponentFactoryContainer.arb
