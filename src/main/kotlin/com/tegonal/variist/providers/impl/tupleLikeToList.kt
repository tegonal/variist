package com.tegonal.variist.providers.impl

import ch.tutteli.kbox.*

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
fun tupleAndTupleLikeToList(maybeTupleOrTupleLike: Any?): List<*>? =
	tupleToList(maybeTupleOrTupleLike) ?: tupleLikeToList(maybeTupleOrTupleLike)

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
fun tupleToList(maybeTuple: Any?): List<*>? = when (maybeTuple) {
	is Tuple2<*, *> -> maybeTuple.toList()
	is Tuple3<*, *, *> -> maybeTuple.toList()
	is Tuple4<*, *, *, *> -> maybeTuple.toList()
	is Tuple5<*, *, *, *, *> -> maybeTuple.toList()
	is Tuple6<*, *, *, *, *, *> -> maybeTuple.toList()
	is Tuple7<*, *, *, *, *, *, *> -> maybeTuple.toList()
	is Tuple8<*, *, *, *, *, *, *, *> -> maybeTuple.toList()
	is Tuple9<*, *, *, *, *, *, *, *, *> -> maybeTuple.toList()
	else -> null
}

/**
 * !! No backward compatibility guarantees !!
 * Reuse at your own risk
 *
 * @since 2.0.0
 */
fun tupleLikeToList(maybeTupleLike: Any?): List<*>? = when (maybeTupleLike) {

	is Tuple2Like<*, *> -> listOf(maybeTupleLike.component1(), maybeTupleLike.component2())
	is Tuple3Like<*, *, *> -> listOf(
		maybeTupleLike.component1(),
		maybeTupleLike.component2(),
		maybeTupleLike.component3()
	)

	is Tuple4Like<*, *, *, *> -> listOf(
		maybeTupleLike.component1(),
		maybeTupleLike.component2(),
		maybeTupleLike.component3(),
		maybeTupleLike.component4()
	)

	is Tuple5Like<*, *, *, *, *> -> listOf(
		maybeTupleLike.component1(),
		maybeTupleLike.component2(),
		maybeTupleLike.component3(),
		maybeTupleLike.component4(),
		maybeTupleLike.component5()
	)

	is Tuple6Like<*, *, *, *, *, *> -> listOf(
		maybeTupleLike.component1(),
		maybeTupleLike.component2(),
		maybeTupleLike.component3(),
		maybeTupleLike.component4(),
		maybeTupleLike.component5(),
		maybeTupleLike.component6()
	)

	is Tuple7Like<*, *, *, *, *, *, *> -> listOf(
		maybeTupleLike.component1(),
		maybeTupleLike.component2(),
		maybeTupleLike.component3(),
		maybeTupleLike.component4(),
		maybeTupleLike.component5(),
		maybeTupleLike.component6(),
		maybeTupleLike.component7()
	)

	is Tuple8Like<*, *, *, *, *, *, *, *> -> listOf(
		maybeTupleLike.component1(),
		maybeTupleLike.component2(),
		maybeTupleLike.component3(),
		maybeTupleLike.component4(),
		maybeTupleLike.component5(),
		maybeTupleLike.component6(),
		maybeTupleLike.component7(),
		maybeTupleLike.component8()
	)

	is Tuple9Like<*, *, *, *, *, *, *, *, *> -> listOf(
		maybeTupleLike.component1(),
		maybeTupleLike.component2(),
		maybeTupleLike.component3(),
		maybeTupleLike.component4(),
		maybeTupleLike.component5(),
		maybeTupleLike.component6(),
		maybeTupleLike.component7(),
		maybeTupleLike.component8(),
		maybeTupleLike.component9()
	)

	else -> null
}
