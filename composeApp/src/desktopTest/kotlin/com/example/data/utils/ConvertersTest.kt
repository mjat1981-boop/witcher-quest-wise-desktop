package com.example.data.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The Room type converter serializes List<String> columns to/from JSON. These
 * cover the round-trip, the empty case, values that need JSON escaping, and the
 * documented fallback to an empty list on malformed input (a corrupt DB cell
 * must not crash the app).
 */
class ConvertersTest {
    private val converters = Converters()

    @Test
    fun roundTripsAListOfStrings() {
        val original = listOf("Igni", "Quen", "Yrden", "Aard", "Axii")
        val json = converters.fromStringList(original)
        assertEquals(original, converters.toStringList(json))
    }

    @Test
    fun roundTripsAnEmptyList() {
        val json = converters.fromStringList(emptyList())
        assertEquals(emptyList(), converters.toStringList(json))
    }

    @Test
    fun preservesOrderAndDuplicates() {
        val original = listOf("b", "a", "b", "c", "a")
        assertEquals(original, converters.toStringList(converters.fromStringList(original)))
    }

    @Test
    fun handlesValuesThatRequireJsonEscaping() {
        val original = listOf("quote\"inside", "back\\slash", "new\nline", "emoji 🐺", "cirilla's choice")
        assertEquals(original, converters.toStringList(converters.fromStringList(original)))
    }

    @Test
    fun toStringListReturnsEmptyOnMalformedJson() {
        // The `?: emptyList()` fallback should absorb a corrupt DB value.
        assertEquals(emptyList(), converters.toStringList("{ this is not a json array"))
    }

    @Test
    fun toStringListReadsAPlainJsonArray() {
        assertEquals(listOf("one", "two"), converters.toStringList("""["one","two"]"""))
    }

    @Test
    fun fromStringListProducesParseableJsonArray() {
        val json = converters.fromStringList(listOf("x", "y"))
        assertTrue(json.trimStart().startsWith("["), "expected a JSON array, got: $json")
    }
}
