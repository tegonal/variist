package com.tegonal.variist.generators.impl

import com.tegonal.variist.config.ComponentFactoryContainer
import com.tegonal.variist.generators.OrderedArgsGenerator
import com.tegonal.variist.utils.BigInt

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 3.0.0
 */
abstract class BaseOrderedArgsGenerator<T>(
	componentFactoryContainer: ComponentFactoryContainer,
	size: Int,
) : BaseSemiOrderedLikeArgsGenerator<T>(componentFactoryContainer, size),
	OrderedArgsGenerator<T> {

	constructor(
		componentFactoryContainer: ComponentFactoryContainer,
		size: Long
	) : this(componentFactoryContainer, sizeLongToInt(size))

	constructor(
		componentFactoryContainer: ComponentFactoryContainer,
		size: BigInt
	) : this(componentFactoryContainer, sizeBigIntToInt(size))


	final override fun generateOneAfterChecks(offset: Int, seedOffset: Int): T =
		generateOneAfterChecks(offset)

	final override fun generateAfterChecks(offset: Int, seedOffset: Int): Sequence<T> =
		generateAfterChecks(offset)


	open fun generateOneAfterChecks(offset: Int): T =
		run {
			// we don't use first() as it checks hasNext() in addition and we know that it has to have one as the
			// Sequence needs to be infinite, according to the ArgsGenerator contract
			generateAfterChecks(offset).iterator().next()
		}

	abstract fun generateAfterChecks(offset: Int): Sequence<T>

}

