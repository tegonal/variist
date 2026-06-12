package com.tegonal.variist.testutils

import com.tegonal.variist.config.ComponentFactoryContainer
import com.tegonal.variist.config._components
import com.tegonal.variist.generators.arb
import com.tegonal.variist.generators.impl.BaseArbArgsGenerator
import com.tegonal.variist.utils.repeatForever

class RepeatGivenListArbArgsGenerator<T>(
	private val list: List<T>,
	componentFactoryContainer: ComponentFactoryContainer = arb._components,
) : BaseArbArgsGenerator<T>(componentFactoryContainer) {

	var seedOffsets = mutableListOf<Int>()
		private set

	constructor(
		finiteSequence: Sequence<T>,
		componentFactoryContainer: ComponentFactoryContainer = com.tegonal.variist.generators.arb._components,
	) : this(finiteSequence.toList(), componentFactoryContainer)

	override fun generate(seedOffset: Int): Sequence<T> {
		seedOffsets.add(seedOffset)
		return repeatForever().flatMap { list }
	}
}
