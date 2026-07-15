@file:JvmName("OrderedCartesianKt")
@file:JvmMultifileClass
// --------------------------------------------------------------------------------------------------------------------
// automatically generated, don't modify here but in:
// gradle/code-generation/src/main/kotlin/code-generation.generate.gradle.kts => generate
// --------------------------------------------------------------------------------------------------------------------
package com.tegonal.variist.generators

import ch.tutteli.kbox.append
import ch.tutteli.kbox.Tuple2
import ch.tutteli.kbox.Tuple3
import ch.tutteli.kbox.Tuple4
import ch.tutteli.kbox.Tuple5
import ch.tutteli.kbox.Tuple6
import ch.tutteli.kbox.Tuple7
import ch.tutteli.kbox.Tuple8
import ch.tutteli.kbox.Tuple9

/**
 * Combines `this` [OrderedArgsGenerator] with the given [other]&nbsp;[OrderedArgsGenerator] resulting in their
 * Cartesian product where the values are transformed into a [Tuple2].
 *
 * The resulting [OrderedArgsGenerator] generates
 * [this.size][OrderedArgsGenerator.size] * [other.size][OrderedArgsGenerator.size] values before repeating.
 *
 * @param other The other [OrderedArgsGenerator] which generates values of type [A2].
 *
 * @return The resulting [OrderedArgsGenerator] which represents the Cartesian product and
 *   generates values of type [Tuple2].
 *
 * @since 2.0.0
 */
@JvmName("cartesianToTuple2")
fun <A1, A2> OrderedArgsGenerator<A1>.cartesian(
	other: OrderedArgsGenerator<A2>
): OrderedArgsGenerator<Tuple2<A1, A2>> = cartesian(other, ::Tuple2)

/**
 * Combines `this` [OrderedArgsGenerator] with the given [other]&nbsp;[OrderedArgsGenerator] resulting in their
 * Cartesian product where the values are transformed into a [Tuple3].
 *
 * The resulting [OrderedArgsGenerator] generates
 * [this.size][OrderedArgsGenerator.size] * [other.size][OrderedArgsGenerator.size] values before repeating.
 *
 * @param other The other [OrderedArgsGenerator] which generates values of type [A3].
 *
 * @return The resulting [OrderedArgsGenerator] which represents the Cartesian product and
 *   generates values of type [Tuple3].
 *
 * @since 2.0.0
 */
@JvmName("cartesianToTuple3")
fun <A1, A2, A3> OrderedArgsGenerator<Tuple2<A1, A2>>.cartesian(
	other: OrderedArgsGenerator<A3>
): OrderedArgsGenerator<Tuple3<A1, A2, A3>> = cartesian(other) { args, otherArg ->
	args.append(otherArg)
}

/**
 * Combines `this` [OrderedArgsGenerator] with the given [other]&nbsp;[OrderedArgsGenerator] resulting in their
 * Cartesian product where the values are transformed into a [Tuple4].
 *
 * The resulting [OrderedArgsGenerator] generates
 * [this.size][OrderedArgsGenerator.size] * [other.size][OrderedArgsGenerator.size] values before repeating.
 *
 * @param other The other [OrderedArgsGenerator] which generates values of type [A4].
 *
 * @return The resulting [OrderedArgsGenerator] which represents the Cartesian product and
 *   generates values of type [Tuple4].
 *
 * @since 2.0.0
 */
@JvmName("cartesianToTuple4")
fun <A1, A2, A3, A4> OrderedArgsGenerator<Tuple3<A1, A2, A3>>.cartesian(
	other: OrderedArgsGenerator<A4>
): OrderedArgsGenerator<Tuple4<A1, A2, A3, A4>> = cartesian(other) { args, otherArg ->
	args.append(otherArg)
}

/**
 * Combines `this` [OrderedArgsGenerator] with the given [other]&nbsp;[OrderedArgsGenerator] resulting in their
 * Cartesian product where the values are transformed into a [Tuple5].
 *
 * The resulting [OrderedArgsGenerator] generates
 * [this.size][OrderedArgsGenerator.size] * [other.size][OrderedArgsGenerator.size] values before repeating.
 *
 * @param other The other [OrderedArgsGenerator] which generates values of type [A5].
 *
 * @return The resulting [OrderedArgsGenerator] which represents the Cartesian product and
 *   generates values of type [Tuple5].
 *
 * @since 2.0.0
 */
@JvmName("cartesianToTuple5")
fun <A1, A2, A3, A4, A5> OrderedArgsGenerator<Tuple4<A1, A2, A3, A4>>.cartesian(
	other: OrderedArgsGenerator<A5>
): OrderedArgsGenerator<Tuple5<A1, A2, A3, A4, A5>> = cartesian(other) { args, otherArg ->
	args.append(otherArg)
}

/**
 * Combines `this` [OrderedArgsGenerator] with the given [other]&nbsp;[OrderedArgsGenerator] resulting in their
 * Cartesian product where the values are transformed into a [Tuple6].
 *
 * The resulting [OrderedArgsGenerator] generates
 * [this.size][OrderedArgsGenerator.size] * [other.size][OrderedArgsGenerator.size] values before repeating.
 *
 * @param other The other [OrderedArgsGenerator] which generates values of type [A6].
 *
 * @return The resulting [OrderedArgsGenerator] which represents the Cartesian product and
 *   generates values of type [Tuple6].
 *
 * @since 2.0.0
 */
@JvmName("cartesianToTuple6")
fun <A1, A2, A3, A4, A5, A6> OrderedArgsGenerator<Tuple5<A1, A2, A3, A4, A5>>.cartesian(
	other: OrderedArgsGenerator<A6>
): OrderedArgsGenerator<Tuple6<A1, A2, A3, A4, A5, A6>> = cartesian(other) { args, otherArg ->
	args.append(otherArg)
}

/**
 * Combines `this` [OrderedArgsGenerator] with the given [other]&nbsp;[OrderedArgsGenerator] resulting in their
 * Cartesian product where the values are transformed into a [Tuple7].
 *
 * The resulting [OrderedArgsGenerator] generates
 * [this.size][OrderedArgsGenerator.size] * [other.size][OrderedArgsGenerator.size] values before repeating.
 *
 * @param other The other [OrderedArgsGenerator] which generates values of type [A7].
 *
 * @return The resulting [OrderedArgsGenerator] which represents the Cartesian product and
 *   generates values of type [Tuple7].
 *
 * @since 2.0.0
 */
@JvmName("cartesianToTuple7")
fun <A1, A2, A3, A4, A5, A6, A7> OrderedArgsGenerator<Tuple6<A1, A2, A3, A4, A5, A6>>.cartesian(
	other: OrderedArgsGenerator<A7>
): OrderedArgsGenerator<Tuple7<A1, A2, A3, A4, A5, A6, A7>> = cartesian(other) { args, otherArg ->
	args.append(otherArg)
}

/**
 * Combines `this` [OrderedArgsGenerator] with the given [other]&nbsp;[OrderedArgsGenerator] resulting in their
 * Cartesian product where the values are transformed into a [Tuple8].
 *
 * The resulting [OrderedArgsGenerator] generates
 * [this.size][OrderedArgsGenerator.size] * [other.size][OrderedArgsGenerator.size] values before repeating.
 *
 * @param other The other [OrderedArgsGenerator] which generates values of type [A8].
 *
 * @return The resulting [OrderedArgsGenerator] which represents the Cartesian product and
 *   generates values of type [Tuple8].
 *
 * @since 2.0.0
 */
@JvmName("cartesianToTuple8")
fun <A1, A2, A3, A4, A5, A6, A7, A8> OrderedArgsGenerator<Tuple7<A1, A2, A3, A4, A5, A6, A7>>.cartesian(
	other: OrderedArgsGenerator<A8>
): OrderedArgsGenerator<Tuple8<A1, A2, A3, A4, A5, A6, A7, A8>> = cartesian(other) { args, otherArg ->
	args.append(otherArg)
}

/**
 * Combines `this` [OrderedArgsGenerator] with the given [other]&nbsp;[OrderedArgsGenerator] resulting in their
 * Cartesian product where the values are transformed into a [Tuple9].
 *
 * The resulting [OrderedArgsGenerator] generates
 * [this.size][OrderedArgsGenerator.size] * [other.size][OrderedArgsGenerator.size] values before repeating.
 *
 * @param other The other [OrderedArgsGenerator] which generates values of type [A9].
 *
 * @return The resulting [OrderedArgsGenerator] which represents the Cartesian product and
 *   generates values of type [Tuple9].
 *
 * @since 2.0.0
 */
@JvmName("cartesianToTuple9")
fun <A1, A2, A3, A4, A5, A6, A7, A8, A9> OrderedArgsGenerator<Tuple8<A1, A2, A3, A4, A5, A6, A7, A8>>.cartesian(
	other: OrderedArgsGenerator<A9>
): OrderedArgsGenerator<Tuple9<A1, A2, A3, A4, A5, A6, A7, A8, A9>> = cartesian(other) { args, otherArg ->
	args.append(otherArg)
}

