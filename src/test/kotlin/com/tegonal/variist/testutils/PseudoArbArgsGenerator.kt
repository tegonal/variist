package com.tegonal.variist.testutils

import com.tegonal.variist.config.ComponentFactoryContainer
import com.tegonal.variist.config._components
import com.tegonal.variist.generators.arb
import com.tegonal.variist.generators.impl.BaseArbArgsGenerator
import com.tegonal.variist.utils.repeatForever

class PseudoArbArgsGenerator<T>(
	private val list: List<T>,
	seedBaseOffset: Int = 0,
	componentFactoryContainer: ComponentFactoryContainer = arb._components,
) : BaseArbArgsGenerator<T>(componentFactoryContainer, seedBaseOffset) {

	constructor(
		finiteSequence: Sequence<T>,
		seedBaseOffset: Int = 0,
		componentFactoryContainer: ComponentFactoryContainer = com.tegonal.variist.generators.arb._components,
	) : this(finiteSequence.toList(), seedBaseOffset, componentFactoryContainer)

	override fun generate(seedOffset: Int): Sequence<T> =
		repeatForever().flatMap { list }.drop((seedBaseOffset + seedOffset) % list.size)
}
