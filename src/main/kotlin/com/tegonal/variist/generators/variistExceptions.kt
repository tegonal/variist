package com.tegonal.variist.generators

/**
 * Caused in case a generation failure occurred.
 *
 * @since 3.0.0
 */
class VariistGenerationException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
