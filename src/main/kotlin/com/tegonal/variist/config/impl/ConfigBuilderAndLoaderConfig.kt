package com.tegonal.variist.config.impl

import com.tegonal.variist.config.VariistConfig
import com.tegonal.variist.config.VariistConfigBuilder
import com.tegonal.variist.config.VariistPropertiesLoaderConfig

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
class ConfigBuilderAndLoaderConfig(
	val builder: VariistConfigBuilder,
	val loaderConfig: VariistPropertiesLoaderConfig
)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
fun ConfigBuilderAndLoaderConfig.build(): VariistConfig = builder.build()
