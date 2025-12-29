package com.tegonal.variist.config

import kotlin.random.Random

/**
 * Factory to create a [Random].
 *
 * @since 2.0.0
 */
interface RandomFactory {
	/**
	 * Creates a [Random] based on the given [seed].
	 */
	fun create(seed: Int): Random
}
