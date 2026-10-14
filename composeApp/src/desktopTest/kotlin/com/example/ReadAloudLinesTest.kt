package com.example

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ReadAloudLinesTest {
    @Test
    fun beastLineNamesTheKindTheWeaknessesAndTheFight() {
        assertEquals(
            "Griffin. Hybrid. Weaknesses: Grapeshot, Hybrid oil. Stay mobile and oil the blade.",
            beastSpokenLine(
                name = "Griffin",
                kind = "Hybrid",
                weaknesses = listOf("Grapeshot", "Hybrid oil"),
                howToFight = "Stay mobile and oil the blade."
            )
        )
    }

    @Test
    fun questNoticeReadsTitleRegionAndDescription() {
        assertEquals(
            "Family Matters. Velen. Find the baron's family.",
            questNoticeSpokenLine("Family Matters", "Velen", "Find the baron's family.")
        )
    }

    @Test
    fun destinyChoiceReadsTheSpokenChoiceAndItsLaterResult() {
        val line = destinyChoiceSpokenLine(
            "Help Cerys Investigate",
            "Cerys becomes queen of Skellige."
        )
        assertEquals("Help Cerys Investigate. Cerys becomes queen of Skellige.", line)
        assertFalse(line.contains("Read aloud"))
    }
}
