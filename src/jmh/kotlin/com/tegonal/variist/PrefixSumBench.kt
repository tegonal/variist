package com.tegonal.variist

import com.tegonal.variist.generators.impl.possibleMaxSizeSafeInIntDomainWithoutDivideTrick
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Fork(3)
@State(Scope.Benchmark)
open class PrefixSumBench {

	@Param(
		"10",
		"100",
		"1000",
		"3000",
		"7000",
		"20000",
		"$possibleMaxSizeSafeInIntDomainWithoutDivideTrick"
	)
	var size: Int = 0
	var firstSum = 0

	@Setup
	fun setup() {
		firstSum = size
	}

	@Benchmark
	fun prefixSumFastBench() {
		// around twice as fast as prefixSumSlowerBench but in the nanoseconds
		// we use an if to determine which one to take, taking this if into account we are only 20% faster for the first
		// number, i.e only "worth" it if we generate multiple ranges and even then, we only safe some nanoseconds
		// per range
		repeat(1000) {
			Long.MAX_VALUE
			prefixSumFast(
				size,
				possibleMaxSizeSafeInIntDomainWithoutDivideTrick,
				1,
				possibleMaxSizeSafeInIntDomainWithoutDivideTrick
			)
		}
	}

	@Benchmark
	fun prefixSumSlowerBench() =
		repeat(1000) {
			prefixSumSlower(
				size,
				possibleMaxSizeSafeInIntDomainWithoutDivideTrick,
				1,
				possibleMaxSizeSafeInIntDomainWithoutDivideTrick
			)
		}
}


private fun prefixSumFast(size: Int, numOfRangesWithMinSize: Int, minSize: Int, possibleMaxSize: Int) =
	prefixSumFast(
		size = size,
		numOfRangesWithMinSize = numOfRangesWithMinSize,
		minSize = minSize,
		possibleMaxSize = possibleMaxSize,
		plus = Int::plus,
		minus = Int::minus,
		times = Int::times,
		divide = Int::div,
		one = 1,
		two = 2
	)

private fun prefixSumSlower(size: Int, numOfRangesWithMinSize: Int, minSize: Int, possibleMaxSize: Int): Int =
	prefixSumSlower(
		size = size,
		numOfRangesWithMinSize = numOfRangesWithMinSize,
		minSize = minSize,
		possibleMaxSize = possibleMaxSize,
		plus = Int::plus,
		minus = Int::minus,
		times = Int::times,
		divide = Int::div,
		mod = Int::rem,
		zero = 0,
		one = 1,
		two = 2
	)

private inline fun <NumberT> prefixSumFast(
	size: NumberT,
	numOfRangesWithMinSize: NumberT,
	minSize: NumberT,
	possibleMaxSize: NumberT,
	crossinline plus: (NumberT, NumberT) -> NumberT,
	crossinline minus: (NumberT, NumberT) -> NumberT,
	crossinline times: (NumberT, NumberT) -> NumberT,
	crossinline divide: (NumberT, NumberT) -> NumberT,
	one: NumberT,
	two: NumberT,
): NumberT where NumberT : Number, NumberT : Comparable<NumberT> {
	val n = plus(minus(size, minSize), one)
	val numOfRangesWithGivenSize = numOfRangesWithSize(size, possibleMaxSize, plus, minus, one)
	// note, as far as I can see it is not possible that both n and numOfRangesWithMinSize + numOfRangesWithGivenSize
	// are odd, i.e. the result is always even, and therefore / 2 is safe (no truncation because of Int/Long arithmetic
	// happens)
	return divide(times(n, plus(numOfRangesWithMinSize, numOfRangesWithGivenSize)), two)
}

@Suppress("DuplicatedCode")
private inline fun <NumberT> prefixSumSlower(
	size: NumberT,
	numOfRangesWithMinSize: NumberT,
	minSize: NumberT,
	possibleMaxSize: NumberT,
	crossinline plus: (NumberT, NumberT) -> NumberT,
	crossinline minus: (NumberT, NumberT) -> NumberT,
	crossinline times: (NumberT, NumberT) -> NumberT,
	crossinline divide: (NumberT, NumberT) -> NumberT,
	crossinline mod: (NumberT, NumberT) -> NumberT,
	zero: NumberT,
	one: NumberT,
	two: NumberT,
): NumberT where NumberT : Number, NumberT : Comparable<NumberT> {
	val n = plus(minus(size, minSize), one)
	val numOfRangesWithGivenSize = numOfRangesWithSize(size, possibleMaxSize, plus, minus, one)
	// usual formula: n * (first + last) / 2 would overflow, hence we divide first. In order not to truncate (due to
	// int division) need to check if n is even or not (one of both is always even).
	return if (mod(n, two) == zero) {
		times(divide(n, two), plus(numOfRangesWithMinSize, numOfRangesWithGivenSize))
	} else {
		times(n, divide(plus(numOfRangesWithMinSize, numOfRangesWithGivenSize), two))
	}
}

private inline fun <NumberT> numOfRangesWithSize(
	size: NumberT,
	possibleMaxSize: NumberT,
	plus: (NumberT, NumberT) -> NumberT,
	minus: (NumberT, NumberT) -> NumberT,
	one: NumberT,
): NumberT where NumberT : Number, NumberT : Comparable<NumberT> = plus(minus(possibleMaxSize, size), one)
