package com.tegonal.variist.config

/**
 * The base class of [Exception]s which are raised in conjunction with [VariistConfig].
 *
 * @since 2.0.0
 */
abstract class VariistConfigException(message: String, cause: Throwable?) : RuntimeException(message, cause)

/**
 * Caused in case a config file cannot be parsed.
 *
 * @since 2.0.0
 */
class VariistParseException(message: String, cause: Throwable? = null) : VariistConfigException(message, cause)

/**
 * Caused in case a specified deadline passed.
 *
 * @since 2.0.0
 */
class VariistDeadlineException(message: String) : VariistConfigException(message, null)
