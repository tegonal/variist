package com.tegonal.variist.generators

import com.tegonal.variist.config._components
import com.tegonal.variist.generators.impl.ConstantArbArgsGenerator
import com.tegonal.variist.generators.impl.checkNotEmptyReturnNullIfOneElementAndOtherwiseIntFromUntilSize

/**
 * Returns an [ArbArgsGenerator] based on the given [args].
 *
 * @since 2.0.0
 */
@JvmName("fromValueList")
fun <T> ArbExtensionPoint.fromList(args: List<T>): ArbArgsGenerator<T> =
	checkNotEmptyReturnNullIfOneElementAndOtherwiseIntFromUntilSize(args.size)?.map(args::get)
		?: ConstantArbArgsGenerator(_components, seedBaseOffset, args.first())
