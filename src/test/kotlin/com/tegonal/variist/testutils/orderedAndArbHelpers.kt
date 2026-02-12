package com.tegonal.variist.testutils

import com.tegonal.variist.config.*
import com.tegonal.variist.generators.ArbExtensionPoint
import com.tegonal.variist.generators.OrderedExtensionPoint
import com.tegonal.variist.generators.SemiOrderedArgsGenerator

fun createOrderedWithCustomConfig(customConfig: VariistConfig): OrderedExtensionPoint =
	ComponentFactoryContainer.createBasedOnConfig(customConfig).ordered

fun createArbWithCustomConfig(customConfig: VariistConfig): ArbExtensionPoint =
	ComponentFactoryContainer.createBasedOnConfig(customConfig).arb

val orderedWithSeed0 = createOrderedWithCustomConfig(VariistConfig(seed = Seed(0)))

fun <T> SemiOrderedArgsGenerator<T>.generateToList(amount: Int): List<T> =
	generate(offset = 0).take(amount).toList()
