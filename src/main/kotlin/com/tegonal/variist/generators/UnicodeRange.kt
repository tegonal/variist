package com.tegonal.variist.generators

import com.tegonal.variist.generators.UnicodeRange.Companion.AFTER_SURROGATES_END
import com.tegonal.variist.generators.UnicodeRange.Companion.BEFORE_SURROGATES_START
import com.tegonal.variist.generators.UnicodeRange.Companion.ASCII_END
import com.tegonal.variist.generators.UnicodeRange.Companion.ASCII_PRINTABLE_END
import com.tegonal.variist.generators.UnicodeRange.Companion.ASCII_PRINTABLE_START
import com.tegonal.variist.generators.UnicodeRange.Companion.MAX_CODE_POINT
import com.tegonal.variist.generators.UnicodeRange.Companion.ISO_8859_1_END
import com.tegonal.variist.generators.UnicodeRange.Companion.ISO_8859_1_PRINTABLE_START
import com.tegonal.variist.generators.UnicodeRange.Companion.BMP_END
import com.tegonal.variist.generators.UnicodeRange.Companion.NON_BMP_START
import com.tegonal.variist.generators.UnicodeRange.Companion.SURROGATES_END
import com.tegonal.variist.generators.UnicodeRange.Companion.SURROGATES_START
import com.tegonal.variist.utils.impl.failIfNegative
import kotlin.collections.binarySearch

/**
 * A range defined via a [start] code point and an [endInclusive] code point, either staying in the
 * Basic Multilingual Plane (BMP) domain (0x0000 - 0xFFFF) or being completely outside of it.
 *
 * @since 2.0.0
 */
class UnicodeRange(override val start: Int, override val endInclusive: Int) : ClosedRange<Int> {

	constructor(start: Char, endInclusive: Char) : this(start.code, endInclusive.code)

	init {
		failIfNegative(start, "start")
		require(endInclusive <= MAX_CODE_POINT) {
			"endInclusive (${endInclusive.formatAsCodepoint()}) must be less than or equal to ${MAX_CODE_POINT.formatAsCodepoint()}"
		}
		require(start <= endInclusive) {
			"start (${start.formatAsCodepoint()}) must be less than or equal to endInclusive (${endInclusive.formatAsCodepoint()}) -- a ${UnicodeRange::class.simpleName} doesn't support to be empty"
		}
		require(Character.isBmpCodePoint(start) == Character.isBmpCodePoint(endInclusive)) {
			"starts (${start.formatAsCodepoint()}) and endInclusive (${endInclusive.formatAsCodepoint()}) must either both be in BPM domain (0x0000 - 0xFFFF) or both above"
		}
	}

	/**
	 * Indicates if this Range is a BMP (Basic Multilingual Plane) or not.
	 * @return `true` if it is a BMP range, false otherwise.
	 */
	fun isBmpRange(): Boolean {
		// since we don't allow that a Unicode rang starts in BMP and ends in non-BMP it is enough to just check start
		return Character.isBmpCodePoint(start)
	}

	override fun toString(): String = "${start.formatAsCodepoint()}..${endInclusive.formatAsCodepoint()}"

	companion object {
		private fun Int.formatAsCodepoint() = "0x" + this.toString(16).uppercase().padStart(4, '0')

		const val SURROGATES_START = 0xD800
		const val SURROGATES_END = 0xDFFF
		const val BEFORE_SURROGATES_START = SURROGATES_START - 1
		const val AFTER_SURROGATES_END = SURROGATES_END + 1

		const val BMP_END = 0xFFFF
		const val NON_BMP_START = BMP_END + 1
		const val MAX_CODE_POINT = 0x10FFFF
		const val ASCII_END = 0x7F
		const val ASCII_PRINTABLE_START = 0x20
		const val ASCII_PRINTABLE_END = ASCII_END - 1
		const val ISO_8859_1_END = 0xFF
		const val ISO_8859_1_PRINTABLE_START = 0xA0
	}
}

/**
 * Predefined [UnicodeRange] collections -- don't depend on enum order, we might break it.
 *
 * @since 2.0.0
 */
enum class UnicodeRanges(open vararg val ranges: UnicodeRange) {
	ASCII(UnicodeRange(0x00, ASCII_END)),
	ASCII_ALPHA_LOWER(UnicodeRange('a', 'z')),
	ASCII_ALPHA_UPPER(UnicodeRange('A', 'Z')),
	ASCII_ALPHA(UnicodeRange('A', 'Z'), UnicodeRange('a', 'z')),
	ASCII_DIGIT(UnicodeRange('0', '9')),
	ASCII_PRINTABLE(UnicodeRange(ASCII_PRINTABLE_START, ASCII_PRINTABLE_END)),
	ISO_8859_1(UnicodeRange(0x00, ISO_8859_1_END)),
	ISO_8859_1_PRINTABLE(
		ranges = arrayOf(UnicodeRange(ISO_8859_1_PRINTABLE_START, ISO_8859_1_END)) + ASCII_PRINTABLE.ranges
	),
	UTF_8(
		// we only exclude surrogates as they are not valid code points in UTF-8 everything else are valid points
		// but maybe not suited (like unassigned code points, private use, noncharacters etc.).
		UnicodeRange(0x0000, BEFORE_SURROGATES_START),
		UnicodeRange(AFTER_SURROGATES_END, BMP_END),
		UnicodeRange(NON_BMP_START, MAX_CODE_POINT),
	),

	// UTF_8_PRINTABLE see further below as it is quite long it comes last
	SURROGATES(UnicodeRange(SURROGATES_START, SURROGATES_END)),
	ALL(UnicodeRange(0x0000, BMP_END), UnicodeRange(NON_BMP_START, MAX_CODE_POINT)),
	CONTROL() {
		override val ranges: Array<out UnicodeRange> by lazy {
			arrayOf(
				UnicodeRange(0x0000, ASCII_PRINTABLE_START - 1),
				UnicodeRange(ASCII_PRINTABLE_END + 1, ISO_8859_1_PRINTABLE_START - 1)
			)
		}
	},
	PRIVATE_USE() {
		override val ranges: Array<out UnicodeRange> by lazy {
			arrayOf(
				UnicodeRange(0xE000, 0xF8FF),
				// plane 15 + 16: private use A and B
				UnicodeRange(0xF0000, 0xFFFFD),
				UnicodeRange(0x100000, 0x10FFFD)
			)
		}
	},

	/**
	 * Kotlins [kotlin.text.isWhitespace] combines Java's [Character.isWhitespace] and [Character.isSpaceChar]
	 * This list includes all as of JDK 11
	 */
	WHITESPACES_KOTLIN() {
		override val ranges: Array<out UnicodeRange> by lazy {
			arrayOf(
				UnicodeRange(0x0009, 0x000D), // TAB, LF, VT, FF, CR
				UnicodeRange(0x001C, 0x001F), // file, group, record + unit separator
				UnicodeRange(0x0020, 0x0020), // space
				UnicodeRange(0x00A0, 0x00A0), // no-break space (nbsp)
				UnicodeRange(0x1680, 0x1680), // ogham space mark
				UnicodeRange(0x2000, 0x200A), // EN quad .. hair space
				UnicodeRange(0x2028, 0x2029), // line + paragraph separator
				UnicodeRange(0x202F, 0x202F), // narrow no-break space
				UnicodeRange(0x205F, 0x205F), // medium mathematical space
				UnicodeRange(0x3000, 0x3000), // ideographic space
				// Note, zero-width space is not a whitespace
			)
		}
	},
	UNASSIGNED() {
		override val ranges: Array<out UnicodeRange> by lazy {
			// a lot of small objects, consider to turn UnicodeRange into a value object backed by a Long
			arrayOf(
				UnicodeRange(0x0378, 0x0379), // GREEK unassigned 1
				UnicodeRange(0x0380, 0x0383), // GREEK unassigned 2
				UnicodeRange(0x038B, 0x038B), // GREEK unassigned 3
				UnicodeRange(0x038D, 0x038D), // GREEK unassigned 4
				UnicodeRange(0x03A2, 0x03A2), // GREEK unassigned 5
				UnicodeRange(0x0530, 0x0530), // CYRILLIC unassigned 1
				UnicodeRange(0x0557, 0x0558), // ARMENIAN unassigned 1
				UnicodeRange(0x058B, 0x058C), // ARMENIAN unassigned 2
				UnicodeRange(0x0590, 0x0590), // ARMENIAN unassigned 3
				UnicodeRange(0x05C8, 0x05CF), // HEBREW unassigned 1
				UnicodeRange(0x05EB, 0x05EE), // HEBREW unassigned 2
				UnicodeRange(0x05F5, 0x05FF), // HEBREW unassigned 3
				UnicodeRange(0x070E, 0x070E), // SYRIAC unassigned 1
				UnicodeRange(0x074B, 0x074C), // SYRIAC unassigned 2
				UnicodeRange(0x07B2, 0x07BF), // THAANA unassigned 1
				UnicodeRange(0x07FB, 0x07FC), // NKO unassigned 1
				UnicodeRange(0x082E, 0x082F), // SAMARITAN unassigned 1
				UnicodeRange(0x083F, 0x083F), // SAMARITAN unassigned 2
				UnicodeRange(0x085C, 0x085D), // MANDAIC unassigned 1
				UnicodeRange(0x085F, 0x085F), // MANDAIC unassigned 2
				UnicodeRange(0x086B, 0x086F), // SYRIAC unassigned 1
				UnicodeRange(0x088F, 0x088F), // ARABIC unassigned 1
				UnicodeRange(0x0892, 0x0896), // ARABIC unassigned 2
				UnicodeRange(0x0984, 0x0984), // BENGALI unassigned 1
				UnicodeRange(0x098D, 0x098E), // BENGALI unassigned 2
				UnicodeRange(0x0991, 0x0992), // BENGALI unassigned 3
				UnicodeRange(0x09A9, 0x09A9), // BENGALI unassigned 4
				UnicodeRange(0x09B1, 0x09B1), // BENGALI unassigned 5
				UnicodeRange(0x09B3, 0x09B5), // BENGALI unassigned 6
				UnicodeRange(0x09BA, 0x09BB), // BENGALI unassigned 7
				UnicodeRange(0x09C5, 0x09C6), // BENGALI unassigned 8
				UnicodeRange(0x09C9, 0x09CA), // BENGALI unassigned 9
				UnicodeRange(0x09CF, 0x09D6), // BENGALI unassigned 10
				UnicodeRange(0x09D8, 0x09DB), // BENGALI unassigned 11
				UnicodeRange(0x09DE, 0x09DE), // BENGALI unassigned 12
				UnicodeRange(0x09E4, 0x09E5), // BENGALI unassigned 13
				UnicodeRange(0x09FF, 0x0A00), // BENGALI unassigned 14
				UnicodeRange(0x0A04, 0x0A04), // GURMUKHI unassigned 1
				UnicodeRange(0x0A0B, 0x0A0E), // GURMUKHI unassigned 2
				UnicodeRange(0x0A11, 0x0A12), // GURMUKHI unassigned 3
				UnicodeRange(0x0A29, 0x0A29), // GURMUKHI unassigned 4
				UnicodeRange(0x0A31, 0x0A31), // GURMUKHI unassigned 5
				UnicodeRange(0x0A34, 0x0A34), // GURMUKHI unassigned 6
				UnicodeRange(0x0A37, 0x0A37), // GURMUKHI unassigned 7
				UnicodeRange(0x0A3A, 0x0A3B), // GURMUKHI unassigned 8
				UnicodeRange(0x0A3D, 0x0A3D), // GURMUKHI unassigned 9
				UnicodeRange(0x0A43, 0x0A46), // GURMUKHI unassigned 10
				UnicodeRange(0x0A49, 0x0A4A), // GURMUKHI unassigned 11
				UnicodeRange(0x0A4E, 0x0A50), // GURMUKHI unassigned 12
				UnicodeRange(0x0A52, 0x0A58), // GURMUKHI unassigned 13
				UnicodeRange(0x0A5D, 0x0A5D), // GURMUKHI unassigned 14
				UnicodeRange(0x0A5F, 0x0A65), // GURMUKHI unassigned 15
				UnicodeRange(0x0A77, 0x0A80), // GURMUKHI unassigned 16
				UnicodeRange(0x0A84, 0x0A84), // GUJARATI unassigned 1
				UnicodeRange(0x0A8E, 0x0A8E), // GUJARATI unassigned 2
				UnicodeRange(0x0A92, 0x0A92), // GUJARATI unassigned 3
				UnicodeRange(0x0AA9, 0x0AA9), // GUJARATI unassigned 4
				UnicodeRange(0x0AB1, 0x0AB1), // GUJARATI unassigned 5
				UnicodeRange(0x0AB4, 0x0AB4), // GUJARATI unassigned 6
				UnicodeRange(0x0ABA, 0x0ABB), // GUJARATI unassigned 7
				UnicodeRange(0x0AC6, 0x0AC6), // GUJARATI unassigned 8
				UnicodeRange(0x0ACA, 0x0ACA), // GUJARATI unassigned 9
				UnicodeRange(0x0ACE, 0x0ACF), // GUJARATI unassigned 10
				UnicodeRange(0x0AD1, 0x0ADF), // GUJARATI unassigned 11
				UnicodeRange(0x0AE4, 0x0AE5), // GUJARATI unassigned 12
				UnicodeRange(0x0AF2, 0x0AF8), // GUJARATI unassigned 13
				UnicodeRange(0x0B00, 0x0B00), // GUJARATI unassigned 14
				UnicodeRange(0x0B04, 0x0B04), // ORIYA unassigned 1
				UnicodeRange(0x0B0D, 0x0B0E), // ORIYA unassigned 2
				UnicodeRange(0x0B11, 0x0B12), // ORIYA unassigned 3
				UnicodeRange(0x0B29, 0x0B29), // ORIYA unassigned 4
				UnicodeRange(0x0B31, 0x0B31), // ORIYA unassigned 5
				UnicodeRange(0x0B34, 0x0B34), // ORIYA unassigned 6
				UnicodeRange(0x0B3A, 0x0B3B), // ORIYA unassigned 7
				UnicodeRange(0x0B45, 0x0B46), // ORIYA unassigned 8
				UnicodeRange(0x0B49, 0x0B4A), // ORIYA unassigned 9
				UnicodeRange(0x0B4E, 0x0B54), // ORIYA unassigned 10
				UnicodeRange(0x0B58, 0x0B5B), // ORIYA unassigned 11
				UnicodeRange(0x0B5E, 0x0B5E), // ORIYA unassigned 12
				UnicodeRange(0x0B64, 0x0B65), // ORIYA unassigned 13
				UnicodeRange(0x0B78, 0x0B81), // ORIYA unassigned 14
				UnicodeRange(0x0B84, 0x0B84), // TAMIL unassigned 1
				UnicodeRange(0x0B8B, 0x0B8D), // TAMIL unassigned 2
				UnicodeRange(0x0B91, 0x0B91), // TAMIL unassigned 3
				UnicodeRange(0x0B96, 0x0B98), // TAMIL unassigned 4
				UnicodeRange(0x0B9B, 0x0B9B), // TAMIL unassigned 5
				UnicodeRange(0x0B9D, 0x0B9D), // TAMIL unassigned 6
				UnicodeRange(0x0BA0, 0x0BA2), // TAMIL unassigned 7
				UnicodeRange(0x0BA5, 0x0BA7), // TAMIL unassigned 8
				UnicodeRange(0x0BAB, 0x0BAD), // TAMIL unassigned 9
				UnicodeRange(0x0BBA, 0x0BBD), // TAMIL unassigned 10
				UnicodeRange(0x0BC3, 0x0BC5), // TAMIL unassigned 11
				UnicodeRange(0x0BC9, 0x0BC9), // TAMIL unassigned 12
				UnicodeRange(0x0BCE, 0x0BCF), // TAMIL unassigned 13
				UnicodeRange(0x0BD1, 0x0BD6), // TAMIL unassigned 14
				UnicodeRange(0x0BD8, 0x0BE5), // TAMIL unassigned 15
				UnicodeRange(0x0BFB, 0x0BFF), // TAMIL unassigned 16
				UnicodeRange(0x0C0D, 0x0C0D), // TELUGU unassigned 1
				UnicodeRange(0x0C11, 0x0C11), // TELUGU unassigned 2
				UnicodeRange(0x0C29, 0x0C29), // TELUGU unassigned 3
				UnicodeRange(0x0C3A, 0x0C3B), // TELUGU unassigned 4
				UnicodeRange(0x0C45, 0x0C45), // TELUGU unassigned 5
				UnicodeRange(0x0C49, 0x0C49), // TELUGU unassigned 6
				UnicodeRange(0x0C4E, 0x0C54), // TELUGU unassigned 7
				UnicodeRange(0x0C57, 0x0C57), // TELUGU unassigned 8
				UnicodeRange(0x0C5B, 0x0C5C), // TELUGU unassigned 9
				UnicodeRange(0x0C5E, 0x0C5F), // TELUGU unassigned 10
				UnicodeRange(0x0C64, 0x0C65), // TELUGU unassigned 11
				UnicodeRange(0x0C70, 0x0C76), // TELUGU unassigned 12
				UnicodeRange(0x0C8D, 0x0C8D), // KANNADA unassigned 1
				UnicodeRange(0x0C91, 0x0C91), // KANNADA unassigned 2
				UnicodeRange(0x0CA9, 0x0CA9), // KANNADA unassigned 3
				UnicodeRange(0x0CB4, 0x0CB4), // KANNADA unassigned 4
				UnicodeRange(0x0CBA, 0x0CBB), // KANNADA unassigned 5
				UnicodeRange(0x0CC5, 0x0CC5), // KANNADA unassigned 6
				UnicodeRange(0x0CC9, 0x0CC9), // KANNADA unassigned 7
				UnicodeRange(0x0CCE, 0x0CD4), // KANNADA unassigned 8
				UnicodeRange(0x0CD7, 0x0CDC), // KANNADA unassigned 9
				UnicodeRange(0x0CDF, 0x0CDF), // KANNADA unassigned 10
				UnicodeRange(0x0CE4, 0x0CE5), // KANNADA unassigned 11
				UnicodeRange(0x0CF0, 0x0CF0), // KANNADA unassigned 12
				UnicodeRange(0x0CF4, 0x0CFF), // KANNADA unassigned 13
				UnicodeRange(0x0D0D, 0x0D0D), // MALAYALAM unassigned 1
				UnicodeRange(0x0D11, 0x0D11), // MALAYALAM unassigned 2
				UnicodeRange(0x0D45, 0x0D45), // MALAYALAM unassigned 3
				UnicodeRange(0x0D49, 0x0D49), // MALAYALAM unassigned 4
				UnicodeRange(0x0D50, 0x0D53), // MALAYALAM unassigned 5
				UnicodeRange(0x0D64, 0x0D65), // MALAYALAM unassigned 6
				UnicodeRange(0x0D80, 0x0D80), // MALAYALAM unassigned 7
				UnicodeRange(0x0D84, 0x0D84), // SINHALA unassigned 1
				UnicodeRange(0x0D97, 0x0D99), // SINHALA unassigned 2
				UnicodeRange(0x0DB2, 0x0DB2), // SINHALA unassigned 3
				UnicodeRange(0x0DBC, 0x0DBC), // SINHALA unassigned 4
				UnicodeRange(0x0DBE, 0x0DBF), // SINHALA unassigned 5
				UnicodeRange(0x0DC7, 0x0DC9), // SINHALA unassigned 6
				UnicodeRange(0x0DCB, 0x0DCE), // SINHALA unassigned 7
				UnicodeRange(0x0DD5, 0x0DD5), // SINHALA unassigned 8
				UnicodeRange(0x0DD7, 0x0DD7), // SINHALA unassigned 9
				UnicodeRange(0x0DE0, 0x0DE5), // SINHALA unassigned 10
				UnicodeRange(0x0DF0, 0x0DF1), // SINHALA unassigned 11
				UnicodeRange(0x0DF5, 0x0E00), // SINHALA unassigned 12
				UnicodeRange(0x0E3B, 0x0E3E), // THAI unassigned 1
				UnicodeRange(0x0E5C, 0x0E80), // THAI unassigned 2
				UnicodeRange(0x0E83, 0x0E83), // LAO unassigned 1
				UnicodeRange(0x0E85, 0x0E85), // LAO unassigned 2
				UnicodeRange(0x0E8B, 0x0E8B), // LAO unassigned 3
				UnicodeRange(0x0EA4, 0x0EA4), // LAO unassigned 4
				UnicodeRange(0x0EA6, 0x0EA6), // LAO unassigned 5
				UnicodeRange(0x0EBE, 0x0EBF), // LAO unassigned 6
				UnicodeRange(0x0EC5, 0x0EC5), // LAO unassigned 7
				UnicodeRange(0x0EC7, 0x0EC7), // LAO unassigned 8
				UnicodeRange(0x0ECF, 0x0ECF), // LAO unassigned 9
				UnicodeRange(0x0EDA, 0x0EDB), // LAO unassigned 10
				UnicodeRange(0x0EE0, 0x0EFF), // LAO unassigned 11
				UnicodeRange(0x0F48, 0x0F48), // TIBETAN unassigned 1
				UnicodeRange(0x0F6D, 0x0F70), // TIBETAN unassigned 2
				UnicodeRange(0x0F98, 0x0F98), // TIBETAN unassigned 3
				UnicodeRange(0x0FBD, 0x0FBD), // TIBETAN unassigned 4
				UnicodeRange(0x0FCD, 0x0FCD), // TIBETAN unassigned 5
				UnicodeRange(0x0FDB, 0x0FFF), // TIBETAN unassigned 6
				UnicodeRange(0x10C6, 0x10C6), // GEORGIAN unassigned 1
				UnicodeRange(0x10C8, 0x10CC), // GEORGIAN unassigned 2
				UnicodeRange(0x10CE, 0x10CF), // GEORGIAN unassigned 3
				UnicodeRange(0x1249, 0x1249), // ETHIOPIC unassigned 1
				UnicodeRange(0x124E, 0x124F), // ETHIOPIC unassigned 2
				UnicodeRange(0x1257, 0x1257), // ETHIOPIC unassigned 3
				UnicodeRange(0x1259, 0x1259), // ETHIOPIC unassigned 4
				UnicodeRange(0x125E, 0x125F), // ETHIOPIC unassigned 5
				UnicodeRange(0x1289, 0x1289), // ETHIOPIC unassigned 6
				UnicodeRange(0x128E, 0x128F), // ETHIOPIC unassigned 7
				UnicodeRange(0x12B1, 0x12B1), // ETHIOPIC unassigned 8
				UnicodeRange(0x12B6, 0x12B7), // ETHIOPIC unassigned 9
				UnicodeRange(0x12BF, 0x12BF), // ETHIOPIC unassigned 10
				UnicodeRange(0x12C1, 0x12C1), // ETHIOPIC unassigned 11
				UnicodeRange(0x12C6, 0x12C7), // ETHIOPIC unassigned 12
				UnicodeRange(0x12D7, 0x12D7), // ETHIOPIC unassigned 13
				UnicodeRange(0x1311, 0x1311), // ETHIOPIC unassigned 14
				UnicodeRange(0x1316, 0x1317), // ETHIOPIC unassigned 15
				UnicodeRange(0x135B, 0x135C), // ETHIOPIC unassigned 16
				UnicodeRange(0x137D, 0x137F), // ETHIOPIC unassigned 17
				UnicodeRange(0x139A, 0x139F), // ETHIOPIC unassigned 18
				UnicodeRange(0x13F6, 0x13F7), // CHEROKEE unassigned 1
				UnicodeRange(0x13FE, 0x13FF), // CHEROKEE unassigned 2
				UnicodeRange(0x169D, 0x169F), // OGHAM unassigned 1
				UnicodeRange(0x16F9, 0x16FF), // RUNIC unassigned 1
				UnicodeRange(0x1716, 0x171E), // TAGALOG unassigned 1
				UnicodeRange(0x1737, 0x173F), // PHILIPPINE unassigned 1
				UnicodeRange(0x1754, 0x175F), // BUHID unassigned 1
				UnicodeRange(0x176D, 0x176D), // TAGBANWA unassigned 1
				UnicodeRange(0x1771, 0x1771), // TAGBANWA unassigned 2
				UnicodeRange(0x1774, 0x177F), // TAGBANWA unassigned 3
				UnicodeRange(0x17DE, 0x17DF), // KHMER unassigned 1
				UnicodeRange(0x17EA, 0x17EF), // KHMER unassigned 2
				UnicodeRange(0x17FA, 0x17FF), // KHMER unassigned 3
				UnicodeRange(0x181A, 0x181F), // MONGOLIAN unassigned 1
				UnicodeRange(0x1879, 0x187F), // MONGOLIAN unassigned 2
				UnicodeRange(0x18AB, 0x18AF), // MONGOLIAN unassigned 3
				UnicodeRange(0x18F6, 0x18FF), // CANADIAN unassigned 1
				UnicodeRange(0x191F, 0x191F), // LIMBU unassigned 1
				UnicodeRange(0x192C, 0x192F), // LIMBU unassigned 2
				UnicodeRange(0x193C, 0x193F), // LIMBU unassigned 3
				UnicodeRange(0x1941, 0x1943), // LIMBU unassigned 4
				UnicodeRange(0x196E, 0x196F), // TAI unassigned 1
				UnicodeRange(0x1975, 0x197F), // TAI unassigned 2
				UnicodeRange(0x19AC, 0x19AF), // NEW unassigned 1
				UnicodeRange(0x19CA, 0x19CF), // NEW unassigned 2
				UnicodeRange(0x19DB, 0x19DD), // NEW unassigned 3
				UnicodeRange(0x1A1C, 0x1A1D), // BUGINESE unassigned 1
				UnicodeRange(0x1A5F, 0x1A5F), // TAI unassigned 1
				UnicodeRange(0x1A7D, 0x1A7E), // TAI unassigned 2
				UnicodeRange(0x1A8A, 0x1A8F), // TAI unassigned 3
				UnicodeRange(0x1A9A, 0x1A9F), // TAI unassigned 4
				UnicodeRange(0x1AAE, 0x1AAF), // TAI unassigned 5
				UnicodeRange(0x1ACF, 0x1AFF), // COMBINING unassigned 1
				UnicodeRange(0x1B4D, 0x1B4D), // BALINESE unassigned 1
				UnicodeRange(0x1BF4, 0x1BFB), // BATAK unassigned 1
				UnicodeRange(0x1C38, 0x1C3A), // LEPCHA unassigned 1
				UnicodeRange(0x1C4A, 0x1C4C), // LEPCHA unassigned 2
				UnicodeRange(0x1C8B, 0x1C8F), // CYRILLIC unassigned 1
				UnicodeRange(0x1CBB, 0x1CBC), // GEORGIAN unassigned 1
				UnicodeRange(0x1CC8, 0x1CCF), // SUNDANESE unassigned 1
				UnicodeRange(0x1CFB, 0x1CFF), // VEDIC unassigned 1
				UnicodeRange(0x1F16, 0x1F17), // GREEK unassigned 1
				UnicodeRange(0x1F1E, 0x1F1F), // GREEK unassigned 2
				UnicodeRange(0x1F46, 0x1F47), // GREEK unassigned 3
				UnicodeRange(0x1F4E, 0x1F4F), // GREEK unassigned 4
				UnicodeRange(0x1F58, 0x1F58), // GREEK unassigned 5
				UnicodeRange(0x1F5A, 0x1F5A), // GREEK unassigned 6
				UnicodeRange(0x1F5C, 0x1F5C), // GREEK unassigned 7
				UnicodeRange(0x1F5E, 0x1F5E), // GREEK unassigned 8
				UnicodeRange(0x1F7E, 0x1F7F), // GREEK unassigned 9
				UnicodeRange(0x1FB5, 0x1FB5), // GREEK unassigned 10
				UnicodeRange(0x1FC5, 0x1FC5), // GREEK unassigned 11
				UnicodeRange(0x1FD4, 0x1FD5), // GREEK unassigned 12
				UnicodeRange(0x1FDC, 0x1FDC), // GREEK unassigned 13
				UnicodeRange(0x1FF0, 0x1FF1), // GREEK unassigned 14
				UnicodeRange(0x1FF5, 0x1FF5), // GREEK unassigned 15
				UnicodeRange(0x1FFF, 0x1FFF), // GREEK unassigned 16
				UnicodeRange(0x2065, 0x2065), // INVISIBLE unassigned 1
				UnicodeRange(0x2072, 0x2073), // SUPERSCRIPT unassigned 1
				UnicodeRange(0x208F, 0x208F), // SUBSCRIPT unassigned 1
				UnicodeRange(0x209D, 0x209F), // LATIN unassigned 1
				UnicodeRange(0x20C1, 0x20CF), // SOM unassigned 1
				UnicodeRange(0x20F1, 0x20FF), // COMBINING unassigned 1
				UnicodeRange(0x218C, 0x218F), // TURNED unassigned 1
				UnicodeRange(0x242A, 0x243F), // SYMBOL unassigned 1
				UnicodeRange(0x244B, 0x245F), // OCR unassigned 1
				UnicodeRange(0x2B74, 0x2B75), // DOWNWARDS unassigned 1
				UnicodeRange(0x2B96, 0x2B96), // RIGHTWARDS unassigned 1
				UnicodeRange(0x2CF4, 0x2CF8), // COPTIC unassigned 1
				UnicodeRange(0x2D26, 0x2D26), // GEORGIAN unassigned 1
				UnicodeRange(0x2D28, 0x2D2C), // GEORGIAN unassigned 2
				UnicodeRange(0x2D2E, 0x2D2F), // GEORGIAN unassigned 3
				UnicodeRange(0x2D68, 0x2D6E), // TIFINAGH unassigned 1
				UnicodeRange(0x2D71, 0x2D7E), // TIFINAGH unassigned 2
				UnicodeRange(0x2D97, 0x2D9F), // ETHIOPIC unassigned 1
				UnicodeRange(0x2DA7, 0x2DA7), // ETHIOPIC unassigned 2
				UnicodeRange(0x2DAF, 0x2DAF), // ETHIOPIC unassigned 3
				UnicodeRange(0x2DB7, 0x2DB7), // ETHIOPIC unassigned 4
				UnicodeRange(0x2DBF, 0x2DBF), // ETHIOPIC unassigned 5
				UnicodeRange(0x2DC7, 0x2DC7), // ETHIOPIC unassigned 6
				UnicodeRange(0x2DCF, 0x2DCF), // ETHIOPIC unassigned 7
				UnicodeRange(0x2DD7, 0x2DD7), // ETHIOPIC unassigned 8
				UnicodeRange(0x2DDF, 0x2DDF), // ETHIOPIC unassigned 9
				UnicodeRange(0x2E5E, 0x2E7F), // OBLIQUE unassigned 1
				UnicodeRange(0x2E9A, 0x2E9A), // CJK unassigned 1
				UnicodeRange(0x2EF4, 0x2EFF), // CJK unassigned 2
				UnicodeRange(0x2FD6, 0x2FEF), // KANGXI unassigned 1
				UnicodeRange(0x3040, 0x3040), // IDEOGRAPHIC unassigned 1
				UnicodeRange(0x3097, 0x3098), // HIRAGANA unassigned 1
				UnicodeRange(0x3100, 0x3104), // KATAKANA unassigned 1
				UnicodeRange(0x3130, 0x3130), // BOPOMOFO unassigned 1
				UnicodeRange(0x318F, 0x318F), // HANGUL unassigned 1
				UnicodeRange(0x31E6, 0x31EE), // CJK unassigned 1
				UnicodeRange(0x321F, 0x321F), // PARENTHESIZED unassigned 1
				UnicodeRange(0xA48D, 0xA48F), // YI unassigned 1
				UnicodeRange(0xA4C7, 0xA4CF), // YI unassigned 2
				UnicodeRange(0xA62C, 0xA63F), // VAI unassigned 1
				UnicodeRange(0xA6F8, 0xA6FF), // BAMUM unassigned 1
				UnicodeRange(0xA7CE, 0xA7CF), // LATIN unassigned 1
				UnicodeRange(0xA7D2, 0xA7D2), // LATIN unassigned 2
				UnicodeRange(0xA7D4, 0xA7D4), // LATIN unassigned 3
				UnicodeRange(0xA7DD, 0xA7F1), // LATIN unassigned 4
				UnicodeRange(0xA82D, 0xA82F), // SYLOTI unassigned 1
				UnicodeRange(0xA83A, 0xA83F), // NORTH unassigned 1
				UnicodeRange(0xA878, 0xA87F), // PHAGS-PA unassigned 1
				UnicodeRange(0xA8C6, 0xA8CD), // SAURASHTRA unassigned 1
				UnicodeRange(0xA8DA, 0xA8DF), // SAURASHTRA unassigned 2
				UnicodeRange(0xA954, 0xA95E), // REJANG unassigned 1
				UnicodeRange(0xA97D, 0xA97F), // HANGUL unassigned 1
				UnicodeRange(0xA9CE, 0xA9CE), // JAVANESE unassigned 1
				UnicodeRange(0xA9DA, 0xA9DD), // JAVANESE unassigned 2
				UnicodeRange(0xA9FF, 0xA9FF), // MYANMAR unassigned 1
				UnicodeRange(0xAA37, 0xAA3F), // CHAM unassigned 1
				UnicodeRange(0xAA4E, 0xAA4F), // CHAM unassigned 2
				UnicodeRange(0xAA5A, 0xAA5B), // CHAM unassigned 3
				UnicodeRange(0xAAC3, 0xAADA), // TAI unassigned 1
				UnicodeRange(0xAAF7, 0xAB00), // MEETEI unassigned 1
				UnicodeRange(0xAB07, 0xAB08), // ETHIOPIC unassigned 1
				UnicodeRange(0xAB0F, 0xAB10), // ETHIOPIC unassigned 2
				UnicodeRange(0xAB17, 0xAB1F), // ETHIOPIC unassigned 3
				UnicodeRange(0xAB27, 0xAB27), // ETHIOPIC unassigned 4
				UnicodeRange(0xAB2F, 0xAB2F), // ETHIOPIC unassigned 5
				UnicodeRange(0xAB6C, 0xAB6F), // MODIFIER unassigned 1
				UnicodeRange(0xABEE, 0xABEF), // MEETEI unassigned 1
				UnicodeRange(0xABFA, 0xABFF), // MEETEI unassigned 2
				UnicodeRange(0xD7A4, 0xD7AF), // Hangul Syllable unassigned 1
				UnicodeRange(0xD7C7, 0xD7CA), // HANGUL unassigned 1
				UnicodeRange(0xD7FC, 0xD7FF), // HANGUL unassigned 2
				UnicodeRange(0xFA6E, 0xFA6F), // CJK unassigned 1
				UnicodeRange(0xFADA, 0xFAFF), // CJK unassigned 2
				UnicodeRange(0xFB07, 0xFB12), // LATIN unassigned 1
				UnicodeRange(0xFB18, 0xFB1C), // ARMENIAN unassigned 1
				UnicodeRange(0xFB37, 0xFB37), // HEBREW unassigned 1
				UnicodeRange(0xFB3D, 0xFB3D), // HEBREW unassigned 2
				UnicodeRange(0xFB3F, 0xFB3F), // HEBREW unassigned 3
				UnicodeRange(0xFB42, 0xFB42), // HEBREW unassigned 4
				UnicodeRange(0xFB45, 0xFB45), // HEBREW unassigned 5
				UnicodeRange(0xFBC3, 0xFBD2), // ARABIC unassigned 1
				UnicodeRange(0xFD90, 0xFD91), // ARABIC unassigned 2
				UnicodeRange(0xFDC8, 0xFDCE), // ARABIC unassigned 3
				UnicodeRange(0xFDD0, 0xFDEF), // ARABIC unassigned 4
				UnicodeRange(0xFE1A, 0xFE1F), // PRESENTATION unassigned 1
				UnicodeRange(0xFE53, 0xFE53), // SMALL unassigned 1
				UnicodeRange(0xFE67, 0xFE67), // SMALL unassigned 2
				UnicodeRange(0xFE6C, 0xFE6F), // SMALL unassigned 3
				UnicodeRange(0xFE75, 0xFE75), // ARABIC unassigned 1
				UnicodeRange(0xFEFD, 0xFEFE), // ARABIC unassigned 2
				UnicodeRange(0xFF00, 0xFF00), // ZERO unassigned 1
				UnicodeRange(0xFFBF, 0xFFC1), // HALFWIDTH unassigned 1
				UnicodeRange(0xFFC8, 0xFFC9), // HALFWIDTH unassigned 2
				UnicodeRange(0xFFD0, 0xFFD1), // HALFWIDTH unassigned 3
				UnicodeRange(0xFFD8, 0xFFD9), // HALFWIDTH unassigned 4
				UnicodeRange(0xFFDD, 0xFFDF), // HALFWIDTH unassigned 5
				UnicodeRange(0xFFE7, 0xFFE7), // FULLWIDTH unassigned 1
				UnicodeRange(0xFFEF, 0xFFF8), // HALFWIDTH unassigned 1
				UnicodeRange(0xFFFE, 0xFFFF), // REPLACEMENT unassigned 1
				UnicodeRange(0x1000C, 0x1000C), // LINEAR unassigned 1
				UnicodeRange(0x10027, 0x10027), // LINEAR unassigned 2
				UnicodeRange(0x1003B, 0x1003B), // LINEAR unassigned 3
				UnicodeRange(0x1003E, 0x1003E), // LINEAR unassigned 4
				UnicodeRange(0x1004E, 0x1004F), // LINEAR unassigned 5
				UnicodeRange(0x1005E, 0x1007F), // LINEAR unassigned 6
				UnicodeRange(0x100FB, 0x100FF), // LINEAR unassigned 7
				UnicodeRange(0x10103, 0x10106), // AEGEAN unassigned 1
				UnicodeRange(0x10134, 0x10136), // AEGEAN unassigned 2
				UnicodeRange(0x1018F, 0x1018F), // NOMISMA unassigned 1
				UnicodeRange(0x1019D, 0x1019F), // ASCIA unassigned 1
				UnicodeRange(0x101A1, 0x101CF), // GREEK unassigned 1
				UnicodeRange(0x101FE, 0x1027F), // PHAISTOS unassigned 1
				UnicodeRange(0x1029D, 0x1029F), // LYCIAN unassigned 1
				UnicodeRange(0x102D1, 0x102DF), // CARIAN unassigned 1
				UnicodeRange(0x102FC, 0x102FF), // COPTIC unassigned 1
				UnicodeRange(0x10324, 0x1032C), // OLD unassigned 1
				UnicodeRange(0x1034B, 0x1034F), // GOTHIC unassigned 1
				UnicodeRange(0x1037B, 0x1037F), // COMBINING unassigned 1
				UnicodeRange(0x1039E, 0x1039E), // UGARITIC unassigned 1
				UnicodeRange(0x103C4, 0x103C7), // OLD unassigned 1
				UnicodeRange(0x103D6, 0x103FF), // OLD unassigned 2
				UnicodeRange(0x1049E, 0x1049F), // OSMANYA unassigned 1
				UnicodeRange(0x104AA, 0x104AF), // OSMANYA unassigned 2
				UnicodeRange(0x104D4, 0x104D7), // OSAGE unassigned 1
				UnicodeRange(0x104FC, 0x104FF), // OSAGE unassigned 2
				UnicodeRange(0x10528, 0x1052F), // ELBASAN unassigned 1
				UnicodeRange(0x10564, 0x1056E), // CAUCASIAN unassigned 1
				UnicodeRange(0x1057B, 0x1057B), // VITHKUQI unassigned 1
				UnicodeRange(0x1058B, 0x1058B), // VITHKUQI unassigned 2
				UnicodeRange(0x10593, 0x10593), // VITHKUQI unassigned 3
				UnicodeRange(0x10596, 0x10596), // VITHKUQI unassigned 4
				UnicodeRange(0x105A2, 0x105A2), // VITHKUQI unassigned 5
				UnicodeRange(0x105B2, 0x105B2), // VITHKUQI unassigned 6
				UnicodeRange(0x105BA, 0x105BA), // VITHKUQI unassigned 7
				UnicodeRange(0x105BD, 0x105BF), // VITHKUQI unassigned 8
				UnicodeRange(0x105F4, 0x105FF), // TODHRI unassigned 1
				UnicodeRange(0x10737, 0x1073F), // LINEAR unassigned 1
				UnicodeRange(0x10756, 0x1075F), // LINEAR unassigned 2
				UnicodeRange(0x10768, 0x1077F), // LINEAR unassigned 3
				UnicodeRange(0x10786, 0x10786), // MODIFIER unassigned 1
				UnicodeRange(0x107B1, 0x107B1), // MODIFIER unassigned 2
				UnicodeRange(0x107BB, 0x107FF), // MODIFIER unassigned 3
				UnicodeRange(0x10806, 0x10807), // CYPRIOT unassigned 1
				UnicodeRange(0x10809, 0x10809), // CYPRIOT unassigned 2
				UnicodeRange(0x10836, 0x10836), // CYPRIOT unassigned 3
				UnicodeRange(0x10839, 0x1083B), // CYPRIOT unassigned 4
				UnicodeRange(0x1083D, 0x1083E), // CYPRIOT unassigned 5
				UnicodeRange(0x10856, 0x10856), // IMPERIAL unassigned 1
				UnicodeRange(0x1089F, 0x108A6), // NABATAEAN unassigned 1
				UnicodeRange(0x108B0, 0x108DF), // NABATAEAN unassigned 2
				UnicodeRange(0x108F3, 0x108F3), // HATRAN unassigned 1
				UnicodeRange(0x108F6, 0x108FA), // HATRAN unassigned 2
				UnicodeRange(0x1091C, 0x1091E), // PHOENICIAN unassigned 1
				UnicodeRange(0x1093A, 0x1093E), // LYDIAN unassigned 1
				UnicodeRange(0x10940, 0x1097F), // LYDIAN unassigned 2
				UnicodeRange(0x109B8, 0x109BB), // MEROITIC unassigned 1
				UnicodeRange(0x109D0, 0x109D1), // MEROITIC unassigned 2
				UnicodeRange(0x10A04, 0x10A04), // KHAROSHTHI unassigned 1
				UnicodeRange(0x10A07, 0x10A0B), // KHAROSHTHI unassigned 2
				UnicodeRange(0x10A14, 0x10A14), // KHAROSHTHI unassigned 3
				UnicodeRange(0x10A18, 0x10A18), // KHAROSHTHI unassigned 4
				UnicodeRange(0x10A36, 0x10A37), // KHAROSHTHI unassigned 5
				UnicodeRange(0x10A3B, 0x10A3E), // KHAROSHTHI unassigned 6
				UnicodeRange(0x10A49, 0x10A4F), // KHAROSHTHI unassigned 7
				UnicodeRange(0x10A59, 0x10A5F), // KHAROSHTHI unassigned 8
				UnicodeRange(0x10AA0, 0x10ABF), // OLD unassigned 1
				UnicodeRange(0x10AE7, 0x10AEA), // MANICHAEAN unassigned 1
				UnicodeRange(0x10AF7, 0x10AFF), // MANICHAEAN unassigned 2
				UnicodeRange(0x10B36, 0x10B38), // AVESTAN unassigned 1
				UnicodeRange(0x10B56, 0x10B57), // INSCRIPTIONAL unassigned 1
				UnicodeRange(0x10B73, 0x10B77), // INSCRIPTIONAL unassigned 2
				UnicodeRange(0x10B92, 0x10B98), // PSALTER unassigned 1
				UnicodeRange(0x10B9D, 0x10BA8), // PSALTER unassigned 2
				UnicodeRange(0x10BB0, 0x10BFF), // PSALTER unassigned 3
				UnicodeRange(0x10C49, 0x10C7F), // OLD unassigned 1
				UnicodeRange(0x10CB3, 0x10CBF), // OLD unassigned 2
				UnicodeRange(0x10CF3, 0x10CF9), // OLD unassigned 3
				UnicodeRange(0x10D28, 0x10D2F), // HANIFI unassigned 1
				UnicodeRange(0x10D3A, 0x10D3F), // HANIFI unassigned 2
				UnicodeRange(0x10D66, 0x10D68), // GARAY unassigned 1
				UnicodeRange(0x10D86, 0x10D8D), // GARAY unassigned 2
				UnicodeRange(0x10D90, 0x10E5F), // GARAY unassigned 3
				UnicodeRange(0x10E7F, 0x10E7F), // RUMI unassigned 1
				UnicodeRange(0x10EAA, 0x10EAA), // YEZIDI unassigned 1
				UnicodeRange(0x10EAE, 0x10EAF), // YEZIDI unassigned 2
				UnicodeRange(0x10EB2, 0x10EC1), // YEZIDI unassigned 3
				UnicodeRange(0x10EC5, 0x10EFB), // ARABIC unassigned 1
				UnicodeRange(0x10F28, 0x10F2F), // OLD unassigned 1
				UnicodeRange(0x10F5A, 0x10F6F), // SOGDIAN unassigned 1
				UnicodeRange(0x10F8A, 0x10FAF), // OLD unassigned 1
				UnicodeRange(0x10FCC, 0x10FDF), // CHORASMIAN unassigned 1
				UnicodeRange(0x10FF7, 0x10FFF), // ELYMAIC unassigned 1
				UnicodeRange(0x1104E, 0x11051), // BRAHMI unassigned 1
				UnicodeRange(0x11076, 0x1107E), // BRAHMI unassigned 2
				UnicodeRange(0x110C3, 0x110CC), // KAITHI unassigned 1
				UnicodeRange(0x110CE, 0x110CF), // KAITHI unassigned 2
				UnicodeRange(0x110E9, 0x110EF), // SORA unassigned 1
				UnicodeRange(0x110FA, 0x110FF), // SORA unassigned 2
				UnicodeRange(0x11135, 0x11135), // CHAKMA unassigned 1
				UnicodeRange(0x11148, 0x1114F), // CHAKMA unassigned 2
				UnicodeRange(0x11177, 0x1117F), // MAHAJANI unassigned 1
				UnicodeRange(0x111E0, 0x111E0), // SHARADA unassigned 1
				UnicodeRange(0x111F5, 0x111FF), // SINHALA unassigned 1
				UnicodeRange(0x11212, 0x11212), // KHOJKI unassigned 1
				UnicodeRange(0x11242, 0x1127F), // KHOJKI unassigned 2
				UnicodeRange(0x11287, 0x11287), // MULTANI unassigned 1
				UnicodeRange(0x11289, 0x11289), // MULTANI unassigned 2
				UnicodeRange(0x1128E, 0x1128E), // MULTANI unassigned 3
				UnicodeRange(0x1129E, 0x1129E), // MULTANI unassigned 4
				UnicodeRange(0x112AA, 0x112AF), // MULTANI unassigned 5
				UnicodeRange(0x112EB, 0x112EF), // KHUDAWADI unassigned 1
				UnicodeRange(0x112FA, 0x112FF), // KHUDAWADI unassigned 2
				UnicodeRange(0x11304, 0x11304), // GRANTHA unassigned 1
				UnicodeRange(0x1130D, 0x1130E), // GRANTHA unassigned 2
				UnicodeRange(0x11311, 0x11312), // GRANTHA unassigned 3
				UnicodeRange(0x11329, 0x11329), // GRANTHA unassigned 4
				UnicodeRange(0x11331, 0x11331), // GRANTHA unassigned 5
				UnicodeRange(0x11334, 0x11334), // GRANTHA unassigned 6
				UnicodeRange(0x1133A, 0x1133A), // GRANTHA unassigned 7
				UnicodeRange(0x11345, 0x11346), // GRANTHA unassigned 8
				UnicodeRange(0x11349, 0x1134A), // GRANTHA unassigned 9
				UnicodeRange(0x1134E, 0x1134F), // GRANTHA unassigned 10
				UnicodeRange(0x11351, 0x11356), // GRANTHA unassigned 11
				UnicodeRange(0x11358, 0x1135C), // GRANTHA unassigned 12
				UnicodeRange(0x11364, 0x11365), // GRANTHA unassigned 13
				UnicodeRange(0x1136D, 0x1136F), // COMBINING unassigned 1
				UnicodeRange(0x11375, 0x1137F), // COMBINING unassigned 2
				UnicodeRange(0x1138A, 0x1138A), // TULU-TIGALARI unassigned 1
				UnicodeRange(0x1138C, 0x1138D), // TULU-TIGALARI unassigned 2
				UnicodeRange(0x1138F, 0x1138F), // TULU-TIGALARI unassigned 3
				UnicodeRange(0x113B6, 0x113B6), // TULU-TIGALARI unassigned 4
				UnicodeRange(0x113C1, 0x113C1), // TULU-TIGALARI unassigned 5
				UnicodeRange(0x113C3, 0x113C4), // TULU-TIGALARI unassigned 6
				UnicodeRange(0x113C6, 0x113C6), // TULU-TIGALARI unassigned 7
				UnicodeRange(0x113CB, 0x113CB), // TULU-TIGALARI unassigned 8
				UnicodeRange(0x113D6, 0x113D6), // TULU-TIGALARI unassigned 9
				UnicodeRange(0x113D9, 0x113E0), // TULU-TIGALARI unassigned 10
				UnicodeRange(0x113E3, 0x113FF), // TULU-TIGALARI unassigned 11
				UnicodeRange(0x1145C, 0x1145C), // NEWA unassigned 1
				UnicodeRange(0x11462, 0x1147F), // NEWA unassigned 2
				UnicodeRange(0x114C8, 0x114CF), // TIRHUTA unassigned 1
				UnicodeRange(0x114DA, 0x1157F), // TIRHUTA unassigned 2
				UnicodeRange(0x115B6, 0x115B7), // SIDDHAM unassigned 1
				UnicodeRange(0x115DE, 0x115FF), // SIDDHAM unassigned 2
				UnicodeRange(0x11645, 0x1164F), // MODI unassigned 1
				UnicodeRange(0x1165A, 0x1165F), // MODI unassigned 2
				UnicodeRange(0x1166D, 0x1167F), // MONGOLIAN unassigned 1
				UnicodeRange(0x116BA, 0x116BF), // TAKRI unassigned 1
				UnicodeRange(0x116CA, 0x116CF), // TAKRI unassigned 2
				UnicodeRange(0x116E4, 0x116FF), // MYANMAR unassigned 1
				UnicodeRange(0x1171B, 0x1171C), // AHOM unassigned 1
				UnicodeRange(0x1172C, 0x1172F), // AHOM unassigned 2
				UnicodeRange(0x11747, 0x117FF), // AHOM unassigned 3
				UnicodeRange(0x1183C, 0x1189F), // DOGRA unassigned 1
				UnicodeRange(0x118F3, 0x118FE), // WARANG unassigned 1
				UnicodeRange(0x11907, 0x11908), // DIVES unassigned 1
				UnicodeRange(0x1190A, 0x1190B), // DIVES unassigned 2
				UnicodeRange(0x11914, 0x11914), // DIVES unassigned 3
				UnicodeRange(0x11917, 0x11917), // DIVES unassigned 4
				UnicodeRange(0x11936, 0x11936), // DIVES unassigned 5
				UnicodeRange(0x11939, 0x1193A), // DIVES unassigned 6
				UnicodeRange(0x11947, 0x1194F), // DIVES unassigned 7
				UnicodeRange(0x1195A, 0x1199F), // DIVES unassigned 8
				UnicodeRange(0x119A8, 0x119A9), // NANDINAGARI unassigned 1
				UnicodeRange(0x119D8, 0x119D9), // NANDINAGARI unassigned 2
				UnicodeRange(0x119E5, 0x119FF), // NANDINAGARI unassigned 3
				UnicodeRange(0x11A48, 0x11A4F), // ZANABAZAR unassigned 1
				UnicodeRange(0x11AA3, 0x11AAF), // SOYOMBO unassigned 1
				UnicodeRange(0x11AF9, 0x11AFF), // PAU unassigned 1
				UnicodeRange(0x11B0A, 0x11BBF), // DEVANAGARI unassigned 1
				UnicodeRange(0x11BE2, 0x11BEF), // SUNUWAR unassigned 1
				UnicodeRange(0x11BFA, 0x11BFF), // SUNUWAR unassigned 2
				UnicodeRange(0x11C09, 0x11C09), // BHAIKSUKI unassigned 1
				UnicodeRange(0x11C37, 0x11C37), // BHAIKSUKI unassigned 2
				UnicodeRange(0x11C46, 0x11C4F), // BHAIKSUKI unassigned 3
				UnicodeRange(0x11C6D, 0x11C6F), // BHAIKSUKI unassigned 4
				UnicodeRange(0x11C90, 0x11C91), // MARCHEN unassigned 1
				UnicodeRange(0x11CA8, 0x11CA8), // MARCHEN unassigned 2
				UnicodeRange(0x11CB7, 0x11CFF), // MARCHEN unassigned 3
				UnicodeRange(0x11D07, 0x11D07), // MASARAM unassigned 1
				UnicodeRange(0x11D0A, 0x11D0A), // MASARAM unassigned 2
				UnicodeRange(0x11D37, 0x11D39), // MASARAM unassigned 3
				UnicodeRange(0x11D3B, 0x11D3B), // MASARAM unassigned 4
				UnicodeRange(0x11D3E, 0x11D3E), // MASARAM unassigned 5
				UnicodeRange(0x11D48, 0x11D4F), // MASARAM unassigned 6
				UnicodeRange(0x11D5A, 0x11D5F), // MASARAM unassigned 7
				UnicodeRange(0x11D66, 0x11D66), // GUNJALA unassigned 1
				UnicodeRange(0x11D69, 0x11D69), // GUNJALA unassigned 2
				UnicodeRange(0x11D8F, 0x11D8F), // GUNJALA unassigned 3
				UnicodeRange(0x11D92, 0x11D92), // GUNJALA unassigned 4
				UnicodeRange(0x11D99, 0x11D9F), // GUNJALA unassigned 5
				UnicodeRange(0x11DAA, 0x11EDF), // GUNJALA unassigned 6
				UnicodeRange(0x11EF9, 0x11EFF), // MAKASAR unassigned 1
				UnicodeRange(0x11F11, 0x11F11), // KAWI unassigned 1
				UnicodeRange(0x11F3B, 0x11F3D), // KAWI unassigned 2
				UnicodeRange(0x11F5B, 0x11FAF), // KAWI unassigned 3
				UnicodeRange(0x11FB1, 0x11FBF), // LISU unassigned 1
				UnicodeRange(0x11FF2, 0x11FFE), // TAMIL unassigned 1
				UnicodeRange(0x1239A, 0x123FF), // CUNEIFORM unassigned 1
				UnicodeRange(0x1246F, 0x1246F), // CUNEIFORM unassigned 2
				UnicodeRange(0x12475, 0x1247F), // CUNEIFORM unassigned 3
				UnicodeRange(0x12544, 0x12F8F), // CUNEIFORM unassigned 4
				UnicodeRange(0x12FF3, 0x12FFF), // CYPRO-MINOAN unassigned 1
				UnicodeRange(0x13456, 0x1345F), // EGYPTIAN unassigned 1
				UnicodeRange(0x143FB, 0x143FF), // EGYPTIAN unassigned 2
				UnicodeRange(0x14647, 0x160FF), // ANATOLIAN unassigned 1
				UnicodeRange(0x1613A, 0x167FF), // GURUNG unassigned 1
				UnicodeRange(0x16A39, 0x16A3F), // BAMUM unassigned 1
				UnicodeRange(0x16A5F, 0x16A5F), // MRO unassigned 1
				UnicodeRange(0x16A6A, 0x16A6D), // MRO unassigned 2
				UnicodeRange(0x16ABF, 0x16ABF), // TANGSA unassigned 1
				UnicodeRange(0x16ACA, 0x16ACF), // TANGSA unassigned 2
				UnicodeRange(0x16AEE, 0x16AEF), // BASSA unassigned 1
				UnicodeRange(0x16AF6, 0x16AFF), // BASSA unassigned 2
				UnicodeRange(0x16B46, 0x16B4F), // PAHAWH unassigned 1
				UnicodeRange(0x16B5A, 0x16B5A), // PAHAWH unassigned 2
				UnicodeRange(0x16B62, 0x16B62), // PAHAWH unassigned 3
				UnicodeRange(0x16B78, 0x16B7C), // PAHAWH unassigned 4
				UnicodeRange(0x16B90, 0x16D3F), // PAHAWH unassigned 5
				UnicodeRange(0x16D7A, 0x16E3F), // KIRAT unassigned 1
				UnicodeRange(0x16E9B, 0x16EFF), // MEDEFAIDRIN unassigned 1
				UnicodeRange(0x16F4B, 0x16F4E), // MIAO unassigned 1
				UnicodeRange(0x16F88, 0x16F8E), // MIAO unassigned 2
				UnicodeRange(0x16FA0, 0x16FDF), // MIAO unassigned 3
				UnicodeRange(0x16FE5, 0x16FEF), // KHITAN unassigned 1
				UnicodeRange(0x16FF2, 0x16FFF), // VIETNAMESE unassigned 1
				UnicodeRange(0x187F8, 0x187FF), // Tangut Ideograph unassigned 1
				UnicodeRange(0x18CD6, 0x18CFE), // KHITAN unassigned 1
				UnicodeRange(0x18D09, 0x1AFEF), // Tangut Ideograph Supplement unassigned 1
				UnicodeRange(0x1AFF4, 0x1AFF4), // KATAKANA unassigned 1
				UnicodeRange(0x1AFFC, 0x1AFFC), // KATAKANA unassigned 2
				UnicodeRange(0x1AFFF, 0x1AFFF), // KATAKANA unassigned 3
				UnicodeRange(0x1B123, 0x1B131), // KATAKANA unassigned 4
				UnicodeRange(0x1B133, 0x1B14F), // HIRAGANA unassigned 1
				UnicodeRange(0x1B153, 0x1B154), // HIRAGANA unassigned 2
				UnicodeRange(0x1B156, 0x1B163), // KATAKANA unassigned 1
				UnicodeRange(0x1B168, 0x1B16F), // KATAKANA unassigned 2
				UnicodeRange(0x1B2FC, 0x1BBFF), // NUSHU unassigned 1
				UnicodeRange(0x1BC6B, 0x1BC6F), // DUPLOYAN unassigned 1
				UnicodeRange(0x1BC7D, 0x1BC7F), // DUPLOYAN unassigned 2
				UnicodeRange(0x1BC89, 0x1BC8F), // DUPLOYAN unassigned 3
				UnicodeRange(0x1BC9A, 0x1BC9B), // DUPLOYAN unassigned 4
				UnicodeRange(0x1BCA4, 0x1CBFF), // SHORTHAND unassigned 1
				UnicodeRange(0x1CCFA, 0x1CCFF), // OUTLINED unassigned 1
				UnicodeRange(0x1CEB4, 0x1CEFF), // BLACK unassigned 1
				UnicodeRange(0x1CF2E, 0x1CF2F), // ZNAMENNY unassigned 1
				UnicodeRange(0x1CF47, 0x1CF4F), // ZNAMENNY unassigned 2
				UnicodeRange(0x1CFC4, 0x1CFFF), // ZNAMENNY unassigned 3
				UnicodeRange(0x1D0F6, 0x1D0FF), // BYZANTINE unassigned 1
				UnicodeRange(0x1D127, 0x1D128), // MUSICAL unassigned 1
				UnicodeRange(0x1D1EB, 0x1D1FF), // MUSICAL unassigned 2
				UnicodeRange(0x1D246, 0x1D2BF), // GREEK unassigned 1
				UnicodeRange(0x1D2D4, 0x1D2DF), // KAKTOVIK unassigned 1
				UnicodeRange(0x1D2F4, 0x1D2FF), // MAYAN unassigned 1
				UnicodeRange(0x1D357, 0x1D35F), // TETRAGRAM unassigned 1
				UnicodeRange(0x1D379, 0x1D3FF), // TALLY unassigned 1
				UnicodeRange(0x1D455, 0x1D455), // MATHEMATICAL unassigned 1
				UnicodeRange(0x1D49D, 0x1D49D), // MATHEMATICAL unassigned 2
				UnicodeRange(0x1D4A0, 0x1D4A1), // MATHEMATICAL unassigned 3
				UnicodeRange(0x1D4A3, 0x1D4A4), // MATHEMATICAL unassigned 4
				UnicodeRange(0x1D4A7, 0x1D4A8), // MATHEMATICAL unassigned 5
				UnicodeRange(0x1D4AD, 0x1D4AD), // MATHEMATICAL unassigned 6
				UnicodeRange(0x1D4BA, 0x1D4BA), // MATHEMATICAL unassigned 7
				UnicodeRange(0x1D4BC, 0x1D4BC), // MATHEMATICAL unassigned 8
				UnicodeRange(0x1D4C4, 0x1D4C4), // MATHEMATICAL unassigned 9
				UnicodeRange(0x1D506, 0x1D506), // MATHEMATICAL unassigned 10
				UnicodeRange(0x1D50B, 0x1D50C), // MATHEMATICAL unassigned 11
				UnicodeRange(0x1D515, 0x1D515), // MATHEMATICAL unassigned 12
				UnicodeRange(0x1D51D, 0x1D51D), // MATHEMATICAL unassigned 13
				UnicodeRange(0x1D53A, 0x1D53A), // MATHEMATICAL unassigned 14
				UnicodeRange(0x1D53F, 0x1D53F), // MATHEMATICAL unassigned 15
				UnicodeRange(0x1D545, 0x1D545), // MATHEMATICAL unassigned 16
				UnicodeRange(0x1D547, 0x1D549), // MATHEMATICAL unassigned 17
				UnicodeRange(0x1D551, 0x1D551), // MATHEMATICAL unassigned 18
				UnicodeRange(0x1D6A6, 0x1D6A7), // MATHEMATICAL unassigned 19
				UnicodeRange(0x1D7CC, 0x1D7CD), // MATHEMATICAL unassigned 20
				UnicodeRange(0x1DA8C, 0x1DA9A), // SIGNWRITING unassigned 1
				UnicodeRange(0x1DAA0, 0x1DAA0), // SIGNWRITING unassigned 2
				UnicodeRange(0x1DAB0, 0x1DEFF), // SIGNWRITING unassigned 3
				UnicodeRange(0x1DF1F, 0x1DF24), // LATIN unassigned 1
				UnicodeRange(0x1DF2B, 0x1DFFF), // LATIN unassigned 2
				UnicodeRange(0x1E007, 0x1E007), // COMBINING unassigned 1
				UnicodeRange(0x1E019, 0x1E01A), // COMBINING unassigned 2
				UnicodeRange(0x1E022, 0x1E022), // COMBINING unassigned 3
				UnicodeRange(0x1E025, 0x1E025), // COMBINING unassigned 4
				UnicodeRange(0x1E02B, 0x1E02F), // COMBINING unassigned 5
				UnicodeRange(0x1E06E, 0x1E08E), // MODIFIER unassigned 1
				UnicodeRange(0x1E090, 0x1E0FF), // COMBINING unassigned 1
				UnicodeRange(0x1E12D, 0x1E12F), // NYIAKENG unassigned 1
				UnicodeRange(0x1E13E, 0x1E13F), // NYIAKENG unassigned 2
				UnicodeRange(0x1E14A, 0x1E14D), // NYIAKENG unassigned 3
				UnicodeRange(0x1E150, 0x1E28F), // NYIAKENG unassigned 4
				UnicodeRange(0x1E2AF, 0x1E2BF), // TOTO unassigned 1
				UnicodeRange(0x1E2FA, 0x1E2FE), // WANCHO unassigned 1
				UnicodeRange(0x1E300, 0x1E4CF), // WANCHO unassigned 2
				UnicodeRange(0x1E4FA, 0x1E5CF), // NAG unassigned 1
				UnicodeRange(0x1E5FB, 0x1E5FE), // OL unassigned 1
				UnicodeRange(0x1E600, 0x1E7DF), // OL unassigned 2
				UnicodeRange(0x1E7E7, 0x1E7E7), // ETHIOPIC unassigned 1
				UnicodeRange(0x1E7EC, 0x1E7EC), // ETHIOPIC unassigned 2
				UnicodeRange(0x1E7EF, 0x1E7EF), // ETHIOPIC unassigned 3
				UnicodeRange(0x1E7FF, 0x1E7FF), // ETHIOPIC unassigned 4
				UnicodeRange(0x1E8C5, 0x1E8C6), // MENDE unassigned 1
				UnicodeRange(0x1E8D7, 0x1E8FF), // MENDE unassigned 2
				UnicodeRange(0x1E94C, 0x1E94F), // ADLAM unassigned 1
				UnicodeRange(0x1E95A, 0x1E95D), // ADLAM unassigned 2
				UnicodeRange(0x1E960, 0x1EC70), // ADLAM unassigned 3
				UnicodeRange(0x1ECB5, 0x1ED00), // INDIC unassigned 1
				UnicodeRange(0x1ED3E, 0x1EDFF), // OTTOMAN unassigned 1
				UnicodeRange(0x1EE04, 0x1EE04), // ARABIC unassigned 1
				UnicodeRange(0x1EE20, 0x1EE20), // ARABIC unassigned 2
				UnicodeRange(0x1EE23, 0x1EE23), // ARABIC unassigned 3
				UnicodeRange(0x1EE25, 0x1EE26), // ARABIC unassigned 4
				UnicodeRange(0x1EE28, 0x1EE28), // ARABIC unassigned 5
				UnicodeRange(0x1EE33, 0x1EE33), // ARABIC unassigned 6
				UnicodeRange(0x1EE38, 0x1EE38), // ARABIC unassigned 7
				UnicodeRange(0x1EE3A, 0x1EE3A), // ARABIC unassigned 8
				UnicodeRange(0x1EE3C, 0x1EE41), // ARABIC unassigned 9
				UnicodeRange(0x1EE43, 0x1EE46), // ARABIC unassigned 10
				UnicodeRange(0x1EE48, 0x1EE48), // ARABIC unassigned 11
				UnicodeRange(0x1EE4A, 0x1EE4A), // ARABIC unassigned 12
				UnicodeRange(0x1EE4C, 0x1EE4C), // ARABIC unassigned 13
				UnicodeRange(0x1EE50, 0x1EE50), // ARABIC unassigned 14
				UnicodeRange(0x1EE53, 0x1EE53), // ARABIC unassigned 15
				UnicodeRange(0x1EE55, 0x1EE56), // ARABIC unassigned 16
				UnicodeRange(0x1EE58, 0x1EE58), // ARABIC unassigned 17
				UnicodeRange(0x1EE5A, 0x1EE5A), // ARABIC unassigned 18
				UnicodeRange(0x1EE5C, 0x1EE5C), // ARABIC unassigned 19
				UnicodeRange(0x1EE5E, 0x1EE5E), // ARABIC unassigned 20
				UnicodeRange(0x1EE60, 0x1EE60), // ARABIC unassigned 21
				UnicodeRange(0x1EE63, 0x1EE63), // ARABIC unassigned 22
				UnicodeRange(0x1EE65, 0x1EE66), // ARABIC unassigned 23
				UnicodeRange(0x1EE6B, 0x1EE6B), // ARABIC unassigned 24
				UnicodeRange(0x1EE73, 0x1EE73), // ARABIC unassigned 25
				UnicodeRange(0x1EE78, 0x1EE78), // ARABIC unassigned 26
				UnicodeRange(0x1EE7D, 0x1EE7D), // ARABIC unassigned 27
				UnicodeRange(0x1EE7F, 0x1EE7F), // ARABIC unassigned 28
				UnicodeRange(0x1EE8A, 0x1EE8A), // ARABIC unassigned 29
				UnicodeRange(0x1EE9C, 0x1EEA0), // ARABIC unassigned 30
				UnicodeRange(0x1EEA4, 0x1EEA4), // ARABIC unassigned 31
				UnicodeRange(0x1EEAA, 0x1EEAA), // ARABIC unassigned 32
				UnicodeRange(0x1EEBC, 0x1EEEF), // ARABIC unassigned 33
				UnicodeRange(0x1EEF2, 0x1EFFF), // ARABIC unassigned 34
				UnicodeRange(0x1F02C, 0x1F02F), // MAHJONG unassigned 1
				UnicodeRange(0x1F094, 0x1F09F), // DOMINO unassigned 1
				UnicodeRange(0x1F0AF, 0x1F0B0), // PLAYING unassigned 1
				UnicodeRange(0x1F0C0, 0x1F0C0), // PLAYING unassigned 2
				UnicodeRange(0x1F0D0, 0x1F0D0), // PLAYING unassigned 3
				UnicodeRange(0x1F0F6, 0x1F0FF), // PLAYING unassigned 4
				UnicodeRange(0x1F1AE, 0x1F1E5), // MASK unassigned 1
				UnicodeRange(0x1F203, 0x1F20F), // SQUARED unassigned 1
				UnicodeRange(0x1F23C, 0x1F23F), // SQUARED unassigned 2
				UnicodeRange(0x1F249, 0x1F24F), // TORTOISE unassigned 1
				UnicodeRange(0x1F252, 0x1F25F), // CIRCLED unassigned 1
				UnicodeRange(0x1F266, 0x1F2FF), // ROUNDED unassigned 1
				UnicodeRange(0x1F6D8, 0x1F6DB), // ELEVATOR unassigned 1
				UnicodeRange(0x1F6ED, 0x1F6EF), // AIRPLANE unassigned 1
				UnicodeRange(0x1F6FD, 0x1F6FF), // ROLLER unassigned 1
				UnicodeRange(0x1F777, 0x1F77A), // LUNAR unassigned 1
				UnicodeRange(0x1F7DA, 0x1F7DF), // NINE unassigned 1
				UnicodeRange(0x1F7EC, 0x1F7EF), // LARGE unassigned 1
				UnicodeRange(0x1F7F1, 0x1F7FF), // HEAVY unassigned 1
				UnicodeRange(0x1F80C, 0x1F80F), // DOWNWARDS unassigned 1
				UnicodeRange(0x1F848, 0x1F84F), // DOWNWARDS unassigned 2
				UnicodeRange(0x1F85A, 0x1F85F), // UP unassigned 1
				UnicodeRange(0x1F888, 0x1F88F), // WIDE-HEADED unassigned 1
				UnicodeRange(0x1F8AE, 0x1F8AF), // WHITE unassigned 1
				UnicodeRange(0x1F8BC, 0x1F8BF), // SOUTH unassigned 1
				UnicodeRange(0x1F8C2, 0x1F8FF), // RIGHTWARDS unassigned 1
				UnicodeRange(0x1FA54, 0x1FA5F), // BLACK unassigned 1
				UnicodeRange(0x1FA6E, 0x1FA6F), // XIANGQI unassigned 1
				UnicodeRange(0x1FA7D, 0x1FA7F), // CRUTCH unassigned 1
				UnicodeRange(0x1FA8A, 0x1FA8E), // HARP unassigned 1
				UnicodeRange(0x1FAC7, 0x1FACD), // FINGERPRINT unassigned 1
				UnicodeRange(0x1FADD, 0x1FADE), // ROOT unassigned 1
				UnicodeRange(0x1FAEA, 0x1FAEF), // FACE unassigned 1
				UnicodeRange(0x1FAF9, 0x1FAFF), // RIGHTWARDS unassigned 1
				UnicodeRange(0x1FB93, 0x1FB93), // UPPER unassigned 1
				UnicodeRange(0x1FBFA, 0x1FFFF), // SEGMENTED unassigned 1
				UnicodeRange(0x2A6E0, 0x2A6FF), // CJK Ideograph Extension B unassigned 1
				UnicodeRange(0x2B73A, 0x2B73F), // CJK Ideograph Extension C unassigned 1
				UnicodeRange(0x2B81E, 0x2B81F), // CJK Ideograph Extension D unassigned 1
				UnicodeRange(0x2CEA2, 0x2CEAF), // CJK Ideograph Extension E unassigned 1
				UnicodeRange(0x2EBE1, 0x2EBEF), // CJK Ideograph Extension F unassigned 1
				UnicodeRange(0x2EE5E, 0x2F7FF), // CJK Ideograph Extension I unassigned 1
				UnicodeRange(0x2FA1E, 0x2FFFF), // CJK unassigned 1
				UnicodeRange(0x3134B, 0x3134F), // CJK Ideograph Extension G unassigned 1
				UnicodeRange(0x323B0, 0xE0000), // CJK Ideograph Extension H unassigned 1
				UnicodeRange(0xE0002, 0xE001F), // LANGUAGE unassigned 1
				UnicodeRange(0xE0080, 0xE00FF), // CANCEL unassigned 1
				UnicodeRange(0xE01F0, 0xEFFFF), // VARIATION unassigned 1
				UnicodeRange(0xFFFFE, 0xFFFFF), // Plane 15 Private Use unassigned 1
				UnicodeRange(0x10FFFE, 0x10FFFF), // Plane 16 Private Use unassigned 1
			)
		}
	},
	UTF_8_PRINTABLE() {
		override val ranges: Array<out UnicodeRange> by lazy {
			arrayOf(
				//UnicodeRange(0x0000, 0x001F), // control
				UnicodeRange(0x0020, 0x007E),
				//UnicodeRange(0x007F, 0x009F), // control
				UnicodeRange(0x00A0, 0x0377),
				//UnicodeRange(0x0378, 0x0379), // GREEK unassigned 1
				UnicodeRange(0x037A, 0x037F),
				//UnicodeRange(0x0380, 0x0383), // GREEK unassigned 2
				UnicodeRange(0x0384, 0x038A),
				//UnicodeRange(0x038B, 0x038B), // GREEK unassigned 3
				UnicodeRange(0x038C, 0x038C),
				//UnicodeRange(0x038D, 0x038D), // GREEK unassigned 4
				UnicodeRange(0x038E, 0x03A1),
				//UnicodeRange(0x03A2, 0x03A2), // GREEK unassigned 5
				UnicodeRange(0x03A3, 0x052F),
				//UnicodeRange(0x0530, 0x0530), // CYRILLIC unassigned 1
				UnicodeRange(0x0531, 0x0556),
				//UnicodeRange(0x0557, 0x0558), // ARMENIAN unassigned 1
				UnicodeRange(0x0559, 0x058A),
				//UnicodeRange(0x058B, 0x058C), // ARMENIAN unassigned 2
				UnicodeRange(0x058D, 0x058F),
				//UnicodeRange(0x0590, 0x0590), // ARMENIAN unassigned 3
				UnicodeRange(0x0591, 0x05C7),
				//UnicodeRange(0x05C8, 0x05CF), // HEBREW unassigned 1
				UnicodeRange(0x05D0, 0x05EA),
				//UnicodeRange(0x05EB, 0x05EE), // HEBREW unassigned 2
				UnicodeRange(0x05EF, 0x05F4),
				//UnicodeRange(0x05F5, 0x05FF), // HEBREW unassigned 3
				UnicodeRange(0x0600, 0x070D),
				//UnicodeRange(0x070E, 0x070E), // SYRIAC unassigned 1
				UnicodeRange(0x070F, 0x074A),
				//UnicodeRange(0x074B, 0x074C), // SYRIAC unassigned 2
				UnicodeRange(0x074D, 0x07B1),
				//UnicodeRange(0x07B2, 0x07BF), // THAANA unassigned 1
				UnicodeRange(0x07C0, 0x07FA),
				//UnicodeRange(0x07FB, 0x07FC), // NKO unassigned 1
				UnicodeRange(0x07FD, 0x082D),
				//UnicodeRange(0x082E, 0x082F), // SAMARITAN unassigned 1
				UnicodeRange(0x0830, 0x083E),
				//UnicodeRange(0x083F, 0x083F), // SAMARITAN unassigned 2
				UnicodeRange(0x0840, 0x085B),
				//UnicodeRange(0x085C, 0x085D), // MANDAIC unassigned 1
				UnicodeRange(0x085E, 0x085E),
				//UnicodeRange(0x085F, 0x085F), // MANDAIC unassigned 2
				UnicodeRange(0x0860, 0x086A),
				//UnicodeRange(0x086B, 0x086F), // SYRIAC unassigned 1
				UnicodeRange(0x0870, 0x088E),
				//UnicodeRange(0x088F, 0x088F), // ARABIC unassigned 1
				UnicodeRange(0x0890, 0x0891),
				//UnicodeRange(0x0892, 0x0896), // ARABIC unassigned 2
				UnicodeRange(0x0897, 0x0983),
				//UnicodeRange(0x0984, 0x0984), // BENGALI unassigned 1
				UnicodeRange(0x0985, 0x098C),
				//UnicodeRange(0x098D, 0x098E), // BENGALI unassigned 2
				UnicodeRange(0x098F, 0x0990),
				//UnicodeRange(0x0991, 0x0992), // BENGALI unassigned 3
				UnicodeRange(0x0993, 0x09A8),
				//UnicodeRange(0x09A9, 0x09A9), // BENGALI unassigned 4
				UnicodeRange(0x09AA, 0x09B0),
				//UnicodeRange(0x09B1, 0x09B1), // BENGALI unassigned 5
				UnicodeRange(0x09B2, 0x09B2),
				//UnicodeRange(0x09B3, 0x09B5), // BENGALI unassigned 6
				UnicodeRange(0x09B6, 0x09B9),
				//UnicodeRange(0x09BA, 0x09BB), // BENGALI unassigned 7
				UnicodeRange(0x09BC, 0x09C4),
				//UnicodeRange(0x09C5, 0x09C6), // BENGALI unassigned 8
				UnicodeRange(0x09C7, 0x09C8),
				//UnicodeRange(0x09C9, 0x09CA), // BENGALI unassigned 9
				UnicodeRange(0x09CB, 0x09CE),
				//UnicodeRange(0x09CF, 0x09D6), // BENGALI unassigned 10
				UnicodeRange(0x09D7, 0x09D7),
				//UnicodeRange(0x09D8, 0x09DB), // BENGALI unassigned 11
				UnicodeRange(0x09DC, 0x09DD),
				//UnicodeRange(0x09DE, 0x09DE), // BENGALI unassigned 12
				UnicodeRange(0x09DF, 0x09E3),
				//UnicodeRange(0x09E4, 0x09E5), // BENGALI unassigned 13
				UnicodeRange(0x09E6, 0x09FE),
				//UnicodeRange(0x09FF, 0x0A00), // BENGALI unassigned 14
				UnicodeRange(0x0A01, 0x0A03),
				//UnicodeRange(0x0A04, 0x0A04), // GURMUKHI unassigned 1
				UnicodeRange(0x0A05, 0x0A0A),
				//UnicodeRange(0x0A0B, 0x0A0E), // GURMUKHI unassigned 2
				UnicodeRange(0x0A0F, 0x0A10),
				//UnicodeRange(0x0A11, 0x0A12), // GURMUKHI unassigned 3
				UnicodeRange(0x0A13, 0x0A28),
				//UnicodeRange(0x0A29, 0x0A29), // GURMUKHI unassigned 4
				UnicodeRange(0x0A2A, 0x0A30),
				//UnicodeRange(0x0A31, 0x0A31), // GURMUKHI unassigned 5
				UnicodeRange(0x0A32, 0x0A33),
				//UnicodeRange(0x0A34, 0x0A34), // GURMUKHI unassigned 6
				UnicodeRange(0x0A35, 0x0A36),
				//UnicodeRange(0x0A37, 0x0A37), // GURMUKHI unassigned 7
				UnicodeRange(0x0A38, 0x0A39),
				//UnicodeRange(0x0A3A, 0x0A3B), // GURMUKHI unassigned 8
				UnicodeRange(0x0A3C, 0x0A3C),
				//UnicodeRange(0x0A3D, 0x0A3D), // GURMUKHI unassigned 9
				UnicodeRange(0x0A3E, 0x0A42),
				//UnicodeRange(0x0A43, 0x0A46), // GURMUKHI unassigned 10
				UnicodeRange(0x0A47, 0x0A48),
				//UnicodeRange(0x0A49, 0x0A4A), // GURMUKHI unassigned 11
				UnicodeRange(0x0A4B, 0x0A4D),
				//UnicodeRange(0x0A4E, 0x0A50), // GURMUKHI unassigned 12
				UnicodeRange(0x0A51, 0x0A51),
				//UnicodeRange(0x0A52, 0x0A58), // GURMUKHI unassigned 13
				UnicodeRange(0x0A59, 0x0A5C),
				//UnicodeRange(0x0A5D, 0x0A5D), // GURMUKHI unassigned 14
				UnicodeRange(0x0A5E, 0x0A5E),
				//UnicodeRange(0x0A5F, 0x0A65), // GURMUKHI unassigned 15
				UnicodeRange(0x0A66, 0x0A76),
				//UnicodeRange(0x0A77, 0x0A80), // GURMUKHI unassigned 16
				UnicodeRange(0x0A81, 0x0A83),
				//UnicodeRange(0x0A84, 0x0A84), // GUJARATI unassigned 1
				UnicodeRange(0x0A85, 0x0A8D),
				//UnicodeRange(0x0A8E, 0x0A8E), // GUJARATI unassigned 2
				UnicodeRange(0x0A8F, 0x0A91),
				//UnicodeRange(0x0A92, 0x0A92), // GUJARATI unassigned 3
				UnicodeRange(0x0A93, 0x0AA8),
				//UnicodeRange(0x0AA9, 0x0AA9), // GUJARATI unassigned 4
				UnicodeRange(0x0AAA, 0x0AB0),
				//UnicodeRange(0x0AB1, 0x0AB1), // GUJARATI unassigned 5
				UnicodeRange(0x0AB2, 0x0AB3),
				//UnicodeRange(0x0AB4, 0x0AB4), // GUJARATI unassigned 6
				UnicodeRange(0x0AB5, 0x0AB9),
				//UnicodeRange(0x0ABA, 0x0ABB), // GUJARATI unassigned 7
				UnicodeRange(0x0ABC, 0x0AC5),
				//UnicodeRange(0x0AC6, 0x0AC6), // GUJARATI unassigned 8
				UnicodeRange(0x0AC7, 0x0AC9),
				//UnicodeRange(0x0ACA, 0x0ACA), // GUJARATI unassigned 9
				UnicodeRange(0x0ACB, 0x0ACD),
				//UnicodeRange(0x0ACE, 0x0ACF), // GUJARATI unassigned 10
				UnicodeRange(0x0AD0, 0x0AD0),
				//UnicodeRange(0x0AD1, 0x0ADF), // GUJARATI unassigned 11
				UnicodeRange(0x0AE0, 0x0AE3),
				//UnicodeRange(0x0AE4, 0x0AE5), // GUJARATI unassigned 12
				UnicodeRange(0x0AE6, 0x0AF1),
				//UnicodeRange(0x0AF2, 0x0AF8), // GUJARATI unassigned 13
				UnicodeRange(0x0AF9, 0x0AFF),
				//UnicodeRange(0x0B00, 0x0B00), // GUJARATI unassigned 14
				UnicodeRange(0x0B01, 0x0B03),
				//UnicodeRange(0x0B04, 0x0B04), // ORIYA unassigned 1
				UnicodeRange(0x0B05, 0x0B0C),
				//UnicodeRange(0x0B0D, 0x0B0E), // ORIYA unassigned 2
				UnicodeRange(0x0B0F, 0x0B10),
				//UnicodeRange(0x0B11, 0x0B12), // ORIYA unassigned 3
				UnicodeRange(0x0B13, 0x0B28),
				//UnicodeRange(0x0B29, 0x0B29), // ORIYA unassigned 4
				UnicodeRange(0x0B2A, 0x0B30),
				//UnicodeRange(0x0B31, 0x0B31), // ORIYA unassigned 5
				UnicodeRange(0x0B32, 0x0B33),
				//UnicodeRange(0x0B34, 0x0B34), // ORIYA unassigned 6
				UnicodeRange(0x0B35, 0x0B39),
				//UnicodeRange(0x0B3A, 0x0B3B), // ORIYA unassigned 7
				UnicodeRange(0x0B3C, 0x0B44),
				//UnicodeRange(0x0B45, 0x0B46), // ORIYA unassigned 8
				UnicodeRange(0x0B47, 0x0B48),
				//UnicodeRange(0x0B49, 0x0B4A), // ORIYA unassigned 9
				UnicodeRange(0x0B4B, 0x0B4D),
				//UnicodeRange(0x0B4E, 0x0B54), // ORIYA unassigned 10
				UnicodeRange(0x0B55, 0x0B57),
				//UnicodeRange(0x0B58, 0x0B5B), // ORIYA unassigned 11
				UnicodeRange(0x0B5C, 0x0B5D),
				//UnicodeRange(0x0B5E, 0x0B5E), // ORIYA unassigned 12
				UnicodeRange(0x0B5F, 0x0B63),
				//UnicodeRange(0x0B64, 0x0B65), // ORIYA unassigned 13
				UnicodeRange(0x0B66, 0x0B77),
				//UnicodeRange(0x0B78, 0x0B81), // ORIYA unassigned 14
				UnicodeRange(0x0B82, 0x0B83),
				//UnicodeRange(0x0B84, 0x0B84), // TAMIL unassigned 1
				UnicodeRange(0x0B85, 0x0B8A),
				//UnicodeRange(0x0B8B, 0x0B8D), // TAMIL unassigned 2
				UnicodeRange(0x0B8E, 0x0B90),
				//UnicodeRange(0x0B91, 0x0B91), // TAMIL unassigned 3
				UnicodeRange(0x0B92, 0x0B95),
				//UnicodeRange(0x0B96, 0x0B98), // TAMIL unassigned 4
				UnicodeRange(0x0B99, 0x0B9A),
				//UnicodeRange(0x0B9B, 0x0B9B), // TAMIL unassigned 5
				UnicodeRange(0x0B9C, 0x0B9C),
				//UnicodeRange(0x0B9D, 0x0B9D), // TAMIL unassigned 6
				UnicodeRange(0x0B9E, 0x0B9F),
				//UnicodeRange(0x0BA0, 0x0BA2), // TAMIL unassigned 7
				UnicodeRange(0x0BA3, 0x0BA4),
				//UnicodeRange(0x0BA5, 0x0BA7), // TAMIL unassigned 8
				UnicodeRange(0x0BA8, 0x0BAA),
				//UnicodeRange(0x0BAB, 0x0BAD), // TAMIL unassigned 9
				UnicodeRange(0x0BAE, 0x0BB9),
				//UnicodeRange(0x0BBA, 0x0BBD), // TAMIL unassigned 10
				UnicodeRange(0x0BBE, 0x0BC2),
				//UnicodeRange(0x0BC3, 0x0BC5), // TAMIL unassigned 11
				UnicodeRange(0x0BC6, 0x0BC8),
				//UnicodeRange(0x0BC9, 0x0BC9), // TAMIL unassigned 12
				UnicodeRange(0x0BCA, 0x0BCD),
				//UnicodeRange(0x0BCE, 0x0BCF), // TAMIL unassigned 13
				UnicodeRange(0x0BD0, 0x0BD0),
				//UnicodeRange(0x0BD1, 0x0BD6), // TAMIL unassigned 14
				UnicodeRange(0x0BD7, 0x0BD7),
				//UnicodeRange(0x0BD8, 0x0BE5), // TAMIL unassigned 15
				UnicodeRange(0x0BE6, 0x0BFA),
				//UnicodeRange(0x0BFB, 0x0BFF), // TAMIL unassigned 16
				UnicodeRange(0x0C00, 0x0C0C),
				//UnicodeRange(0x0C0D, 0x0C0D), // TELUGU unassigned 1
				UnicodeRange(0x0C0E, 0x0C10),
				//UnicodeRange(0x0C11, 0x0C11), // TELUGU unassigned 2
				UnicodeRange(0x0C12, 0x0C28),
				//UnicodeRange(0x0C29, 0x0C29), // TELUGU unassigned 3
				UnicodeRange(0x0C2A, 0x0C39),
				//UnicodeRange(0x0C3A, 0x0C3B), // TELUGU unassigned 4
				UnicodeRange(0x0C3C, 0x0C44),
				//UnicodeRange(0x0C45, 0x0C45), // TELUGU unassigned 5
				UnicodeRange(0x0C46, 0x0C48),
				//UnicodeRange(0x0C49, 0x0C49), // TELUGU unassigned 6
				UnicodeRange(0x0C4A, 0x0C4D),
				//UnicodeRange(0x0C4E, 0x0C54), // TELUGU unassigned 7
				UnicodeRange(0x0C55, 0x0C56),
				//UnicodeRange(0x0C57, 0x0C57), // TELUGU unassigned 8
				UnicodeRange(0x0C58, 0x0C5A),
				//UnicodeRange(0x0C5B, 0x0C5C), // TELUGU unassigned 9
				UnicodeRange(0x0C5D, 0x0C5D),
				//UnicodeRange(0x0C5E, 0x0C5F), // TELUGU unassigned 10
				UnicodeRange(0x0C60, 0x0C63),
				//UnicodeRange(0x0C64, 0x0C65), // TELUGU unassigned 11
				UnicodeRange(0x0C66, 0x0C6F),
				//UnicodeRange(0x0C70, 0x0C76), // TELUGU unassigned 12
				UnicodeRange(0x0C77, 0x0C8C),
				//UnicodeRange(0x0C8D, 0x0C8D), // KANNADA unassigned 1
				UnicodeRange(0x0C8E, 0x0C90),
				//UnicodeRange(0x0C91, 0x0C91), // KANNADA unassigned 2
				UnicodeRange(0x0C92, 0x0CA8),
				//UnicodeRange(0x0CA9, 0x0CA9), // KANNADA unassigned 3
				UnicodeRange(0x0CAA, 0x0CB3),
				//UnicodeRange(0x0CB4, 0x0CB4), // KANNADA unassigned 4
				UnicodeRange(0x0CB5, 0x0CB9),
				//UnicodeRange(0x0CBA, 0x0CBB), // KANNADA unassigned 5
				UnicodeRange(0x0CBC, 0x0CC4),
				//UnicodeRange(0x0CC5, 0x0CC5), // KANNADA unassigned 6
				UnicodeRange(0x0CC6, 0x0CC8),
				//UnicodeRange(0x0CC9, 0x0CC9), // KANNADA unassigned 7
				UnicodeRange(0x0CCA, 0x0CCD),
				//UnicodeRange(0x0CCE, 0x0CD4), // KANNADA unassigned 8
				UnicodeRange(0x0CD5, 0x0CD6),
				//UnicodeRange(0x0CD7, 0x0CDC), // KANNADA unassigned 9
				UnicodeRange(0x0CDD, 0x0CDE),
				//UnicodeRange(0x0CDF, 0x0CDF), // KANNADA unassigned 10
				UnicodeRange(0x0CE0, 0x0CE3),
				//UnicodeRange(0x0CE4, 0x0CE5), // KANNADA unassigned 11
				UnicodeRange(0x0CE6, 0x0CEF),
				//UnicodeRange(0x0CF0, 0x0CF0), // KANNADA unassigned 12
				UnicodeRange(0x0CF1, 0x0CF3),
				//UnicodeRange(0x0CF4, 0x0CFF), // KANNADA unassigned 13
				UnicodeRange(0x0D00, 0x0D0C),
				//UnicodeRange(0x0D0D, 0x0D0D), // MALAYALAM unassigned 1
				UnicodeRange(0x0D0E, 0x0D10),
				//UnicodeRange(0x0D11, 0x0D11), // MALAYALAM unassigned 2
				UnicodeRange(0x0D12, 0x0D44),
				//UnicodeRange(0x0D45, 0x0D45), // MALAYALAM unassigned 3
				UnicodeRange(0x0D46, 0x0D48),
				//UnicodeRange(0x0D49, 0x0D49), // MALAYALAM unassigned 4
				UnicodeRange(0x0D4A, 0x0D4F),
				//UnicodeRange(0x0D50, 0x0D53), // MALAYALAM unassigned 5
				UnicodeRange(0x0D54, 0x0D63),
				//UnicodeRange(0x0D64, 0x0D65), // MALAYALAM unassigned 6
				UnicodeRange(0x0D66, 0x0D7F),
				//UnicodeRange(0x0D80, 0x0D80), // MALAYALAM unassigned 7
				UnicodeRange(0x0D81, 0x0D83),
				//UnicodeRange(0x0D84, 0x0D84), // SINHALA unassigned 1
				UnicodeRange(0x0D85, 0x0D96),
				//UnicodeRange(0x0D97, 0x0D99), // SINHALA unassigned 2
				UnicodeRange(0x0D9A, 0x0DB1),
				//UnicodeRange(0x0DB2, 0x0DB2), // SINHALA unassigned 3
				UnicodeRange(0x0DB3, 0x0DBB),
				//UnicodeRange(0x0DBC, 0x0DBC), // SINHALA unassigned 4
				UnicodeRange(0x0DBD, 0x0DBD),
				//UnicodeRange(0x0DBE, 0x0DBF), // SINHALA unassigned 5
				UnicodeRange(0x0DC0, 0x0DC6),
				//UnicodeRange(0x0DC7, 0x0DC9), // SINHALA unassigned 6
				UnicodeRange(0x0DCA, 0x0DCA),
				//UnicodeRange(0x0DCB, 0x0DCE), // SINHALA unassigned 7
				UnicodeRange(0x0DCF, 0x0DD4),
				//UnicodeRange(0x0DD5, 0x0DD5), // SINHALA unassigned 8
				UnicodeRange(0x0DD6, 0x0DD6),
				//UnicodeRange(0x0DD7, 0x0DD7), // SINHALA unassigned 9
				UnicodeRange(0x0DD8, 0x0DDF),
				//UnicodeRange(0x0DE0, 0x0DE5), // SINHALA unassigned 10
				UnicodeRange(0x0DE6, 0x0DEF),
				//UnicodeRange(0x0DF0, 0x0DF1), // SINHALA unassigned 11
				UnicodeRange(0x0DF2, 0x0DF4),
				//UnicodeRange(0x0DF5, 0x0E00), // SINHALA unassigned 12
				UnicodeRange(0x0E01, 0x0E3A),
				//UnicodeRange(0x0E3B, 0x0E3E), // THAI unassigned 1
				UnicodeRange(0x0E3F, 0x0E5B),
				//UnicodeRange(0x0E5C, 0x0E80), // THAI unassigned 2
				UnicodeRange(0x0E81, 0x0E82),
				//UnicodeRange(0x0E83, 0x0E83), // LAO unassigned 1
				UnicodeRange(0x0E84, 0x0E84),
				//UnicodeRange(0x0E85, 0x0E85), // LAO unassigned 2
				UnicodeRange(0x0E86, 0x0E8A),
				//UnicodeRange(0x0E8B, 0x0E8B), // LAO unassigned 3
				UnicodeRange(0x0E8C, 0x0EA3),
				//UnicodeRange(0x0EA4, 0x0EA4), // LAO unassigned 4
				UnicodeRange(0x0EA5, 0x0EA5),
				//UnicodeRange(0x0EA6, 0x0EA6), // LAO unassigned 5
				UnicodeRange(0x0EA7, 0x0EBD),
				//UnicodeRange(0x0EBE, 0x0EBF), // LAO unassigned 6
				UnicodeRange(0x0EC0, 0x0EC4),
				//UnicodeRange(0x0EC5, 0x0EC5), // LAO unassigned 7
				UnicodeRange(0x0EC6, 0x0EC6),
				//UnicodeRange(0x0EC7, 0x0EC7), // LAO unassigned 8
				UnicodeRange(0x0EC8, 0x0ECE),
				//UnicodeRange(0x0ECF, 0x0ECF), // LAO unassigned 9
				UnicodeRange(0x0ED0, 0x0ED9),
				//UnicodeRange(0x0EDA, 0x0EDB), // LAO unassigned 10
				UnicodeRange(0x0EDC, 0x0EDF),
				//UnicodeRange(0x0EE0, 0x0EFF), // LAO unassigned 11
				UnicodeRange(0x0F00, 0x0F47),
				//UnicodeRange(0x0F48, 0x0F48), // TIBETAN unassigned 1
				UnicodeRange(0x0F49, 0x0F6C),
				//UnicodeRange(0x0F6D, 0x0F70), // TIBETAN unassigned 2
				UnicodeRange(0x0F71, 0x0F97),
				//UnicodeRange(0x0F98, 0x0F98), // TIBETAN unassigned 3
				UnicodeRange(0x0F99, 0x0FBC),
				//UnicodeRange(0x0FBD, 0x0FBD), // TIBETAN unassigned 4
				UnicodeRange(0x0FBE, 0x0FCC),
				//UnicodeRange(0x0FCD, 0x0FCD), // TIBETAN unassigned 5
				UnicodeRange(0x0FCE, 0x0FDA),
				//UnicodeRange(0x0FDB, 0x0FFF), // TIBETAN unassigned 6
				UnicodeRange(0x1000, 0x10C5),
				//UnicodeRange(0x10C6, 0x10C6), // GEORGIAN unassigned 1
				UnicodeRange(0x10C7, 0x10C7),
				//UnicodeRange(0x10C8, 0x10CC), // GEORGIAN unassigned 2
				UnicodeRange(0x10CD, 0x10CD),
				//UnicodeRange(0x10CE, 0x10CF), // GEORGIAN unassigned 3
				UnicodeRange(0x10D0, 0x1248),
				//UnicodeRange(0x1249, 0x1249), // ETHIOPIC unassigned 1
				UnicodeRange(0x124A, 0x124D),
				//UnicodeRange(0x124E, 0x124F), // ETHIOPIC unassigned 2
				UnicodeRange(0x1250, 0x1256),
				//UnicodeRange(0x1257, 0x1257), // ETHIOPIC unassigned 3
				UnicodeRange(0x1258, 0x1258),
				//UnicodeRange(0x1259, 0x1259), // ETHIOPIC unassigned 4
				UnicodeRange(0x125A, 0x125D),
				//UnicodeRange(0x125E, 0x125F), // ETHIOPIC unassigned 5
				UnicodeRange(0x1260, 0x1288),
				//UnicodeRange(0x1289, 0x1289), // ETHIOPIC unassigned 6
				UnicodeRange(0x128A, 0x128D),
				//UnicodeRange(0x128E, 0x128F), // ETHIOPIC unassigned 7
				UnicodeRange(0x1290, 0x12B0),
				//UnicodeRange(0x12B1, 0x12B1), // ETHIOPIC unassigned 8
				UnicodeRange(0x12B2, 0x12B5),
				//UnicodeRange(0x12B6, 0x12B7), // ETHIOPIC unassigned 9
				UnicodeRange(0x12B8, 0x12BE),
				//UnicodeRange(0x12BF, 0x12BF), // ETHIOPIC unassigned 10
				UnicodeRange(0x12C0, 0x12C0),
				//UnicodeRange(0x12C1, 0x12C1), // ETHIOPIC unassigned 11
				UnicodeRange(0x12C2, 0x12C5),
				//UnicodeRange(0x12C6, 0x12C7), // ETHIOPIC unassigned 12
				UnicodeRange(0x12C8, 0x12D6),
				//UnicodeRange(0x12D7, 0x12D7), // ETHIOPIC unassigned 13
				UnicodeRange(0x12D8, 0x1310),
				//UnicodeRange(0x1311, 0x1311), // ETHIOPIC unassigned 14
				UnicodeRange(0x1312, 0x1315),
				//UnicodeRange(0x1316, 0x1317), // ETHIOPIC unassigned 15
				UnicodeRange(0x1318, 0x135A),
				//UnicodeRange(0x135B, 0x135C), // ETHIOPIC unassigned 16
				UnicodeRange(0x135D, 0x137C),
				//UnicodeRange(0x137D, 0x137F), // ETHIOPIC unassigned 17
				UnicodeRange(0x1380, 0x1399),
				//UnicodeRange(0x139A, 0x139F), // ETHIOPIC unassigned 18
				UnicodeRange(0x13A0, 0x13F5),
				//UnicodeRange(0x13F6, 0x13F7), // CHEROKEE unassigned 1
				UnicodeRange(0x13F8, 0x13FD),
				//UnicodeRange(0x13FE, 0x13FF), // CHEROKEE unassigned 2
				UnicodeRange(0x1400, 0x169C),
				//UnicodeRange(0x169D, 0x169F), // OGHAM unassigned 1
				UnicodeRange(0x16A0, 0x16F8),
				//UnicodeRange(0x16F9, 0x16FF), // RUNIC unassigned 1
				UnicodeRange(0x1700, 0x1715),
				//UnicodeRange(0x1716, 0x171E), // TAGALOG unassigned 1
				UnicodeRange(0x171F, 0x1736),
				//UnicodeRange(0x1737, 0x173F), // PHILIPPINE unassigned 1
				UnicodeRange(0x1740, 0x1753),
				//UnicodeRange(0x1754, 0x175F), // BUHID unassigned 1
				UnicodeRange(0x1760, 0x176C),
				//UnicodeRange(0x176D, 0x176D), // TAGBANWA unassigned 1
				UnicodeRange(0x176E, 0x1770),
				//UnicodeRange(0x1771, 0x1771), // TAGBANWA unassigned 2
				UnicodeRange(0x1772, 0x1773),
				//UnicodeRange(0x1774, 0x177F), // TAGBANWA unassigned 3
				UnicodeRange(0x1780, 0x17DD),
				//UnicodeRange(0x17DE, 0x17DF), // KHMER unassigned 1
				UnicodeRange(0x17E0, 0x17E9),
				//UnicodeRange(0x17EA, 0x17EF), // KHMER unassigned 2
				UnicodeRange(0x17F0, 0x17F9),
				//UnicodeRange(0x17FA, 0x17FF), // KHMER unassigned 3
				UnicodeRange(0x1800, 0x1819),
				//UnicodeRange(0x181A, 0x181F), // MONGOLIAN unassigned 1
				UnicodeRange(0x1820, 0x1878),
				//UnicodeRange(0x1879, 0x187F), // MONGOLIAN unassigned 2
				UnicodeRange(0x1880, 0x18AA),
				//UnicodeRange(0x18AB, 0x18AF), // MONGOLIAN unassigned 3
				UnicodeRange(0x18B0, 0x18F5),
				//UnicodeRange(0x18F6, 0x18FF), // CANADIAN unassigned 1
				UnicodeRange(0x1900, 0x191E),
				//UnicodeRange(0x191F, 0x191F), // LIMBU unassigned 1
				UnicodeRange(0x1920, 0x192B),
				//UnicodeRange(0x192C, 0x192F), // LIMBU unassigned 2
				UnicodeRange(0x1930, 0x193B),
				//UnicodeRange(0x193C, 0x193F), // LIMBU unassigned 3
				UnicodeRange(0x1940, 0x1940),
				//UnicodeRange(0x1941, 0x1943), // LIMBU unassigned 4
				UnicodeRange(0x1944, 0x196D),
				//UnicodeRange(0x196E, 0x196F), // TAI unassigned 1
				UnicodeRange(0x1970, 0x1974),
				//UnicodeRange(0x1975, 0x197F), // TAI unassigned 2
				UnicodeRange(0x1980, 0x19AB),
				//UnicodeRange(0x19AC, 0x19AF), // NEW unassigned 1
				UnicodeRange(0x19B0, 0x19C9),
				//UnicodeRange(0x19CA, 0x19CF), // NEW unassigned 2
				UnicodeRange(0x19D0, 0x19DA),
				//UnicodeRange(0x19DB, 0x19DD), // NEW unassigned 3
				UnicodeRange(0x19DE, 0x1A1B),
				//UnicodeRange(0x1A1C, 0x1A1D), // BUGINESE unassigned 1
				UnicodeRange(0x1A1E, 0x1A5E),
				//UnicodeRange(0x1A5F, 0x1A5F), // TAI unassigned 1
				UnicodeRange(0x1A60, 0x1A7C),
				//UnicodeRange(0x1A7D, 0x1A7E), // TAI unassigned 2
				UnicodeRange(0x1A7F, 0x1A89),
				//UnicodeRange(0x1A8A, 0x1A8F), // TAI unassigned 3
				UnicodeRange(0x1A90, 0x1A99),
				//UnicodeRange(0x1A9A, 0x1A9F), // TAI unassigned 4
				UnicodeRange(0x1AA0, 0x1AAD),
				//UnicodeRange(0x1AAE, 0x1AAF), // TAI unassigned 5
				UnicodeRange(0x1AB0, 0x1ACE),
				//UnicodeRange(0x1ACF, 0x1AFF), // COMBINING unassigned 1
				UnicodeRange(0x1B00, 0x1B4C),
				//UnicodeRange(0x1B4D, 0x1B4D), // BALINESE unassigned 1
				UnicodeRange(0x1B4E, 0x1BF3),
				//UnicodeRange(0x1BF4, 0x1BFB), // BATAK unassigned 1
				UnicodeRange(0x1BFC, 0x1C37),
				//UnicodeRange(0x1C38, 0x1C3A), // LEPCHA unassigned 1
				UnicodeRange(0x1C3B, 0x1C49),
				//UnicodeRange(0x1C4A, 0x1C4C), // LEPCHA unassigned 2
				UnicodeRange(0x1C4D, 0x1C8A),
				//UnicodeRange(0x1C8B, 0x1C8F), // CYRILLIC unassigned 1
				UnicodeRange(0x1C90, 0x1CBA),
				//UnicodeRange(0x1CBB, 0x1CBC), // GEORGIAN unassigned 1
				UnicodeRange(0x1CBD, 0x1CC7),
				//UnicodeRange(0x1CC8, 0x1CCF), // SUNDANESE unassigned 1
				UnicodeRange(0x1CD0, 0x1CFA),
				//UnicodeRange(0x1CFB, 0x1CFF), // VEDIC unassigned 1
				UnicodeRange(0x1D00, 0x1F15),
				//UnicodeRange(0x1F16, 0x1F17), // GREEK unassigned 1
				UnicodeRange(0x1F18, 0x1F1D),
				//UnicodeRange(0x1F1E, 0x1F1F), // GREEK unassigned 2
				UnicodeRange(0x1F20, 0x1F45),
				//UnicodeRange(0x1F46, 0x1F47), // GREEK unassigned 3
				UnicodeRange(0x1F48, 0x1F4D),
				//UnicodeRange(0x1F4E, 0x1F4F), // GREEK unassigned 4
				UnicodeRange(0x1F50, 0x1F57),
				//UnicodeRange(0x1F58, 0x1F58), // GREEK unassigned 5
				UnicodeRange(0x1F59, 0x1F59),
				//UnicodeRange(0x1F5A, 0x1F5A), // GREEK unassigned 6
				UnicodeRange(0x1F5B, 0x1F5B),
				//UnicodeRange(0x1F5C, 0x1F5C), // GREEK unassigned 7
				UnicodeRange(0x1F5D, 0x1F5D),
				//UnicodeRange(0x1F5E, 0x1F5E), // GREEK unassigned 8
				UnicodeRange(0x1F5F, 0x1F7D),
				//UnicodeRange(0x1F7E, 0x1F7F), // GREEK unassigned 9
				UnicodeRange(0x1F80, 0x1FB4),
				//UnicodeRange(0x1FB5, 0x1FB5), // GREEK unassigned 10
				UnicodeRange(0x1FB6, 0x1FC4),
				//UnicodeRange(0x1FC5, 0x1FC5), // GREEK unassigned 11
				UnicodeRange(0x1FC6, 0x1FD3),
				//UnicodeRange(0x1FD4, 0x1FD5), // GREEK unassigned 12
				UnicodeRange(0x1FD6, 0x1FDB),
				//UnicodeRange(0x1FDC, 0x1FDC), // GREEK unassigned 13
				UnicodeRange(0x1FDD, 0x1FEF),
				//UnicodeRange(0x1FF0, 0x1FF1), // GREEK unassigned 14
				UnicodeRange(0x1FF2, 0x1FF4),
				//UnicodeRange(0x1FF5, 0x1FF5), // GREEK unassigned 15
				UnicodeRange(0x1FF6, 0x1FFE),
				//UnicodeRange(0x1FFF, 0x1FFF), // GREEK unassigned 16
				UnicodeRange(0x2000, 0x2064),
				//UnicodeRange(0x2065, 0x2065), // INVISIBLE unassigned 1
				UnicodeRange(0x2066, 0x2071),
				//UnicodeRange(0x2072, 0x2073), // SUPERSCRIPT unassigned 1
				UnicodeRange(0x2074, 0x208E),
				//UnicodeRange(0x208F, 0x208F), // SUBSCRIPT unassigned 1
				UnicodeRange(0x2090, 0x209C),
				//UnicodeRange(0x209D, 0x209F), // LATIN unassigned 1
				UnicodeRange(0x20A0, 0x20C0),
				//UnicodeRange(0x20C1, 0x20CF), // SOM unassigned 1
				UnicodeRange(0x20D0, 0x20F0),
				//UnicodeRange(0x20F1, 0x20FF), // COMBINING unassigned 1
				UnicodeRange(0x2100, 0x218B),
				//UnicodeRange(0x218C, 0x218F), // TURNED unassigned 1
				UnicodeRange(0x2190, 0x2429),
				//UnicodeRange(0x242A, 0x243F), // SYMBOL unassigned 1
				UnicodeRange(0x2440, 0x244A),
				//UnicodeRange(0x244B, 0x245F), // OCR unassigned 1
				UnicodeRange(0x2460, 0x2B73),
				//UnicodeRange(0x2B74, 0x2B75), // DOWNWARDS unassigned 1
				UnicodeRange(0x2B76, 0x2B95),
				//UnicodeRange(0x2B96, 0x2B96), // RIGHTWARDS unassigned 1
				UnicodeRange(0x2B97, 0x2CF3),
				//UnicodeRange(0x2CF4, 0x2CF8), // COPTIC unassigned 1
				UnicodeRange(0x2CF9, 0x2D25),
				//UnicodeRange(0x2D26, 0x2D26), // GEORGIAN unassigned 1
				UnicodeRange(0x2D27, 0x2D27),
				//UnicodeRange(0x2D28, 0x2D2C), // GEORGIAN unassigned 2
				UnicodeRange(0x2D2D, 0x2D2D),
				//UnicodeRange(0x2D2E, 0x2D2F), // GEORGIAN unassigned 3
				UnicodeRange(0x2D30, 0x2D67),
				//UnicodeRange(0x2D68, 0x2D6E), // TIFINAGH unassigned 1
				UnicodeRange(0x2D6F, 0x2D70),
				//UnicodeRange(0x2D71, 0x2D7E), // TIFINAGH unassigned 2
				UnicodeRange(0x2D7F, 0x2D96),
				//UnicodeRange(0x2D97, 0x2D9F), // ETHIOPIC unassigned 1
				UnicodeRange(0x2DA0, 0x2DA6),
				//UnicodeRange(0x2DA7, 0x2DA7), // ETHIOPIC unassigned 2
				UnicodeRange(0x2DA8, 0x2DAE),
				//UnicodeRange(0x2DAF, 0x2DAF), // ETHIOPIC unassigned 3
				UnicodeRange(0x2DB0, 0x2DB6),
				//UnicodeRange(0x2DB7, 0x2DB7), // ETHIOPIC unassigned 4
				UnicodeRange(0x2DB8, 0x2DBE),
				//UnicodeRange(0x2DBF, 0x2DBF), // ETHIOPIC unassigned 5
				UnicodeRange(0x2DC0, 0x2DC6),
				//UnicodeRange(0x2DC7, 0x2DC7), // ETHIOPIC unassigned 6
				UnicodeRange(0x2DC8, 0x2DCE),
				//UnicodeRange(0x2DCF, 0x2DCF), // ETHIOPIC unassigned 7
				UnicodeRange(0x2DD0, 0x2DD6),
				//UnicodeRange(0x2DD7, 0x2DD7), // ETHIOPIC unassigned 8
				UnicodeRange(0x2DD8, 0x2DDE),
				//UnicodeRange(0x2DDF, 0x2DDF), // ETHIOPIC unassigned 9
				UnicodeRange(0x2DE0, 0x2E5D),
				//UnicodeRange(0x2E5E, 0x2E7F), // OBLIQUE unassigned 1
				UnicodeRange(0x2E80, 0x2E99),
				//UnicodeRange(0x2E9A, 0x2E9A), // CJK unassigned 1
				UnicodeRange(0x2E9B, 0x2EF3),
				//UnicodeRange(0x2EF4, 0x2EFF), // CJK unassigned 2
				UnicodeRange(0x2F00, 0x2FD5),
				//UnicodeRange(0x2FD6, 0x2FEF), // KANGXI unassigned 1
				UnicodeRange(0x2FF0, 0x303F),
				//UnicodeRange(0x3040, 0x3040), // IDEOGRAPHIC unassigned 1
				UnicodeRange(0x3041, 0x3096),
				//UnicodeRange(0x3097, 0x3098), // HIRAGANA unassigned 1
				UnicodeRange(0x3099, 0x30FF),
				//UnicodeRange(0x3100, 0x3104), // KATAKANA unassigned 1
				UnicodeRange(0x3105, 0x312F),
				//UnicodeRange(0x3130, 0x3130), // BOPOMOFO unassigned 1
				UnicodeRange(0x3131, 0x318E),
				//UnicodeRange(0x318F, 0x318F), // HANGUL unassigned 1
				UnicodeRange(0x3190, 0x31E5),
				//UnicodeRange(0x31E6, 0x31EE), // CJK unassigned 1
				UnicodeRange(0x31EF, 0x321E),
				//UnicodeRange(0x321F, 0x321F), // PARENTHESIZED unassigned 1
				UnicodeRange(0x3220, 0xA48C),
				//UnicodeRange(0xA48D, 0xA48F), // YI unassigned 1
				UnicodeRange(0xA490, 0xA4C6),
				//UnicodeRange(0xA4C7, 0xA4CF), // YI unassigned 2
				UnicodeRange(0xA4D0, 0xA62B),
				//UnicodeRange(0xA62C, 0xA63F), // VAI unassigned 1
				UnicodeRange(0xA640, 0xA6F7),
				//UnicodeRange(0xA6F8, 0xA6FF), // BAMUM unassigned 1
				UnicodeRange(0xA700, 0xA7CD),
				//UnicodeRange(0xA7CE, 0xA7CF), // LATIN unassigned 1
				UnicodeRange(0xA7D0, 0xA7D1),
				//UnicodeRange(0xA7D2, 0xA7D2), // LATIN unassigned 2
				UnicodeRange(0xA7D3, 0xA7D3),
				//UnicodeRange(0xA7D4, 0xA7D4), // LATIN unassigned 3
				UnicodeRange(0xA7D5, 0xA7DC),
				//UnicodeRange(0xA7DD, 0xA7F1), // LATIN unassigned 4
				UnicodeRange(0xA7F2, 0xA82C),
				//UnicodeRange(0xA82D, 0xA82F), // SYLOTI unassigned 1
				UnicodeRange(0xA830, 0xA839),
				//UnicodeRange(0xA83A, 0xA83F), // NORTH unassigned 1
				UnicodeRange(0xA840, 0xA877),
				//UnicodeRange(0xA878, 0xA87F), // PHAGS-PA unassigned 1
				UnicodeRange(0xA880, 0xA8C5),
				//UnicodeRange(0xA8C6, 0xA8CD), // SAURASHTRA unassigned 1
				UnicodeRange(0xA8CE, 0xA8D9),
				//UnicodeRange(0xA8DA, 0xA8DF), // SAURASHTRA unassigned 2
				UnicodeRange(0xA8E0, 0xA953),
				//UnicodeRange(0xA954, 0xA95E), // REJANG unassigned 1
				UnicodeRange(0xA95F, 0xA97C),
				//UnicodeRange(0xA97D, 0xA97F), // HANGUL unassigned 1
				UnicodeRange(0xA980, 0xA9CD),
				//UnicodeRange(0xA9CE, 0xA9CE), // JAVANESE unassigned 1
				UnicodeRange(0xA9CF, 0xA9D9),
				//UnicodeRange(0xA9DA, 0xA9DD), // JAVANESE unassigned 2
				UnicodeRange(0xA9DE, 0xA9FE),
				//UnicodeRange(0xA9FF, 0xA9FF), // MYANMAR unassigned 1
				UnicodeRange(0xAA00, 0xAA36),
				//UnicodeRange(0xAA37, 0xAA3F), // CHAM unassigned 1
				UnicodeRange(0xAA40, 0xAA4D),
				//UnicodeRange(0xAA4E, 0xAA4F), // CHAM unassigned 2
				UnicodeRange(0xAA50, 0xAA59),
				//UnicodeRange(0xAA5A, 0xAA5B), // CHAM unassigned 3
				UnicodeRange(0xAA5C, 0xAAC2),
				//UnicodeRange(0xAAC3, 0xAADA), // TAI unassigned 1
				UnicodeRange(0xAADB, 0xAAF6),
				//UnicodeRange(0xAAF7, 0xAB00), // MEETEI unassigned 1
				UnicodeRange(0xAB01, 0xAB06),
				//UnicodeRange(0xAB07, 0xAB08), // ETHIOPIC unassigned 1
				UnicodeRange(0xAB09, 0xAB0E),
				//UnicodeRange(0xAB0F, 0xAB10), // ETHIOPIC unassigned 2
				UnicodeRange(0xAB11, 0xAB16),
				//UnicodeRange(0xAB17, 0xAB1F), // ETHIOPIC unassigned 3
				UnicodeRange(0xAB20, 0xAB26),
				//UnicodeRange(0xAB27, 0xAB27), // ETHIOPIC unassigned 4
				UnicodeRange(0xAB28, 0xAB2E),
				//UnicodeRange(0xAB2F, 0xAB2F), // ETHIOPIC unassigned 5
				UnicodeRange(0xAB30, 0xAB6B),
				//UnicodeRange(0xAB6C, 0xAB6F), // MODIFIER unassigned 1
				UnicodeRange(0xAB70, 0xABED),
				//UnicodeRange(0xABEE, 0xABEF), // MEETEI unassigned 1
				UnicodeRange(0xABF0, 0xABF9),
				//UnicodeRange(0xABFA, 0xABFF), // MEETEI unassigned 2
				UnicodeRange(0xAC00, 0xD7A3),
				//UnicodeRange(0xD7A4, 0xD7AF), // Hangul Syllable unassigned 1
				UnicodeRange(0xD7B0, 0xD7C6),
				//UnicodeRange(0xD7C7, 0xD7CA), // HANGUL unassigned 1
				UnicodeRange(0xD7CB, 0xD7FB),
				//UnicodeRange(0xD7FC, 0xD7FF), // HANGUL unassigned 2
				//UnicodeRange(0xD800, 0xDFFF), // Non Private Use High Surrogate
				//UnicodeRange(0xE000, 0xF8FF), // Private Use
				UnicodeRange(0xF900, 0xFA6D),
				//UnicodeRange(0xFA6E, 0xFA6F), // CJK unassigned 1
				UnicodeRange(0xFA70, 0xFAD9),
				//UnicodeRange(0xFADA, 0xFAFF), // CJK unassigned 2
				UnicodeRange(0xFB00, 0xFB06),
				//UnicodeRange(0xFB07, 0xFB12), // LATIN unassigned 1
				UnicodeRange(0xFB13, 0xFB17),
				//UnicodeRange(0xFB18, 0xFB1C), // ARMENIAN unassigned 1
				UnicodeRange(0xFB1D, 0xFB36),
				//UnicodeRange(0xFB37, 0xFB37), // HEBREW unassigned 1
				UnicodeRange(0xFB38, 0xFB3C),
				//UnicodeRange(0xFB3D, 0xFB3D), // HEBREW unassigned 2
				UnicodeRange(0xFB3E, 0xFB3E),
				//UnicodeRange(0xFB3F, 0xFB3F), // HEBREW unassigned 3
				UnicodeRange(0xFB40, 0xFB41),
				//UnicodeRange(0xFB42, 0xFB42), // HEBREW unassigned 4
				UnicodeRange(0xFB43, 0xFB44),
				//UnicodeRange(0xFB45, 0xFB45), // HEBREW unassigned 5
				UnicodeRange(0xFB46, 0xFBC2),
				//UnicodeRange(0xFBC3, 0xFBD2), // ARABIC unassigned 1
				UnicodeRange(0xFBD3, 0xFD8F),
				//UnicodeRange(0xFD90, 0xFD91), // ARABIC unassigned 2
				UnicodeRange(0xFD92, 0xFDC7),
				//UnicodeRange(0xFDC8, 0xFDCE), // ARABIC unassigned 3
				UnicodeRange(0xFDCF, 0xFDCF),
				//UnicodeRange(0xFDD0, 0xFDEF), // ARABIC unassigned 4
				UnicodeRange(0xFDF0, 0xFE19),
				//UnicodeRange(0xFE1A, 0xFE1F), // PRESENTATION unassigned 1
				UnicodeRange(0xFE20, 0xFE52),
				//UnicodeRange(0xFE53, 0xFE53), // SMALL unassigned 1
				UnicodeRange(0xFE54, 0xFE66),
				//UnicodeRange(0xFE67, 0xFE67), // SMALL unassigned 2
				UnicodeRange(0xFE68, 0xFE6B),
				//UnicodeRange(0xFE6C, 0xFE6F), // SMALL unassigned 3
				UnicodeRange(0xFE70, 0xFE74),
				//UnicodeRange(0xFE75, 0xFE75), // ARABIC unassigned 1
				UnicodeRange(0xFE76, 0xFEFC),
				//UnicodeRange(0xFEFD, 0xFEFE), // ARABIC unassigned 2
				UnicodeRange(0xFEFF, 0xFEFF),
				//UnicodeRange(0xFF00, 0xFF00), // ZERO unassigned 1
				UnicodeRange(0xFF01, 0xFFBE),
				//UnicodeRange(0xFFBF, 0xFFC1), // HALFWIDTH unassigned 1
				UnicodeRange(0xFFC2, 0xFFC7),
				//UnicodeRange(0xFFC8, 0xFFC9), // HALFWIDTH unassigned 2
				UnicodeRange(0xFFCA, 0xFFCF),
				//UnicodeRange(0xFFD0, 0xFFD1), // HALFWIDTH unassigned 3
				UnicodeRange(0xFFD2, 0xFFD7),
				//UnicodeRange(0xFFD8, 0xFFD9), // HALFWIDTH unassigned 4
				UnicodeRange(0xFFDA, 0xFFDC),
				//UnicodeRange(0xFFDD, 0xFFDF), // HALFWIDTH unassigned 5
				UnicodeRange(0xFFE0, 0xFFE6),
				//UnicodeRange(0xFFE7, 0xFFE7), // FULLWIDTH unassigned 1
				UnicodeRange(0xFFE8, 0xFFEE),
				//UnicodeRange(0xFFEF, 0xFFF8), // HALFWIDTH unassigned 1
				UnicodeRange(0xFFF9, 0xFFFD),
				//UnicodeRange(0xFFFE, 0xFFFF), // REPLACEMENT unassigned 1
				UnicodeRange(0x10000, 0x1000B),
				//UnicodeRange(0x1000C, 0x1000C), // LINEAR unassigned 1
				UnicodeRange(0x1000D, 0x10026),
				//UnicodeRange(0x10027, 0x10027), // LINEAR unassigned 2
				UnicodeRange(0x10028, 0x1003A),
				//UnicodeRange(0x1003B, 0x1003B), // LINEAR unassigned 3
				UnicodeRange(0x1003C, 0x1003D),
				//UnicodeRange(0x1003E, 0x1003E), // LINEAR unassigned 4
				UnicodeRange(0x1003F, 0x1004D),
				//UnicodeRange(0x1004E, 0x1004F), // LINEAR unassigned 5
				UnicodeRange(0x10050, 0x1005D),
				//UnicodeRange(0x1005E, 0x1007F), // LINEAR unassigned 6
				UnicodeRange(0x10080, 0x100FA),
				//UnicodeRange(0x100FB, 0x100FF), // LINEAR unassigned 7
				UnicodeRange(0x10100, 0x10102),
				//UnicodeRange(0x10103, 0x10106), // AEGEAN unassigned 1
				UnicodeRange(0x10107, 0x10133),
				//UnicodeRange(0x10134, 0x10136), // AEGEAN unassigned 2
				UnicodeRange(0x10137, 0x1018E),
				//UnicodeRange(0x1018F, 0x1018F), // NOMISMA unassigned 1
				UnicodeRange(0x10190, 0x1019C),
				//UnicodeRange(0x1019D, 0x1019F), // ASCIA unassigned 1
				UnicodeRange(0x101A0, 0x101A0),
				//UnicodeRange(0x101A1, 0x101CF), // GREEK unassigned 1
				UnicodeRange(0x101D0, 0x101FD),
				//UnicodeRange(0x101FE, 0x1027F), // PHAISTOS unassigned 1
				UnicodeRange(0x10280, 0x1029C),
				//UnicodeRange(0x1029D, 0x1029F), // LYCIAN unassigned 1
				UnicodeRange(0x102A0, 0x102D0),
				//UnicodeRange(0x102D1, 0x102DF), // CARIAN unassigned 1
				UnicodeRange(0x102E0, 0x102FB),
				//UnicodeRange(0x102FC, 0x102FF), // COPTIC unassigned 1
				UnicodeRange(0x10300, 0x10323),
				//UnicodeRange(0x10324, 0x1032C), // OLD unassigned 1
				UnicodeRange(0x1032D, 0x1034A),
				//UnicodeRange(0x1034B, 0x1034F), // GOTHIC unassigned 1
				UnicodeRange(0x10350, 0x1037A),
				//UnicodeRange(0x1037B, 0x1037F), // COMBINING unassigned 1
				UnicodeRange(0x10380, 0x1039D),
				//UnicodeRange(0x1039E, 0x1039E), // UGARITIC unassigned 1
				UnicodeRange(0x1039F, 0x103C3),
				//UnicodeRange(0x103C4, 0x103C7), // OLD unassigned 1
				UnicodeRange(0x103C8, 0x103D5),
				//UnicodeRange(0x103D6, 0x103FF), // OLD unassigned 2
				UnicodeRange(0x10400, 0x1049D),
				//UnicodeRange(0x1049E, 0x1049F), // OSMANYA unassigned 1
				UnicodeRange(0x104A0, 0x104A9),
				//UnicodeRange(0x104AA, 0x104AF), // OSMANYA unassigned 2
				UnicodeRange(0x104B0, 0x104D3),
				//UnicodeRange(0x104D4, 0x104D7), // OSAGE unassigned 1
				UnicodeRange(0x104D8, 0x104FB),
				//UnicodeRange(0x104FC, 0x104FF), // OSAGE unassigned 2
				UnicodeRange(0x10500, 0x10527),
				//UnicodeRange(0x10528, 0x1052F), // ELBASAN unassigned 1
				UnicodeRange(0x10530, 0x10563),
				//UnicodeRange(0x10564, 0x1056E), // CAUCASIAN unassigned 1
				UnicodeRange(0x1056F, 0x1057A),
				//UnicodeRange(0x1057B, 0x1057B), // VITHKUQI unassigned 1
				UnicodeRange(0x1057C, 0x1058A),
				//UnicodeRange(0x1058B, 0x1058B), // VITHKUQI unassigned 2
				UnicodeRange(0x1058C, 0x10592),
				//UnicodeRange(0x10593, 0x10593), // VITHKUQI unassigned 3
				UnicodeRange(0x10594, 0x10595),
				//UnicodeRange(0x10596, 0x10596), // VITHKUQI unassigned 4
				UnicodeRange(0x10597, 0x105A1),
				//UnicodeRange(0x105A2, 0x105A2), // VITHKUQI unassigned 5
				UnicodeRange(0x105A3, 0x105B1),
				//UnicodeRange(0x105B2, 0x105B2), // VITHKUQI unassigned 6
				UnicodeRange(0x105B3, 0x105B9),
				//UnicodeRange(0x105BA, 0x105BA), // VITHKUQI unassigned 7
				UnicodeRange(0x105BB, 0x105BC),
				//UnicodeRange(0x105BD, 0x105BF), // VITHKUQI unassigned 8
				UnicodeRange(0x105C0, 0x105F3),
				//UnicodeRange(0x105F4, 0x105FF), // TODHRI unassigned 1
				UnicodeRange(0x10600, 0x10736),
				//UnicodeRange(0x10737, 0x1073F), // LINEAR unassigned 1
				UnicodeRange(0x10740, 0x10755),
				//UnicodeRange(0x10756, 0x1075F), // LINEAR unassigned 2
				UnicodeRange(0x10760, 0x10767),
				//UnicodeRange(0x10768, 0x1077F), // LINEAR unassigned 3
				UnicodeRange(0x10780, 0x10785),
				//UnicodeRange(0x10786, 0x10786), // MODIFIER unassigned 1
				UnicodeRange(0x10787, 0x107B0),
				//UnicodeRange(0x107B1, 0x107B1), // MODIFIER unassigned 2
				UnicodeRange(0x107B2, 0x107BA),
				//UnicodeRange(0x107BB, 0x107FF), // MODIFIER unassigned 3
				UnicodeRange(0x10800, 0x10805),
				//UnicodeRange(0x10806, 0x10807), // CYPRIOT unassigned 1
				UnicodeRange(0x10808, 0x10808),
				//UnicodeRange(0x10809, 0x10809), // CYPRIOT unassigned 2
				UnicodeRange(0x1080A, 0x10835),
				//UnicodeRange(0x10836, 0x10836), // CYPRIOT unassigned 3
				UnicodeRange(0x10837, 0x10838),
				//UnicodeRange(0x10839, 0x1083B), // CYPRIOT unassigned 4
				UnicodeRange(0x1083C, 0x1083C),
				//UnicodeRange(0x1083D, 0x1083E), // CYPRIOT unassigned 5
				UnicodeRange(0x1083F, 0x10855),
				//UnicodeRange(0x10856, 0x10856), // IMPERIAL unassigned 1
				UnicodeRange(0x10857, 0x1089E),
				//UnicodeRange(0x1089F, 0x108A6), // NABATAEAN unassigned 1
				UnicodeRange(0x108A7, 0x108AF),
				//UnicodeRange(0x108B0, 0x108DF), // NABATAEAN unassigned 2
				UnicodeRange(0x108E0, 0x108F2),
				//UnicodeRange(0x108F3, 0x108F3), // HATRAN unassigned 1
				UnicodeRange(0x108F4, 0x108F5),
				//UnicodeRange(0x108F6, 0x108FA), // HATRAN unassigned 2
				UnicodeRange(0x108FB, 0x1091B),
				//UnicodeRange(0x1091C, 0x1091E), // PHOENICIAN unassigned 1
				UnicodeRange(0x1091F, 0x10939),
				//UnicodeRange(0x1093A, 0x1093E), // LYDIAN unassigned 1
				UnicodeRange(0x1093F, 0x1093F),
				//UnicodeRange(0x10940, 0x1097F), // LYDIAN unassigned 2
				UnicodeRange(0x10980, 0x109B7),
				//UnicodeRange(0x109B8, 0x109BB), // MEROITIC unassigned 1
				UnicodeRange(0x109BC, 0x109CF),
				//UnicodeRange(0x109D0, 0x109D1), // MEROITIC unassigned 2
				UnicodeRange(0x109D2, 0x10A03),
				//UnicodeRange(0x10A04, 0x10A04), // KHAROSHTHI unassigned 1
				UnicodeRange(0x10A05, 0x10A06),
				//UnicodeRange(0x10A07, 0x10A0B), // KHAROSHTHI unassigned 2
				UnicodeRange(0x10A0C, 0x10A13),
				//UnicodeRange(0x10A14, 0x10A14), // KHAROSHTHI unassigned 3
				UnicodeRange(0x10A15, 0x10A17),
				//UnicodeRange(0x10A18, 0x10A18), // KHAROSHTHI unassigned 4
				UnicodeRange(0x10A19, 0x10A35),
				//UnicodeRange(0x10A36, 0x10A37), // KHAROSHTHI unassigned 5
				UnicodeRange(0x10A38, 0x10A3A),
				//UnicodeRange(0x10A3B, 0x10A3E), // KHAROSHTHI unassigned 6
				UnicodeRange(0x10A3F, 0x10A48),
				//UnicodeRange(0x10A49, 0x10A4F), // KHAROSHTHI unassigned 7
				UnicodeRange(0x10A50, 0x10A58),
				//UnicodeRange(0x10A59, 0x10A5F), // KHAROSHTHI unassigned 8
				UnicodeRange(0x10A60, 0x10A9F),
				//UnicodeRange(0x10AA0, 0x10ABF), // OLD unassigned 1
				UnicodeRange(0x10AC0, 0x10AE6),
				//UnicodeRange(0x10AE7, 0x10AEA), // MANICHAEAN unassigned 1
				UnicodeRange(0x10AEB, 0x10AF6),
				//UnicodeRange(0x10AF7, 0x10AFF), // MANICHAEAN unassigned 2
				UnicodeRange(0x10B00, 0x10B35),
				//UnicodeRange(0x10B36, 0x10B38), // AVESTAN unassigned 1
				UnicodeRange(0x10B39, 0x10B55),
				//UnicodeRange(0x10B56, 0x10B57), // INSCRIPTIONAL unassigned 1
				UnicodeRange(0x10B58, 0x10B72),
				//UnicodeRange(0x10B73, 0x10B77), // INSCRIPTIONAL unassigned 2
				UnicodeRange(0x10B78, 0x10B91),
				//UnicodeRange(0x10B92, 0x10B98), // PSALTER unassigned 1
				UnicodeRange(0x10B99, 0x10B9C),
				//UnicodeRange(0x10B9D, 0x10BA8), // PSALTER unassigned 2
				UnicodeRange(0x10BA9, 0x10BAF),
				//UnicodeRange(0x10BB0, 0x10BFF), // PSALTER unassigned 3
				UnicodeRange(0x10C00, 0x10C48),
				//UnicodeRange(0x10C49, 0x10C7F), // OLD unassigned 1
				UnicodeRange(0x10C80, 0x10CB2),
				//UnicodeRange(0x10CB3, 0x10CBF), // OLD unassigned 2
				UnicodeRange(0x10CC0, 0x10CF2),
				//UnicodeRange(0x10CF3, 0x10CF9), // OLD unassigned 3
				UnicodeRange(0x10CFA, 0x10D27),
				//UnicodeRange(0x10D28, 0x10D2F), // HANIFI unassigned 1
				UnicodeRange(0x10D30, 0x10D39),
				//UnicodeRange(0x10D3A, 0x10D3F), // HANIFI unassigned 2
				UnicodeRange(0x10D40, 0x10D65),
				//UnicodeRange(0x10D66, 0x10D68), // GARAY unassigned 1
				UnicodeRange(0x10D69, 0x10D85),
				//UnicodeRange(0x10D86, 0x10D8D), // GARAY unassigned 2
				UnicodeRange(0x10D8E, 0x10D8F),
				//UnicodeRange(0x10D90, 0x10E5F), // GARAY unassigned 3
				UnicodeRange(0x10E60, 0x10E7E),
				//UnicodeRange(0x10E7F, 0x10E7F), // RUMI unassigned 1
				UnicodeRange(0x10E80, 0x10EA9),
				//UnicodeRange(0x10EAA, 0x10EAA), // YEZIDI unassigned 1
				UnicodeRange(0x10EAB, 0x10EAD),
				//UnicodeRange(0x10EAE, 0x10EAF), // YEZIDI unassigned 2
				UnicodeRange(0x10EB0, 0x10EB1),
				//UnicodeRange(0x10EB2, 0x10EC1), // YEZIDI unassigned 3
				UnicodeRange(0x10EC2, 0x10EC4),
				//UnicodeRange(0x10EC5, 0x10EFB), // ARABIC unassigned 1
				UnicodeRange(0x10EFC, 0x10F27),
				//UnicodeRange(0x10F28, 0x10F2F), // OLD unassigned 1
				UnicodeRange(0x10F30, 0x10F59),
				//UnicodeRange(0x10F5A, 0x10F6F), // SOGDIAN unassigned 1
				UnicodeRange(0x10F70, 0x10F89),
				//UnicodeRange(0x10F8A, 0x10FAF), // OLD unassigned 1
				UnicodeRange(0x10FB0, 0x10FCB),
				//UnicodeRange(0x10FCC, 0x10FDF), // CHORASMIAN unassigned 1
				UnicodeRange(0x10FE0, 0x10FF6),
				//UnicodeRange(0x10FF7, 0x10FFF), // ELYMAIC unassigned 1
				UnicodeRange(0x11000, 0x1104D),
				//UnicodeRange(0x1104E, 0x11051), // BRAHMI unassigned 1
				UnicodeRange(0x11052, 0x11075),
				//UnicodeRange(0x11076, 0x1107E), // BRAHMI unassigned 2
				UnicodeRange(0x1107F, 0x110C2),
				//UnicodeRange(0x110C3, 0x110CC), // KAITHI unassigned 1
				UnicodeRange(0x110CD, 0x110CD),
				//UnicodeRange(0x110CE, 0x110CF), // KAITHI unassigned 2
				UnicodeRange(0x110D0, 0x110E8),
				//UnicodeRange(0x110E9, 0x110EF), // SORA unassigned 1
				UnicodeRange(0x110F0, 0x110F9),
				//UnicodeRange(0x110FA, 0x110FF), // SORA unassigned 2
				UnicodeRange(0x11100, 0x11134),
				//UnicodeRange(0x11135, 0x11135), // CHAKMA unassigned 1
				UnicodeRange(0x11136, 0x11147),
				//UnicodeRange(0x11148, 0x1114F), // CHAKMA unassigned 2
				UnicodeRange(0x11150, 0x11176),
				//UnicodeRange(0x11177, 0x1117F), // MAHAJANI unassigned 1
				UnicodeRange(0x11180, 0x111DF),
				//UnicodeRange(0x111E0, 0x111E0), // SHARADA unassigned 1
				UnicodeRange(0x111E1, 0x111F4),
				//UnicodeRange(0x111F5, 0x111FF), // SINHALA unassigned 1
				UnicodeRange(0x11200, 0x11211),
				//UnicodeRange(0x11212, 0x11212), // KHOJKI unassigned 1
				UnicodeRange(0x11213, 0x11241),
				//UnicodeRange(0x11242, 0x1127F), // KHOJKI unassigned 2
				UnicodeRange(0x11280, 0x11286),
				//UnicodeRange(0x11287, 0x11287), // MULTANI unassigned 1
				UnicodeRange(0x11288, 0x11288),
				//UnicodeRange(0x11289, 0x11289), // MULTANI unassigned 2
				UnicodeRange(0x1128A, 0x1128D),
				//UnicodeRange(0x1128E, 0x1128E), // MULTANI unassigned 3
				UnicodeRange(0x1128F, 0x1129D),
				//UnicodeRange(0x1129E, 0x1129E), // MULTANI unassigned 4
				UnicodeRange(0x1129F, 0x112A9),
				//UnicodeRange(0x112AA, 0x112AF), // MULTANI unassigned 5
				UnicodeRange(0x112B0, 0x112EA),
				//UnicodeRange(0x112EB, 0x112EF), // KHUDAWADI unassigned 1
				UnicodeRange(0x112F0, 0x112F9),
				//UnicodeRange(0x112FA, 0x112FF), // KHUDAWADI unassigned 2
				UnicodeRange(0x11300, 0x11303),
				//UnicodeRange(0x11304, 0x11304), // GRANTHA unassigned 1
				UnicodeRange(0x11305, 0x1130C),
				//UnicodeRange(0x1130D, 0x1130E), // GRANTHA unassigned 2
				UnicodeRange(0x1130F, 0x11310),
				//UnicodeRange(0x11311, 0x11312), // GRANTHA unassigned 3
				UnicodeRange(0x11313, 0x11328),
				//UnicodeRange(0x11329, 0x11329), // GRANTHA unassigned 4
				UnicodeRange(0x1132A, 0x11330),
				//UnicodeRange(0x11331, 0x11331), // GRANTHA unassigned 5
				UnicodeRange(0x11332, 0x11333),
				//UnicodeRange(0x11334, 0x11334), // GRANTHA unassigned 6
				UnicodeRange(0x11335, 0x11339),
				//UnicodeRange(0x1133A, 0x1133A), // GRANTHA unassigned 7
				UnicodeRange(0x1133B, 0x11344),
				//UnicodeRange(0x11345, 0x11346), // GRANTHA unassigned 8
				UnicodeRange(0x11347, 0x11348),
				//UnicodeRange(0x11349, 0x1134A), // GRANTHA unassigned 9
				UnicodeRange(0x1134B, 0x1134D),
				//UnicodeRange(0x1134E, 0x1134F), // GRANTHA unassigned 10
				UnicodeRange(0x11350, 0x11350),
				//UnicodeRange(0x11351, 0x11356), // GRANTHA unassigned 11
				UnicodeRange(0x11357, 0x11357),
				//UnicodeRange(0x11358, 0x1135C), // GRANTHA unassigned 12
				UnicodeRange(0x1135D, 0x11363),
				//UnicodeRange(0x11364, 0x11365), // GRANTHA unassigned 13
				UnicodeRange(0x11366, 0x1136C),
				//UnicodeRange(0x1136D, 0x1136F), // COMBINING unassigned 1
				UnicodeRange(0x11370, 0x11374),
				//UnicodeRange(0x11375, 0x1137F), // COMBINING unassigned 2
				UnicodeRange(0x11380, 0x11389),
				//UnicodeRange(0x1138A, 0x1138A), // TULU-TIGALARI unassigned 1
				UnicodeRange(0x1138B, 0x1138B),
				//UnicodeRange(0x1138C, 0x1138D), // TULU-TIGALARI unassigned 2
				UnicodeRange(0x1138E, 0x1138E),
				//UnicodeRange(0x1138F, 0x1138F), // TULU-TIGALARI unassigned 3
				UnicodeRange(0x11390, 0x113B5),
				//UnicodeRange(0x113B6, 0x113B6), // TULU-TIGALARI unassigned 4
				UnicodeRange(0x113B7, 0x113C0),
				//UnicodeRange(0x113C1, 0x113C1), // TULU-TIGALARI unassigned 5
				UnicodeRange(0x113C2, 0x113C2),
				//UnicodeRange(0x113C3, 0x113C4), // TULU-TIGALARI unassigned 6
				UnicodeRange(0x113C5, 0x113C5),
				//UnicodeRange(0x113C6, 0x113C6), // TULU-TIGALARI unassigned 7
				UnicodeRange(0x113C7, 0x113CA),
				//UnicodeRange(0x113CB, 0x113CB), // TULU-TIGALARI unassigned 8
				UnicodeRange(0x113CC, 0x113D5),
				//UnicodeRange(0x113D6, 0x113D6), // TULU-TIGALARI unassigned 9
				UnicodeRange(0x113D7, 0x113D8),
				//UnicodeRange(0x113D9, 0x113E0), // TULU-TIGALARI unassigned 10
				UnicodeRange(0x113E1, 0x113E2),
				//UnicodeRange(0x113E3, 0x113FF), // TULU-TIGALARI unassigned 11
				UnicodeRange(0x11400, 0x1145B),
				//UnicodeRange(0x1145C, 0x1145C), // NEWA unassigned 1
				UnicodeRange(0x1145D, 0x11461),
				//UnicodeRange(0x11462, 0x1147F), // NEWA unassigned 2
				UnicodeRange(0x11480, 0x114C7),
				//UnicodeRange(0x114C8, 0x114CF), // TIRHUTA unassigned 1
				UnicodeRange(0x114D0, 0x114D9),
				//UnicodeRange(0x114DA, 0x1157F), // TIRHUTA unassigned 2
				UnicodeRange(0x11580, 0x115B5),
				//UnicodeRange(0x115B6, 0x115B7), // SIDDHAM unassigned 1
				UnicodeRange(0x115B8, 0x115DD),
				//UnicodeRange(0x115DE, 0x115FF), // SIDDHAM unassigned 2
				UnicodeRange(0x11600, 0x11644),
				//UnicodeRange(0x11645, 0x1164F), // MODI unassigned 1
				UnicodeRange(0x11650, 0x11659),
				//UnicodeRange(0x1165A, 0x1165F), // MODI unassigned 2
				UnicodeRange(0x11660, 0x1166C),
				//UnicodeRange(0x1166D, 0x1167F), // MONGOLIAN unassigned 1
				UnicodeRange(0x11680, 0x116B9),
				//UnicodeRange(0x116BA, 0x116BF), // TAKRI unassigned 1
				UnicodeRange(0x116C0, 0x116C9),
				//UnicodeRange(0x116CA, 0x116CF), // TAKRI unassigned 2
				UnicodeRange(0x116D0, 0x116E3),
				//UnicodeRange(0x116E4, 0x116FF), // MYANMAR unassigned 1
				UnicodeRange(0x11700, 0x1171A),
				//UnicodeRange(0x1171B, 0x1171C), // AHOM unassigned 1
				UnicodeRange(0x1171D, 0x1172B),
				//UnicodeRange(0x1172C, 0x1172F), // AHOM unassigned 2
				UnicodeRange(0x11730, 0x11746),
				//UnicodeRange(0x11747, 0x117FF), // AHOM unassigned 3
				UnicodeRange(0x11800, 0x1183B),
				//UnicodeRange(0x1183C, 0x1189F), // DOGRA unassigned 1
				UnicodeRange(0x118A0, 0x118F2),
				//UnicodeRange(0x118F3, 0x118FE), // WARANG unassigned 1
				UnicodeRange(0x118FF, 0x11906),
				//UnicodeRange(0x11907, 0x11908), // DIVES unassigned 1
				UnicodeRange(0x11909, 0x11909),
				//UnicodeRange(0x1190A, 0x1190B), // DIVES unassigned 2
				UnicodeRange(0x1190C, 0x11913),
				//UnicodeRange(0x11914, 0x11914), // DIVES unassigned 3
				UnicodeRange(0x11915, 0x11916),
				//UnicodeRange(0x11917, 0x11917), // DIVES unassigned 4
				UnicodeRange(0x11918, 0x11935),
				//UnicodeRange(0x11936, 0x11936), // DIVES unassigned 5
				UnicodeRange(0x11937, 0x11938),
				//UnicodeRange(0x11939, 0x1193A), // DIVES unassigned 6
				UnicodeRange(0x1193B, 0x11946),
				//UnicodeRange(0x11947, 0x1194F), // DIVES unassigned 7
				UnicodeRange(0x11950, 0x11959),
				//UnicodeRange(0x1195A, 0x1199F), // DIVES unassigned 8
				UnicodeRange(0x119A0, 0x119A7),
				//UnicodeRange(0x119A8, 0x119A9), // NANDINAGARI unassigned 1
				UnicodeRange(0x119AA, 0x119D7),
				//UnicodeRange(0x119D8, 0x119D9), // NANDINAGARI unassigned 2
				UnicodeRange(0x119DA, 0x119E4),
				//UnicodeRange(0x119E5, 0x119FF), // NANDINAGARI unassigned 3
				UnicodeRange(0x11A00, 0x11A47),
				//UnicodeRange(0x11A48, 0x11A4F), // ZANABAZAR unassigned 1
				UnicodeRange(0x11A50, 0x11AA2),
				//UnicodeRange(0x11AA3, 0x11AAF), // SOYOMBO unassigned 1
				UnicodeRange(0x11AB0, 0x11AF8),
				//UnicodeRange(0x11AF9, 0x11AFF), // PAU unassigned 1
				UnicodeRange(0x11B00, 0x11B09),
				//UnicodeRange(0x11B0A, 0x11BBF), // DEVANAGARI unassigned 1
				UnicodeRange(0x11BC0, 0x11BE1),
				//UnicodeRange(0x11BE2, 0x11BEF), // SUNUWAR unassigned 1
				UnicodeRange(0x11BF0, 0x11BF9),
				//UnicodeRange(0x11BFA, 0x11BFF), // SUNUWAR unassigned 2
				UnicodeRange(0x11C00, 0x11C08),
				//UnicodeRange(0x11C09, 0x11C09), // BHAIKSUKI unassigned 1
				UnicodeRange(0x11C0A, 0x11C36),
				//UnicodeRange(0x11C37, 0x11C37), // BHAIKSUKI unassigned 2
				UnicodeRange(0x11C38, 0x11C45),
				//UnicodeRange(0x11C46, 0x11C4F), // BHAIKSUKI unassigned 3
				UnicodeRange(0x11C50, 0x11C6C),
				//UnicodeRange(0x11C6D, 0x11C6F), // BHAIKSUKI unassigned 4
				UnicodeRange(0x11C70, 0x11C8F),
				//UnicodeRange(0x11C90, 0x11C91), // MARCHEN unassigned 1
				UnicodeRange(0x11C92, 0x11CA7),
				//UnicodeRange(0x11CA8, 0x11CA8), // MARCHEN unassigned 2
				UnicodeRange(0x11CA9, 0x11CB6),
				//UnicodeRange(0x11CB7, 0x11CFF), // MARCHEN unassigned 3
				UnicodeRange(0x11D00, 0x11D06),
				//UnicodeRange(0x11D07, 0x11D07), // MASARAM unassigned 1
				UnicodeRange(0x11D08, 0x11D09),
				//UnicodeRange(0x11D0A, 0x11D0A), // MASARAM unassigned 2
				UnicodeRange(0x11D0B, 0x11D36),
				//UnicodeRange(0x11D37, 0x11D39), // MASARAM unassigned 3
				UnicodeRange(0x11D3A, 0x11D3A),
				//UnicodeRange(0x11D3B, 0x11D3B), // MASARAM unassigned 4
				UnicodeRange(0x11D3C, 0x11D3D),
				//UnicodeRange(0x11D3E, 0x11D3E), // MASARAM unassigned 5
				UnicodeRange(0x11D3F, 0x11D47),
				//UnicodeRange(0x11D48, 0x11D4F), // MASARAM unassigned 6
				UnicodeRange(0x11D50, 0x11D59),
				//UnicodeRange(0x11D5A, 0x11D5F), // MASARAM unassigned 7
				UnicodeRange(0x11D60, 0x11D65),
				//UnicodeRange(0x11D66, 0x11D66), // GUNJALA unassigned 1
				UnicodeRange(0x11D67, 0x11D68),
				//UnicodeRange(0x11D69, 0x11D69), // GUNJALA unassigned 2
				UnicodeRange(0x11D6A, 0x11D8E),
				//UnicodeRange(0x11D8F, 0x11D8F), // GUNJALA unassigned 3
				UnicodeRange(0x11D90, 0x11D91),
				//UnicodeRange(0x11D92, 0x11D92), // GUNJALA unassigned 4
				UnicodeRange(0x11D93, 0x11D98),
				//UnicodeRange(0x11D99, 0x11D9F), // GUNJALA unassigned 5
				UnicodeRange(0x11DA0, 0x11DA9),
				//UnicodeRange(0x11DAA, 0x11EDF), // GUNJALA unassigned 6
				UnicodeRange(0x11EE0, 0x11EF8),
				//UnicodeRange(0x11EF9, 0x11EFF), // MAKASAR unassigned 1
				UnicodeRange(0x11F00, 0x11F10),
				//UnicodeRange(0x11F11, 0x11F11), // KAWI unassigned 1
				UnicodeRange(0x11F12, 0x11F3A),
				//UnicodeRange(0x11F3B, 0x11F3D), // KAWI unassigned 2
				UnicodeRange(0x11F3E, 0x11F5A),
				//UnicodeRange(0x11F5B, 0x11FAF), // KAWI unassigned 3
				UnicodeRange(0x11FB0, 0x11FB0),
				//UnicodeRange(0x11FB1, 0x11FBF), // LISU unassigned 1
				UnicodeRange(0x11FC0, 0x11FF1),
				//UnicodeRange(0x11FF2, 0x11FFE), // TAMIL unassigned 1
				UnicodeRange(0x11FFF, 0x12399),
				//UnicodeRange(0x1239A, 0x123FF), // CUNEIFORM unassigned 1
				UnicodeRange(0x12400, 0x1246E),
				//UnicodeRange(0x1246F, 0x1246F), // CUNEIFORM unassigned 2
				UnicodeRange(0x12470, 0x12474),
				//UnicodeRange(0x12475, 0x1247F), // CUNEIFORM unassigned 3
				UnicodeRange(0x12480, 0x12543),
				//UnicodeRange(0x12544, 0x12F8F), // CUNEIFORM unassigned 4
				UnicodeRange(0x12F90, 0x12FF2),
				//UnicodeRange(0x12FF3, 0x12FFF), // CYPRO-MINOAN unassigned 1
				UnicodeRange(0x13000, 0x13455),
				//UnicodeRange(0x13456, 0x1345F), // EGYPTIAN unassigned 1
				UnicodeRange(0x13460, 0x143FA),
				//UnicodeRange(0x143FB, 0x143FF), // EGYPTIAN unassigned 2
				UnicodeRange(0x14400, 0x14646),
				//UnicodeRange(0x14647, 0x160FF), // ANATOLIAN unassigned 1
				UnicodeRange(0x16100, 0x16139),
				//UnicodeRange(0x1613A, 0x167FF), // GURUNG unassigned 1
				UnicodeRange(0x16800, 0x16A38),
				//UnicodeRange(0x16A39, 0x16A3F), // BAMUM unassigned 1
				UnicodeRange(0x16A40, 0x16A5E),
				//UnicodeRange(0x16A5F, 0x16A5F), // MRO unassigned 1
				UnicodeRange(0x16A60, 0x16A69),
				//UnicodeRange(0x16A6A, 0x16A6D), // MRO unassigned 2
				UnicodeRange(0x16A6E, 0x16ABE),
				//UnicodeRange(0x16ABF, 0x16ABF), // TANGSA unassigned 1
				UnicodeRange(0x16AC0, 0x16AC9),
				//UnicodeRange(0x16ACA, 0x16ACF), // TANGSA unassigned 2
				UnicodeRange(0x16AD0, 0x16AED),
				//UnicodeRange(0x16AEE, 0x16AEF), // BASSA unassigned 1
				UnicodeRange(0x16AF0, 0x16AF5),
				//UnicodeRange(0x16AF6, 0x16AFF), // BASSA unassigned 2
				UnicodeRange(0x16B00, 0x16B45),
				//UnicodeRange(0x16B46, 0x16B4F), // PAHAWH unassigned 1
				UnicodeRange(0x16B50, 0x16B59),
				//UnicodeRange(0x16B5A, 0x16B5A), // PAHAWH unassigned 2
				UnicodeRange(0x16B5B, 0x16B61),
				//UnicodeRange(0x16B62, 0x16B62), // PAHAWH unassigned 3
				UnicodeRange(0x16B63, 0x16B77),
				//UnicodeRange(0x16B78, 0x16B7C), // PAHAWH unassigned 4
				UnicodeRange(0x16B7D, 0x16B8F),
				//UnicodeRange(0x16B90, 0x16D3F), // PAHAWH unassigned 5
				UnicodeRange(0x16D40, 0x16D79),
				//UnicodeRange(0x16D7A, 0x16E3F), // KIRAT unassigned 1
				UnicodeRange(0x16E40, 0x16E9A),
				//UnicodeRange(0x16E9B, 0x16EFF), // MEDEFAIDRIN unassigned 1
				UnicodeRange(0x16F00, 0x16F4A),
				//UnicodeRange(0x16F4B, 0x16F4E), // MIAO unassigned 1
				UnicodeRange(0x16F4F, 0x16F87),
				//UnicodeRange(0x16F88, 0x16F8E), // MIAO unassigned 2
				UnicodeRange(0x16F8F, 0x16F9F),
				//UnicodeRange(0x16FA0, 0x16FDF), // MIAO unassigned 3
				UnicodeRange(0x16FE0, 0x16FE4),
				//UnicodeRange(0x16FE5, 0x16FEF), // KHITAN unassigned 1
				UnicodeRange(0x16FF0, 0x16FF1),
				//UnicodeRange(0x16FF2, 0x16FFF), // VIETNAMESE unassigned 1
				UnicodeRange(0x17000, 0x187F7),
				//UnicodeRange(0x187F8, 0x187FF), // Tangut Ideograph unassigned 1
				UnicodeRange(0x18800, 0x18CD5),
				//UnicodeRange(0x18CD6, 0x18CFE), // KHITAN unassigned 1
				UnicodeRange(0x18CFF, 0x18D08),
				//UnicodeRange(0x18D09, 0x1AFEF), // Tangut Ideograph Supplement unassigned 1
				UnicodeRange(0x1AFF0, 0x1AFF3),
				//UnicodeRange(0x1AFF4, 0x1AFF4), // KATAKANA unassigned 1
				UnicodeRange(0x1AFF5, 0x1AFFB),
				//UnicodeRange(0x1AFFC, 0x1AFFC), // KATAKANA unassigned 2
				UnicodeRange(0x1AFFD, 0x1AFFE),
				//UnicodeRange(0x1AFFF, 0x1AFFF), // KATAKANA unassigned 3
				UnicodeRange(0x1B000, 0x1B122),
				//UnicodeRange(0x1B123, 0x1B131), // KATAKANA unassigned 4
				UnicodeRange(0x1B132, 0x1B132),
				//UnicodeRange(0x1B133, 0x1B14F), // HIRAGANA unassigned 1
				UnicodeRange(0x1B150, 0x1B152),
				//UnicodeRange(0x1B153, 0x1B154), // HIRAGANA unassigned 2
				UnicodeRange(0x1B155, 0x1B155),
				//UnicodeRange(0x1B156, 0x1B163), // KATAKANA unassigned 1
				UnicodeRange(0x1B164, 0x1B167),
				//UnicodeRange(0x1B168, 0x1B16F), // KATAKANA unassigned 2
				UnicodeRange(0x1B170, 0x1B2FB),
				//UnicodeRange(0x1B2FC, 0x1BBFF), // NUSHU unassigned 1
				UnicodeRange(0x1BC00, 0x1BC6A),
				//UnicodeRange(0x1BC6B, 0x1BC6F), // DUPLOYAN unassigned 1
				UnicodeRange(0x1BC70, 0x1BC7C),
				//UnicodeRange(0x1BC7D, 0x1BC7F), // DUPLOYAN unassigned 2
				UnicodeRange(0x1BC80, 0x1BC88),
				//UnicodeRange(0x1BC89, 0x1BC8F), // DUPLOYAN unassigned 3
				UnicodeRange(0x1BC90, 0x1BC99),
				//UnicodeRange(0x1BC9A, 0x1BC9B), // DUPLOYAN unassigned 4
				UnicodeRange(0x1BC9C, 0x1BCA3),
				//UnicodeRange(0x1BCA4, 0x1CBFF), // SHORTHAND unassigned 1
				UnicodeRange(0x1CC00, 0x1CCF9),
				//UnicodeRange(0x1CCFA, 0x1CCFF), // OUTLINED unassigned 1
				UnicodeRange(0x1CD00, 0x1CEB3),
				//UnicodeRange(0x1CEB4, 0x1CEFF), // BLACK unassigned 1
				UnicodeRange(0x1CF00, 0x1CF2D),
				//UnicodeRange(0x1CF2E, 0x1CF2F), // ZNAMENNY unassigned 1
				UnicodeRange(0x1CF30, 0x1CF46),
				//UnicodeRange(0x1CF47, 0x1CF4F), // ZNAMENNY unassigned 2
				UnicodeRange(0x1CF50, 0x1CFC3),
				//UnicodeRange(0x1CFC4, 0x1CFFF), // ZNAMENNY unassigned 3
				UnicodeRange(0x1D000, 0x1D0F5),
				//UnicodeRange(0x1D0F6, 0x1D0FF), // BYZANTINE unassigned 1
				UnicodeRange(0x1D100, 0x1D126),
				//UnicodeRange(0x1D127, 0x1D128), // MUSICAL unassigned 1
				UnicodeRange(0x1D129, 0x1D1EA),
				//UnicodeRange(0x1D1EB, 0x1D1FF), // MUSICAL unassigned 2
				UnicodeRange(0x1D200, 0x1D245),
				//UnicodeRange(0x1D246, 0x1D2BF), // GREEK unassigned 1
				UnicodeRange(0x1D2C0, 0x1D2D3),
				//UnicodeRange(0x1D2D4, 0x1D2DF), // KAKTOVIK unassigned 1
				UnicodeRange(0x1D2E0, 0x1D2F3),
				//UnicodeRange(0x1D2F4, 0x1D2FF), // MAYAN unassigned 1
				UnicodeRange(0x1D300, 0x1D356),
				//UnicodeRange(0x1D357, 0x1D35F), // TETRAGRAM unassigned 1
				UnicodeRange(0x1D360, 0x1D378),
				//UnicodeRange(0x1D379, 0x1D3FF), // TALLY unassigned 1
				UnicodeRange(0x1D400, 0x1D454),
				//UnicodeRange(0x1D455, 0x1D455), // MATHEMATICAL unassigned 1
				UnicodeRange(0x1D456, 0x1D49C),
				//UnicodeRange(0x1D49D, 0x1D49D), // MATHEMATICAL unassigned 2
				UnicodeRange(0x1D49E, 0x1D49F),
				//UnicodeRange(0x1D4A0, 0x1D4A1), // MATHEMATICAL unassigned 3
				UnicodeRange(0x1D4A2, 0x1D4A2),
				//UnicodeRange(0x1D4A3, 0x1D4A4), // MATHEMATICAL unassigned 4
				UnicodeRange(0x1D4A5, 0x1D4A6),
				//UnicodeRange(0x1D4A7, 0x1D4A8), // MATHEMATICAL unassigned 5
				UnicodeRange(0x1D4A9, 0x1D4AC),
				//UnicodeRange(0x1D4AD, 0x1D4AD), // MATHEMATICAL unassigned 6
				UnicodeRange(0x1D4AE, 0x1D4B9),
				//UnicodeRange(0x1D4BA, 0x1D4BA), // MATHEMATICAL unassigned 7
				UnicodeRange(0x1D4BB, 0x1D4BB),
				//UnicodeRange(0x1D4BC, 0x1D4BC), // MATHEMATICAL unassigned 8
				UnicodeRange(0x1D4BD, 0x1D4C3),
				//UnicodeRange(0x1D4C4, 0x1D4C4), // MATHEMATICAL unassigned 9
				UnicodeRange(0x1D4C5, 0x1D505),
				//UnicodeRange(0x1D506, 0x1D506), // MATHEMATICAL unassigned 10
				UnicodeRange(0x1D507, 0x1D50A),
				//UnicodeRange(0x1D50B, 0x1D50C), // MATHEMATICAL unassigned 11
				UnicodeRange(0x1D50D, 0x1D514),
				//UnicodeRange(0x1D515, 0x1D515), // MATHEMATICAL unassigned 12
				UnicodeRange(0x1D516, 0x1D51C),
				//UnicodeRange(0x1D51D, 0x1D51D), // MATHEMATICAL unassigned 13
				UnicodeRange(0x1D51E, 0x1D539),
				//UnicodeRange(0x1D53A, 0x1D53A), // MATHEMATICAL unassigned 14
				UnicodeRange(0x1D53B, 0x1D53E),
				//UnicodeRange(0x1D53F, 0x1D53F), // MATHEMATICAL unassigned 15
				UnicodeRange(0x1D540, 0x1D544),
				//UnicodeRange(0x1D545, 0x1D545), // MATHEMATICAL unassigned 16
				UnicodeRange(0x1D546, 0x1D546),
				//UnicodeRange(0x1D547, 0x1D549), // MATHEMATICAL unassigned 17
				UnicodeRange(0x1D54A, 0x1D550),
				//UnicodeRange(0x1D551, 0x1D551), // MATHEMATICAL unassigned 18
				UnicodeRange(0x1D552, 0x1D6A5),
				//UnicodeRange(0x1D6A6, 0x1D6A7), // MATHEMATICAL unassigned 19
				UnicodeRange(0x1D6A8, 0x1D7CB),
				//UnicodeRange(0x1D7CC, 0x1D7CD), // MATHEMATICAL unassigned 20
				UnicodeRange(0x1D7CE, 0x1DA8B),
				//UnicodeRange(0x1DA8C, 0x1DA9A), // SIGNWRITING unassigned 1
				UnicodeRange(0x1DA9B, 0x1DA9F),
				//UnicodeRange(0x1DAA0, 0x1DAA0), // SIGNWRITING unassigned 2
				UnicodeRange(0x1DAA1, 0x1DAAF),
				//UnicodeRange(0x1DAB0, 0x1DEFF), // SIGNWRITING unassigned 3
				UnicodeRange(0x1DF00, 0x1DF1E),
				//UnicodeRange(0x1DF1F, 0x1DF24), // LATIN unassigned 1
				UnicodeRange(0x1DF25, 0x1DF2A),
				//UnicodeRange(0x1DF2B, 0x1DFFF), // LATIN unassigned 2
				UnicodeRange(0x1E000, 0x1E006),
				//UnicodeRange(0x1E007, 0x1E007), // COMBINING unassigned 1
				UnicodeRange(0x1E008, 0x1E018),
				//UnicodeRange(0x1E019, 0x1E01A), // COMBINING unassigned 2
				UnicodeRange(0x1E01B, 0x1E021),
				//UnicodeRange(0x1E022, 0x1E022), // COMBINING unassigned 3
				UnicodeRange(0x1E023, 0x1E024),
				//UnicodeRange(0x1E025, 0x1E025), // COMBINING unassigned 4
				UnicodeRange(0x1E026, 0x1E02A),
				//UnicodeRange(0x1E02B, 0x1E02F), // COMBINING unassigned 5
				UnicodeRange(0x1E030, 0x1E06D),
				//UnicodeRange(0x1E06E, 0x1E08E), // MODIFIER unassigned 1
				UnicodeRange(0x1E08F, 0x1E08F),
				//UnicodeRange(0x1E090, 0x1E0FF), // COMBINING unassigned 1
				UnicodeRange(0x1E100, 0x1E12C),
				//UnicodeRange(0x1E12D, 0x1E12F), // NYIAKENG unassigned 1
				UnicodeRange(0x1E130, 0x1E13D),
				//UnicodeRange(0x1E13E, 0x1E13F), // NYIAKENG unassigned 2
				UnicodeRange(0x1E140, 0x1E149),
				//UnicodeRange(0x1E14A, 0x1E14D), // NYIAKENG unassigned 3
				UnicodeRange(0x1E14E, 0x1E14F),
				//UnicodeRange(0x1E150, 0x1E28F), // NYIAKENG unassigned 4
				UnicodeRange(0x1E290, 0x1E2AE),
				//UnicodeRange(0x1E2AF, 0x1E2BF), // TOTO unassigned 1
				UnicodeRange(0x1E2C0, 0x1E2F9),
				//UnicodeRange(0x1E2FA, 0x1E2FE), // WANCHO unassigned 1
				UnicodeRange(0x1E2FF, 0x1E2FF),
				//UnicodeRange(0x1E300, 0x1E4CF), // WANCHO unassigned 2
				UnicodeRange(0x1E4D0, 0x1E4F9),
				//UnicodeRange(0x1E4FA, 0x1E5CF), // NAG unassigned 1
				UnicodeRange(0x1E5D0, 0x1E5FA),
				//UnicodeRange(0x1E5FB, 0x1E5FE), // OL unassigned 1
				UnicodeRange(0x1E5FF, 0x1E5FF),
				//UnicodeRange(0x1E600, 0x1E7DF), // OL unassigned 2
				UnicodeRange(0x1E7E0, 0x1E7E6),
				//UnicodeRange(0x1E7E7, 0x1E7E7), // ETHIOPIC unassigned 1
				UnicodeRange(0x1E7E8, 0x1E7EB),
				//UnicodeRange(0x1E7EC, 0x1E7EC), // ETHIOPIC unassigned 2
				UnicodeRange(0x1E7ED, 0x1E7EE),
				//UnicodeRange(0x1E7EF, 0x1E7EF), // ETHIOPIC unassigned 3
				UnicodeRange(0x1E7F0, 0x1E7FE),
				//UnicodeRange(0x1E7FF, 0x1E7FF), // ETHIOPIC unassigned 4
				UnicodeRange(0x1E800, 0x1E8C4),
				//UnicodeRange(0x1E8C5, 0x1E8C6), // MENDE unassigned 1
				UnicodeRange(0x1E8C7, 0x1E8D6),
				//UnicodeRange(0x1E8D7, 0x1E8FF), // MENDE unassigned 2
				UnicodeRange(0x1E900, 0x1E94B),
				//UnicodeRange(0x1E94C, 0x1E94F), // ADLAM unassigned 1
				UnicodeRange(0x1E950, 0x1E959),
				//UnicodeRange(0x1E95A, 0x1E95D), // ADLAM unassigned 2
				UnicodeRange(0x1E95E, 0x1E95F),
				//UnicodeRange(0x1E960, 0x1EC70), // ADLAM unassigned 3
				UnicodeRange(0x1EC71, 0x1ECB4),
				//UnicodeRange(0x1ECB5, 0x1ED00), // INDIC unassigned 1
				UnicodeRange(0x1ED01, 0x1ED3D),
				//UnicodeRange(0x1ED3E, 0x1EDFF), // OTTOMAN unassigned 1
				UnicodeRange(0x1EE00, 0x1EE03),
				//UnicodeRange(0x1EE04, 0x1EE04), // ARABIC unassigned 1
				UnicodeRange(0x1EE05, 0x1EE1F),
				//UnicodeRange(0x1EE20, 0x1EE20), // ARABIC unassigned 2
				UnicodeRange(0x1EE21, 0x1EE22),
				//UnicodeRange(0x1EE23, 0x1EE23), // ARABIC unassigned 3
				UnicodeRange(0x1EE24, 0x1EE24),
				//UnicodeRange(0x1EE25, 0x1EE26), // ARABIC unassigned 4
				UnicodeRange(0x1EE27, 0x1EE27),
				//UnicodeRange(0x1EE28, 0x1EE28), // ARABIC unassigned 5
				UnicodeRange(0x1EE29, 0x1EE32),
				//UnicodeRange(0x1EE33, 0x1EE33), // ARABIC unassigned 6
				UnicodeRange(0x1EE34, 0x1EE37),
				//UnicodeRange(0x1EE38, 0x1EE38), // ARABIC unassigned 7
				UnicodeRange(0x1EE39, 0x1EE39),
				//UnicodeRange(0x1EE3A, 0x1EE3A), // ARABIC unassigned 8
				UnicodeRange(0x1EE3B, 0x1EE3B),
				//UnicodeRange(0x1EE3C, 0x1EE41), // ARABIC unassigned 9
				UnicodeRange(0x1EE42, 0x1EE42),
				//UnicodeRange(0x1EE43, 0x1EE46), // ARABIC unassigned 10
				UnicodeRange(0x1EE47, 0x1EE47),
				//UnicodeRange(0x1EE48, 0x1EE48), // ARABIC unassigned 11
				UnicodeRange(0x1EE49, 0x1EE49),
				//UnicodeRange(0x1EE4A, 0x1EE4A), // ARABIC unassigned 12
				UnicodeRange(0x1EE4B, 0x1EE4B),
				//UnicodeRange(0x1EE4C, 0x1EE4C), // ARABIC unassigned 13
				UnicodeRange(0x1EE4D, 0x1EE4F),
				//UnicodeRange(0x1EE50, 0x1EE50), // ARABIC unassigned 14
				UnicodeRange(0x1EE51, 0x1EE52),
				//UnicodeRange(0x1EE53, 0x1EE53), // ARABIC unassigned 15
				UnicodeRange(0x1EE54, 0x1EE54),
				//UnicodeRange(0x1EE55, 0x1EE56), // ARABIC unassigned 16
				UnicodeRange(0x1EE57, 0x1EE57),
				//UnicodeRange(0x1EE58, 0x1EE58), // ARABIC unassigned 17
				UnicodeRange(0x1EE59, 0x1EE59),
				//UnicodeRange(0x1EE5A, 0x1EE5A), // ARABIC unassigned 18
				UnicodeRange(0x1EE5B, 0x1EE5B),
				//UnicodeRange(0x1EE5C, 0x1EE5C), // ARABIC unassigned 19
				UnicodeRange(0x1EE5D, 0x1EE5D),
				//UnicodeRange(0x1EE5E, 0x1EE5E), // ARABIC unassigned 20
				UnicodeRange(0x1EE5F, 0x1EE5F),
				//UnicodeRange(0x1EE60, 0x1EE60), // ARABIC unassigned 21
				UnicodeRange(0x1EE61, 0x1EE62),
				//UnicodeRange(0x1EE63, 0x1EE63), // ARABIC unassigned 22
				UnicodeRange(0x1EE64, 0x1EE64),
				//UnicodeRange(0x1EE65, 0x1EE66), // ARABIC unassigned 23
				UnicodeRange(0x1EE67, 0x1EE6A),
				//UnicodeRange(0x1EE6B, 0x1EE6B), // ARABIC unassigned 24
				UnicodeRange(0x1EE6C, 0x1EE72),
				//UnicodeRange(0x1EE73, 0x1EE73), // ARABIC unassigned 25
				UnicodeRange(0x1EE74, 0x1EE77),
				//UnicodeRange(0x1EE78, 0x1EE78), // ARABIC unassigned 26
				UnicodeRange(0x1EE79, 0x1EE7C),
				//UnicodeRange(0x1EE7D, 0x1EE7D), // ARABIC unassigned 27
				UnicodeRange(0x1EE7E, 0x1EE7E),
				//UnicodeRange(0x1EE7F, 0x1EE7F), // ARABIC unassigned 28
				UnicodeRange(0x1EE80, 0x1EE89),
				//UnicodeRange(0x1EE8A, 0x1EE8A), // ARABIC unassigned 29
				UnicodeRange(0x1EE8B, 0x1EE9B),
				//UnicodeRange(0x1EE9C, 0x1EEA0), // ARABIC unassigned 30
				UnicodeRange(0x1EEA1, 0x1EEA3),
				//UnicodeRange(0x1EEA4, 0x1EEA4), // ARABIC unassigned 31
				UnicodeRange(0x1EEA5, 0x1EEA9),
				//UnicodeRange(0x1EEAA, 0x1EEAA), // ARABIC unassigned 32
				UnicodeRange(0x1EEAB, 0x1EEBB),
				//UnicodeRange(0x1EEBC, 0x1EEEF), // ARABIC unassigned 33
				UnicodeRange(0x1EEF0, 0x1EEF1),
				//UnicodeRange(0x1EEF2, 0x1EFFF), // ARABIC unassigned 34
				UnicodeRange(0x1F000, 0x1F02B),
				//UnicodeRange(0x1F02C, 0x1F02F), // MAHJONG unassigned 1
				UnicodeRange(0x1F030, 0x1F093),
				//UnicodeRange(0x1F094, 0x1F09F), // DOMINO unassigned 1
				UnicodeRange(0x1F0A0, 0x1F0AE),
				//UnicodeRange(0x1F0AF, 0x1F0B0), // PLAYING unassigned 1
				UnicodeRange(0x1F0B1, 0x1F0BF),
				//UnicodeRange(0x1F0C0, 0x1F0C0), // PLAYING unassigned 2
				UnicodeRange(0x1F0C1, 0x1F0CF),
				//UnicodeRange(0x1F0D0, 0x1F0D0), // PLAYING unassigned 3
				UnicodeRange(0x1F0D1, 0x1F0F5),
				//UnicodeRange(0x1F0F6, 0x1F0FF), // PLAYING unassigned 4
				UnicodeRange(0x1F100, 0x1F1AD),
				//UnicodeRange(0x1F1AE, 0x1F1E5), // MASK unassigned 1
				UnicodeRange(0x1F1E6, 0x1F202),
				//UnicodeRange(0x1F203, 0x1F20F), // SQUARED unassigned 1
				UnicodeRange(0x1F210, 0x1F23B),
				//UnicodeRange(0x1F23C, 0x1F23F), // SQUARED unassigned 2
				UnicodeRange(0x1F240, 0x1F248),
				//UnicodeRange(0x1F249, 0x1F24F), // TORTOISE unassigned 1
				UnicodeRange(0x1F250, 0x1F251),
				//UnicodeRange(0x1F252, 0x1F25F), // CIRCLED unassigned 1
				UnicodeRange(0x1F260, 0x1F265),
				//UnicodeRange(0x1F266, 0x1F2FF), // ROUNDED unassigned 1
				UnicodeRange(0x1F300, 0x1F6D7),
				//UnicodeRange(0x1F6D8, 0x1F6DB), // ELEVATOR unassigned 1
				UnicodeRange(0x1F6DC, 0x1F6EC),
				//UnicodeRange(0x1F6ED, 0x1F6EF), // AIRPLANE unassigned 1
				UnicodeRange(0x1F6F0, 0x1F6FC),
				//UnicodeRange(0x1F6FD, 0x1F6FF), // ROLLER unassigned 1
				UnicodeRange(0x1F700, 0x1F776),
				//UnicodeRange(0x1F777, 0x1F77A), // LUNAR unassigned 1
				UnicodeRange(0x1F77B, 0x1F7D9),
				//UnicodeRange(0x1F7DA, 0x1F7DF), // NINE unassigned 1
				UnicodeRange(0x1F7E0, 0x1F7EB),
				//UnicodeRange(0x1F7EC, 0x1F7EF), // LARGE unassigned 1
				UnicodeRange(0x1F7F0, 0x1F7F0),
				//UnicodeRange(0x1F7F1, 0x1F7FF), // HEAVY unassigned 1
				UnicodeRange(0x1F800, 0x1F80B),
				//UnicodeRange(0x1F80C, 0x1F80F), // DOWNWARDS unassigned 1
				UnicodeRange(0x1F810, 0x1F847),
				//UnicodeRange(0x1F848, 0x1F84F), // DOWNWARDS unassigned 2
				UnicodeRange(0x1F850, 0x1F859),
				//UnicodeRange(0x1F85A, 0x1F85F), // UP unassigned 1
				UnicodeRange(0x1F860, 0x1F887),
				//UnicodeRange(0x1F888, 0x1F88F), // WIDE-HEADED unassigned 1
				UnicodeRange(0x1F890, 0x1F8AD),
				//UnicodeRange(0x1F8AE, 0x1F8AF), // WHITE unassigned 1
				UnicodeRange(0x1F8B0, 0x1F8BB),
				//UnicodeRange(0x1F8BC, 0x1F8BF), // SOUTH unassigned 1
				UnicodeRange(0x1F8C0, 0x1F8C1),
				//UnicodeRange(0x1F8C2, 0x1F8FF), // RIGHTWARDS unassigned 1
				UnicodeRange(0x1F900, 0x1FA53),
				//UnicodeRange(0x1FA54, 0x1FA5F), // BLACK unassigned 1
				UnicodeRange(0x1FA60, 0x1FA6D),
				//UnicodeRange(0x1FA6E, 0x1FA6F), // XIANGQI unassigned 1
				UnicodeRange(0x1FA70, 0x1FA7C),
				//UnicodeRange(0x1FA7D, 0x1FA7F), // CRUTCH unassigned 1
				UnicodeRange(0x1FA80, 0x1FA89),
				//UnicodeRange(0x1FA8A, 0x1FA8E), // HARP unassigned 1
				UnicodeRange(0x1FA8F, 0x1FAC6),
				//UnicodeRange(0x1FAC7, 0x1FACD), // FINGERPRINT unassigned 1
				UnicodeRange(0x1FACE, 0x1FADC),
				//UnicodeRange(0x1FADD, 0x1FADE), // ROOT unassigned 1
				UnicodeRange(0x1FADF, 0x1FAE9),
				//UnicodeRange(0x1FAEA, 0x1FAEF), // FACE unassigned 1
				UnicodeRange(0x1FAF0, 0x1FAF8),
				//UnicodeRange(0x1FAF9, 0x1FAFF), // RIGHTWARDS unassigned 1
				UnicodeRange(0x1FB00, 0x1FB92),
				//UnicodeRange(0x1FB93, 0x1FB93), // UPPER unassigned 1
				UnicodeRange(0x1FB94, 0x1FBF9),
				//UnicodeRange(0x1FBFA, 0x1FFFF), // SEGMENTED unassigned 1
				UnicodeRange(0x20000, 0x2A6DF),
				//UnicodeRange(0x2A6E0, 0x2A6FF), // CJK Ideograph Extension B unassigned 1
				UnicodeRange(0x2A700, 0x2B739),
				//UnicodeRange(0x2B73A, 0x2B73F), // CJK Ideograph Extension C unassigned 1
				UnicodeRange(0x2B740, 0x2B81D),
				//UnicodeRange(0x2B81E, 0x2B81F), // CJK Ideograph Extension D unassigned 1
				UnicodeRange(0x2B820, 0x2CEA1),
				//UnicodeRange(0x2CEA2, 0x2CEAF), // CJK Ideograph Extension E unassigned 1
				UnicodeRange(0x2CEB0, 0x2EBE0),
				//UnicodeRange(0x2EBE1, 0x2EBEF), // CJK Ideograph Extension F unassigned 1
				UnicodeRange(0x2EBF0, 0x2EE5D),
				//UnicodeRange(0x2EE5E, 0x2F7FF), // CJK Ideograph Extension I unassigned 1
				UnicodeRange(0x2F800, 0x2FA1D),
				//UnicodeRange(0x2FA1E, 0x2FFFF), // CJK unassigned 1
				UnicodeRange(0x30000, 0x3134A),
				//UnicodeRange(0x3134B, 0x3134F), // CJK Ideograph Extension G unassigned 1
				UnicodeRange(0x31350, 0x323AF),
				//UnicodeRange(0x323B0, 0xE0000), // CJK Ideograph Extension H unassigned 1
				UnicodeRange(0xE0001, 0xE0001),
				//UnicodeRange(0xE0002, 0xE001F), // LANGUAGE unassigned 1
				UnicodeRange(0xE0020, 0xE007F),
				//UnicodeRange(0xE0080, 0xE00FF), // CANCEL unassigned 1
				UnicodeRange(0xE0100, 0xE01EF),
				//UnicodeRange(0xE01F0, 0xEFFFF), // VARIATION unassigned 1
				//UnicodeRange(0xF0000, 0xFFFFD), // Plane 15 Private Use
				//UnicodeRange(0xFFFFE, 0xFFFFF), // Plane 15 Private Use unassigned 1
				//UnicodeRange(0x100000, 0x10FFFD), // Plane 16 Private Use
				//UnicodeRange(0x10FFFE, 0x10FFFF), // Plane 16 Private Use unassigned 1
			)
		}
	},
	;

	/**
	 * Indicates whether the given [codePoint] is part of this [UnicodeRange] collection or not.
	 */
	operator fun contains(codePoint: Int): Boolean =
		if (ranges.size <= 10) {
			ranges.any { codePoint in it }
		} else {
			// only works because we know the ranges are sorted (we don't enforce an invariant for performance reasons
			// but we check it in tests)
			val r = ranges.asList().binarySearch {
				val startRelativeToCodePoint = it.start.compareTo(codePoint)
				if (startRelativeToCodePoint < 0) {
					// if start is before codePoint it is in range...
					val endRelativeToCodePoint = it.endInclusive.compareTo(codePoint)
					//... if end is after
					if (endRelativeToCodePoint >= 0) 0
					else endRelativeToCodePoint
				} else {
					startRelativeToCodePoint
				}
			}
			// binarySearch returns a negative result if no ranges matched
			r >= 0
		}
}
