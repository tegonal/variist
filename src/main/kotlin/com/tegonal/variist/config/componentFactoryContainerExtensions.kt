package com.tegonal.variist.config

import com.tegonal.variist.config.impl.*
import com.tegonal.variist.generators.ArbExtensionPoint
import com.tegonal.variist.generators.OrderedExtensionPoint
import com.tegonal.variist.generators.impl.DefaultArbExtensionPoint
import com.tegonal.variist.generators.impl.DefaultOrderedExtensionPoint
import com.tegonal.variist.providers.*
import com.tegonal.variist.providers.impl.DefaultArgsGeneratorToArgumentsConverter
import com.tegonal.variist.providers.impl.DefaultGenericArgsGeneratorCombiner
import com.tegonal.variist.utils.impl.loadService
import kotlin.random.Random
import kotlin.reflect.KClass

/**
 * Creates a [ComponentFactoryContainer] based on the given [config].
 *
 * The given [config] is used as [VariistConfig] and influences
 * [SuffixArgsGeneratorDecider] ([VariistConfig.activeSuffixArgsGeneratorDecider]) and
 * [ArgsRangeDecider] ([VariistConfig.activeArgsRangeDecider])
 *
 * @since 2.0.0
 */
fun ComponentFactoryContainer.Companion.createBasedOnConfig(config: VariistConfig): ComponentFactoryContainer =
	ComponentFactoryContainer.create(
		mapOf(
			VariistConfig::class createSingletonVia { config },
			SuffixArgsGeneratorDecider::class createVia {
				loadService(config.activeSuffixArgsGeneratorDecider)
			},
			GenericArgsGeneratorCombiner::class createSingletonVia { _ ->
				DefaultGenericArgsGeneratorCombiner()
			},
			ArgsGeneratorToArgumentsConverter::class createSingletonVia { _ ->
				DefaultArgsGeneratorToArgumentsConverter()
			},
			ArgsRangeDecider::class createSingletonVia { _ ->
				loadService(config.activeArgsRangeDecider)
			},

			RandomFactory::class createSingletonVia {
				KotlinRandomFactory()
			}
		),
		mapOf(
			//TODO 2.1.0 allow to sort them?
			createChainFromServiceLoaders<AnnotationDataDeducer>(),
		)
	)

/**
 * Creates a [ComponentFactoryContainer] based on the given [components] and [chainedComponents].
 *
 * @since 2.0.0
 */
fun ComponentFactoryContainer.Companion.create(
	components: Map<KClass<*>, ComponentFactory>,
	chainedComponents: Map<KClass<*>, Sequence<ComponentFactory>> = emptyMap()
) = DefaultComponentFactoryContainer.create(components, chainedComponents)

/**
 * Quick access to build a [VariistConfig] from this [ComponentFactoryContainer].
 *
 * @since 2.0.0
 */
val ComponentFactoryContainer.config: VariistConfig get() = build<VariistConfig>()

/**
 * Creates a [Random] whose seed is based on the [VariistConfig.seed] of this [ComponentFactoryContainer] and the given
 * [seedOffset].
 *
 * @since 2.0.0
 */
fun ComponentFactoryContainer.createVariistRandom(seedOffset: Int): Random =
	build<RandomFactory>().create(config.seed.value + seedOffset).also { random ->
		config.skip?.also { repeat(it) { random.nextInt() } }
	}

/**
 * Creates an [OrderedExtensionPoint] based on this [ComponentFactoryContainer].
 *
 * @since 2.0.0
 */
val ComponentFactoryContainer.ordered get() : OrderedExtensionPoint = DefaultOrderedExtensionPoint(this)

/**
 * Creates an [ArbExtensionPoint] based on this [ComponentFactoryContainer].
 *
 * @since 2.0.0
 */
val ComponentFactoryContainer.arb get() : ArbExtensionPoint = DefaultArbExtensionPoint(this, seedBaseOffset = 0)


/**
 * Returns the component of type [T] using a corresponding factory or throws an [IllegalStateException]
 * in case no factory was found which is able to build a component of the given type.
 *
 * @throws IllegalStateException in case [ComponentFactoryContainer.buildOrNull] returns `null`
 *   because not suitable factory was found.
 * @throws ClassCastException in case the factory returns an illegal type.
 *
 * @since 2.0.0
 *
 * Copied from [Atrium](https://atriumlib.org).
 */
inline fun <reified T : Any> ComponentFactoryContainer.build(): T = buildOrNull(T::class)
	?: error("No factory is registered in this ComponentFactoryContainer which is able to build a ${T::class.qualifiedName}")

/**
 * Returns a chain of components of type [T] using a corresponding factory or throws an [IllegalStateException]
 * in case no factory was found which is able to build a chain of components of the given type.

 * @throws IllegalStateException in case [ComponentFactoryContainer.buildChainedOrNull] returns `null`
 *   because no suitable factory was found.
 * @throws ClassCastException in case one of factories returns an illegal type.
 *
 * @since 2.0.0
 *
 * Copied from [Atrium](https://atriumlib.org).
 */
inline fun <reified T : Any> ComponentFactoryContainer.buildChained(): Sequence<T> = buildChainedOrNull(T::class)
	?: error("No factory is registered in this ComponentFactoryContainer which is able to build a chain of ${T::class.qualifiedName}")
