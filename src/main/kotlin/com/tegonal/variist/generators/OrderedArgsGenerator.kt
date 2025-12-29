package com.tegonal.variist.generators

/**
 * Represents an [ArgsGenerator] which provides the method [generate] which generates [T]s always in the same
 * order and a finite number ([size]) before repeating.
 *
 * @since 2.0.0
 */
//TODO 2.1.0 introduce an interface which signifies that the values are statically known.
// There are cases where we materialise an OrderedArgsGenerator because we think it is small enough but what if the
// generation of the values is very expensive, then it would be better to not materialise it. If we have something like
// StaticOrderedArgsGenerator and DynamicOrderedArgsGenerator then we can distinguish.
// Once we have it, we can check if it is worth materialising for combinators like map
interface OrderedArgsGenerator<out T> : SemiOrderedArgsGenerator<T> {

	/**
	 * Returns the maximum of values `this` generator is able to generate before it starts over again.
	 *
	 * @since 2.0.0
	 */
	override val size: Int

	/**
	 * Returns an infinite [Sequence] of values starting at [offset] and repeating after reaching [size] of values
	 * where always the same values are generated when called multiple times.
	 *
	 * @since 2.0.0
	 */
	override fun generate(offset: Int): Sequence<T>
}
