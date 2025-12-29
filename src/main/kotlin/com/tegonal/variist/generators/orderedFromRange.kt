package com.tegonal.variist.generators

/**
 * Returns an [OrderedArgsGenerator] generating [Char]s based on the given [args].
 *
 * @since 2.0.0
 */
fun OrderedExtensionPoint.fromRange(args: CharRange): OrderedArgsGenerator<Char> =
	fromProgression(args)

/**
 * Returns an [OrderedArgsGenerator] generating [Char]s based on the given [args].
 *
 * @since 2.0.0
 */
fun OrderedExtensionPoint.fromRange(args: IntRange): OrderedArgsGenerator<Int> =
	fromProgression(args)

/**
 * Returns an [OrderedArgsGenerator] generating [Char]s based on the given [args].
 *
 * @since 2.0.0
 */
fun OrderedExtensionPoint.fromRange(args: LongRange): OrderedArgsGenerator<Long> =
	fromProgression(args)

