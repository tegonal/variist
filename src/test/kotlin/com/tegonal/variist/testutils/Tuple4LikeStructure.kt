package com.tegonal.variist.testutils

import ch.tutteli.kbox.Tuple2Like
import ch.tutteli.kbox.Tuple4Like

data class Tuple2LikeStructure<A1, A2>(
	val a1: A1,
	val a2: A2,
) : Tuple2Like<A1, A2>


data class Tuple4LikeStructure<A1, A2, A3, A4>(
	val a1: A1,
	val a2: A2,
	val a3: A3,
	val a4: A4
) : Tuple4Like<A1, A2, A3, A4>
