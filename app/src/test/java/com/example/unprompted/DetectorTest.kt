package com.example.unprompted

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DetectorTest {
    @Test
    fun newAgentNamesResolveToTheirOwnBrand() {
        val cases = mapOf(
            "Claude Duet" to "duet",
            "Grok Bot" to "grokbot",
            "GrokBot" to "grokbot",
            "Alexa+" to "alexa",
            "Opera Neon" to "neon",
            "Hark" to "hark",
            "Lindy" to "lindy",
            "Poke" to "poke",
            "Genspark" to "genspark",
            "Devin" to "devin",
            "nanobot" to "nanobot",
        )
        for ((label, brandId) in cases) {
            val match = Detector.match("org.example.utility", label)
            assertEquals(label, brandId, match?.brandId)
            assertEquals(label, MatchKind.LIKELY, match?.kind)
        }
    }

    @Test
    fun shortAgentNamesDoNotMatchInsideUnrelatedWords() {
        for (label in listOf("Poker", "Harkness", "LindyHop", "Devinson", "AlexaMusic")) {
            assertNull(label, Detector.match("org.example.utility", label))
        }
    }

    @Test
    fun packageRulesStillIdentifyKnownAppsAndNewAgents() {
        val known = Detector.match("com.anthropic.claude", "Claude")
        assertEquals("claude", known?.brandId)
        assertEquals(MatchKind.KNOWN, known?.kind)
        assertEquals("alexa", Detector.match("com.amazon.dee.app", "Assistant")?.brandId)
        assertEquals("devin", Detector.match("com.cognition.mobile", "Assistant")?.brandId)
    }
}
