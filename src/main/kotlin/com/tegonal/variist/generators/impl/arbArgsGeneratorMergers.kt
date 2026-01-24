package com.tegonal.variist.generators.impl

import com.tegonal.variist.utils.impl.checkIsPositive
import com.tegonal.variist.generators.ArbArgsGenerator
import com.tegonal.variist.generators._core

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class ArbArgsGeneratorMerger<T>(
	a1GeneratorWithWeight: Pair<Int, ArbArgsGenerator<T>>,
	a2GeneratorWithWeight: Pair<Int, ArbArgsGenerator<T>>,
) : BaseArbArgsGenerator<T>(
	// note, we don't (and cannot) check that a1Generator and a2Generator use the same ComponentContainer,
	// should you run into weird behaviour (such as one generator uses seed X and the other seed Y) then most likely
	// someone used two different initial factories
	a1GeneratorWithWeight.second._core,
), ArbArgsGenerator<T> {

	private val a1Generator = a1GeneratorWithWeight.second
	private val a2Generator = a2GeneratorWithWeight.second
	private val a1Weight = a1GeneratorWithWeight.first
	private val totalWeightPlus1 = Math.addExact(Math.addExact(a1Weight, a2GeneratorWithWeight.first), 1)

	init {
		checkIsPositive(a1Weight, "(1.) weight")
		checkIsPositive(a2GeneratorWithWeight.first, "(2.) weight")
	}

	override fun generateOne(seedOffset: Int): T = createVariistRandom(seedOffset).let { variistRandom ->
		val r = variistRandom.nextInt(1, totalWeightPlus1)
		return if (r <= a1Weight) a1Generator.generateOne(seedOffset)
		else a2Generator.generateOne(seedOffset)
	}

	override fun generate(seedOffset: Int): Sequence<T> = createVariistRandom(seedOffset).let { variistRandom ->
		Sequence {
			object : Iterator<T> {
				private val a1Iterator = a1Generator.generate(seedOffset).iterator()
				private val a2Iterator = a2Generator.generate(seedOffset).iterator()

				override fun hasNext(): Boolean = true
				override fun next(): T {
					val r = variistRandom.nextInt(1, totalWeightPlus1)
					return if (r <= a1Weight) a1Iterator.next()
					else a2Iterator.next()
				}
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
class MultiArbArgsGeneratorIndexOfMerger<T>(
	firstGeneratorWithWeight: Pair<Int, ArbArgsGenerator<T>>,
	secondGeneratorWithWeight: Pair<Int, ArbArgsGenerator<T>>,
	otherGeneratorsWithWeight: Array<out Pair<Int, ArbArgsGenerator<T>>>,
) : BaseArbArgsGenerator<T>(
	// note, we don't (and cannot) check that a1Generator and a2Generator use the same ComponentContainer,
	// should you run into weird behaviour (such as one generator uses seed X and the other seed Y) then most likely
	// someone used two different initial factories
	firstGeneratorWithWeight.second._core,
), ArbArgsGenerator<T> {
	private val generators: Array<ArbArgsGenerator<T>>
	private val cumulativeWeights: Array<Int>
	private val totalWeightPlus1: Int

	init {
		val firstWeight = firstGeneratorWithWeight.first
		val secondWeight = secondGeneratorWithWeight.first
		checkIsPositive(firstWeight, "(1.) weight")
		checkIsPositive(secondWeight, "(2.) weight")
		otherGeneratorsWithWeight.forEachIndexed { index, it ->
			checkIsPositive(it.first) { "(${index + 3}.) weight" }
		}

		val totalGenerators = otherGeneratorsWithWeight.size + 2
		generators = Array(totalGenerators) { index ->
			when (index) {
				0 -> firstGeneratorWithWeight.second
				1 -> secondGeneratorWithWeight.second
				else -> otherGeneratorsWithWeight[index - 2].second
			}
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

	override fun generateOne(seedOffset: Int): T = createVariistRandom(seedOffset).let { variistRandom ->
		val r = variistRandom.nextInt(1, totalWeightPlus1)
		val index = cumulativeWeights.indexOfFirst { it >= r }
		generators[index].generateOne(seedOffset)
	}

	override fun generate(seedOffset: Int): Sequence<T> = createVariistRandom(seedOffset).let { variistRandom ->
		Sequence {
			object : Iterator<T> {
				private val iterators = Array(generators.size) { generators[it].generate(seedOffset).iterator() }

				override fun hasNext(): Boolean = true
				override fun next(): T {
					val r = variistRandom.nextInt(1, totalWeightPlus1)
					val index = cumulativeWeights.indexOfFirst { it >= r }
					return iterators[index].next()
				}
			}
		}
	}
}
