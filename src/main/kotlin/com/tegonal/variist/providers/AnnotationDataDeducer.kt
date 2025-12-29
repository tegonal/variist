package com.tegonal.variist.providers

import java.lang.reflect.Method

/**
 * Responsible to deduce [AnnotationData] based on a given test [Method] and the args source method name.
 *
 * @since 2.0.0
 */
interface AnnotationDataDeducer {
	/**
	 * Deduces [AnnotationData] based on the given [testMethod] or returns `null` in case it cannot be deduced.
	 */
	fun deduce(testMethod: Method): AnnotationData?
}
