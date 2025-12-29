package com.tegonal.variist.config

/**
 * Responsible to create a [ComponentFactoryContainer].
 *
 * @since 2.0.0
 */
interface ComponentFactoryContainerFactory {
	fun create(): ComponentFactoryContainer
}
