package com.tegonal.variist.utils

import com.tegonal.variist.utils.impl.ForeverConstantSequence
import com.tegonal.variist.utils.impl.ForeverUnitSequence
import com.tegonal.variist.utils.impl.RepeatingArraySequence
import com.tegonal.variist.utils.impl.RepeatingListSequence

/**
 * Returns an infinite [Sequence] of [Unit].
 *
 * @since 2.0.0
 */
fun repeatForever(): Sequence<Unit> = ForeverUnitSequence

/**
 * Returns an infinite [Sequence] backed by the given [list], starting at the given [offset].
 *
 * @since 2.0.0
 */
fun <T> repeatForever(list: List<T>, offset: Int = 0): Sequence<T> = RepeatingListSequence(list, offset)

/**
 * Returns an infinite [Sequence] backed by the given [array], starting at the given [offset].
 *
 * @since 2.0.0
 */
fun <T> repeatForever(array: Array<T>, offset: Int = 0): Sequence<T> = RepeatingArraySequence(array, offset)

/**
 * Returns an infinite [Sequence] backed by the given [constant].
 *
 * @since 2.0.0
 */
fun <T> repeatForever(constant: T): Sequence<T> = ForeverConstantSequence(constant)
