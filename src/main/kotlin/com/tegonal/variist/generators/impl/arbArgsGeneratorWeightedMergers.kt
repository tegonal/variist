package com.tegonal.variist.generators.impl

import com.tegonal.variist.config._components
import com.tegonal.variist.generators.ArbArgsGenerator
import com.tegonal.variist.utils.deriveChildSeedOffset
import com.tegonal.variist.utils.deriveTwoChildSeedOffsets
import com.tegonal.variist.utils.impl.checkIsPositive
import kotlin.random.Random

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class ArbArgsGeneratorWeightedMerger<T>(
	a1GeneratorWithWeight: Pair<Int, ArbArgsGenerator<T>>,
	a2GeneratorWithWeight: Pair<Int, ArbArgsGenerator<T>>,
) : BaseArbArgsGenerator<T>(
	// note, we don't (and cannot) check that a1Generator and a2Generator use the same ComponentContainer,
	// should you run into weird behaviour (such as one generator uses seed X and the other seed Y) then most likely
	// someone used two different initial factories
	a1GeneratorWithWeight.second._components,
) {

	private val a1Generator = a1GeneratorWithWeight.second
	private val a2Generator = a2GeneratorWithWeight.second
	private val a1Weight = a1GeneratorWithWeight.first
	private val totalWeightPlus1 = Math.addExact(Math.addExact(a1Weight, a2GeneratorWithWeight.first), 1)

	init {
		checkIsPositive(a1Weight, "1st weight")
		checkIsPositive(a2GeneratorWithWeight.first, "2nd weight")
	}

	override fun generateOne(seedOffset: Int): T = createVariistRandom(seedOffset).let { variistRandom ->
		val r = variistRandom.nextInt(1, totalWeightPlus1)
		val (seedOffset1, seedOffset2) = deriveTwoChildSeedOffsets(seedOffset)
		return if (r <= a1Weight) a1Generator.generateOne(seedOffset1)
		else a2Generator.generateOne(seedOffset2)
	}

	override fun generate(seedOffset: Int): Sequence<T> = Sequence {
		object : Iterator<T> {
			private val variistRandom = createVariistRandom(seedOffset)
			private val a1Iterator = a1Generator.generate(deriveChildSeedOffset(seedOffset, 1)).iterator()
			private val a2Iterator = a2Generator.generate(deriveChildSeedOffset(seedOffset, 2)).iterator()

			override fun hasNext(): Boolean = true
			override fun next(): T = variistRandom.nextInt(1, totalWeightPlus1).let { r ->
				if (r <= a1Weight) a1Iterator.next()
				else a2Iterator.next()
			}
		}
	}
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class MultiArbArgsGeneratorIndexOfWeightedMerger<T>(
	firstGeneratorWithWeight: Pair<Int, ArbArgsGenerator<T>>,
	secondGeneratorWithWeight: Pair<Int, ArbArgsGenerator<T>>,
	otherGeneratorsWithWeight: Array<out Pair<Int, ArbArgsGenerator<T>>>,
) : BaseMultiArbArgsMerger<T>(
	firstGeneratorWithWeight.second,
	secondGeneratorWithWeight.second,
	otherGeneratorsWithWeight.map { it.second }.toTypedArray(),
) {
	private val cumulativeWeights: Array<Int>
	private val totalWeightPlus1: Int

	init {
		val firstWeight = firstGeneratorWithWeight.first
		val secondWeight = secondGeneratorWithWeight.first
		checkIsPositive(firstWeight, "1st weight")
		checkIsPositive(secondWeight, "2nd weight")
		otherGeneratorsWithWeight.forEachIndexed { index, it ->
			checkIsPositive(it.first) { "${index + 3}${if (index == 0) "rd" else "th"} weight" }
		}

		var acc = 0
		cumulativeWeights = Array(totalGenerators) { index ->
			Math.addExact(
				acc,
				when (index) {
					0 -> firstWeight
					1 -> secondWeight
					else -> otherGeneratorsWithWeight[index - 2].first
				}
			).also { acc = it }
		}

		totalWeightPlus1 = Math.addExact(acc, 1)
	}

	override fun getFirstIndex(seedOffset: Int) = createVariistRandom(seedOffset).let(::nextIndex)

	private fun nextIndex(variistRandom: Random): Int {
		val r = variistRandom.nextInt(1, totalWeightPlus1)
		return cumulativeWeights.indexOfFirst { it >= r }
	}

	override fun iteratorFactory(seedOffset: Int): IteratorsWithOffset<T> =
		object : IteratorsWithOffset<T>(generators, seedOffset) {
			private val variistRandom = createVariistRandom(seedOffset)

			override fun nextIndex(): Int = nextIndex(variistRandom)
		}
}
