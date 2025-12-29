package com.tegonal.variist.config

import com.tegonal.variist.providers.ArgsRangeDecider
import kotlin.reflect.KClass

/**
 * Manages the factories to create the different components of Variist.
 *
 * It takes basically the responsibility of a dependency injection facility, tailored for Variist - initially copied
 * from [Atrium](https://atriumlib.org).
 *
 * @since 2.0.0
 */
interface ComponentFactoryContainer {

	/**
	 * Returns the component of type [T] using a corresponding factory or `null` in case no factory was found
	 * which is able to build a component of the given type.
	 *
	 * @throws ClassCastException in case the factory returns an illegal type.
	 */
	fun <T : Any> buildOrNull(kClass: KClass<T>): T?

	/**
	 * Returns a chain of components of type [T] using a corresponding chain of factories or `null`
	 * in case no chain was found which is able to build a chain of components of the given type.
	 *
	 * @throws ClassCastException in case one of factories returns an illegal type.
	 */
	fun <T : Any> buildChainedOrNull(kClass: KClass<T>): Sequence<T>?

	/**
	 * Returns a factory which is able to build a component for the given [kClass]
	 * or `null` in case no factory was found which is able to build a component of the given type.
	 */
	fun getFactoryOrNull(kClass: KClass<*>): ComponentFactory?

	/**
	 * Returns a chain of factories which shall build a chain of components of the specified [kClass]
	 * or `null` in case no chain was found which is able to build a chain of components of the given type.
	 */
	fun getFactoryForChainedOrNull(kClass: KClass<*>): Sequence<ComponentFactory>?

	/**
	 * Merges the given [componentFactoryContainer] (if not `null`) with `this` [ComponentFactoryContainer]
	 * creating a new [ComponentFactoryContainer] where defined dependencies in [componentFactoryContainer]
	 * will have precedence over dependencies defined in this instance.
	 *
	 * For instance, if this object has defined a [ArgsRangeDecider] and
	 * the given [componentFactoryContainer] as well, then the resulting [ComponentFactoryContainer] will return
	 * the [ArgsRangeDecider] of [componentFactoryContainer] when asked for it.
	 */
	fun merge(componentFactoryContainer: ComponentFactoryContainer?): ComponentFactoryContainer

	/**
	 * Extension point for [ComponentFactoryContainer] (such as factory methods).
	 */
	companion object
}

/**
 * Provides a [build] lambda which produces the component and specifies via [producesSingleton] whether this
 * component should be treated as singleton or not.
 *
 * @since 2.0.0
 *
 * Copied from [Atrium](https://atriumlib.org).
 */
class ComponentFactory(val build: (ComponentFactoryContainer) -> Any, val producesSingleton: Boolean)
