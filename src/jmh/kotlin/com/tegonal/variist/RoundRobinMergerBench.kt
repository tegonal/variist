package com.tegonal.variist

import com.tegonal.variist.generators.ArbArgsGenerator
import com.tegonal.variist.generators._core
import com.tegonal.variist.generators.arb
import com.tegonal.variist.generators.impl.BaseArbArgsGenerator
import com.tegonal.variist.generators.impl.MultiArbArgsGeneratorRoundRobinMerger
import com.tegonal.variist.generators.int
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Fork(3)
@State(Scope.Benchmark)
open class RoundRobinMergerBench {

	lateinit var firstGenerator: ArbArgsGenerator<Int>
	lateinit var secondGenerator: ArbArgsGenerator<Int>

	@Setup
	fun setup() {
		firstGenerator = arb.int()
		secondGenerator = arb.arb.int()
	}

	// speed is not significant, sure more memory but not much, I don't think it would justifiy an extra implementation
	// benchmark					max		error	benchmark			min		error		diff
	// arbTwoSpecialised			5684.5	1.69%	multiMerger			5601.9	4.88%		98.54740
	// arbTwoSpecialised:·alloc	328.0	0.00%	multiMerger:alloc	376.0	0.00%		114.63296
	@Benchmark
	fun arbTwoSpecialised() =
		ArbArgsGeneratorRoundRobinMerger(
			firstGenerator,
			secondGenerator
		).generate().take(1000).count()

	@Benchmark
	fun multiMerger() =
		MultiArbArgsGeneratorRoundRobinMerger(
			firstGenerator,
			secondGenerator,
			emptyArray(),
			seedBaseOffset = 0
		).generate().take(1000).count()
}

class ArbArgsGeneratorRoundRobinMerger<T>(
	private val a1Generator: ArbArgsGenerator<T>,
	private val a2Generator: ArbArgsGenerator<T>,
) : BaseArbArgsGenerator<T>(
	// note, we don't (and cannot) check that a1Generator and a2Generator use the same ComponentContainer,
	// should you run into weird behaviour (such as one generator uses seed X and the other seed Y) then most likely
	// someone used two different initial factories
	a1Generator._core,
), ArbArgsGenerator<T> {

	override fun generateOne(seedOffset: Int): T =
		if (seedOffset % 2 == 0) a1Generator.generateOne(seedOffset)
		else a2Generator.generateOne(seedOffset)

	override fun generate(seedOffset: Int): Sequence<T> = Sequence {
		object : Iterator<T> {
			private val a1Iterator = a1Generator.generate(seedOffset).iterator()
			private val a2Iterator = a2Generator.generate(seedOffset).iterator()
			private var isFirst = seedOffset % 2 == 0

			override fun hasNext(): Boolean = true
			override fun next(): T = run {
				if (isFirst) a1Iterator.next()
				else a2Iterator.next()
			}.also {
				isFirst = !isFirst
			}
		}
	}
}
