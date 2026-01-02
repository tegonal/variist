package com.tegonal.variist.generators

import ch.tutteli.kbox.Tuple
import com.tegonal.variist.config._components
import com.tegonal.variist.generators.impl.BigIntFromToArbArgsGenerator
import com.tegonal.variist.generators.impl.IntFromToArbArgsGenerator
import com.tegonal.variist.generators.impl.LongFromToArbArgsGenerator
import com.tegonal.variist.utils.BigInt
import com.tegonal.variist.utils.toBigInt
import kotlin.math.abs

/**
 * Returns an [ArbArgsGenerator] generating [Char]s based on the given [progression].
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.fromProgression(progression: CharProgression): ArbArgsGenerator<Char> =
	fromProgression(
		IntProgression.fromClosedRange(progression.first.code, progression.last.code, progression.step)
	).map { it.toChar() }

/**
 * Returns an [ArbArgsGenerator] generating [Int]s based on the given [progression].
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.fromProgression(progression: IntProgression): ArbArgsGenerator<Int> =
	when (val step = progression.step) {
		1 -> intFromTo(progression.first, progression.last)
		-1 -> intFromTo(progression.last, progression.first)
		else -> {
			val (start, endInclusive) =
				if (step > 0) Tuple(progression.first, progression.last)
				else Tuple(progression.last, progression.first)
			val stepAbs = abs(step)
			val numberOfSteps = (endInclusive.toLong() - start) / stepAbs + 1
			val numberOfStepsI = numberOfSteps.toInt()

			//TODO 2.1.0 bench at what point it makes sense to calculate it, I just guess that for small progressions
			// storing it in a list is more efficient and memory is neglectable. But maybe this can be increased to x
			// or should be decreased to y
			if (numberOfSteps < 50L) fromList(progression.toList())
			else {
				// check that toInt() did not overflow
				if (numberOfSteps == numberOfStepsI.toLong()) {
					IntFromToArbArgsGenerator(_components, seedBaseOffset, 0, numberOfStepsI).map {
						start + it * stepAbs
					}
				} else {
					LongFromToArbArgsGenerator(_components, seedBaseOffset, 0, numberOfSteps).map {
						(start + it * stepAbs).toInt()
					}
				}
			}
		}
	}

/**
 * Returns an [ArbArgsGenerator] generating [Long]s based on the given [progression].
 *
 * @since 2.0.0
 */
fun ArbExtensionPoint.fromProgression(progression: LongProgression): ArbArgsGenerator<Long> =
	when (val step = progression.step) {
		1L -> longFromTo(progression.first, progression.last)
		-1L -> longFromTo(progression.last, progression.first)
		else -> {
			val (start, endInclusive) =
				if (step > 0L) Tuple(progression.first, progression.last)
				else Tuple(progression.last, progression.first)
			val stepAbs = abs(step)
			val diff = endInclusive.toBigInt() - start.toBigInt()
			val numberOfSteps = diff / stepAbs.toBigInt() + BigInt.ONE

			//TODO 2.1.0 bench at what point it makes sense to calculate it, I just guess that for small progressions,
			// storing it in a list is more efficient and memory is neglectable. But maybe this can be increased to x
			// or should be decreased to y
			if (numberOfSteps < 50.toBigInt()) fromList(progression.toList())
			else {
				if (numberOfSteps.bitLength() <= 63) {
					LongFromToArbArgsGenerator(_components, seedBaseOffset, 0, numberOfSteps.toLong()).map {
						start + it * stepAbs
					}
				} else {
					BigIntFromToArbArgsGenerator(_components, seedBaseOffset, BigInt.ZERO, numberOfSteps).map {
						start + it.toLong() * stepAbs
					}
				}
			}
		}
	}

//TODO 2.1.0 introduce BigIntProgression?
