@file:JvmName("SemiOrderedZipKt")
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
 * Zips `this` [SemiOrderedLikeArgsGenerator] with the given [other]&nbsp;[ArbArgsGenerator].
 *
 * @param other The other [ArbArgsGenerator] which generates values of type [A2].
 *
 * @return The resulting [SemiOrderedArgsGenerator] which generates values of type [Tuple2].
 *
 * @since 2.0.0
 */
@JvmName("zipToTuple2")
fun <A1, A2> SemiOrderedLikeArgsGenerator<A1>.zip(
	other: ArbArgsGenerator<A2>
): SemiOrderedArgsGenerator<Tuple2<A1, A2>> = zip(other, ::Tuple2)

/**
 * Zips `this` [SemiOrderedLikeArgsGenerator] with the given [other]&nbsp;[ArbArgsGenerator].
 *
 * @param other The other [ArbArgsGenerator] which generates values of type [A3].
 *
 * @return The resulting [SemiOrderedArgsGenerator] which generates values of type [Tuple3].
 *
 * @since 2.0.0
 */
@JvmName("zipToTuple3")
fun <A1, A2, A3> SemiOrderedLikeArgsGenerator<Tuple2<A1, A2>>.zip(
	other: ArbArgsGenerator<A3>
): SemiOrderedArgsGenerator<Tuple3<A1, A2, A3>> = zip(other) { args, otherArg ->
	args.append(otherArg)
}

/**
 * Zips `this` [SemiOrderedLikeArgsGenerator] with the given [other]&nbsp;[ArbArgsGenerator].
 *
 * @param other The other [ArbArgsGenerator] which generates values of type [A4].
 *
 * @return The resulting [SemiOrderedArgsGenerator] which generates values of type [Tuple4].
 *
 * @since 2.0.0
 */
@JvmName("zipToTuple4")
fun <A1, A2, A3, A4> SemiOrderedLikeArgsGenerator<Tuple3<A1, A2, A3>>.zip(
	other: ArbArgsGenerator<A4>
): SemiOrderedArgsGenerator<Tuple4<A1, A2, A3, A4>> = zip(other) { args, otherArg ->
	args.append(otherArg)
}

/**
 * Zips `this` [SemiOrderedLikeArgsGenerator] with the given [other]&nbsp;[ArbArgsGenerator].
 *
 * @param other The other [ArbArgsGenerator] which generates values of type [A5].
 *
 * @return The resulting [SemiOrderedArgsGenerator] which generates values of type [Tuple5].
 *
 * @since 2.0.0
 */
@JvmName("zipToTuple5")
fun <A1, A2, A3, A4, A5> SemiOrderedLikeArgsGenerator<Tuple4<A1, A2, A3, A4>>.zip(
	other: ArbArgsGenerator<A5>
): SemiOrderedArgsGenerator<Tuple5<A1, A2, A3, A4, A5>> = zip(other) { args, otherArg ->
	args.append(otherArg)
}

/**
 * Zips `this` [SemiOrderedLikeArgsGenerator] with the given [other]&nbsp;[ArbArgsGenerator].
 *
 * @param other The other [ArbArgsGenerator] which generates values of type [A6].
 *
 * @return The resulting [SemiOrderedArgsGenerator] which generates values of type [Tuple6].
 *
 * @since 2.0.0
 */
@JvmName("zipToTuple6")
fun <A1, A2, A3, A4, A5, A6> SemiOrderedLikeArgsGenerator<Tuple5<A1, A2, A3, A4, A5>>.zip(
	other: ArbArgsGenerator<A6>
): SemiOrderedArgsGenerator<Tuple6<A1, A2, A3, A4, A5, A6>> = zip(other) { args, otherArg ->
	args.append(otherArg)
}

/**
 * Zips `this` [SemiOrderedLikeArgsGenerator] with the given [other]&nbsp;[ArbArgsGenerator].
 *
 * @param other The other [ArbArgsGenerator] which generates values of type [A7].
 *
 * @return The resulting [SemiOrderedArgsGenerator] which generates values of type [Tuple7].
 *
 * @since 2.0.0
 */
@JvmName("zipToTuple7")
fun <A1, A2, A3, A4, A5, A6, A7> SemiOrderedLikeArgsGenerator<Tuple6<A1, A2, A3, A4, A5, A6>>.zip(
	other: ArbArgsGenerator<A7>
): SemiOrderedArgsGenerator<Tuple7<A1, A2, A3, A4, A5, A6, A7>> = zip(other) { args, otherArg ->
	args.append(otherArg)
}

/**
 * Zips `this` [SemiOrderedLikeArgsGenerator] with the given [other]&nbsp;[ArbArgsGenerator].
 *
 * @param other The other [ArbArgsGenerator] which generates values of type [A8].
 *
 * @return The resulting [SemiOrderedArgsGenerator] which generates values of type [Tuple8].
 *
 * @since 2.0.0
 */
@JvmName("zipToTuple8")
fun <A1, A2, A3, A4, A5, A6, A7, A8> SemiOrderedLikeArgsGenerator<Tuple7<A1, A2, A3, A4, A5, A6, A7>>.zip(
	other: ArbArgsGenerator<A8>
): SemiOrderedArgsGenerator<Tuple8<A1, A2, A3, A4, A5, A6, A7, A8>> = zip(other) { args, otherArg ->
	args.append(otherArg)
}

/**
 * Zips `this` [SemiOrderedLikeArgsGenerator] with the given [other]&nbsp;[ArbArgsGenerator].
 *
 * @param other The other [ArbArgsGenerator] which generates values of type [A9].
 *
 * @return The resulting [SemiOrderedArgsGenerator] which generates values of type [Tuple9].
 *
 * @since 2.0.0
 */
@JvmName("zipToTuple9")
fun <A1, A2, A3, A4, A5, A6, A7, A8, A9> SemiOrderedLikeArgsGenerator<Tuple8<A1, A2, A3, A4, A5, A6, A7, A8>>.zip(
	other: ArbArgsGenerator<A9>
): SemiOrderedArgsGenerator<Tuple9<A1, A2, A3, A4, A5, A6, A7, A8, A9>> = zip(other) { args, otherArg ->
	args.append(otherArg)
}

