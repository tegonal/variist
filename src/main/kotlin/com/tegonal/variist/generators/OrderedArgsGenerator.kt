package com.tegonal.variist.generators

/**
 * Represents an [ArgsGenerator] which provides the method [generate] which generates [T]s always in the same
 * order and a finite number ([size]) before repeating.
 *
 * @since 2.0.0
 */
//TODO 3.2.0 introduce an interface which signifies that the values are statically known.
// There are cases where we materialise an OrderedArgsGenerator because we think it is small enough but what if the
// generation of the values is very expensive because it for instance have to fetch data from a database or from files,
// then it would be better to not materialise it. If we have something like StaticOrderedArgsGenerator and
// DynamicOrderedArgsGenerator then we can distinguish the cases and only materialise in case of a
// StaticOrderedArgsGenerator.
// Also Static/Dynamic is not very intuitive, search for a better name
interface OrderedArgsGenerator<out T> : SemiOrderedLikeArgsGenerator<T> {

	/**
	 * Returns the maximum of values `this` generator is able to generate before it starts over again.
	 */
	override val size: Int

	/**
	 * Returns the value at the given [offset].
	 */
	fun generateOne(offset: Int): T =
		@Suppress("DEPRECATION")
		generateOne(offset, seedOffset = 0)

	/**
	 * Returns an infinite [Sequence] of values starting at [offset] and repeating after reaching [size] of values
	 * where the same values are always generated when called multiple times.
	 */
	fun generate(offset: Int): Sequence<T> =
		@Suppress("DEPRECATION")
		generate(offset, seedOffset = 0)

	@Deprecated("Use generateOne without seedOffset because seedOffset is ignored. This method mainly exists so that you can abstract over SemiOrderedLikeArgsGenerator")
	override fun generateOne(offset: Int, seedOffset: Int): T

	@Deprecated("Use generate without seedOffset because seedOffset is ignored. This method mainly exists so that you can abstract over SemiOrderedLikeArgsGenerator")
	override fun generate(offset: Int, seedOffset: Int): Sequence<T>
}
