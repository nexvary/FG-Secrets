package com.fgmachines.secrets

import com.fgmachines.secrets.data.*
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class CatalogTest {
    private fun json() = File("src/main/assets/catalog.json").readText()
    @Test fun catalogHasCompleteSourceSequencesAndNoExcludedContent() {
        val topics = parseCatalog(json())
        assertEquals(71, topics.size)
        assertEquals((1..38).toList(), topics.filter { it.id.startsWith("ADH-") }.map { it.sourceItem })
        assertEquals((1..33).toList(), topics.filter { it.id.startsWith("INS-") }.map { it.sourceItem })
        assertEquals(2, topics.map { it.source }.distinct().size)
        assertFalse(json().contains("Hilti", ignoreCase = true))
        assertTrue(topics.all { it.status.contains("غير محقق") })
    }
    @Test fun retainsDetailsPreviouslyOmittedFromDisplayTitles() {
        val topics = parseCatalog(json())
        assertTrue(topics.first { it.id == "ADH-017" }.originalText.contains("20 دقيقة"))
        assertTrue(topics.first { it.id == "INS-033" }.originalText.contains("مكونين"))
        assertTrue(topics.first { it.id == "INS-001" }.originalNotes.contains("BONDY"))
    }
    @Test fun searchNormalizesArabicWithoutChangingStoredText() {
        val t = parseCatalog(json()).first()
        assertTrue(t.matches("غِرَاء")); assertTrue(t.matches("adh-001")); assertTrue(t.matches("  المكبس  "))
        assertEquals("اسرار", searchKey("أَسْرَار"))
    }
    @Test fun duplicateAndUnsupportedSchemaAreRejected() {
        val root = JSONObject(json()); root.getJSONArray("topics").put(root.getJSONArray("topics").getJSONObject(0))
        try { parseCatalog(root.toString()); fail("Duplicate accepted") } catch (_: IllegalArgumentException) {}
        root.put("schemaVersion", 99)
        try { parseCatalog(root.toString()); fail("Unsupported schema accepted") } catch (_: IllegalArgumentException) {}
    }
    @Test fun titlePacksCannotUpgradeReliabilityOrUseUnsafeIds() {
        val root = JSONObject(json())
        root.getJSONArray("topics").getJSONObject(0).put("status", "Production Validated")
        try { parseCatalog(root.toString()); fail("Reliability claim accepted") } catch (_: IllegalArgumentException) {}
        val other = JSONObject(json())
        other.getJSONArray("topics").getJSONObject(0).put("id", "../../bad")
        try { parseCatalog(other.toString()); fail("Invalid ID accepted") } catch (_: IllegalArgumentException) {}
    }
    @Test fun emptyCatalogIsSafe() { assertTrue(parseCatalog("{\"schemaVersion\":2,\"topics\":[]}").isEmpty()) }
}
