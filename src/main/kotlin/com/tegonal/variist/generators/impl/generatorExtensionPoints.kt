package com.tegonal.variist.generators.impl

import com.tegonal.variist.config.ComponentFactoryContainer
import com.tegonal.variist.config.ComponentFactoryContainerProvider
import com.tegonal.variist.generators.ArbExtensionPoint
import com.tegonal.variist.generators.GeneratorExtensionPoint
import com.tegonal.variist.generators.OrderedExtensionPoint
import com.tegonal.variist.generators.SemiOrderedExtensionPoint

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.3.0
 */
abstract class BaseGeneratorExtensionPoint(
	override val componentFactoryContainer: ComponentFactoryContainer,
) : GeneratorExtensionPoint, ComponentFactoryContainerProvider {

	override val arb: ArbExtensionPoint
		get() = DefaultArbExtensionPoint(componentFactoryContainer)

	override val ordered: OrderedExtensionPoint
		get() = DefaultOrderedExtensionPoint(componentFactoryContainer)

	override val semiOrdered: SemiOrderedExtensionPoint
		get() = DefaultSemiOrderedExtensionPoint(componentFactoryContainer)
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class DefaultOrderedExtensionPoint(
	componentFactoryContainer: ComponentFactoryContainer,
) : BaseGeneratorExtensionPoint(componentFactoryContainer), OrderedExtensionPoint

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class DefaultSemiOrderedExtensionPoint(
	componentFactoryContainer: ComponentFactoryContainer,
) : BaseGeneratorExtensionPoint(componentFactoryContainer), SemiOrderedExtensionPoint

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class DefaultArbExtensionPoint(
	componentFactoryContainer: ComponentFactoryContainer,
) : BaseGeneratorExtensionPoint(componentFactoryContainer), ArbExtensionPoint
