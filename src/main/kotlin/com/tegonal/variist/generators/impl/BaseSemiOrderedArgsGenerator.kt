package com.tegonal.variist.generators.impl

import com.tegonal.variist.config.ComponentFactoryContainer
import com.tegonal.variist.config._components
import com.tegonal.variist.generators.CoreSemiOrderedArgsGenerator
import com.tegonal.variist.generators.OrderedArgsGenerator
import com.tegonal.variist.utils.BigInt
import com.tegonal.variist.utils.impl.checkIsPositive

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
abstract class BaseSemiOrderedArgsGenerator<T>(
	final override val componentFactoryContainer: ComponentFactoryContainer,
	final override val size: Int,
) : CoreSemiOrderedArgsGenerator<T> {

	constructor(generator: CoreSemiOrderedArgsGenerator<*>, size: Int) : this(generator._components, size)
	constructor(generator: CoreSemiOrderedArgsGenerator<*>, size: Long) : this(generator._components, size)
	constructor(generator: CoreSemiOrderedArgsGenerator<*>, size: BigInt) : this(generator._components, size)

	constructor(
		componentFactoryContainer: ComponentFactoryContainer,
		size: Long
	) : this(
		componentFactoryContainer,
		size.toInt().also {
			check(it.toLong() == size) {
				// toInt() overflowed
				"${OrderedArgsGenerator::class.simpleName}.${OrderedArgsGenerator<*>::size.name} only supports Int, the given size ($size) is bigger"
			}
		},
	)

	constructor(componentFactoryContainer: ComponentFactoryContainer, size: BigInt) : this(
		componentFactoryContainer,
		size.toInt().also {
			check(size.bitLength() <= 31) {
				"${OrderedArgsGenerator::class.simpleName}.${OrderedArgsGenerator<*>::size.name} only supports Int, the given size ($size) is bigger"
			}
		}
	)

	init {
		checkIsPositive(size, "size")
	}

	final override fun generateOne(offset: Int): T = generateOne(offset, seedOffset = 0)
	final override fun generate(offset: Int): Sequence<T> = generate(offset, seedOffset = 0)

	final override fun generateOne(offset: Int, seedOffset: Int): T {
		checkOffset(offset)
		return generateOneAfterChecks(offset, seedOffset)
	}

	final override fun generate(offset: Int, seedOffset: Int): Sequence<T> {
		checkOffset(offset)
		return generateAfterChecks(offset, seedOffset)
	}

	private fun checkOffset(offset: Int) {
		check(offset >= 0) {
			"negative offsets are not supported, given $offset"
		}
	}

	open fun generateOneAfterChecks(offset: Int, seedOffset: Int): T = run {
		// we don't use first() as it checks hasNext() in addition and we know that it has to have one as the
		// Sequence needs to be infinite according to the ArgsGenerator contract
		generateAfterChecks(offset, seedOffset).iterator().next()
	}

	abstract fun generateAfterChecks(offset: Int, seedOffset: Int): Sequence<T>
}
