@file:Suppress("unused")

package com.tegonal.variist

import com.tegonal.variist.generators.OrderedArgsGenerator
import com.tegonal.variist.generators.impl.InternalDangerousApi
import com.tegonal.variist.generators.impl.OrderedArgsGeneratorMapper
import com.tegonal.variist.generators.impl.transformInternal
import com.tegonal.variist.generators.intFromUntil
import com.tegonal.variist.generators.ordered
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 300, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 300, timeUnit = TimeUnit.MILLISECONDS)
@Fork(3)
@State(Scope.Benchmark)
open class OrderedMapperBench {

	@Param("1", "25", "50", "75", "100")//@Param("1", "25", "40", "50", "75", "90", "100")
	var takePercentage: Int = 0
	var take = 0

	@Param("12", "30", "57", "110", "300", "500")
	var numOfInts = 1

	lateinit var g: OrderedArgsGenerator<Int>


	@Setup
	fun setup() {
		take = numOfInts * takePercentage / 100
		if (take <= 0) take = 1
		g = ordered.intFromUntil(0, numOfInts)
	}

	@Benchmark
	fun mapper() =
		OrderedArgsGeneratorMapper(g) {
			it.toChar()
		}.generate(0).take(take).count()

	// mapper and transformInternal are roughly equivalent in terms of speed
	// However, the mapper uses about 70% of the memory transformInternal uses

	@OptIn(InternalDangerousApi::class)
	@Benchmark
	fun transform() =
		g.transformInternal { seq -> seq.map { it.toChar() } }
			.generate(0).take(take).count()
}
