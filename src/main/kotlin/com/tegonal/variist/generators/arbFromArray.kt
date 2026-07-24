package com.tegonal.variist.generators

import com.tegonal.variist.generators.impl.checkNotEmptyCreateIndexBasedGenerator

/**
 * Returns an [ArbArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.fromArray(args: ByteArray): ArbArgsGenerator<Byte> =
	checkNotEmptyCreateIndexBasedGenerator(args.size, args::get)

/**
 * Returns an [ArbArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.fromArray(args: CharArray): ArbArgsGenerator<Char> =
	checkNotEmptyCreateIndexBasedGenerator(args.size, args::get)


/**
 * Returns an [ArbArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.fromArray(args: ShortArray): ArbArgsGenerator<Short> =
	checkNotEmptyCreateIndexBasedGenerator(args.size, args::get)

/**
 * Returns an [ArbArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.fromArray(args: IntArray): ArbArgsGenerator<Int> =
	checkNotEmptyCreateIndexBasedGenerator(args.size, args::get)

/**
 * Returns an [ArbArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.fromArray(args: LongArray): ArbArgsGenerator<Long> =
	checkNotEmptyCreateIndexBasedGenerator(args.size, args::get)

/**
 * Returns an [ArbArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.fromArray(args: FloatArray): ArbArgsGenerator<Float> =
	checkNotEmptyCreateIndexBasedGenerator(args.size, args::get)

/**
 * Returns an [ArbArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.fromArray(args: DoubleArray): ArbArgsGenerator<Double> =
	checkNotEmptyCreateIndexBasedGenerator(args.size, args::get)

/**
 * Returns an [ArbArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.fromArray(args: BooleanArray): ArbArgsGenerator<Boolean> =
	checkNotEmptyCreateIndexBasedGenerator(args.size, args::get)

/**
 * Returns an [ArbArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun <T> ArbExtensionPoint.fromArray(args: Array<out T>): ArbArgsGenerator<T> =
	checkNotEmptyCreateIndexBasedGenerator(args.size, args::get)
