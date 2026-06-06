package readme.examples

import ch.tutteli.kbox.Tuple
import com.tegonal.variist.generators.*
import com.tegonal.variist.providers.*
import com.tegonal.variist.utils.BigInt
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.api.Order

@Suppress("unused", "UNUSED_PARAMETER")
@Order(1)
//snippet-combine-manually-start
class CombineManuallyTest : PredefinedArgsProviders {

	@ParameterizedTest
	@ArgsSource("arbNumbersAndChar")
	fun bar(i: Int, l: Long, d: Double, b: BigInt, c: Char) {
		//...
	}

	companion object {
		@JvmStatic
		fun arbNumbersAndChar() = run { // use run to let the compiler infer the return type
			val numbers = Tuple(
				arb.int().zip(arb.long()), // combines them into an ArbArgsGenerators<Tuple2<Int, Long>>
				arb.double(),
				arb.bigIntFromUntil(BigInt.ZERO, BigInt.TEN)
			).combineAll() // combines all into an ArbArgsGenerators<Tuple3<...>>

			Tuple(
				numbers, // can again be used in a tuple to define that it shall be combined
				arb.char()
			) // the ArgsProvider will flatten all tuples (also nested), resulting in 5 arguments (see bar above)
		}
	}
}
//snippet-combine-manually-end

