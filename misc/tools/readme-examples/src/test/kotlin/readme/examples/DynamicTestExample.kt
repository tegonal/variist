package readme.examples

import com.tegonal.variist.config.ArgsRangeOptions
import com.tegonal.variist.config.TestType
import com.tegonal.variist.generators.generateAndTakeBasedOnDecider
import com.tegonal.variist.generators.intFromUntil
import com.tegonal.variist.generators.ordered
import com.tegonal.variist.providers.AnnotationData
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import readme.examples.jupiter.ReadmeTest

@Order(2)
class DynamicTestExample : ReadmeTest {

	@Test
	fun `code-dynamic-test-1`() {
		//snippet-dynamic-test-import-insert

		//snippet-dynamic-test-1-insert
	}

	@Test
	fun `code-dynamic-test-2`() {

		//snippet-dynamic-test-2-insert
	}

	//snippet-dynamic-test-2-start
	@TestFactory
	fun orderedExample() =
		ordered.intFromUntil(1, 100)
			.generateAndTakeBasedOnDecider(
				AnnotationData(ArgsRangeOptions(profile = TestType.Integration.name, maxArgs = 20))
			)
			.map { positiveNumber ->
				dynamicTest("$positiveNumber") {
					assertTrue(positiveNumber * -1 < 0)
				}
			}
	//snippet-dynamic-test-2-end
}
