package com.tegonal.variist.generators

import com.tegonal.variist.generators.impl.throwMaterialisingSemiOrderedArgsGeneratorNotSupported

@Suppress("UnusedReceiverParameter")
@Deprecated(
	""""
	Materialising means fixing the undefined/random part of an SemiOrderedArgsGenerator.
	Normally you do not want to turn an SemiOrderedArgsGenerator into an OrderedArgsGenerator as you basically
	loose the randomness you added in the first place.
	If you still want to do this, then use:

	let { g -> g.generate().take(g.size) }.filter(predicate).toList().let(ordered::fromList)
	""",
	ReplaceWith("let { g -> g.generate().take(g.size) }.filter(predicate).toList().let(ordered::fromList)"),
	level = DeprecationLevel.ERROR
)
fun <T> SemiOrderedArgsGenerator<T>.filterMaterialised(@Suppress("UNUSED_PARAMETER") predicate: (T) -> Boolean): OrderedArgsGenerator<T> =
	throwMaterialisingSemiOrderedArgsGeneratorNotSupported()

@Suppress("UnusedReceiverParameter")
@Deprecated(
	""""
	Materialising means fixing the undefined/random part of an SemiOrderedArgsGenerator.
	Normally you do not want to turn an SemiOrderedArgsGenerator into an OrderedArgsGenerator as you basically
	loose the randomness you added in the first place.
	If you still want to do this, then use:

	let { g -> g.generate().take(g.size) }.filterNot(predicate).toList().let(ordered::fromList)
	""",
	ReplaceWith("let { g -> g.generate().take(g.size) }.filterNot(predicate).toList().let(ordered::fromList)"),
	level = DeprecationLevel.ERROR
)
fun <T> SemiOrderedArgsGenerator<T>.filterNotMaterialised(@Suppress("UNUSED_PARAMETER") predicate: (T) -> Boolean): OrderedArgsGenerator<T> =
	throwMaterialisingSemiOrderedArgsGeneratorNotSupported()


@Suppress("UnusedReceiverParameter")
@Deprecated(
	""""
	Materialising means fixing the undefined/random part of an SemiOrderedArgsGenerator.
	Normally you do not want to turn an SemiOrderedArgsGenerator into an OrderedArgsGenerator as you basically
	loose the randomness you added in the first place.
	If you still want to do this, then use:

	let { g -> g.generate().take(g.size) }.let(transform).toList().let(ordered::fromList)
	""",
	ReplaceWith("let { g -> g.generate().take(g.size) }.filter(predicate).toList().let(ordered::fromList)"),
	level = DeprecationLevel.ERROR
)
fun <T, R> SemiOrderedArgsGenerator<T>.transformMaterialised(@Suppress("UNUSED_PARAMETER") transform: (Sequence<T>) -> Sequence<R>): OrderedArgsGenerator<T> =
	throwMaterialisingSemiOrderedArgsGeneratorNotSupported()
