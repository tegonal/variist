package readme.examples

import com.tegonal.variist.generators.of
import com.tegonal.variist.generators.ordered
import com.tegonal.variist.generators.toArbArgsGenerator

//snippet-random-helper-import-start
import com.tegonal.variist.utils.createVariistRandom
import com.tegonal.variist.utils.pickOneRandomly
import com.tegonal.variist.utils.takeRandomly
//snippet-random-helper-import-end

import org.junit.jupiter.api.Test
import readme.examples.jupiter.ReadmeTest

@Suppress("UNUSED_VARIABLE", "unused")
class RandomHelperExample : ReadmeTest {

	@Test
	fun `code-random-helper`() {
		//snippet-random-helper-import-insert

		// Imagine the list is more complicated than that (involves, filtering, flatMap etc.), because if not, then
		// better define it via `arb` or `ordered` because this is most likely more efficient (would not allocate the
		// memory for 1001 Ints)
		val someList = (0..1000).toList()
		val i1: Int = someList.pickOneRandomly()
		val l1: List<Int> = someList.takeRandomly(10)

		// Imagine a more complicated ordered which combines multiple generators and in the end maps to a model.
		// In case you want to re-use it in another context than ParameterizedTests those helpers might come in handy
		val complicatedSetup = ordered.of(1, 2, 3)
		val i2: Int = complicatedSetup.pickOneRandomly()
		val l2: List<Int> = complicatedSetup.takeRandomly(100)
		// and of course, if you want to do more than that, then you can always turn your OrderedArgsGenerator
		// into an ArbArgsGenerator and then work on Sequence:
		val l3: Set<Int> = complicatedSetup.toArbArgsGenerator().generate()
			.map { it + i1 + i2 }
			//...
			.take(50)
			.toSet()

		// creates a Random based on the configured seed, i.e. if you fix the seed, then you get a deterministic result
		createVariistRandom().let { random ->
			val i = random.nextInt()
			//...
		}
	}
}
