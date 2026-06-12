package com.tegonal.variist.testutils

import com.tegonal.variist.config.ComponentFactoryContainer
import com.tegonal.variist.config._components
import com.tegonal.variist.generators.arb
import com.tegonal.variist.generators.impl.BaseArbArgsGenerator
import com.tegonal.variist.utils.repeatForever

class RepeatProvidedListArbArgsGenerator<T>(
	private val listProvider: (seedOffset: Int) -> List<T>,
	componentFactoryContainer: ComponentFactoryContainer = arb._components,
) : BaseArbArgsGenerator<T>(componentFactoryContainer) {

	var seedOffsets = mutableListOf<Int>()
		private set

	override fun generate(seedOffset: Int): Sequence<T> {
		seedOffsets.add(seedOffset)
		return repeatForever().flatMap { listProvider(seedOffset) }
	}
}
