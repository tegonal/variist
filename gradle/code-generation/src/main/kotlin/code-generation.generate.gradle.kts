import ch.tutteli.kbox.Tuple
import ch.tutteli.kbox.a2
import ch.tutteli.kbox.append

val generationFolder: ConfigurableFileCollection = project.files("src/main/generated/kotlin")

val mainPackageName = "com.tegonal.variist"
val mainPackageNameAsPath = mainPackageName.replace('.', '/')

fun dontModifyNotice(place: String) =
	"""
		|// --------------------------------------------------------------------------------------------------------------------
		|// automatically generated, don't modify here but in:
		|// $place
		|// --------------------------------------------------------------------------------------------------------------------
		|
	""".trimMargin()

val numOfArgs = 10

val generate: TaskProvider<Task> = tasks.register("generate") {

	val dontModifyNotice =
		dontModifyNotice("gradle/code-generation/src/main/kotlin/code-generation.generate.gradle.kts => generate")

	fun createStringBuilder(packageName: String) = StringBuilder(dontModifyNotice)
		.append("package ").append(packageName).append("\n\n")

	doFirst {
		val packageDir = File(generationFolder.asPath + "/" + mainPackageNameAsPath)
		fun StringBuilder.writeToFile(fileName: String) {
			this.writeToFile(packageDir.resolve(fileName))
		}

		fun tupleType(arity: Int, tArgs: String) = when (arity) {
			1 -> tArgs
			else -> "Tuple$arity"
		}

		fun tupleTypeWithTypeArgs(arity: Int, tArgs: String) = when (arity) {
			1 -> tArgs
			else -> "Tuple$arity<$tArgs>"
		}

		fun StringBuilder.importTupleTypes() =
			append("import ch.tutteli.kbox.append\n")
				.append("import ch.tutteli.kbox.Tuple2\n")
				.append("import ch.tutteli.kbox.Tuple3\n")
				.append("import ch.tutteli.kbox.Tuple4\n")
				.append("import ch.tutteli.kbox.Tuple5\n")
				.append("import ch.tutteli.kbox.Tuple6\n")
				.append("import ch.tutteli.kbox.Tuple7\n")
				.append("import ch.tutteli.kbox.Tuple8\n")
				.append("import ch.tutteli.kbox.Tuple9\n")

		val semiOrderedArgsLikeGeneratorCombineAll = listOf(
			Tuple("orderedCombineAllGenerated", "OrderedCombineAllKt", "OrderedArgsGenerator", "cartesian"),
			Tuple("semiOrderedCombineAllGenerated", "SemiOrderedCombineAllKt", "SemiOrderedArgsGenerator", "combine"),
			Tuple("arbCombineAllGenerated", "ArbCombineAllKt", "ArbArgsGenerator", "zip")
		).map {
			it.append(
				createStringBuilder("$mainPackageName.generators")
					.importTupleTypes()
					.appendLine()
			)
		}

		val semiOrderedArgsLikeGeneratorCartesian = listOf(
			Tuple("orderedCartesianGenerated", "OrderedCartesianKt", "OrderedArgsGenerator"),
			Tuple("semiOrderedCartesianGenerated", "SemiOrderedCartesianKt", "SemiOrderedArgsGenerator"),
		).map {
			it.append(
				StringBuilder("@file:JvmName(\"${it.a2}\")\n@file:JvmMultifileClass\n")
					.append(dontModifyNotice)
					.append("package ").append(mainPackageName).append(".generators").append("\n\n")
					.importTupleTypes()
					.appendLine()
			)
		}

		val arbZip = listOf(
			Tuple("arbZipGenerated", "ArbZipKt", "ArbArgsGenerator"),
			Tuple("semiOrderedZipGenerated", "SemiOrderedZipKt", "SemiOrderedArgsGenerator")
		).map {
			it.append(
				StringBuilder("@file:JvmName(\"${it.a2}\")\n@file:JvmMultifileClass\n")
					.append(dontModifyNotice)
					.append("package ").append(mainPackageName).append(".generators").append("\n\n")
					.importTupleTypes()
					.appendLine()
			)
		}

		val zipDependent = listOf(
			Tuple("arbZipDependentGenerated", "ArbZipDependentKt", "ArbArgsGenerator"),
			Tuple("semiOrderedZipDependentGenerated", "SemiOrderedZipDependentKt", "SemiOrderedArgsGenerator")
		).map {
			it.append(
				StringBuilder("@file:JvmName(\"${it.a2}\")\n@file:JvmMultifileClass\n")
					.append(dontModifyNotice)
					.append("package ").append(mainPackageName).append(".generators").append("\n\n")
					.importTupleTypes()
					.appendLine()
			)
		}

		(1..numOfArgs).forEach { upperNumber ->
			val numbers = (1..upperNumber).toList()
			val typeArgs = numbers.joinToString { "A$it" }
			val tupleX = tupleTypeWithTypeArgs(upperNumber, typeArgs)

			if (upperNumber > 1) {

				if (upperNumber <= 9) {
					semiOrderedArgsLikeGeneratorCombineAll.forEach { (_, _, className, methodName, sb) ->
						val combine3ToX = (if (upperNumber > 3) "\n\t\t" else "") +
							(3..upperNumber).joinToString("\n\t\t") { ".$methodName(component$it()) { args, a$it -> args.append(a$it) }" }
						val otherClassName =
							if (className == "SemiOrderedArgsGenerator") "ArgsGenerator" else className
						val argsGenerators = (2..upperNumber).joinToString(",\n\t") { "$otherClassName<A$it>" }

						//TODO 2.1.0 come up with a solution which combines in one go so that we don't have to
						// create intermediate Pair, Triple .. until reaching the final TupleN
						// Moreover, such an implementation would also allow to provide a custom transform function
						sb.append(
							"""
							|/**
							| * ${if (className == "ArbArgsGenerator") "Zips" else "Combines"} the [component1] [$className] with ${if (upperNumber < 3) "the [component2] [$otherClassName]" else "all other [$otherClassName] from left to right"}
							| * resulting in a [$className] which generates [${"Tuple$upperNumber"}].
							|${
								when (className) {
									"OrderedArgsGenerator" -> {
										"""
										| *
										| * The resulting [OrderedArgsGenerator] generates the product of all [OrderedArgsGenerator.size] values before repeating.
										| *""".trimMargin()
									}

									"SemiOrderedArgsGenerator" -> {
										"""
										| *
										| * How the [ArgsGenerator]s are combined depends on their type:
										| *   - [SemiOrderedArgsGenerator]s are combined using [cartesian]
										| *   - [ArbArgsGenerator]s are combined using [zip]
										| *""".trimMargin()
									}

									else -> " *"
								}
							}
							| * @since 2.0.0
							| */
							|fun <$typeArgs> ${"Tuple$upperNumber"}<
							|	$className<A1>,
							|	$argsGenerators
							|>.combineAll(): $className<${tupleTypeWithTypeArgs(upperNumber, typeArgs)}> =
							|	component1().$methodName(component2(), ::Tuple2)$combine3ToX
							|
							""".trimMargin()
						).appendLine()
					}
				}
			}

			val upperNumberPlus1 = upperNumber + 1
			if (upperNumberPlus1 <= 9) {
				val typeArgsPlus1 = (1..upperNumberPlus1).joinToString { "A$it" }
				val tupleXPlus1 = tupleTypeWithTypeArgs(upperNumberPlus1, typeArgsPlus1)

				semiOrderedArgsLikeGeneratorCartesian.forEach { (_, _, className, sb) ->
					sb.append(
						"""
						|/**
						| * Combines `this` [${className}] with the given [other]&nbsp;[${className}] resulting in their
						| * cartesian product where the values are transformed into a [${"Tuple$upperNumberPlus1"}].
						| *
						| * The resulting [$className] generates
						| * [this.size][$className.size] * [other.size][$className.size] values before repeating.
						| *
						| * @param other The other [${className}] which generates values of type [A$upperNumberPlus1].
						| *
						| * @return The resulting [${className}] which represents the cartesian product and
						| *   generates values of type [${"Tuple$upperNumberPlus1"}].
						| *
						| * @since 2.0.0
						| */
						|@JvmName("cartesianToTuple${upperNumberPlus1}")
						|fun <$typeArgsPlus1> ${className}<$tupleX>.cartesian(
						|	other: ${className}<A$upperNumberPlus1>
						|): ${className}<$tupleXPlus1> = cartesian(other${
							if (upperNumber == 1) ", ::Tuple2)"
							else """) { args, otherArg ->
								|	args.append(otherArg)
								|}""".trimMargin()
						}
						|""".trimMargin()
					).appendLine()
				}

				arbZip.forEach { (_, _, className, sb) ->
					sb.append(
						"""
						|/**
						| * Zips `this` [${className}] with the given [other]&nbsp;[ArbArgsGenerator].
						| *
						| * @param other The other [ArbArgsGenerator] which generates values of type [A$upperNumberPlus1].
						| *
						| * @return The resulting [${className}] which generates values of type [${
							"Tuple$upperNumberPlus1"
						}].
						| *
						| * @since 2.0.0
						| */
						|@JvmName("zipToTuple${upperNumberPlus1}")
						|fun <$typeArgsPlus1> ${className}<$tupleX>.zip(
						|	other: ArbArgsGenerator<A$upperNumberPlus1>
						|): ${className}<$tupleXPlus1> = zip(other${
							if (upperNumber == 1) ", ::Tuple2)"
							else """) { args, otherArg ->
							 |	args.append(otherArg)
							 |}""".trimMargin()
						}
						|""".trimMargin()
					).appendLine()
				}

				zipDependent.forEach { (_, _, className, sb) ->
					val tupleType = tupleType(upperNumber, "A1")
					sb.append(
						"""
						|/**
						| * Creates for each generated value of type [$tupleType] by `this` [$className] an [ArbArgsGenerator]
						| * with the help of the given [otherFactory] and then zips the value of `this` [${className}]
						| * with one value of the other [ArbArgsGenerator].
						| *
						| * @param otherFactory Builds an [ArbArgsGenerator] based on a given value of type [$tupleType].
						| *
						| * @param A1 The type of values generated by `this` [$className].
						| * @param A2 The type of values generated by the other [ArbArgsGenerator] (built by the given [otherFactory]).
						| *
						| * @return The resulting [${className}] which generates values of type [${"Tuple$upperNumberPlus1"}].
						| *
						| * @since 2.0.0
						| */
						|@JvmName("zipDependentToTuple${upperNumberPlus1}")
						|fun <$typeArgsPlus1> ${className}<$tupleX>.zipDependent(
						|	otherFactory: ArbExtensionPoint.($tupleX) -> ArbArgsGenerator<A$upperNumberPlus1>
						|): ${className}<$tupleXPlus1> = zipDependent(otherFactory${
							if (upperNumber == 1) ", ::Tuple2)"
							else """) { args, otherArg ->
								 |	args.append(otherArg)
								 |}""".trimMargin()
						}
						|""".trimMargin()
					).appendLine()
				}
			}

		}

		semiOrderedArgsLikeGeneratorCombineAll.forEach { (fileName, _, _, _, sb) ->
			sb.writeToFile("generators/$fileName.kt")
		}
		semiOrderedArgsLikeGeneratorCartesian.forEach { (fileName, _, _, sb) ->
			sb.writeToFile("generators/$fileName.kt")
		}

		arbZip.forEach { (fileName, _, _, sb) ->
			sb.writeToFile("generators/$fileName.kt")
		}

		zipDependent.forEach { (fileName, _, _, sb) ->
			sb.writeToFile("generators/$fileName.kt")
		}
	}
}
generationFolder.builtBy(generate)

tasks.register("generateAll") {
	dependsOn(generate)
}

private fun StringBuilder.writeToFile(file: File) {
	file.parentFile.mkdirs()
	file.writeText(this.toString())
}
