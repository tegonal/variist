<!-- for main -->
<!--
[![Download](https://img.shields.io/badge/Download-v2.0.0--RC--2-%23007ec6)](https://central.sonatype.com/artifact/com.tegonal.variist/variist/2.0.0-RC-3)
[![EUPL 1.2](https://img.shields.io/badge/%E2%9A%96-EUPL%201.2-%230b45a6)](https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12 "License")
[![Quality Assurance](https://github.com/tegonal/variist/actions/workflows/quality-assurance.yml/badge.svg?event=push&branch=main)](https://github.com/tegonal/variist/actions/workflows/quality-assurance.yml?query=branch%3Amain)
[![Newcomers Welcome](https://img.shields.io/badge/%F0%9F%91%8B-Newcomers%20Welcome-blueviolet)](https://github.com/tegonal/variist/issues?q=is%3Aissue+is%3Aopen+label%3A%22good+first+issue%22 "Ask in discussions for help")
-->
<!-- for main end -->
<!-- for release -->

[![Download](https://img.shields.io/badge/Download-v2.0.0--RC--2-%23007ec6)](https://central.sonatype.com/artifact/com.tegonal.variist/variist/2.0.0-RC-3)
[![EUPL](https://img.shields.io/badge/%E2%9A%96-EUPL%201.2-%230b45a6)](https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12 "License")
[![Newcomers Welcome](https://img.shields.io/badge/%F0%9F%91%8B-Newcomers%20Welcome-blueviolet)](https://github.com/tegonal/variist/issues?q=is%3Aissue+is%3Aopen+label%3A%22good+first+issue%22 "Ask in discussions for help")

<!-- for release end -->

# Variist

![Variist](https://raw.githubusercontent.com/tegonal/variist/main/.idea/icon.png) V like Variist,
a Kotlin library which helps you in setting up data, particularly for JUnit's parameterized tests and prioritise them in
case you don't have enough time to execute all of them.

---
❗ You are taking a *sneak peek* at the next version. It could be that some features you find on this page are not
released yet.  
Please have a look at the README of the corresponding release/git tag. Latest
version: [README of v2.0.0-RC-3](https://github.com/tegonal/variist/tree/main/README.md).

---

**Table of Contents**

- [Into](#intro)
- [Installation](#installation)
- [Examples](#examples)
	- [Your first parameterized Test](#your-first-parameterized-test)
	- [Ordered and arbitrary arguments generators](#ordered-and-arbitrary-arguments-generators)
		- [Predefined ordered factories](#predefined-ordered-factories)
		- [Predefined arb factories](#predefined-arb-factories)
		- [Predefined args providers](#predefined-args-providers)
	- [Combinators](#combinators)
		- [Generic combine](#generic-combine)
		- [ordered.cartesian](#ordered-cartesian)
		- [zip](#zip)
		- [zipDependent](#zipDependent)
		- [flatZipDependent](#flatZipDependent)
		- [transform](#transform)
		- [map](#map)
		- [filter/filterNot](#filter)
		- [chunked](#chunked)
		- [ordered.concatenation](#ordered-concatenation)
		- [arb.mergeWeighted](#arb-mergeWeighted)
		- [ordered.toArbArgsGenerator](#ordered-toArbArgsGenerator)
        - [ordered.fromArbs](#ordered-fromArbs) 
- [Use Variist in other contexts than JUnit](#use-variist-in-other-contexts-than-junit)
- [Configuration](#configuration)
	- [Profiles and Envs](#profiles-and-envs)
	- [Adjust the number of runs](#adjust-the-number-of-args)
		- [maxArgs](#maxArgs)
		- [requestedMinArgs](#requestedMinArgs)
	- [Fixing the seed](#fixing-the-seed)
		- [Error Deadlines](#errordeadlines)
	- [Change the ArgsRangeDecider](#change-the-ArgsRangeDecider)
	- [Use an own AnnotationDataDeducer](#use-an-own-AnnotationDataDeducer)
	- [Use a SuffixArgsGeneratorDecider](#use-a-SuffixArgsGeneratorDecider)
- [Helpers](#helpers)
	- [Random helpers](#random-helpers)
	- [Sequence helpers](#sequence-helpers)
	- [BigInt helpers](#bigint-helpers)
- [Code Documentation](#code-documentation)
- [Contributors and contribute](#contributors-and-contribute)
- [License](#license)

# Intro

Variist might resemble a property based testing library but is more data-driven oriented.
Its focus is on tests that take longer (integration, e2e and system integration tests) where shrinking is too costly.
But of course, you can also use it for data-driven unit tests (see [ordered.fromArbs](#ordered-fromArbs) as an example).  
It comes with extra support for JUnit but can
also [be used in other contexts](#use-variist-in-other-contexts-than-junit)
where you want to generate data (or with other test-runners).

Since it is only an addition to JUnit (a library, not an own test-runner as e.g. jqwik) you do not have to change any
existing JUnit setup.

Take a look at [Your first parameterized Test](#your-first-parameterized-test) to see how you can use it and then
come back to the [Installation](#installation) section to see what dependency you need to set up.

# Installation

Variist is published to maven central.

*build.gradle.kts*:

```kotlin
repositories {
	mavenCentral()
}
dependencies {
	testImplementation("com.tegonal.variist:variist:2.0.0-RC-3")
}
```

Minimum requirements:

- Kotlin: 1.9
- JDK: 11
- JUnit: 5.13.0

# Examples

## Your first parameterized Test

Likewise JUnit Jupiter params provides `@MethodSource`, Variist provides `@ArgsSource`.

<!--suppress HtmlUnknownTag -->
<code-first-1>

```kotlin
package readme.examples

import com.tegonal.variist.providers.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest

class FirstTest : PredefinedArgsProviders {

	@ParameterizedTest
	@ArgsSource("myProvider")
	fun positiveNumberTimesMinusOneIsNegative(positiveNumber: Int) {
		assertTrue(positiveNumber * -1 < 0)
	}

	companion object {
		@JvmStatic
		fun myProvider() = 1..20
	}
}
```

</code-first-1>

At a first glance, `ArgsSource` in the above example behaves the same way as
[ `MethodSource`](https://docs.junit.org/current/user-guide/#writing-tests-parameterized-tests-sources-MethodSource)
but the runtime behaviour is different. If you run the above code, then per default only a window of 5 values is taken
from the range `1..20` based on a randomly chosen seed resulting in 5 runs.
The base assumption of Variist is that your tests are expensive to execute and that you don't have the time to run
all values of interested in one go. But, eventually, over multiple runs, it would still be nice to cover all values.
The [configuration](#configuration) section explains how we can adjust the default profiles and more.
For now, we continue without going too much into configuration details.

> [!Note]
> Unfortunately, due to a [bug in intellij](https://youtrack.jetbrains.com/issue/KTIJ-35010), you won't be able to
> click on `myProvider` and jump to the function definition. As workaround, you can define your own ArgsSource as
> follows:
> ```kotlin
> package com.example
> 
> import com.tegonal.variist.providers.ArgsArgumentProvider
> import com.tegonal.variist.providers.ArgsSourceLike
> import org.intellij.lang.annotations.Language
> import org.junit.jupiter.params.provider.ArgumentsSource
> 
> @Target(AnnotationTarget.FUNCTION)
> @Retention(AnnotationRetention.RUNTIME)
> @MustBeDocumented
> @ArgumentsSource(ArgsArgumentProvider::class)
> @ArgsSourceLike
> annotation class ArgsSource(
>     @Suppress("unused") // it is used via reflection by Variist
>     @Language("jvm-method-name") val methodName: String,
> )
> ```

Although Variist allows that one can provide "raw" values as in `1..20` (could also have been `listOf(1, 2, 3, ...)`)
it provides `ArgsGenerator`s which are way more efficient.
Raw values are turned into a `List` and then passed to `ordered.fromList`. The next section outlines what `ordered` is.

## Ordered and arbitrary arguments generators

Variist provides two entry points to create an `ArgsGenenerator`: `ordered` and `arb`.

`ordered` can be used to define an ordered (not to be confused with sorted) list of finite
values where the corresponding `OrderedArgsGenerator` generates a sequence which repeats them indefinitely.
For instance, if you use `ordered.of('a', 'b')` as provider, then `OrderedArgsGenerator.size = 2` and correspondingly
it results in two runs. You either will get `'a'` in the first run and in the second `'b'` or
you will get `'b'` in the first run and `'a'` in the second.
That is because the resulting sequence repeats indefinitely `'a', 'b', 'a', 'b', ... `
and it depends on a randomly chosen seed what offset is taken.

The "counterpart" of `ordered` is `arb` that allows to create `ArbArgsGenerator`s which per definition generate
an infinite sequence of values where it is basically not known if they follow some order or not.
The default implementations are almost all based on `Random`.
The number of runs of such a provider is in theory infinite as well (`ArbArgsGenerator.size` doesn't exist) but gets
limited by the [profile](#profiles-and-envs) the test falls into, the [environment](#profiles-and-envs) where the test
runs and what configuration was set up for this combination.
Also `OrderedArgsGenerator` are limited by profile/env but introduce an own limit in
addition by its `size` property. Following an example of how to use an `ArgsGenerator` as an `ArgsSource` provider.

<code-arb-provider>

```kotlin
class ArbProviderTest : PredefinedArgsProviders {

	@ParameterizedTest
	@ArgsSource("arb1To50000")
	fun positiveNumberTimesMinusOneIsNegative(positiveNumber: Int) {
		assertTrue(positiveNumber * -1 < 0)
	}

	companion object {
		@JvmStatic
		fun arb1To50000() = arb.intFromTo(1, 50_000)
	}
}
```

</code-arb-provider>

In contrast to the [first parameterized test example](#your-first-parameterized-test) where we used
raw values (which are turned into a `List` and then passed to `ordered.fromList`), we now want to be sure the test
covers integers from `1..50000` and not only `1..20` (and in [predefined args providers](#predefined-args-providers) you
will see how we
define that it shall work for all positive integers). But why did we use `arb` and not `ordered`? The short answer: too
many values -- say your test takes ~2s and you cannot parallelise, running all would already take ~24h -- and most
likely this is not your single test. In this case we do not expect that we cover all cases in a reasonable amount of
time, hence we use `arb`. An `ArbArgsGenerator` has a different runtime behaviour regarding:

1. how [multiple ArgsGenerators are combined implicitly](#generic-combine) (which we discuss there)
2. how many runs are generated

Let's take a look at a simple example to see the difference in how many runs are generated.
Where `OrderedArgsGenerator`s generate a window of all possible values (i.e. still ordered),
an `ArbArgsGenerator` generates arbitrary/random values (possibly the same value multiple times).
Following an example where `maxArgs=5` (more on `maxArgs` in the [adjust the number of args](#adjust-the-number-of-args)
section):

```kotlin
ordered.of(1, 2, 3) // results in 3 runs: 1, 2, 3 or 2, 3, 1 or 3, 1, 2
arb.of(1, 2, 3)     // results in 5 runs, order unknown (will change to 3 runs in v2.1.0)
```

As a rule of thumb, use `ordered` only if you have explicit restrictions and you would test all of them if the tests
were faster. Whenever you are in doubt, use `arb` and switch to `ordered` once you are convinced that it is better
suited. Read on to get a better understanding how they differ (especially the [Combinators](#combinators) section).

### Predefined ordered factories

Following a few examples what predefined factories exist on `ordered` (take a look at the
[Code Documentation](#code-documentation) to see all):

<code-ordered-1>

```kotlin
import com.tegonal.variist.generators.*

enum class Color {
	Red, Blue, Green
}

ordered.of(1, 3, 2)
ordered.fromEnum<Color>()
ordered.fromList(listOf(6, 8, 1))
ordered.fromArray(arrayOf(4, 2, 7))
ordered.fromRange(1..10)
ordered.fromProgression(1..10 step 2)

ordered.boolean()
ordered.intFromUntil(1, 5)
ordered.longFromTo(1, 5)
//...
```

</code-ordered-1>

### Predefined arb factories

Following a few examples what predefined factories exist on `arb` (take a look at the
[Code Documentation](#code-documentation) to see all):

<code-arb-1>

```kotlin
import com.tegonal.variist.generators.*

enum class Color {
	Red, Blue, Green
}

arb.of(1, 2, 3)
arb.fromEnum<Color>()
arb.fromList(listOf(1, 2, 3))
arb.fromArray(arrayOf(1, 2, 3))
arb.fromRange(1..10)
arb.fromProgression(1..10 step 2)

arb.boolean()
arb.char()
arb.int()
arb.long()
arb.double()

arb.intPositive()
arb.intNegative()
arb.longPositiveAndZero()
arb.longNegativeAndZero()
// ...
arb.intFromUntil(-1000, 1000)
arb.longFromTo(7, 420)
arb.bigIntFromUntil(BigInt.ZERO, BigInt.TEN)
//...

LocalDate.now().let { now ->
	arb.localDateFromUntil(now, now.plusMonths(4))
}
LocalDateTime.now().let { now ->
	arb.localDateTimeFromUntil(now, now.plusHours(48), ChronoUnit.MINUTES)
}
// ZonedDateTime/OffsetDateTime
//...

arb.charRange(minInclusive = 'A', maxInclusive = 'Z', minSize = 1)
arb.intBounds(minInclusive = 1, maxInclusive = 1000, minSize = 3, maxSize = 10)

arb.longBoundsBased(minInclusive = -10, maxInclusive = 10, minSize = 0, maxSize = 4) { lower, upper ->
	//..
}
//...

arb.string(minLength = 0, maxLength = 20, allowedRanges = UnicodeRanges.ASCII_PRINTABLE.ranges)
```

</code-arb-1>

### Predefined args providers

For some `arb` and a few `ordered` definitions we provide predefined args providers which you can use in `ArgsSource`.
You only need to extend (directly or indirectly) Variist's `PredefinedArgsProviders`. Following an example:

<code-predefined-1>

```kotlin
package readme.examples

import com.tegonal.variist.providers.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest


class PredefinedArgsProvidersTest : PredefinedArgsProviders {

	@ParameterizedTest
	// uses the predefined ArbArgsGenerator arbIntPositive defined in PredefinedArgsProviders
	@ArgsSource("arbIntPositive")
	fun positiveNumberTimesMinusOneIsNegative(positiveNumber: Int) {
		assertTrue(positiveNumber * -1 < 0)
	}
}
```

</code-predefined-1>

Typically, you will reuse your custom providers in several tests. We recommend you create your own interfaces which
contain predefined `ArgsSource` providers and one `ArgsProviders` which extends all of them
(see `com.tegonal.variist.providers.PredefinedArgsProviders` for an example) and you might want to extend
from Variist's `PredefinedArgsProviders` as well.

## Combinators

Variist provides different combinators to produce new `ArgsGenerator`.

### Generic Combine

The most frequently used combinator is probably to combine multiple `ArgsGenerator`s in some way. A reason why
we added a bit of magic to Variist. The idiomatic way to define that we want to combine multiple generators is
to use `Tuple` (from ch.tutteli.kbox) which exists up to `Tuple9`:

<code-combine-tuple>

```kotlin
import ch.tutteli.kbox.Tuple
import com.tegonal.variist.generators.*
import com.tegonal.variist.providers.*
import org.junit.jupiter.params.ParameterizedTest

class CombineTupleTest : PredefinedArgsProviders {

	@ParameterizedTest
	@ArgsSource("ageAndArbName")
	fun foo(age: Int, name: String) {
		//...
	}

	companion object {
		@JvmStatic
		fun ageAndArbName() = Tuple(
			ordered.intFromTo(15, 30),
			arb.string(minLength = 3, maxLength = 50)
		)
	}
}
```

</code-combine-tuple>

Combining two `OrderedArgsGenerator`s A and B (or `SemiOrderedArgsGenerator`s) results in an `OrderedArgsGenerator`
representing their cartesian product and the size correspondingly `A.size * B.size`. I.e. such combinations can
grow quickly, but Variist has you covered in therms that this is just a definition (nothing generated yet) and
you still execute only a window of those values in a fast and efficient way.
On the other hand, combining two `ArbArgsGenerator` means zipping them and results in another `ArbArgsGenerator`.

Combining an `OrderedArgsGenerators` and an `ArbArgsGenerator` works as well (as shown in the example) and uses
again zip behaviour, where the result is no longer an `OrderedArgsGenerators` but a `SemiOrderedArgsGenerators` (which
still has a `size` property). You only need to make sure that your first `ArgsGenerator` in the tuple is a
`SemiOrderedArgsGenerator` (`OrderedArgsGenerator` is a subtype of `SemiOrderedArgsGenerator`).
If your first `ArgsGenerator` in the tuple is an `ArbArgsGenerator` then all generators which follow need to be an
`ArbArgsGenerator` as well (otherwise it will fail at runtime).

Maybe you are asking yourself how many runs result out of the above definition. As long as no `maxArgs` definition
restricts it, it will result in `SemiOrderedArgsGenerator.size` runs.
So for `ordered.intFromTo(15, 30)`, we will get 30 - 15 + 1 = 16 runs at max (+1 since bounds are inclusive for
`intFromTo`). Which means we combine 16 arbitrary names with the defined ages.

What if you want to combine more than 9 `ArgsGenerators`? In such a case you have to combine them via
`TupleX.combineAll()` to get an `ArgsGenerators<TupleX<...>>` which then again can be used in a tuple.
Or use `cartesian`, `zip` to create an `ArgsGenerators<Tuple2<...>>`.
Following an example (using less than 9 `ArgsGenerators` for brevity -- imports omitted, same as in
`CombineTupleTest` above)

<code-combine-manually>

```kotlin
class CombineManuallyTest : PredefinedArgsProviders {

	@ParameterizedTest
	@ArgsSource("arbNumbersAndChar")
	fun bar(i: Int, l: Long, d: Double, b: BigInt, c: Char) {
		//...
	}

	companion object {
		@JvmStatic
		fun arbNumbersAndChar() = run { // use run to let the compiler infer the return type
			val numbers = Tuple(
				arb.int().zip(arb.long()), // combines them into an ArbArgsGenerators<Tuple2<Int, Long>>
				arb.double(),
				arb.bigIntFromUntil(BigInt.ZERO, BigInt.TEN)
			).combineAll() // combines all into an ArbArgsGenerators<Tuple3<...>>

			Tuple(
				numbers, // can again be used in a tuple to define that it shall be combined
				arb.char()
			) // the ArgsProvider will flatten all tuples, resulting in 5 arguments (see bar above)
		}
	}
}
```

</code-combine-manually>

Note two things. First, tuples are flattened in the process of transforming the definition into JUnit's `Arguments`.
Second, `Tuple2`/`Tuple3` are just type aliases for `Pair`/`Triple`. Which means, if you want that an argument is like a
`Pair`/`Triple` without being flattened, then define e.g. a `data class`.

The advantage of using tuples instead of manual `cartesian`/`zip` are:

- a) readability (less consecutive `combine` method calls -- less cluttering) and
- b) you only define that you would like to combine them without actually doing it. Which allows that you can for
  instance `append` another `ArgsGenerator` to the tuple, replace one at a specific position, `glue` tuples together and
  more (see the documentation of [kbox](https://github.com/robstoll/kbox) regarding tuples).

`cartesian` and `zip` provide an overload which takes a `transform` function so that you can turn the generates values
pairwise into something else than `Tuple2`.

### ordered cartesian

As mentioned in [generic combine](#generic-combine), combining multiple `OrderArgsGenerator`s by using a `Tuple` uses
`cartesian` behind the scenes and results in a new `OrderArgsGenerator` which represents the cartesian product of them,
i.e. all possible combinations.

<code-cartesian-1>

```kotlin
ordered.of(1, 2).cartesian(ordered.of('A', 'B'))
```

</code-cartesian-1>

For the above example, the possible combinations are 1/A, 2/A, 1/B, 2/B
You can pass a `transform` function as last argument and map the values pairwise to another type:

<code-cartesian-2>

```kotlin
ordered.of(1, 2).cartesian(ordered.of(4, 5)) { i1, i2 ->
	i1 + i2
}
```

</code-cartesian-2>

The above example results in an `OrderedArgsGenerator<Int>` with the values 5 (1+4), 6 (2+4), 6 (1+5), 7 (2+5)
(the order of the values is implementation specific). As you can see, an `OrderedArgsGenerator` can also generate the
same value multiple times.

### zip

As mentioned in [generic combine](#generic-combine), combining multiple `ArbArgsGenerator`s by using a `Tuple` uses
`zip` behind the scenes and results in a new `ArbArgsGenerator`:

<code-zip-arb-1>

```kotlin
arb.intFromUntil(1, 100).zip(arb.charFromTo('A', 'Z'))
```

</code-zip-arb-1>

For the above example, the possible combinations are 1/A, 2/A,... 99/A, 1/B, 2/B... 99/Z.
As outlined in [ordered and arbitrary arguments generators](#ordered-and-arbitrary-arguments-generators), the order of
the generated values of an `ArbArgsGenerator` is undefined and it could occur that you see a combination more than once.
You can pass a `transform` function as last argument and map the values pairwise to another type:

<code-zip-arb-2>

```kotlin
arb.intFromUntil(1, 100).zip(arb.intFromUntil(1000, 2000)) { i1, i2 ->
	i1 + i2
}
```

</code-zip-arb-2>

The above example results in an `ArbArgsGenerator<Int>` generating values from 1001 until 2098 where 1002 until 1099 are
more likely to appear since they result twice (1+1001 and 2+1000 = 1002 etc.).

You can also use `zip` to combine a `(Semi)OrderedArgsGenerator` with an `ArbArgsGenerator` resulting in a
`SemiOrderedArgsGenerator` (the same happens if you define a Tuple with a `(Semi)OrderedArgsGenerator` as first element,
and one of the other elements is an `ArbArgsGenerator` -- see [generic combine](#generic-combine)):

<code-zip-semi>

```kotlin
ordered.intFromUntil(1, 20).zip(arb.intFromUntil(1000, 2000)) { i1, i2 ->
	i1 + i2
}
```

</code-zip-semi>

The resulting `SemiOrderedArgsGenerator` has still the same size as the initial `(Semi)OrderedArgsGenerator`

We do not provide a `zip` which combines two `SemiOrderedArgsGenerator` as we did not stumble over a use case so far.
Take a look at [cartesian](#ordered-cartesian) which is most likely how you want to combine two
`SemiOrderedArgsGenerator`. Or in case you do not want the cartesian product but just one random value of your second
`SemiOrderedArgsGenerator`, then [turn it into an ArbArgsGenerator](#ordered-toArbArgsGenerator) first.

[Let us know your use case](https://github.com/tegonal/variist/discussions/new?category=ideas&subject=ordered.zip%20another%20ordered)
if you still want to zip another `(Semi)OrderedArgsGenerator` and how the semantics should look like.

### zipDependent

In many cases you have dependencies between `ArgsGenerators`. Something like, you need two `Int` where the first
is less than the second (defining two bounds). For this particular case we provide an optimised implementation which
adheres uniform distribution at its core: `arb.intBounds` or `arb.intRange` depending on what you need. And you can use
`arb.xyzBoundsBased` as building block to create other bounds based `ArbArgsGenerators`. For other relationships you
can use `zipDependent` as follows:

<code-zip-dependent-arb>

```kotlin
arb.intFromTo(1, 10).zipDependent { a ->
	arb.intFromTo(11 - a, 10)
}
```

</code-zip-dependent-arb>

`zipDependent` takes a factory which creates an `ArbArgsGenerator` based on a value this `ArgsGenerator` creates
and then uses `ArbArgsGenerator.generateOne` to combine the value with one value of this other generator.
It also exists on `SemiOrderedArgsGenerator` and even on `OrderedArgsGenerator` where the resulting generator
is an `SemiOrderedArgsGenerator`.
Following an example:

<code-zip-dependent-ordered-arb>

```kotlin
enum class Color {
	Red, Blue, Green
}

ordered.fromEnum<Color>().zipDependent({ color ->
	arb.hexColor(dominant = color)
}) { _, hex -> hex }
```

</code-zip-dependent-ordered-arb>

Note three things, first `hexColor` does not exist (yet) in Variist and is only there for illustration purposes.
Secondly, `zipDependent` also has two overloads (like `cross`/`zip`) where the one with a `transform` function
allows to turn the values into something else than `Tuple2`. And last but not least, this is a way to define that
we want to have x test runs at maximum where x is the number of elements in the `Color` enum but we are not
interested in `Color` as such but something arbitrary which depends on it.
If we did not want to limit the number of runs, we could also have used [arb.mergeWeighted](#arb-mergeweighted) instead.

As with `zip` we do not provide a `SemiOrderedArgsGenerator.zipDependent` where the `otherFactory` creates another
`SemiOrderedArgsGenerator`. Take a look at [flatZipDependent](#flatZipDependent) which might be what you are looking
for.

### flatZipDependent

If you want to zip not only one value but multiple values from the `ArbArgsGenerator` which was created based on a
value of your `ArbArgsGenerator`/`SemiOrderedArbArgsGenerator`, then you can use flatZipDependent:

<code-flat-zip-dependent-arb>

```kotlin
arb.intFromTo(1, 10).flatZipDependent(amount = 2) { a ->
	arb.intFromTo(11 - a, 10)
}

ordered.intFromTo(1, 10).flatZipDependent(amount = 3) { a ->
	arb.intFromTo(11 - a, 10)
}
```

</code-flat-zip-dependent-arb>

`OrderedArgsGenerator` provides a `flatZipDependentMaterialised` which expects a factory that creates another
`OrderedArgsGenerator` based on a given value from the first `OrderedArgsGenerator` and in contrast to
`flatZipDependent` does not take an `amount` but the individual lengths of the created `OrderedArgsGenerator`s.
Following an example:

<code-flat-zip-dependent-ordered-ordered>

```kotlin
enum class Color {
	Red, Blue, Green
}

ordered.fromEnum<Color>().flatZipDependentMaterialised { color ->
	// the resulting OrderedArgsGenerator might differ in size
	ordered.colorMoods(color)
}
```

</code-flat-zip-dependent-ordered-ordered>

As the name implies, using it means that `this` `OrderedArgsGenerator` gets materialised and also that the resulting
`OrderedArgsGenerator` gets materialised. You can think of it as `toList().flatMap { ... }.let(ordered:fromList)` but
does a bit more behind the scene. Also `flatZipDependentMaterialised` provides an overload which lets you pass a
`transform` function in case you want to combine the values to something else than `Tuple2`.

You may be wondering why this method does not exist for `SemiOrderedArgsGenerator`, the next section will shed light
on it.

### transform

Variist provides different means to transform `ArgsGenerator`s. But not all extension methods are defined
for all types of `ArgsGenerator`. For instance, since an `OrderedArgsGenerator` needs to know how many values it can
generate before repeating them, methods like `filter` require that a full cycle gets materialised first.
Such methods are signified with a `Materialised` suffix. A `SemiOrderedArgsGenerator` on the other hand does not
even provide a `Materialised` version as materialising would mean you fix the arbitrary part of it and most likely that
would not be the intention. To be precise, `SemiOrderedArgsGenerator` actually provides the `Materialised` function but
with a deprecation level `Error`. We have it there to remind you that you most likely don't want to use it (better than
let you search for it just to realise after a while that it does not exist for `SemiOrderedArgsGenerator`).

Most transformation functionality is based on `transform`/`transformMaterialised` where a function operates on the
generated `Sequence` and must return another `Sequence` which still adheres to the corresponding
`ArgsGenerator` contract (e.g. the `Sequence` must still be infinite in case of `ArbArgsGenerator`).
Following an example:

<code-transform>

```kotlin
arb.intFromTo(1, 10).transform { sequence ->
	// return a generated value twice
	sequence.flatMap { listOf(it, it) }
}

ordered.intFromTo(1, 10).transformMaterialised { sequence ->
	sequence.zipWithNext()
}
```

</code-transform>

Which means you can use `transform` as building block for custom transformations based on `Sequence`.
Some functions are so common, that Variist provides them as extension of `ArgsGenerator` as well,
the following section outlines some.

### map

You can map values of `ArgsGenerator` to another type by providing a mapping function:

<code-map>

```kotlin
val now = LocalDate.now()
arb.localDateFromTo(now.withDayOfYear(1), now).map { localDate -> localDate.atTime(12, 0) }

ordered.intFromTo('A'.code, 'Z'.code).map { it.toChar() }
```

</code-map>

### filter

Filtering an `ArbArgsGenerator` can be done via `filter`/`filterNot`.
Filtering an `OrderedArgsGenerator` via `filterMaterialised`/`filterNotMaterialised` (see [transform](#transform) for
an explanation about `Materialised` and why it does not exist for `SemiOrderedArgsGenerator`).

<code-filter>

```kotlin
arb.intFromUntil(1, 1000).filterNot { it == 523 }
ordered.intFromUntil(1, 1000).filterNotMaterialised { it == 523 }
```

</code-filter>

Be aware of that filtering an `ArbArgsGenerator` might be a simple way to achieve something but could be slow due to
the nature of the generator generating arbitrary values. Following an example:

<code-dont-filter>

```kotlin
// prefer a progression as follows ...
arb.fromProgression(1..1000 step 2)
// ... instead of filter (which is slower as every ~2nd time the number is even)
arb.intFromTo(1, 1000).filter { it % 2 == 1 }
```

</code-dont-filter>

### chunked

If you want to take a static number of values from an `ArbArgsGenerator` and transform them into another type, then
`chunked` comes in handy where as for `Sequence` two overloads are provided. The first transforms the values into a
`List` and the second takes a `transform` function in addition which allows to map the `List` of values into something
else.

<code-chunked>

```kotlin
arb.intFromTo(1, 100).chunked(3)
arb.intFromTo(1, 100).chunked(3) { it.sorted() }
arb.charFromTo('a', 't').zip(arb.intFromTo(1, 100)).chunked(3) { it.toMap() }
```

</code-chunked>

So far we did not come across a use case where `chunked` would be valuable for `OrderedArgsGenerator` and hence do not
provide a shortcut. [Let us know your use cases](https://github.com/tegonal/variist/discussions/new?category=ideas),
we happily add the shortcut if it is of value (we try to not clutter the API with methods we have not
used ourselves so far).

### ordered concatenation

Concatenating `OrderedArgsGenerators` can be done via `+` or via `concatAll` where the resulting `size` will
correspondingly be the size of `this.size` + `other.size`.

<code-concat>

```kotlin
ordered.of(1, 2) + ordered.intFromTo(100, 120)

// works with Iterable/Sequence as well
(0..3).map {
	val offset = 10 * it
	ordered.of(0 + offset, 3 + offset)
}.concatAll() // generates 0,3, 10,13, 20,23, 30,33, ...
```

</code-concat>

Concatenating an `ArbArgsGenerator` is not possible as its size is infinite, i.e. we would never see the values
of the second `ArbArgsGenerator`. But you can merge them, see next section.

### arb mergeWeighted

Sometimes you want to use two or more `ArbArgsGenerator`s as source of a test. In such a case you can use
`arb.mergeWeighted` to merge them where you can define the weighting of the individual generators:

<code-mergeWeighted>

```kotlin
arb.mergeWeighted(
	80 to arb.intFromUntil(100, 200),
	10 to arb.of(201),
	10 to arb.of(null)
)
```

</code-mergeWeighted>

The weighting does not need to add up to 100. If they do, then the numbers correspond to percentage. So in the above
case, out of 100 generated values, around 80 will be between 100 and 200 (exclusive), around 10 will be 201 and
around 10 will be `null`. The defined weighting is uniformly distributed, which means that for a small number of values,
it might be skewed; for example, 85 values could fall between 100 and 200, etc.



### ordered fromArbs

If you find yourself in the situation where you define multiple `ParameterizedTests` which contain all more or less the 
same test-logic but with different ranges of values, then one is quickly tempted to include more than one expectation in 
the test method, as the boilerplate is just too annoying (jump to the usage of [ordered.fromArbs](#ordered-fromArbs-good-solution)
if you are not interested in knowing why it exists). Following an example:

<code-fromArbs-problem>

```kotlin
@Test
fun null_null__no_error() {
	checkRequestedMinArgsMaxArgs(null, null)
}

@ParameterizedTest
@ArgsSource("arbIntPositive")
fun positiveInt_null__no_error(positiveInt: Int) {
	checkRequestedMinArgsMaxArgs(positiveInt, null)
}

@ParameterizedTest
@ArgsSource("arbIntPositive")
fun null_positiveInt__no_error(positiveInt: Int) {
	checkRequestedMinArgsMaxArgs(null, positiveInt)
}

@ParameterizedTest
@ArgsSource("arbIntPositive")
fun same_positiveInt__no_error(positiveInt: Int) {
	checkRequestedMinArgsMaxArgs(positiveInt, positiveInt)
}
//...
```

</code-fromArbs-problem>

Merging it simplifies maintenance...

<code-fromArbs-bad-solution>

```kotlin
@ParameterizedTest
@ArgsSource("arbIntPositive")
fun checkRequestedMinArgsMaxArgs(positiveInt: Int) {
	checkRequestedMinArgsMaxArgs(null, null)
	checkRequestedMinArgsMaxArgs(positiveInt, null)
	checkRequestedMinArgsMaxArgs(null, positiveInt)
	checkRequestedMinArgsMaxArgs(positiveInt, positiveInt)
}
```

</code-fromArbs-bad-solution>

.. but in exchange for a more generic test method name. And the more expectations you add to the test method 
the less obvious it becomes which expectation failed on failure. Re-executing just the failure case,
debugging it etc. becomes more tedious. And you know what, we forgot to add the use case where `requestedMinArgs` is
a positive int and `maxArgs` is greater. For this we need two arguments where the second depends on the first.

At first, that might sound complicated to achieve and you might be tempted to fall back to `ValueSource` to cover the 
5 cases with fixed values. But you don't need to give up the arbitrariness. `ordered.fromArbs` helps in such a situation
where you re-use the test-logic and define different valid data ranges:

<a name="ordered-fromArbs-good-solution"></a>

<code-fromArbs>

```kotlin
@ParameterizedTest
@ArgsSource("requestedMinArgsMaxArgsHappyCases")
fun requestedMinArgs_maxArgs_happy_cases(requestedMinArgs: Int?, maxArgs: Int?) {
	checkRequestedMinArgsMaxArgs(requestedMinArgs, maxArgs)
}

companion object {
	@JvmStatic
	fun requestedMinArgsMaxArgsHappyCases() = ordered.fromArbs(
		arb.of(Tuple(null, null)),
		arb.intPositive().map { Tuple(it, null) },
		arb.intPositive().map { Tuple(null, it) },
		arb.intPositive().map { Tuple(it, it) },
		arb.intBounds(minInclusive = 1, minSize = 2)
	)
}
```

</code-fromArbs>

You could also use `arb.mergeWeighted` to achieve more or less the same but with a different runtime behaviour. 
Using `ordered.fromArbs` limits the number of generated runs by the number of specified `ArbArgsGenerators` 
where using `arb.mergeWeighted` doesn't impose that limit and more runs than defined generators could be generated
(see [Adjust the number of runs](#adjust-the-number-of-args)). As an example, say the profile of the test and the 
environment the test runs in allows to run 100 variations, then `ordered.fromArbs` will result in 5 runs and 
`arb.mergeWeighted` would result in 100. For this particular case we don't think there is much value in running
that many variations all the time (a waste of resources and time) and prefer `ordered.fromArbs`.

### ordered toArbArgsGenerator

You can turn a `(Semi)OrderedArgsGenerator` into an `ArbArgsGenerator` by using the `toArbArgsGenerator()` extension
method.

# Use Variist in other contexts than JUnit

Variist is not bound to `@ParameterizedTest`s, not even to JUnit. It is a library which can be used whenever
you have a data-driven situation (and you do not have time to consider all of it).
For instance, we have used it in load tests as source for (arbitrary) input.
You can also use it in combination
with [JUnit's Dynamic Tests](https://docs.junit.org/current/user-guide/#writing-tests-dynamic-tests)
Following an example:

<code-dynamic-test-1>

```kotlin
import com.tegonal.variist.generators.arb
import com.tegonal.variist.generators.generateAndTakeBasedOnDecider
import com.tegonal.variist.generators.string
import com.tegonal.variist.generators.zip
import com.tegonal.variist.providers.PredefinedNumberProviders.Companion.arbIntPositive
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

class DynamicTest : PredefinedArgsProviders {

	@TestFactory
	fun arbExample() =
		arbIntPositive().zip(arb.string(maxLength = 20))
			.generateAndTakeBasedOnDecider()
			.map { (positiveNumber, label) ->
				dynamicTest("$positiveNumber $label") {
					assertTrue(positiveNumber * -1 < 0)
				}
			}
}
```

</code-dynamic-test-1>

Note however, that all the magic of `ArgsSource` is not available (yet). Which means:

- you need to combine ArgsGenerators manually (see [zip](#zip) and [ordered.cartesian](#ordered-cartesian)) or
  use [combineAll](#generic-combine) if you deal with generators in `Tuple`s -- the good side, you do not lose the types
  as you would with JUnit's `Arguments`.
- A defined [SuffixArgsGenerator](#use-a-suffixArgsGeneratorDecider) is ignored (we would lose the types again).
- definitions like `@ArgSourceOptions` are ignored, but as long as you use `generateAndTakeBasedOnDecider` the defined
  seed and co. (see [fixing the seed](#fixing-the-seed)) are taken into account.
- and you can pass `AnnotationData` to `generateAndTakeBasedOnDecider` to get more or less back the same options as with
  `ArgSourceOptions`.

<code-dynamic-test-2>

```kotlin
@TestFactory
fun orderedExample() =
	ordered.intFromUntil(1, 100)
		.generateAndTakeBasedOnDecider(
			AnnotationData.outsideParameterizedTest(
				argsRangeOptions = ArgsRangeOptions(profile = "Integration", maxArgs = 20)
			)
		)
		.map { positiveNumber ->
			dynamicTest("$positiveNumber") {
				assertTrue(positiveNumber * -1 < 0)
			}
		}
```

</code-dynamic-test-2>

# Configuration

Variist provides a configuration via `VariistConfig` which per default can be customised via
`variist.properties`.
This file needs to be available on your classpath. Typically, you put it in src/test/resources.
Next to `variist.properties` which is intended to make project based adjustments
(e.g. change `Miniamlist.defaultProfile` to `E2E`, see [Profiles and Envs](#profiles-and-envs)), you can create a
`variist.local.properties` which you should add on your git ignore list.
This file overwrites settings in `variist.properties` and is intended for personal adjustments and debugging.

More documentation about the configuration will follow, in the meantime, take a look at the KDoc of VariistConfig.

## Profiles and Envs

Variist steers how many sets of args, which usually is the same as how many runs, will result at maximum
per `ParameterizedTest` by the profile definition in use for the particular test and the environment the test runs in.
There are other limiting factors which are outlined in [adjust the number of args](#adjust-the-number-of-args).

Variist comes with two predefined enums: `TestType`s which are used as profile names (and which provides constants
via `.ForAnnotation` so that it can be used in an annotation) and predefined `Env`s.
Take a look at `VariistConfig.testProfiles` to see what `maxArgs` are defined per default per profile and env.

The active environment is determined based on (first match wins):

1. the definition of `activeEnv` in `variist.local.properties`
2. from environment variables (GitHub and GitLab env vars),
3. the definition of `activeEnv` in `variist.properties`
4. defaulting to `Local` if not defined.

The profile of a test is determined based on (first match wins):

1. a defined `ArgsSourceOptions` on the test method itself specifying `profile`
2. a defined `ArgsSourceOptions` on the test class specifying `profile`
3. a defined `ArgsSourceOptions` on a super type specifying `profile`
4. the definition of `defaultProfile` in `variist.local.properties`
5. the definition of `defaultProfile` in `variist.properties`
6. defaulting to `Integration` if not defined

Following an example:

<code-args-source-options-profile>

```kotlin
import com.tegonal.variist.config.TestType
import com.tegonal.variist.providers.ArgsSource
import com.tegonal.variist.providers.ArgsSourceOptions
import org.junit.jupiter.params.ParameterizedTest

@ArgsSourceOptions(profile = TestType.ForAnnotation.SystemIntegration)
abstract class BaseSystemIntegrationTest : PredefinedArgsProviders {
	// some common setup or whatever

	@ParameterizedTest
	@ArgsSource("arbIntBoundsMinSize2")
	fun commonFoo(lowerBound: Int, upperBound: Int) {
		//...
	}
}

@ArgsSourceOptions(profile = TestType.ForAnnotation.E2E)
class ArgsSourceOptionsProfileTest : BaseSystemIntegrationTest() {

	@ParameterizedTest
	@ArgsSource("arbBoolean")
	fun foo(isDefined: Boolean) {
		//...
	}

	@ParameterizedTest
	@ArgsSource("arbInt")
	@ArgsSourceOptions(profile = TestType.ForAnnotation.Unit)
	fun bar(i: Int) {
		//...
	}
}
```

</code-args-source-options-profile>

Say we run all tests of `ArgsSourceOptionsProfileTest`, assuming no `defaultProfile` was defined
in `variist(.local).properties` the default fallback comes into play which
is `Integration`. For the above tests it doesn't matter as `BaseSystemIntegrationTest` (the
super class of the active test class) has defined:

```
@ArgsSourceOptions(profile = TestType.ForAnnotation.SystemIntegration)
```

which means all tests are run with profile `SystemIntegrationn` if not specified otherwise.
This is the case for `commonFoo` which is defined in `BaseSystemIntegrationTest` (although executed in the end as
part of `ArgsSourceOptionsProfileTest`).

`ArgsSourceOptionsProfileTest` itself defines:

Variist comes with two predefined enums, `TestType`s which are used as profile names and predefined `Env`s.
See `VariistConfig.testProfiles` for what `maxArgs` are defined per default.

## Fixing the seed

Variist outputs the used seed once the config is fully loaded. Use it in `variist.local.properties` to fix the
seed to e.g. a previous run. You might want to restrict `maxArgs` in such a case as well and use `skip`
to skip some runs, i.e. jump to a particular run.

### ErrorDeadlines

If you fix one of the following properties, then an error deadline is added to your `variist.local.properties`:

- seed
- skip
- maxArgs
- requestedMinArgs

The deadline will remind you that you should remove (comment out) those values again, as they are intended for debugging
or when you temporarily want to execute more tests than defined by your [activeEnv](#profiles-and-envs).

You can adjust the default deadline (60 minutes) via `remindAboutFixedPropertiesAfterMinutes`.

Variist assumes your `variist.local.properties` is located under `./src/test/resources` relative to the
the directory from which you execute java -- if you run tests in IntelliJ this corresponds to the project dir.
If you place it under a different directory, then use the property `variistPropertiesDir` to adjust it (e.g. in the
`variist.properties`-file or directly in `variist.local.properties`). Following an example:

```properties
variistPropertiesDir=./src/jvmTest/resources
```

## Change the ArgsRangeDecider

An `ArgsRangeDecider` is responsible to decide from which offset and how many arguments shall be taken from an
`ArgsGenerator`. The offset is only taken into account for `(Semi)OrderedArgsGenerator`s.

The default implementation is solely based on the configured [profiles](#profiles-and-envs) - more implementations will
follow in an upcoming version of Variist.
```
@ArgsSourceOptions(profile = TestType.ForAnnotation.E2E)
```

which means all tests defined in it are run with profile `E2E` if not specified otherwise.
For `foo` this is actually the case and the configuration for `E2E` applies.
Yet, for `bar` we defined an own profile on the test method itself:

```
@ArgsSourceOptions(profile = TestType.ForAnnotation.Unit)
```

and correspondingly the configuration for `Unit` applies for `bar`.

## Adjust the number of Args

The configured [ArgsRangeDecider](#change-the-ArgsRangeDecider) decides what range of args an `ArgsGenerator` generates,
and consequently how many runs result. Per contract, an `ArgsRangeDecider` should take defined [`maxArgs`](#maxArgs),
[`(Semi)OrderedArgsGenerator.size`](#ordered-and-arbitrary-arguments-generators) as well as [
`requestedMinArgs`](#requestedMinArgs) into account during its decision (next to
other data which an [own AnnotationDataDeducer](#use-an-own-AnnotationDataDeducer) might deduce).

### maxArgs

As outlined in [profile and envs](#profiles-and-envs), the number of runs is primarily determined based on the active
profile and env, its defined `TestConfig` respectively, where the property `maxArgs` restricts the maximum
number of runs. If you use an `(Semi)OrderedArgsGenerator` as `ArgsSource`, then its `size` limits the maximum number of runs
as well -- this is at least the default behaviour, you could
also [use your own ArgsRangeDecider](#change-the-ArgsRangeDecider) which takes other limiting factors into account.

Yet, you might have a test where you want to restrict it even more, regardless what was
configured for the active profile/env combination (e.g. because it is too expensive, or because it causes errors if it
is executed more than the defined number etc.). This can be done via `ArgsSourceOptions.maxArgs`. Following an example:

<code-args-source-options-max-args>

```kotlin
@ParameterizedTest
@ArgsSource("arbBoolean")
@ArgsSourceOptions(maxArgs = 1)
fun loginFailure(isLocked: Boolean) {
	//...
}
```

</code-args-source-options-max-args>

The test `loginFailure` is guaranteed to be run only once. As with setting the [profile](#profiles-and-envs), `maxArgs`
can be defined not only on the test method but also on the test-class and a super-type of the test-class.
And you can also set it in `variist.local.properties` which takes precedence over `ArgsSourceOptions`,
which comes in handy if you have [fixed the seed](#fixing-the-seed) and you don't want to re-run all tests.

Note, that you cannot increase the number of runs via `maxArgs`, it has only a limiting factor. If you want to increase
the number of runs, then use `requestedMinArgs`, see next section.

### requestedMinArgs

In the same vain as `maxArgs` you can define a `requestedMinArgs`. In contrast to `maxArgs` it is not a hard
requirement, it is only requested. For instance, if you use an `OrderedArgsGenerator` as `ArgsSource` and define
`requestedMinArgs=50` but `OrderedArgsGenerator.size=10` then only 10 runs will result, which can still be more than
what the active [profile/env combination](#profiles-and-envs) defines via `maxArgs`. I.e. the `requestedMinArgs` defined
in an annotation can overrule what was defined in `VariistConfig.testProfiles`. `SemiOrderedArgsGenerators.size` does
not limit a `requestedMinArgs` as it has an arbitrary part which does not repeat. In other words,
if `requestedMinArgs=50` but `SemiOrderedArgsGenerators.size=10` then nevertheless, 50 runs will be the result.

`requestedMinArgs` is especially useful if you write a new test and want to execute a greater number of test runs 
than what you would usually want in the `Local` env.

As with `maxArgs`, you can define `requestedMinArgs` in `ArgsSourceOptions` and `variist.local.properties` where
`variist.local.properties` takes precedence again.
We omit an example here, we guess the usage of `ArgsSourceOptions` should be clear by now, otherwise take a look
at [maxArgs](#maxargs) and [profile](#profiles-and-envs).
The default implementation is solely based on the configured [profiles and envs](#profiles-and-envs), [
`OrderedArgsGenerator.size`](#ordered-and-arbitrary-arguments-generators) and additional defined [`maxArgs`](#maxArgs) [
`requestedMinArgs`](#requestedMinArgs).
More implementations will follow in an upcoming version of Variist.

If you want to provide an own implementation, then you need to make it available to be loaded via `ServiceLoader`.
Create the file `src/resource/META-INF/services/com.tegonal.variist.providers.ArgsRangeDecider` and put the fully
qualified name in it. Moreover, you need to set `activeArgRangeDecider` in the VariistConfig
(typically via `variist.properties`) to the fully qualified name as well.

Take a look at the next section in case your `ArgsRangeDecider` should take into account other static data defined via
an annotation.

## Use an own AnnotationDataDeducer

You can define an own `AnnotationDataDeducer` which deduces more data next to [`profile`](#profiles-and-envs),  [
`maxArgs`](#maxArgs) and [`requestedMinArgs`](#requestedMinArgs) which one can define in the annotation
`ArgsSourceOptions`.

You need to make it available to be loaded via `ServiceLoader`.
Create the file `src/resource/META-INF/services/com.tegonal.variist.providers.AnnotationDataDeducer` and put the
fully qualified name in it.

If your deducer is based on a single annotation, then you might want to base it on `BaseAnnotationDataDeducer`.

## Use a SuffixArgsGeneratorDecider

A `SuffixArgsGeneratorDecider` is responsible to decide if an `ArgsGenerator` shall be combined as suffix (i.e. as last)
with the `ArgsGenerator`(s) defined by the method specified in `ArgsSource`.

This can be handy if you for instance have a kind of test which always require something. This way you don't have to add
it to every single `ArgsSource` but can define it in a single place.
Imagine you have implemented a SuffixArgsGeneratorDecider which always returns `arb.fromEnum<Color>()`. In the test you
could then write:

<code-suffix-args-generator>

```kotlin
@ParameterizedTest
@ArgsSource("arbIntPositive") // comes from PredefinedArgsProviders
fun foo(i: Int, c: Color) {
	// the argument i comes from arbIntPositive
	// the argument c comes from the SuffixArgsGeneratorDecider
}
```

</code-suffix-args-generator>

If you want to provide an own implementation, then you need to make it available to be loaded via `ServiceLoader`.
Create the file `src/resource/META-INF/services/com.tegonal.variist.providers.SuffixArgsGeneratorDeciderr` and
put the fully qualified name in it. Moreover, you need to set `activeArgRangeDecider` in the VariistConfig
(typically via `variist.properties`) to the fully qualified name as well.

Note, that you can still define
a [ParameterResolver](https://docs.junit.org/current/user-guide/#writing-tests-dependency-injection),
instead (or in addition). Variist is only an addition to JUnit, you can use all other constructs as well.
There is a difference though, if you define that your `SuffixArgsGeneratorDecider` returns an `OrderedArgsGenerator`
then the cartesian product results as explained in [generic combine](#generic-combine).

# Helpers

Variist provides some helpers in addition to `ArgGenerators` and the `ArgsSource` machinery.

## Random helpers

Variist provides some helper methods and functionality in case you want to add randomness but still benefit
from the possibility to re-run it in a deterministic way (by [fixing the seed](#fixing-the-seed)).

<code-random-helper>

```kotlin
import com.tegonal.variist.utils.createVariistRandom
import com.tegonal.variist.utils.pickOneRandomly
import com.tegonal.variist.utils.takeRandomly

// Imagine the list is more complicated than that (involves, filtering, flatMap etc.), because if not, then
// better define it via `arb` or `ordered` because this is most likely more efficient (would not allocate the
// memory for 1001 Ints)
val someList = (0..1000).toList()
val i1: Int = someList.pickOneRandomly()
val l1: List<Int> = someList.takeRandomly(10)

// Imagine a more complicated ordered which combines multiple generators and in the end maps to a model.
// In case you want to re-use it in another context than ParameterizedTests those helpers might come in handy
val complicatedSetup = ordered.of(1, 2, 3)
val i2: Int = complicatedSetup.pickOneRandomly()
val l2: List<Int> = complicatedSetup.takeRandomly(100)
// and of course, if you want to do more than that, then you can always turn your (Semi)OrderedArgsGenerator
// into an ArbArgsGenerator and then work on Sequence:
val l3: Set<Int> = complicatedSetup.toArbArgsGenerator().generate()
	.map { it + i1 + i2 }
	//...
	.take(50)
	.toSet()

// creates a Random based on the configured seed, i.e. if you fix the seed, then you get a deterministic result
createVariistRandom().let { random ->
	val i = random.nextInt()
	//...
}
```

</code-random-helper>

## Sequence helpers

In case you want to repeat something forever, then `repeatForever` might come in handy for you as well:

<code-repeat-forever>

```kotlin
import com.tegonal.variist.utils.repeatForever

// creates a Sequence which yields the given constant forever
repeatForever(constant = 1)

// creates a Sequence which yields 1, 2, 3 forever
repeatForever(arrayOf(1, 2, 3), offset = 0)

// creates a Sequence which yields 2, 3, 1 forever
repeatForever(listOf(1, 2, 3), offset = 1)

// repeats Unit forever and can be used as a building block
repeatForever().flatMap { _ ->
	// will repeat 11, 22, 33 forever
	sequenceOf(1, 2, 3).mapIndexed { index, it -> it + (index + 1) * 10 }
}
```

</code-repeat-forever>

## BigInt helpers

We think `BigInteger` is too cumbersome to write and hence use `BigInt` instead (also aligns better with Kotlin's choice
to use `Int` instead of `Integer`. For instance, we use `ordered.bigIntFromUntil` instead of
`ordered.bigIntegerFromUntil`. And since it would look odd if this function takes `BigInteger`, we introduced
a corresponding `typealias` and an extension method `toBigInt` for `Int` and `Long`.

Last but not least, we provide the extension method `Random.nextBigInt`.

# Code Documentation

Code documentation can be found on github-pages: <https://tegonal.github.io/variist/latest#/kdoc>.

# Contributors and contribute

Our thanks go to [code contributors](https://github.com/tegonal/variist/graphs/contributors)
as well as all other contributors (e.g. bug reporters, feature request creators etc.)

You are more than welcome to contribute as well:

- star this repository if you like/use it
- [open a bug](https://github.com/tegonal/variist/issues/new?template=bug_report.md) if you find one
- Open a [new discussion](https://github.com/tegonal/variist/discussions/new?category=ideas) if you
  are missing a feature
- [ask a question](https://github.com/tegonal/variist/discussions/new?category=q-a)
  so that we better understand where we can improve.
- have a look at
  the [help wanted issues](https://github.com/tegonal/variist/issues?q=is%3Aissue+is%3Aopen+label%3A%22help+wanted%22).

Please have a look at
[CONTRIBUTING.md](https://github.com/tegonal/variist/tree/main/.github/CONTRIBUTING.md)
for further suggestions and guidelines.

# License

Variist is licensed
under [European Union Public Licence 1.2](https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12).

- Classes which are copied from [junit-jupiter-params/junit-platform-commons](https://github.com/junit-team/junit5)
  are licensed under [EPL 2.0](https://www.eclipse.org/legal/epl-v20.html) (see
  src/main/lib/java/com/tegonal/variist/export/org/junit).
- Copied some classes and interfaces from [Atrium](https://atriumlib.org) licensed
  under [EUPL 1.2](https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12).

Variist is using

- [junit-jupiter-params/junit-platform-commons](https://github.com/junit-team/junit5) licensed
  under [EPL 2.0](https://www.eclipse.org/legal/epl-v20.html)
- [KBox](https://github.com/robstoll/kbox) licensed under [Apache 2.0](https://opensource.org/licenses/Apache2.0)
