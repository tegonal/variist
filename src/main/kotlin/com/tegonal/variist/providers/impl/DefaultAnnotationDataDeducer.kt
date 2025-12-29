package com.tegonal.variist.providers.impl

import com.tegonal.variist.providers.AnnotationData
import com.tegonal.variist.providers.ArgsSourceOptions
import com.tegonal.variist.providers.BaseAnnotationDataDeducer
import com.tegonal.variist.providers.fromOptions

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class DefaultAnnotationDataDeducer : BaseAnnotationDataDeducer<ArgsSourceOptions>(ArgsSourceOptions::class) {

	override fun annotationToAnnotationData(
		annotation: ArgsSourceOptions
	): AnnotationData = AnnotationData.fromOptions(annotation)
}
