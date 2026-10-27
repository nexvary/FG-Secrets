package com.fgmachines.secrets

import android.graphics.BitmapFactory
import com.fgmachines.secrets.ui.renderIndustry
import com.fgmachines.secrets.ui.visualIndustries
import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayOutputStream

class InfographicTest {
    @Test fun everyPublishedGuideExportsAReadablePng() {
        assertEquals(2, visualIndustries.size)
        visualIndustries.forEach { guide ->
            assertTrue(guide.sourceUrl.startsWith("https://"))
            assertTrue(guide.sections.size >= 6)
            val bitmap = renderIndustry(guide)
            try {
                assertEquals(1080, bitmap.width)
                assertTrue(bitmap.height > 1500)
                assertTrue(bitmap.height < 12000)
                val bytes = ByteArrayOutputStream().use {
                    assertTrue(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it))
                    it.toByteArray()
                }
                val decoded = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                assertNotNull(decoded)
                assertEquals(bitmap.height, decoded.height)
                decoded.recycle()
            } finally { bitmap.recycle() }
        }
    }
}
