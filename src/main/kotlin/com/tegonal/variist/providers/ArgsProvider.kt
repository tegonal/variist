package com.tegonal.variist.providers

import ch.tutteli.kbox.failIf
import com.tegonal.variist.config.ArgsRangeOptions
import com.tegonal.variist.config._components
import com.tegonal.variist.config.build
import com.tegonal.variist.config.buildChained
import com.tegonal.variist.export.org.junit.jupiter.params.provider.MethodArgumentsProvider
import com.tegonal.variist.generators.ArgsGenerator
import com.tegonal.variist.generators.fromList
import com.tegonal.variist.generators.ordered
import com.tegonal.variist.providers.impl.tupleAndTupleLikeToList
import com.tegonal.variist.utils.impl.FEATURE_REQUEST_URL
import com.tegonal.variist.utils.impl.getPropertyValue
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.support.ParameterDeclarations
import java.lang.reflect.Method
import java.util.*
import java.util.stream.Stream
import kotlin.streams.asStream

/**
 * The [org.junit.jupiter.params.provider.ArgumentsProvider] for [ArgsSource].
 *
 * Copied and adapted from [org.junit.jupiter.params.provider.MethodArgumentsProvider] (Eclipse Public License 2.0)
 *
 * @since 2.0.0
 */
class ArgsProvider : ArgumentsProvider {

	@Suppress("DEPRECATION")
	override fun provideArguments(
		parameters: ParameterDeclarations,
		context: ExtensionContext
	): Stream<out Arguments> = provideArguments(context)

	// we know it is deprecated but users using an older version of JUnit (< 5.13) would fail if we would already
	// override the overload which expects ParameterDeclarations in addition. We will switch with JUnit 6
	@Suppress("OVERRIDE_DEPRECATION")
	override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
		val testClass = context.requiredTestClass
		val testMethod = context.requiredTestMethod
		val testInstance = context.testInstance.orElse(null)

		val annotations = testMethod.annotations.filter {
			it.annotationClass.annotations.any { metaAnnotation ->
				metaAnnotation.annotationClass == ArgsSourceLike::class
			}
		}

		failIf(annotations.isEmpty()) {
			"It looks like you forgot to annotate your ArgsSource annotation with ArgsSourceLike, at least couldn't find an annotation on ${testMethod.name} which itself is annotated with ArgsSourceLike"
		}
		val annotation = annotations.singleOrNull()
			?: error("For now we don't support to provide more than one ArgsSourceLike annotation, please open a feature request: $FEATURE_REQUEST_URL&title=annotate%20with%20multiple%20ArgsSourceLike")

		val methodName = annotation.getPropertyValue<String>("methodName")

		val factoryMethod = MethodArgumentsProvider.findFactoryMethod(testClass, Optional.of(testMethod), methodName)
			.also { MethodArgumentsProvider.validateFactoryMethod(it, testInstance) }

		val factoryResult = context.executableInvoker.invoke(factoryMethod, testInstance)
		return factoryResultToArguments(factoryResult, testMethod).asStream()
	}

	private fun factoryResultToArguments(result: Any, testMethod: Method): Sequence<Arguments> {
		val maybeArgGenerators = toListOfMaybeArgGenerators(result)
		check(maybeArgGenerators.isNotEmpty()) { "no ArgGenerators/Args defined" }
		val (first, maybeArgGeneratorsRest) = when (val first = maybeArgGenerators.first()) {
			is ArgsGenerator<*> -> first to maybeArgGenerators.drop(1)
			else -> {
				// assuming raw values. Since no ArgsGenerator is involved, we cannot use a custom `ordered` and
				// fallback to the default `ordered`
				ordered.fromList(maybeArgGenerators) to emptyList()
			}
		}
		return generateArguments(first, maybeArgGeneratorsRest, testMethod)
	}

	private fun toListOfMaybeArgGenerators(result: Any): List<Any?> = tupleAndTupleLikeToList(result) ?: when (result) {
		is ArgsGenerator<*> -> listOf(result)
		is Iterable<*> -> result.toList()
		is Sequence<*> -> result.toList()
		is Array<*> -> result.toList()
		is ByteArray -> result.toList()
		is CharArray -> result.toList()
		is ShortArray -> result.toList()
		is IntArray -> result.toList()
		is LongArray -> result.toList()
		is FloatArray -> result.toList()
		is DoubleArray -> result.toList()
		is BooleanArray -> result.toList()

		else -> throw UnsupportedOperationException("don't know how to convert ${result::class.qualifiedName ?: result} into Arguments, please open a feature request: $FEATURE_REQUEST_URL&title=Convert%20${result::class}%20to%20Arguments")
	}

	private fun generateArguments(
		argsGenerator: ArgsGenerator<*>,
		restMaybeArgGenerators: List<*>,
		testMethod: Method,
	): Sequence<Arguments> = argsGenerator._components.let { components ->
		val annotationDataDeducers = components.buildChained<AnnotationDataDeducer>()
		val suffixArgsGeneratorDecider = components.build<SuffixArgsGeneratorDecider>()
		val genericArgsGeneratorCombiner = components.build<GenericArgsGeneratorCombiner>()
		val argsGeneratorToArgumentsConverter = components.build<ArgsGeneratorToArgumentsConverter>()

		val annotationData = annotationDataDeducers.fold(
			AnnotationData(ArgsRangeOptions())
		) { data, deducer ->
			deducer.deduce(testMethod)?.let { data.merge(it) } ?: data
		}

		val restMaybeArgGeneratorsWithSuffix = suffixArgsGeneratorDecider.computeSuffixArgsGenerator(annotationData)
			?.let { suffixArgsGenerator -> restMaybeArgGenerators + listOf(suffixArgsGenerator) }
			?: restMaybeArgGenerators

		genericArgsGeneratorCombiner.combineFirstWithRest(argsGenerator, restMaybeArgGeneratorsWithSuffix)
			.let { argsGeneratorsCombined ->
				argsGeneratorToArgumentsConverter.toArguments(
					testMethod.declaringClass.name + "#" + testMethod.name,
					annotationData,
					argsGeneratorsCombined
				)
			}
	}
}

