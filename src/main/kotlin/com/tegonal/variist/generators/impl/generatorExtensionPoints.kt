package com.tegonal.variist.generators.impl

import com.tegonal.variist.config.ComponentFactoryContainer
import com.tegonal.variist.config.ComponentFactoryContainerProvider
import com.tegonal.variist.config.arb
import com.tegonal.variist.config.ordered
import com.tegonal.variist.config.semiOrdered
import com.tegonal.variist.generators.ArbExtensionPoint
import com.tegonal.variist.generators.OrderedExtensionPoint
import com.tegonal.variist.generators.SemiOrderedExtensionPoint

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class DefaultOrderedExtensionPoint(
	override val componentFactoryContainer: ComponentFactoryContainer
) : OrderedExtensionPoint, ComponentFactoryContainerProvider {
	override val arb: ArbExtensionPoint get() = componentFactoryContainer.arb
	override val ordered: OrderedExtensionPoint get() = componentFactoryContainer.ordered
	override val semiOrdered: SemiOrderedExtensionPoint get() = componentFactoryContainer.semiOrdered
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class DefaultSemiOrderedExtensionPoint(
	override val componentFactoryContainer: ComponentFactoryContainer
) : SemiOrderedExtensionPoint, ComponentFactoryContainerProvider {
	override val arb: ArbExtensionPoint get() = componentFactoryContainer.arb
	override val ordered: OrderedExtensionPoint get() = componentFactoryContainer.ordered
	override val semiOrdered: SemiOrderedExtensionPoint get() = componentFactoryContainer.semiOrdered
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class DefaultArbExtensionPoint(
	override val componentFactoryContainer: ComponentFactoryContainer,
	/**
	 * Will be added to [com.tegonal.variist.config.VariistConfig.seed].
	 *
	 * Is allowed to be negative.
	 */
	override val seedBaseOffset: Int,
) : ArbExtensionPoint, ComponentFactoryContainerProvider {

	override val arb: ArbExtensionPoint
		get() = DefaultArbExtensionPoint(
			componentFactoryContainer,
			// expected that this can overflow in the worst case
			seedBaseOffset + 1
		)
	override val ordered: OrderedExtensionPoint get() = componentFactoryContainer.ordered
	override val semiOrdered: SemiOrderedExtensionPoint get() = componentFactoryContainer.semiOrdered
}
