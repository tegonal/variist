package com.tegonal.variist.generators

import com.tegonal.variist.config._components
import com.tegonal.variist.generators.impl.ConstantArbArgsGenerator
import com.tegonal.variist.generators.impl.checkNotEmptyReturnNullIfOneElementAndOtherwiseIntFromUntilSize

/**
 * Returns an [ArbArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.fromArray(args: ByteArray): ArbArgsGenerator<Byte> =
	checkNotEmptyReturnNullIfOneElementAndOtherwiseIntFromUntilSize(args.size)?.map(args::get)
		?: ConstantArbArgsGenerator(_components, seedBaseOffset, args.first())

/**
 * Returns an [ArbArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.fromArray(args: CharArray): ArbArgsGenerator<Char> =
	checkNotEmptyReturnNullIfOneElementAndOtherwiseIntFromUntilSize(args.size)?.map(args::get)
		?: ConstantArbArgsGenerator(_components, seedBaseOffset, args.first())


/**
 * Returns an [ArbArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.fromArray(args: ShortArray): ArbArgsGenerator<Short> =
	checkNotEmptyReturnNullIfOneElementAndOtherwiseIntFromUntilSize(args.size)?.map(args::get)
		?: ConstantArbArgsGenerator(_components, seedBaseOffset, args.first())

/**
 * Returns an [ArbArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.fromArray(args: IntArray): ArbArgsGenerator<Int> =
	checkNotEmptyReturnNullIfOneElementAndOtherwiseIntFromUntilSize(args.size)?.map(args::get)
		?: ConstantArbArgsGenerator(_components, seedBaseOffset, args.first())

/**
 * Returns an [ArbArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.fromArray(args: LongArray): ArbArgsGenerator<Long> =
	checkNotEmptyReturnNullIfOneElementAndOtherwiseIntFromUntilSize(args.size)?.map(args::get)
		?: ConstantArbArgsGenerator(_components, seedBaseOffset, args.first())

/**
 * Returns an [ArbArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.fromArray(args: FloatArray): ArbArgsGenerator<Float> =
	checkNotEmptyReturnNullIfOneElementAndOtherwiseIntFromUntilSize(args.size)?.map(args::get)
		?: ConstantArbArgsGenerator(_components, seedBaseOffset, args.first())

/**
 * Returns an [ArbArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.fromArray(args: DoubleArray): ArbArgsGenerator<Double> =
	checkNotEmptyReturnNullIfOneElementAndOtherwiseIntFromUntilSize(args.size)?.map(args::get)
		?: ConstantArbArgsGenerator(_components, seedBaseOffset, args.first())

/**
 * Returns an [ArbArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.fromArray(args: BooleanArray): ArbArgsGenerator<Boolean> =
	checkNotEmptyReturnNullIfOneElementAndOtherwiseIntFromUntilSize(args.size)?.map(args::get)
		?: ConstantArbArgsGenerator(_components, seedBaseOffset, args.first())

/**
 * Returns an [ArbArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
fun <T> ArbExtensionPoint.fromArray(args: Array<out T>): ArbArgsGenerator<T> =
	checkNotEmptyReturnNullIfOneElementAndOtherwiseIntFromUntilSize(args.size)?.map(args::get)
		?: ConstantArbArgsGenerator(_components, seedBaseOffset, args.first())
