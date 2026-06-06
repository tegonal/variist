package com.tegonal.variist.generators.impl

import com.tegonal.variist.config.ComponentFactoryContainer
import com.tegonal.variist.config.ComponentFactoryContainerProvider
import com.tegonal.variist.generators.ArbExtensionPoint
import com.tegonal.variist.generators.GeneratorExtensionPoint
import com.tegonal.variist.generators.OrderedExtensionPoint
import com.tegonal.variist.generators.SemiOrderedExtensionPoint

abstract class BaseGeneratorExtensionPoint(
	override val componentFactoryContainer: ComponentFactoryContainer,
	/**
	 * Will be added to [com.tegonal.variist.config.VariistConfig.seed] for generators consisting of an arbitrary part.
	 *
	 * Is allowed to be negative.
	 */
	override val seedBaseOffset: Int,
) : GeneratorExtensionPoint, ComponentFactoryContainerProvider {
	override val arb: ArbExtensionPoint
		get() = DefaultArbExtensionPoint(
			componentFactoryContainer,
			// expected that this can overflow in the worst case
			seedBaseOffset + SEED_OFFSET_STEP
		)
	override val ordered: OrderedExtensionPoint
		get() = DefaultOrderedExtensionPoint(
			componentFactoryContainer,
			// expected that this can overflow in the worst case
			seedBaseOffset + SEED_OFFSET_STEP
		)
	override val semiOrdered: SemiOrderedExtensionPoint
		get() = DefaultSemiOrderedExtensionPoint(
			componentFactoryContainer,
			// expected that this can overflow in the worst case
			seedBaseOffset + SEED_OFFSET_STEP
		)
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class DefaultOrderedExtensionPoint(
	componentFactoryContainer: ComponentFactoryContainer,
	seedBaseOffset: Int,
) : BaseGeneratorExtensionPoint(componentFactoryContainer, seedBaseOffset), OrderedExtensionPoint

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class DefaultSemiOrderedExtensionPoint(
	componentFactoryContainer: ComponentFactoryContainer,
	seedBaseOffset: Int,
) : BaseGeneratorExtensionPoint(componentFactoryContainer, seedBaseOffset), SemiOrderedExtensionPoint

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class DefaultArbExtensionPoint(
	componentFactoryContainer: ComponentFactoryContainer,
	seedBaseOffset: Int,
) : BaseGeneratorExtensionPoint(componentFactoryContainer, seedBaseOffset), ArbExtensionPoint
