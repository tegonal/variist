package com.tegonal.variist.generators.impl

import com.tegonal.variist.generators.OrderedArgsGenerator

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * Note, we tried to re-use OrderedArgsGeneratorTransformer for other operations by providing a generic transform
 * extension method which required a newSize in addition. Yet, we ran into 3 bugs in a row, so it seems it is
 * too dangerous to leave it into the wild. In particular one needs to take care of:
 * - that the newSize is actually correct (we cannot check this)
 * - the offset still works as it should. For instance, filtering means the offset has to happen after generation,
 *   i.e. dropping elements and this can harm performance in case there are a lot of values (which can happen easily
 *   when combining generators). The same problem applies when adding elements (e.g. via flatMap). Something like
 *   Sequence.chunked needs extra care as the resulting stream should be ordered finite, repeating after size
 *
 * Use this method only if you are more than convinced that the resulting Sequence still has the same semantics as the
 * original in terms of order, OrderedArgsGenerator.size, and a used offset
 *
 */
@InternalDangerousApi
fun <R, T> OrderedArgsGenerator<T>.transformInternal(
	transform: (Sequence<T>) -> Sequence<R>
): OrderedArgsGenerator<R> = OrderedArgsGeneratorTransformer(this) { it, _ -> transform(it) }
