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

	final override fun deduce(testMethod: Method): AnnotationData? {
		val classData = deduceData(testMethod.declaringClass)
		val methodData = deduceData(testMethod)
		return when {
			classData == null -> methodData
			methodData == null -> classData
			else -> classData.merge(methodData)
		}
	}

	private fun deduceData(element: AnnotatedElement): AnnotationData? =
		AnnotationSupport.findAnnotation(element, annotationClass.java)
			.map { annotationToAnnotationData(it) }.getOrNull()

	/**
	 * Responsible to deduce the [AnnotationData] based on a given [annotation].
	 */
	protected abstract fun annotationToAnnotationData(annotation: A): AnnotationData
}
