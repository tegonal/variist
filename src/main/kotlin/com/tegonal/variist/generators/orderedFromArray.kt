package com.tegonal.variist.generators

import com.tegonal.variist.config._components
import com.tegonal.variist.generators.impl.ArrayOrderedArgsGenerator
import com.tegonal.variist.generators.impl.ConstantOrderedArgsGenerator
import com.tegonal.variist.generators.impl.checkNotEmptyCreateIndexBasedGenerator

/**
 * Returns an [OrderedArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun OrderedExtensionPoint.fromArray(args: ByteArray): OrderedArgsGenerator<Byte> =
	checkNotEmptyCreateIndexBasedGenerator(args.size, args::get)

/**
 * Returns an [OrderedArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun OrderedExtensionPoint.fromArray(args: CharArray): OrderedArgsGenerator<Char> =
	checkNotEmptyCreateIndexBasedGenerator(args.size, args::get)

/**
 * Returns an [OrderedArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun OrderedExtensionPoint.fromArray(args: ShortArray): OrderedArgsGenerator<Short> =
	checkNotEmptyCreateIndexBasedGenerator(args.size, args::get)

/**
 * Returns an [OrderedArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun OrderedExtensionPoint.fromArray(args: IntArray): OrderedArgsGenerator<Int> =
	checkNotEmptyCreateIndexBasedGenerator(args.size, args::get)

/**
 * Returns an [OrderedArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun OrderedExtensionPoint.fromArray(args: LongArray): OrderedArgsGenerator<Long> =
	checkNotEmptyCreateIndexBasedGenerator(args.size, args::get)

/**
 * Returns an [OrderedArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun OrderedExtensionPoint.fromArray(args: FloatArray): OrderedArgsGenerator<Float> =
	checkNotEmptyCreateIndexBasedGenerator(args.size, args::get)

/**
 * Returns an [OrderedArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun OrderedExtensionPoint.fromArray(args: DoubleArray): OrderedArgsGenerator<Double> =
	checkNotEmptyCreateIndexBasedGenerator(args.size, args::get)

/**
 * Returns an [OrderedArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun OrderedExtensionPoint.fromArray(args: BooleanArray): OrderedArgsGenerator<Boolean> =
	checkNotEmptyCreateIndexBasedGenerator(args.size, args::get)

/**
 * Returns an [OrderedArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun <T> OrderedExtensionPoint.fromArray(args: Array<out T>): OrderedArgsGenerator<T> =
	if (args.size == 1) ConstantOrderedArgsGenerator(_components, args.first())
	else ArrayOrderedArgsGenerator(_components, args)
