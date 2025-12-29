package com.tegonal.variist.providers

import org.junit.platform.commons.support.AnnotationSupport
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.KClass

/**
 * Deduces [AnnotationData] based on a given annotation [A] where the annotation is searched
 * on the class and the method.
 *
 * @since 2.0.0
 */
abstract class BaseAnnotationDataDeducer<A : Annotation>(
	private val annotationClass: KClass<A>
) : AnnotationDataDeducer {
	final override fun deduce(
		testMethod: Method,
		argsSourceMethodName: String
	): AnnotationData? {
		val classData = deduceData(testMethod.declaringClass, argsSourceMethodName)
		val methodData = deduceData(testMethod, argsSourceMethodName)
		return when {
			classData != null && methodData != null -> classData.merge(methodData)
			classData != null && methodData == null -> classData
			methodData != null -> methodData
			else -> null
		}
	}

	private fun deduceData(element: AnnotatedElement, argsSourceMethodName: String): AnnotationData? =
		AnnotationSupport.findAnnotation(element, annotationClass.java)
			.map { annotationToAnnotationData(argsSourceMethodName, it) }.getOrNull()

	abstract fun annotationToAnnotationData(argsSourceMethodName: String, annotation: A): AnnotationData
}
