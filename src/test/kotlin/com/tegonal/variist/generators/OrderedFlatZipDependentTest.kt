package com.tegonal.variist.generators

import ch.tutteli.atrium.api.fluent.en_GB.inAnyOrder
import ch.tutteli.atrium.api.fluent.en_GB.only
import ch.tutteli.atrium.api.fluent.en_GB.toContain
import ch.tutteli.atrium.api.fluent.en_GB.values
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.kbox.Tuple
import com.tegonal.variist.config._components
import com.tegonal.variist.config.ordered
import com.tegonal.variist.testutils.RepeatGivenListArbArgsGenerator
import org.junit.jupiter.api.TestFactory
import kotlin.test.Test

class OrderedFlatZipDependentTest : AbstractOrderedArgsGeneratorTest<Int>() {

	override fun createGenerators(modifiedOrdered: OrderedExtensionPoint) = listOf(1, 2, 3).let { l ->
		val generator = modifiedOrdered.fromList(l)
		sequenceOf(
			Tuple(
				"flatZipDependent - amount 1",
				generator.flatZipDependent(amount = 1, { a1 ->
					RepeatGivenListArbArgsGenerator(listOf(a1 - 1), generator._components)
				}) { a1, a2 ->
					a1 + a2
				},
				//1+0, 2+1, 3+2
				listOf(1, 3, 5)
			),
			Tuple(
				"flatZipDependent - amount 2",
				generator.flatZipDependent(amount = 2, { a1 ->
					RepeatGivenListArbArgsGenerator(listOf(a1 - 2, a1 - 1), generator._components)
				}) { a1, a2 ->
					a1 + a2
				},
				//1+-1/1+0, 2+0/2+1, 3+1/3+2,
				listOf(0, 1, 2, 3, 4, 5)
			),
			Tuple(
				"flatZipDependentMaterialised",
				generator.flatZipDependentMaterialised({ a1 ->
					a1.toLong().let { ordered.intFromUntil(a1 - 1, a1) }
				}) { a1, a2 ->
					a1 + a2
				},
				//1+0, 2+1, 3+2
				listOf(1, 3, 5)
			),
		)
	}

	@TestFactory
	override fun offsetPlusXReturnsTheSameAsOffsetXMinus1JustShifted() =
		offsetPlusXReturnsTheSameAsOffsetXMinus1JustShiftedTest {
			// this "law" does not hold for flatZipDependent as soon as amount > 1
			createGenerators(customComponentFactoryContainer.ordered).filter { it.first != "flatZipDependent - amount 2" }
		}

	@Test
	fun flatZipDependentMaterialised_Tuple2() {
		val l = ordered.of(1, 2, 3).flatZipDependentMaterialised {
			when (it) {
				1 -> ordered.of('A', 'B', 'C')
				2 -> ordered.of('B')
				else -> ordered.of('B', 'C')
			}
		}.toList()

		expect(l).toContain.inAnyOrder.only.values(
			1 to 'A', 1 to 'B', 1 to 'C',
			2 to 'B',
			3 to 'B', 3 to 'C'
		)
	}
}
