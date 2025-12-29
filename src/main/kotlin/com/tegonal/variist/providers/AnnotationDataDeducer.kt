package com.tegonal.variist.providers

import java.lang.reflect.Method

/**
 * Responsible to deduce [AnnotationData] based on a given test [Method] and the args source method name.
 *
 * @since 2.0.0
 */
interface AnnotationDataDeducer {
	fun deduce(testMethod: Method, argsSourceMethodName: String): AnnotationData?
}
