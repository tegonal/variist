package com.tegonal.variist.config


/**
 * Marker interface for types which can safely be cast to [ComponentFactoryContainerProvider].
 *
 * They don't reveal the interface publicly, so that it doesn't clutter their public API.
 *
 * @since 2.0.0
 */
interface IsComponentFactoryContainerProvider

/**
 * Casts `this` to a [ComponentFactoryContainerProvider] and returns the [ComponentFactoryContainerProvider.componentFactoryContainer].

 * @since 2.0.0
 */
@Suppress("ObjectPropertyName")
val IsComponentFactoryContainerProvider._components: ComponentFactoryContainer
	get() = run {
		this as? ComponentFactoryContainerProvider
			?: error("$this is marked as ${IsComponentFactoryContainerProvider::class.qualifiedName} but is not a ${ComponentFactoryContainerProvider::class.qualifiedName}")
	}.componentFactoryContainer

/**
 * Type which provides a [ComponentFactoryContainer] via property [componentFactoryContainer].
 *
 * @since 2.0.0
 */
interface ComponentFactoryContainerProvider {
	val componentFactoryContainer: ComponentFactoryContainer
}
