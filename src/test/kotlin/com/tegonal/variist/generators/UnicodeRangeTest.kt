package com.tegonal.variist.generators

import ch.tutteli.atrium.api.fluent.en_GB.feature
import ch.tutteli.atrium.api.fluent.en_GB.notToThrow
import ch.tutteli.atrium.api.fluent.en_GB.toBeLessThan
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.atrium.api.verbs.expectGrouped
import ch.tutteli.kbox.ifWithinBound
import com.tegonal.variist.testutils.atrium.endInclusive
import com.tegonal.variist.testutils.atrium.start
import com.tegonal.variist.testutils.atrium.toBeIn
import com.tegonal.variist.testutils.atrium.toBeInIf
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test

class UnicodeRangeTest {

	@Test
	fun utf8_printable_plus_private_use_control_and_unassigned_is_utf8() {
		val merged =
			UnicodeRanges.UTF_8_PRINTABLE.ranges.toMutableList() + UnicodeRanges.PRIVATE_USE.ranges + UnicodeRanges.CONTROL.ranges + UnicodeRanges.UNASSIGNED.ranges

		val mergedSorted = merged.sortedBy { it.start }
		var counter = 0
		var currentRange = mergedSorted[counter]
		UnicodeRanges.UTF_8.ranges.forEach { range ->
			(range.start..range.endInclusive).forEach {
				if (it !in currentRange) {
					val report = mergedSorted.ifWithinBound(
						++counter,
						thenBlock = {
							currentRange = mergedSorted[counter]
							it !in currentRange
						},
						elseBlock = { true }
					)
					if (report) {
						expect(it).toBeIn(currentRange)
					}
				}
			}
		}
	}

	@Test
	fun rangesAreSortedAndNoOverlap() {
		UnicodeRanges.entries.forEach {
			if (it.ranges.size > 1) {
				expect(it) {
					it.ranges.asSequence().zipWithNext { a, b ->
						feature("range", { a }) {
							start.toBeLessThan(b.start)
							endInclusive.toBeLessThan(b.start)
						}
					}.count()
				}
			}
		}
	}


//	@Test
	@Suppress("KotlinUnreachableCode")
	fun charsCorrectlyAssigned() {
		// deactivated as the unicode-list is rather static and this test takes about 20s, activate if you change something
		return
		val tabToCr = 0x0009..0x000D
		val fileToUnitSeparator = 0x001C..0x001F

		var counter = 0
		var currentRange = inUnassignedButShouldnt[counter]

		expectGrouped {
			// only here so that we have at least one expectation
			expect(1).toEqual(1)
			(0..UnicodeRange.MAX_CODE_POINT).forEach { codePoint ->
				try {
					val charType = Character.getType(codePoint)
					// using the top-level verb on purpose as adding everything to expectGrouped results in an
					// out-of-memory error, we only catch failures and append them (so report all failures)
					ch.tutteli.atrium.api.verbs.expect(codePoint) {
						feature(
							"${"0x%04X".format(codePoint)} (${String(Character.toChars(codePoint))}) with character type $charType",
							{ this }
						) {
							val isAsciiLower = codePoint >= 'a'.code && codePoint <= 'z'.code
							val isAsciiUpper = codePoint >= 'A'.code && codePoint <= 'Z'.code
							val isInAsciiRange = codePoint <= UnicodeRange.ASCII_END
							val isInIso88591Range = codePoint <= UnicodeRange.ISO_8859_1_END
							when {
								charType == Character.PRIVATE_USE.toInt() -> {
									toBeInIf {
										it == UnicodeRanges.PRIVATE_USE ||
											it == UnicodeRanges.UTF_8 ||
											it == UnicodeRanges.ALL
									}
								}

								charType == Character.UNASSIGNED.toInt() -> {
									val shouldBePrintable = codePoint >= currentRange.start &&
										(codePoint <= currentRange.endInclusive ||
											inUnassignedButShouldnt.ifWithinBound(
												++counter,
												thenBlock = {
													currentRange = inUnassignedButShouldnt[counter]
													codePoint in currentRange
												}, elseBlock = {
													false
												})
											)

									toBeInIf {
										// JDKs usually don't include all valid unicode codePoints in CharacterData
										shouldBePrintable && it == UnicodeRanges.UTF_8_PRINTABLE ||
											shouldBePrintable.not() && it == UnicodeRanges.UNASSIGNED ||
											it == UnicodeRanges.UTF_8 ||
											it == UnicodeRanges.ALL
									}
									feature("Char.isDefined") { Character.isDefined(this) }.toEqual(false)
								}

								charType == Character.CONTROL.toInt() -> {
									// Unicode considers TAB and co as control, same for file separator and co.
									// Kotlin includes them in char.isWhitespace
									val shouldBeWhitespace = codePoint in tabToCr || codePoint in fileToUnitSeparator

									toBeInIf {
										it == UnicodeRanges.CONTROL ||
											it == UnicodeRanges.UTF_8 ||
											it == UnicodeRanges.ALL ||
											isInAsciiRange && it == UnicodeRanges.ASCII ||
											isInIso88591Range && it == UnicodeRanges.ISO_8859_1 ||
											shouldBeWhitespace && it == UnicodeRanges.WHITESPACES_KOTLIN
									}
								}

								charType == Character.SPACE_SEPARATOR.toInt() ||
									charType == Character.PARAGRAPH_SEPARATOR.toInt() ||
									Character.isWhitespace(codePoint) || Character.isSpaceChar(codePoint) -> toBeInIf {
									it == UnicodeRanges.WHITESPACES_KOTLIN ||
										(isInAsciiRange && (it == UnicodeRanges.ASCII || it == UnicodeRanges.ASCII_PRINTABLE)) ||
										(isInIso88591Range && (it == UnicodeRanges.ISO_8859_1 || it == UnicodeRanges.ISO_8859_1_PRINTABLE)) ||
										it == UnicodeRanges.UTF_8 ||
										it == UnicodeRanges.UTF_8_PRINTABLE ||
										it == UnicodeRanges.ALL
								}

								charType == Character.SURROGATE.toInt() -> toBeInIf {
									it == UnicodeRanges.SURROGATES ||
										it == UnicodeRanges.ALL
								}

								else -> {
									toBeInIf {
										isInAsciiRange && run {
											it == UnicodeRanges.ASCII || it == UnicodeRanges.ASCII_PRINTABLE ||
												it == UnicodeRanges.ASCII_ALPHA_UPPER && isAsciiUpper ||
												it == UnicodeRanges.ASCII_ALPHA_LOWER && isAsciiLower ||
												it == UnicodeRanges.ASCII_ALPHA && (isAsciiLower || isAsciiUpper) ||
												it == UnicodeRanges.ASCII_DIGIT && codePoint in '0'.code..'9'.code
										} ||
											isInIso88591Range && run {
											it == UnicodeRanges.ISO_8859_1 || it == UnicodeRanges.ISO_8859_1_PRINTABLE ||
												it == UnicodeRanges.ISO_8859_1_ALPHA && (
												isAsciiLower || isAsciiUpper ||
													codePoint in 'À'.code..'Ö'.code ||
													codePoint in 'Ø'.code..'ö'.code ||
													codePoint in 'ø'.code..'ÿ'.code
												)
										} ||
											it == UnicodeRanges.UTF_8 ||
											it == UnicodeRanges.UTF_8_PRINTABLE ||
											it == UnicodeRanges.ALL
									}
								}
							}
						}
					}
				} catch (e: AssertionError) {
					expect { throw e }.notToThrow()
				}
			}
		}
	}

	enum class UnicodeType(val text: String) {
		UNASSIGNED("unassigned"),
		CONTROL("control"),
		PRIVATE_USE("private use"),
		SURROGATE("surrogate"),
		PRINTABLE("printable"),
	}


	// also activate charsCorrectlyAssigned if you change something here so that you verify the changes
	//	@Test
	fun generateLists() {
		val utf8Printable = StringBuilder()
		val unassigned = StringBuilder()
		val control = StringBuilder()
		val privateUse = StringBuilder()

		var start = 0
		var currentName = "<not-defined>"
		var counter = 1
		var currentType = UnicodeType.CONTROL
		var currentComment = UnicodeType.CONTROL.text


		fun unicodeRange(start: Int, end: Int) =
			"UnicodeRange(0x" + "%04X".format(start) + ", 0x" + "%04X".format(end) + "), "

		fun appendUtf8(endCodePoint: Int) {
			if (currentType != UnicodeType.PRINTABLE) utf8Printable.append("//")
			utf8Printable.append(unicodeRange(start, endCodePoint))
			if (currentType != UnicodeType.PRINTABLE) utf8Printable.append("// ").append(currentComment)
			utf8Printable.append("\n")
		}

		fun appendNonUtf8(endCodePoint: Int) {
			when (currentType) {
				UnicodeType.UNASSIGNED -> unassigned
				UnicodeType.CONTROL -> control
				UnicodeType.PRIVATE_USE -> privateUse
				UnicodeType.SURROGATE,
				UnicodeType.PRINTABLE -> null
			}?.also {
				it.append(unicodeRange(start, endCodePoint))
				if (currentType == UnicodeType.UNASSIGNED) it.append("// ").append(currentComment)
				it.append("\n")
			}
		}

		fun updateComment(type: UnicodeType, description: String) {
			if (type != UnicodeType.PRINTABLE) {
				val name = when {
					type == UnicodeType.CONTROL -> "control"
					description.startsWith("<") -> description.substring(1).substringBefore(",")
					else -> description.substringBefore(" ")
				}
				currentComment = if (type == UnicodeType.UNASSIGNED) {
					if (currentName == name) {
						++counter
					} else {
						currentName = name
						counter = 1
					}
					"$currentName ${UnicodeType.UNASSIGNED.text} $counter"
				} else {
					name
				}
			}
		}

		fun determineUnicodeType(description: String): UnicodeType =
			when {
				description == "<control>" -> UnicodeType.CONTROL
				description.contains("Surrogate") -> UnicodeType.SURROGATE
				description.contains("Private Use") -> UnicodeType.PRIVATE_USE
				else -> UnicodeType.PRINTABLE
			}

		fun appendAll(endCodePoint: Int, type: UnicodeType, description: String) {
			appendNonUtf8(endCodePoint)
			appendUtf8(endCodePoint)
			updateComment(type, description)
		}

		fun appendIfDifferentType(endCodePoint: Int, type: UnicodeType, description: String) {
			if (type != currentType) {
				appendAll(endCodePoint, type, description)
				currentType = type
				start = endCodePoint + 1
			}
		}

		val resource = this::class.java.getResourceAsStream("/UnicodeData.txt")
			?: error("You need to download UnicodeData.txt first and put into src/test/resources.\nYou can download it from:\nhttps://www.unicode.org/Public/UNIDATA/UnicodeData.txt")

		var previousCodePoint = -1
		var previousDescription = ""
		resource.bufferedReader().useLines { lines ->
			lines.forEach { line ->
				if (line.isBlank()) return@forEach
				val parts = line.split(';')
				val codePoint = parts[0].toInt(16)
				val description = parts[1]
				val isLast = line.contains("Last>")
				if (codePoint - 1 != previousCodePoint && isLast.not()) {
					appendIfDifferentType(previousCodePoint, UnicodeType.UNASSIGNED, previousDescription)
				}

				previousCodePoint = codePoint
				previousDescription = description
				val type = determineUnicodeType(description)
				appendIfDifferentType(codePoint - 1, type, description)
			}
		}
		appendIfDifferentType(previousCodePoint, UnicodeType.UNASSIGNED, previousDescription)
		val type = UnicodeType.UNASSIGNED
		if (type == currentType) {
			appendAll(UnicodeRange.MAX_CODE_POINT, type, "unassigned")
		}

		println("control:\n$control\n\nprivate use:\n$privateUse\n\nunassigned:\n$unassigned\n\nutf8Printable:\n$utf8Printable")
	}

	// also activate charsCorrectlyAssigned if you change something here so that you verify the changes
	//	@Test
	fun generateInJdkUnassignedButShouldnt() {
		var startRange = -1
		var lastUnassigned = 0
		var counter = 0
		val ranges = UnicodeRanges.UNASSIGNED.ranges
		var currentRange = ranges[counter]
		(0..UnicodeRange.MAX_CODE_POINT).forEach { codePoint ->
			if (Character.getType(codePoint) == Character.UNASSIGNED.toInt()) {
				val notDefined = codePoint < currentRange.start ||
					codePoint !in currentRange &&
					ranges.ifWithinBound(
						++counter,
						thenBlock = {
							currentRange = ranges[counter]
							codePoint !in currentRange
						},
						elseBlock = {
							true
						})
				if (notDefined) {
					if (startRange == -1) {
						startRange = codePoint
						lastUnassigned = codePoint
					} else if (codePoint - 1 == lastUnassigned) {
						lastUnassigned = codePoint
					} else {
						println("UnicodeRange(0x${"%04X".format(startRange)}, 0x${"%04X".format(lastUnassigned)}),")
						startRange = codePoint
						lastUnassigned = codePoint
					}
				}
			}
		}
		println("UnicodeRange(0x${"%04X".format(startRange)}, 0x${"%04X".format(lastUnassigned)}),")
	}

	val inUnassignedButShouldnt = listOf(
		UnicodeRange(0x0560, 0x0560),
		UnicodeRange(0x0588, 0x0588),
		UnicodeRange(0x05EF, 0x05EF),
		UnicodeRange(0x061D, 0x061D),
		UnicodeRange(0x07FD, 0x07FF),
		UnicodeRange(0x0870, 0x088E),
		UnicodeRange(0x0890, 0x0891),
		UnicodeRange(0x0897, 0x089F),
		UnicodeRange(0x08B5, 0x08B5),
		UnicodeRange(0x08BE, 0x08D3),
		UnicodeRange(0x09FE, 0x09FE),
		UnicodeRange(0x0A76, 0x0A76),
		UnicodeRange(0x0B55, 0x0B55),
		UnicodeRange(0x0C04, 0x0C04),
		UnicodeRange(0x0C3C, 0x0C3C),
		UnicodeRange(0x0C5D, 0x0C5D),
		UnicodeRange(0x0C77, 0x0C77),
		UnicodeRange(0x0C84, 0x0C84),
		UnicodeRange(0x0CDD, 0x0CDD),
		UnicodeRange(0x0CF3, 0x0CF3),
		UnicodeRange(0x0D04, 0x0D04),
		UnicodeRange(0x0D81, 0x0D81),
		UnicodeRange(0x0E86, 0x0E86),
		UnicodeRange(0x0E89, 0x0E89),
		UnicodeRange(0x0E8C, 0x0E8C),
		UnicodeRange(0x0E8E, 0x0E93),
		UnicodeRange(0x0E98, 0x0E98),
		UnicodeRange(0x0EA0, 0x0EA0),
		UnicodeRange(0x0EA8, 0x0EA9),
		UnicodeRange(0x0EAC, 0x0EAC),
		UnicodeRange(0x0EBA, 0x0EBA),
		UnicodeRange(0x0ECE, 0x0ECE),
		UnicodeRange(0x170D, 0x170D),
		UnicodeRange(0x1715, 0x1715),
		UnicodeRange(0x171F, 0x171F),
		UnicodeRange(0x180F, 0x180F),
		UnicodeRange(0x1878, 0x1878),
		UnicodeRange(0x1ABF, 0x1ACE),
		UnicodeRange(0x1B4C, 0x1B4C),
		UnicodeRange(0x1B4E, 0x1B4F),
		UnicodeRange(0x1B7D, 0x1B7F),
		UnicodeRange(0x1C89, 0x1C8A),
		UnicodeRange(0x1C90, 0x1CBA),
		UnicodeRange(0x1CBD, 0x1CBF),
		UnicodeRange(0x1CFA, 0x1CFA),
		UnicodeRange(0x1DFA, 0x1DFA),
		UnicodeRange(0x20C0, 0x20C0),
		UnicodeRange(0x2427, 0x2429),
		UnicodeRange(0x2B97, 0x2B97),
		UnicodeRange(0x2BBA, 0x2BBC),
		UnicodeRange(0x2BC9, 0x2BC9),
		UnicodeRange(0x2BD3, 0x2BEB),
		UnicodeRange(0x2BF0, 0x2BFF),
		UnicodeRange(0x2C2F, 0x2C2F),
		UnicodeRange(0x2C5F, 0x2C5F),
		UnicodeRange(0x2E4A, 0x2E5D),
		UnicodeRange(0x2FFC, 0x2FFF),
		UnicodeRange(0x312F, 0x312F),
		UnicodeRange(0x31BB, 0x31BF),
		UnicodeRange(0x31E4, 0x31E5),
		UnicodeRange(0x31EF, 0x31EF),
		UnicodeRange(0x4DB6, 0x4DBF),
		UnicodeRange(0x9FF0, 0x9FFF),
		UnicodeRange(0xA7AF, 0xA7AF),
		UnicodeRange(0xA7B8, 0xA7CD),
		UnicodeRange(0xA7D0, 0xA7D1),
		UnicodeRange(0xA7D3, 0xA7D3),
		UnicodeRange(0xA7D5, 0xA7DC),
		UnicodeRange(0xA7F2, 0xA7F6),
		UnicodeRange(0xA82C, 0xA82C),
		UnicodeRange(0xA8FE, 0xA8FF),
		UnicodeRange(0xAB66, 0xAB6B),
		UnicodeRange(0xFBC2, 0xFBC2),
		UnicodeRange(0xFD40, 0xFD4F),
		UnicodeRange(0xFDCF, 0xFDCF),
		UnicodeRange(0xFDFE, 0xFDFF),
		UnicodeRange(0x1019C, 0x1019C),
		UnicodeRange(0x10570, 0x1057A),
		UnicodeRange(0x1057C, 0x1058A),
		UnicodeRange(0x1058C, 0x10592),
		UnicodeRange(0x10594, 0x10595),
		UnicodeRange(0x10597, 0x105A1),
		UnicodeRange(0x105A3, 0x105B1),
		UnicodeRange(0x105B3, 0x105B9),
		UnicodeRange(0x105BB, 0x105BC),
		UnicodeRange(0x105C0, 0x105F3),
		UnicodeRange(0x10780, 0x10785),
		UnicodeRange(0x10787, 0x107B0),
		UnicodeRange(0x107B2, 0x107BA),
		UnicodeRange(0x10A34, 0x10A35),
		UnicodeRange(0x10A48, 0x10A48),
		UnicodeRange(0x10D00, 0x10D27),
		UnicodeRange(0x10D30, 0x10D39),
		UnicodeRange(0x10D40, 0x10D65),
		UnicodeRange(0x10D69, 0x10D85),
		UnicodeRange(0x10D8E, 0x10D8F),
		UnicodeRange(0x10E80, 0x10EA9),
		UnicodeRange(0x10EAB, 0x10EAD),
		UnicodeRange(0x10EB0, 0x10EB1),
		UnicodeRange(0x10EC2, 0x10EC4),
		UnicodeRange(0x10EFC, 0x10F27),
		UnicodeRange(0x10F30, 0x10F59),
		UnicodeRange(0x10F70, 0x10F89),
		UnicodeRange(0x10FB0, 0x10FCB),
		UnicodeRange(0x10FE0, 0x10FF6),
		UnicodeRange(0x11070, 0x11075),
		UnicodeRange(0x110C2, 0x110C2),
		UnicodeRange(0x110CD, 0x110CD),
		UnicodeRange(0x11144, 0x11147),
		UnicodeRange(0x111CE, 0x111CF),
		UnicodeRange(0x1123F, 0x11241),
		UnicodeRange(0x1133B, 0x1133B),
		UnicodeRange(0x11380, 0x11389),
		UnicodeRange(0x1138B, 0x1138B),
		UnicodeRange(0x1138E, 0x1138E),
		UnicodeRange(0x11390, 0x113B5),
		UnicodeRange(0x113B7, 0x113C0),
		UnicodeRange(0x113C2, 0x113C2),
		UnicodeRange(0x113C5, 0x113C5),
		UnicodeRange(0x113C7, 0x113CA),
		UnicodeRange(0x113CC, 0x113D5),
		UnicodeRange(0x113D7, 0x113D8),
		UnicodeRange(0x113E1, 0x113E2),
		UnicodeRange(0x1145A, 0x1145A),
		UnicodeRange(0x1145E, 0x11461),
		UnicodeRange(0x116B8, 0x116B9),
		UnicodeRange(0x116D0, 0x116E3),
		UnicodeRange(0x1171A, 0x1171A),
		UnicodeRange(0x11740, 0x11746),
		UnicodeRange(0x11800, 0x1183B),
		UnicodeRange(0x11900, 0x11906),
		UnicodeRange(0x11909, 0x11909),
		UnicodeRange(0x1190C, 0x11913),
		UnicodeRange(0x11915, 0x11916),
		UnicodeRange(0x11918, 0x11935),
		UnicodeRange(0x11937, 0x11938),
		UnicodeRange(0x1193B, 0x11946),
		UnicodeRange(0x11950, 0x11959),
		UnicodeRange(0x119A0, 0x119A7),
		UnicodeRange(0x119AA, 0x119D7),
		UnicodeRange(0x119DA, 0x119E4),
		UnicodeRange(0x11A84, 0x11A85),
		UnicodeRange(0x11A9D, 0x11A9D),
		UnicodeRange(0x11AB0, 0x11ABF),
		UnicodeRange(0x11B00, 0x11B09),
		UnicodeRange(0x11BC0, 0x11BE1),
		UnicodeRange(0x11BF0, 0x11BF9),
		UnicodeRange(0x11D60, 0x11D65),
		UnicodeRange(0x11D67, 0x11D68),
		UnicodeRange(0x11D6A, 0x11D8E),
		UnicodeRange(0x11D90, 0x11D91),
		UnicodeRange(0x11D93, 0x11D98),
		UnicodeRange(0x11DA0, 0x11DA9),
		UnicodeRange(0x11EE0, 0x11EF8),
		UnicodeRange(0x11F00, 0x11F10),
		UnicodeRange(0x11F12, 0x11F3A),
		UnicodeRange(0x11F3E, 0x11F5A),
		UnicodeRange(0x11FB0, 0x11FB0),
		UnicodeRange(0x11FC0, 0x11FF1),
		UnicodeRange(0x11FFF, 0x11FFF),
		UnicodeRange(0x12F90, 0x12FF2),
		UnicodeRange(0x1342F, 0x13455),
		UnicodeRange(0x13460, 0x143FA),
		UnicodeRange(0x16100, 0x16139),
		UnicodeRange(0x16A70, 0x16ABE),
		UnicodeRange(0x16AC0, 0x16AC9),
		UnicodeRange(0x16D40, 0x16D79),
		UnicodeRange(0x16E40, 0x16E9A),
		UnicodeRange(0x16F45, 0x16F4A),
		UnicodeRange(0x16F4F, 0x16F4F),
		UnicodeRange(0x16F7F, 0x16F87),
		UnicodeRange(0x16FE2, 0x16FE4),
		UnicodeRange(0x16FF0, 0x16FF1),
		UnicodeRange(0x187ED, 0x187F7),
		UnicodeRange(0x18AF3, 0x18CD5),
		UnicodeRange(0x18CFF, 0x18D08),
		UnicodeRange(0x1AFF0, 0x1AFF3),
		UnicodeRange(0x1AFF5, 0x1AFFB),
		UnicodeRange(0x1AFFD, 0x1AFFE),
		UnicodeRange(0x1B11F, 0x1B122),
		UnicodeRange(0x1B132, 0x1B132),
		UnicodeRange(0x1B150, 0x1B152),
		UnicodeRange(0x1B155, 0x1B155),
		UnicodeRange(0x1B164, 0x1B167),
		UnicodeRange(0x1CC00, 0x1CCF9),
		UnicodeRange(0x1CD00, 0x1CEB3),
		UnicodeRange(0x1CF00, 0x1CF2D),
		UnicodeRange(0x1CF30, 0x1CF46),
		UnicodeRange(0x1CF50, 0x1CFC3),
		UnicodeRange(0x1D1E9, 0x1D1EA),
		UnicodeRange(0x1D2C0, 0x1D2D3),
		UnicodeRange(0x1D2E0, 0x1D2F3),
		UnicodeRange(0x1D372, 0x1D378),
		UnicodeRange(0x1DF00, 0x1DF1E),
		UnicodeRange(0x1DF25, 0x1DF2A),
		UnicodeRange(0x1E030, 0x1E06D),
		UnicodeRange(0x1E08F, 0x1E08F),
		UnicodeRange(0x1E100, 0x1E12C),
		UnicodeRange(0x1E130, 0x1E13D),
		UnicodeRange(0x1E140, 0x1E149),
		UnicodeRange(0x1E14E, 0x1E14F),
		UnicodeRange(0x1E290, 0x1E2AE),
		UnicodeRange(0x1E2C0, 0x1E2F9),
		UnicodeRange(0x1E2FF, 0x1E2FF),
		UnicodeRange(0x1E4D0, 0x1E4F9),
		UnicodeRange(0x1E5D0, 0x1E5FA),
		UnicodeRange(0x1E5FF, 0x1E5FF),
		UnicodeRange(0x1E7E0, 0x1E7E6),
		UnicodeRange(0x1E7E8, 0x1E7EB),
		UnicodeRange(0x1E7ED, 0x1E7EE),
		UnicodeRange(0x1E7F0, 0x1E7FE),
		UnicodeRange(0x1E94B, 0x1E94B),
		UnicodeRange(0x1EC71, 0x1ECB4),
		UnicodeRange(0x1ED01, 0x1ED3D),
		UnicodeRange(0x1F10D, 0x1F10F),
		UnicodeRange(0x1F12F, 0x1F12F),
		UnicodeRange(0x1F16C, 0x1F16F),
		UnicodeRange(0x1F1AD, 0x1F1AD),
		UnicodeRange(0x1F6D5, 0x1F6D7),
		UnicodeRange(0x1F6DC, 0x1F6DF),
		UnicodeRange(0x1F6F9, 0x1F6FC),
		UnicodeRange(0x1F774, 0x1F776),
		UnicodeRange(0x1F77B, 0x1F77F),
		UnicodeRange(0x1F7D5, 0x1F7D9),
		UnicodeRange(0x1F7E0, 0x1F7EB),
		UnicodeRange(0x1F7F0, 0x1F7F0),
		UnicodeRange(0x1F8B0, 0x1F8BB),
		UnicodeRange(0x1F8C0, 0x1F8C1),
		UnicodeRange(0x1F90C, 0x1F90F),
		UnicodeRange(0x1F93F, 0x1F93F),
		UnicodeRange(0x1F94D, 0x1F94F),
		UnicodeRange(0x1F96C, 0x1F97F),
		UnicodeRange(0x1F998, 0x1F9BF),
		UnicodeRange(0x1F9C1, 0x1F9CF),
		UnicodeRange(0x1F9E7, 0x1FA53),
		UnicodeRange(0x1FA60, 0x1FA6D),
		UnicodeRange(0x1FA70, 0x1FA7C),
		UnicodeRange(0x1FA80, 0x1FA89),
		UnicodeRange(0x1FA8F, 0x1FAC6),
		UnicodeRange(0x1FACE, 0x1FADC),
		UnicodeRange(0x1FADF, 0x1FAE9),
		UnicodeRange(0x1FAF0, 0x1FAF8),
		UnicodeRange(0x1FB00, 0x1FB92),
		UnicodeRange(0x1FB94, 0x1FBF9),
		UnicodeRange(0x2A6D7, 0x2A6DF),
		UnicodeRange(0x2B735, 0x2B739),
		UnicodeRange(0x2EBF0, 0x2EE5D),
		UnicodeRange(0x30000, 0x3134A),
		UnicodeRange(0x31350, 0x323AF),
	)
}
