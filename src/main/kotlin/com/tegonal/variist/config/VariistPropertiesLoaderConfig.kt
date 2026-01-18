package com.tegonal.variist.config

import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.HashMap

/**
 * Contains properties which are not exposed via [VariistConfig] and influence the loading of the local
 * [VariistConfig] file.
 *
 * @since 2.0.0
 */
class VariistPropertiesLoaderConfig(
	/**
	 * Defines in about how many minutes the reminder triggers when fixing a [VariistConfig] property.
	 * Not all properties are considered being fixed when defined, at the time of writing those are:
	 * - [VariistConfig.seed],
	 * - [VariistConfig.skip],
	 * - [VariistConfig.requestedMinArgs]
	 * - [VariistConfig.maxArgs])
	 */
	var remindAboutFixedPropertiesAfterMinutes: Int = 60,

	/**
	 * Defines where the local properties files (e.g. `variist.local.properties`) are stored, i.a. that Variist can
	 * can be added/updated/removed [errorDeadlines].
	 */
	var localPropertiesDir: Path = Paths.get("./src/test/resources"),

	/**
	 * Fixing certain config properties (such as [VariistConfigBuilder.seed], [VariistConfigBuilder.skip],
	 * [VariistConfigBuilder.requestedMinArgs], [VariistConfigBuilder.maxArgs]
	 */
	var errorDeadlines: HashMap<String, LocalDateTime> = HashMap(),

	/**
	 * If defined, then the local config, i.e. what is usually loaded via `variist.local.properties`, is loaded from
	 * another properties file with the given resource name.
	 *
	 * [localPropertiesPrefix] must to be defined in such as case as well.
	 */
	var localPropertiesResourceName: String? = null,

	/**
	 * Specifies the prefix which identifies Variist properties in the properties file specified by
	 * [localPropertiesResourceName].
	 *
	 * If this is defined, then [localPropertiesResourceName] must be defined as well.
	 */
	var localPropertiesPrefix: String? = null,
)
