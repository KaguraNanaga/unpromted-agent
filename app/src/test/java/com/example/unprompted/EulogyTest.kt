package com.example.unprompted

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EulogyTest {
    private val facts = RawFacts("Sample AI", System.currentTimeMillis() - 30L * 86_400_000L, 10_485_760L)

    @Test
    fun everyDetectedBrandCanRotateThroughBothLanguages() {
        for (brand in Detector.brands) {
            for (zh in listOf(false, true)) {
                repeat(120) { variant ->
                    val text = Eulogy.write("org.example.${brand.id}", brand.id, facts, variant, zh)
                    assertTrue("${brand.id}, zh=$zh, variant=$variant", text.isNotBlank())
                    assertFalse("${brand.id}, zh=$zh, variant=$variant", text.contains("null"))
                }
            }
        }
    }

    @Test
    fun agentRotationIncludesTheNewVacationEulogies() {
        val english = (0 until 80).map { Eulogy.write("com.openai.chatgpt", "chatgpt", facts, it, false) }
        val chinese = (0 until 80).map { Eulogy.write("com.openai.chatgpt", "chatgpt", facts, it, true) }
        assertTrue(english.any { it.contains("permanent vacation, no checkout") })
        assertTrue(chinese.any { it.contains("永久休假的屋子") })
    }

    @Test
    fun nonAgentAndUnknownAppsKeepTheirGeneralEulogies() {
        for (brandId in listOf("deepseek", null)) {
            repeat(120) { variant ->
                val text = Eulogy.write("org.example.generic", brandId, facts, variant, false)
                assertTrue(text.isNotBlank())
                assertFalse(text.contains("permanent vacation, no checkout"))
            }
        }
    }
}
