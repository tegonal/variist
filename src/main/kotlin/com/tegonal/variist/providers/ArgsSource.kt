package com.tegonal.variist.providers

import com.tegonal.variist.config.TestType
import com.tegonal.variist.config.VariistConfig
import com.tegonal.variist.generators.ArgsGenerator
import org.intellij.lang.annotations.Language
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsSource
import java.lang.annotation.Inherited

/**
 * Annotation which specifies a method which returns either an [ArgsGenerator] or [Arguments] (or raw values) which
 * are used in a [ParameterizedTest].
 *
 * @since 2.0.0
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@ArgumentsSource(ArgsProvider::class)
@ArgsSourceLike
annotation class ArgsSource(
	/**
	 * The name of the method which returns an [ArgsGenerator] or [Arguments] (or raw values).
	 */
	@Language("jvm-method-name") val methodName: String,
)

/**
 * Marker annotation for annotations which act as [ArgsSource], i.e. provide a `methodName: String` property which is
 * used to retrieve [ArgsGenerator] or [Arguments] (or raw values).
 *
 * @since 2.0.0
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ArgsSourceLike

/**
 * Additional options to steer the [ArgsRangeDecider] which is responsible to define what range from an [ArgsGenerator]
 * is used in a [ParameterizedTest].
 *
 * @since 2.0.0
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
annotation class ArgsSourceOptions(
	/**
	 * Taken into account if non-empty and will take precedence over [VariistConfig.defaultProfile].
	 *
	 * If you use the predefined [TestType]s as profile names, then use [TestType.ForAnnotation]
	 */
	val profile: String = "",

	/**
	 * Taken into account if > 0 and should influence an [ArgsRangeDecider]'s choice of [ArgsRange.take].
	 */
	val maxArgs: Int = -1,

	/**
	 * Taken into account if > 0 and should influence an [ArgsRangeDecider]'s choice of [ArgsRange.take].
	 */
	val requestedMinArgs: Int = -1,
)
