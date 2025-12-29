package com.tegonal.variist.providers

import com.tegonal.variist.generators.arb
import com.tegonal.variist.generators.ordered

/**
 * Provides predefined args providers based on the default [arb] and [ordered].
 *
 * @since 2.0.0
 */
interface PredefinedArgsProviders :
	PredefinedBooleanProviders,
	PredefinedBoundProviders,
	PredefinedCharProviders,
	PredefinedNumberProviders
