package com.fgmachines.secrets

import android.graphics.Bitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import java.io.File

class NavigationTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private fun screenshot(name: String) {
        compose.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val directory = File(instrumentation.targetContext.getExternalFilesDir(null), "screenshots").apply { mkdirs() }
        val bitmap = instrumentation.uiAutomation.takeScreenshot() ?: error("Screenshot unavailable")
        File(directory, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
    }
    private fun scrollTo(text: String): SemanticsNodeInteraction {
        compose.onNodeWithTag("page").performScrollToNode(hasText(text))
        return compose.onNodeWithText(text)
    }
    private fun back() { compose.onNodeWithContentDescription("رجوع").performClick() }
    @Test fun searchFavoriteNavigationCalculatorsAndLanguage() {
        compose.waitUntil(15000) { compose.onAllNodesWithText("النتائج: 71").fetchSemanticsNodes().isNotEmpty() }
        screenshot("01-home-ar")
        compose.onNodeWithText("ابحث بالاسم أو المصطلح أو الرقم").performTextInput("ADH-001")
        compose.onNodeWithText("النتائج: 1").assertExists()
        compose.onNodeWithText("تصنيع غراء المكبس").performClick()
        scrollTo("النص الأصلي المنقول").assertIsDisplayed()
        screenshot("02-topic-ar")
        scrollTo("حفظ في المفضلة").performClick()
        back()
        compose.onNodeWithText("المفضلة", useUnmergedTree = true).performClick()
        compose.onNodeWithText("تصنيع غراء المكبس").assertExists()
        screenshot("03-favorites-ar")
        compose.onNodeWithText("المزيد", useUnmergedTree = true).performClick()
        screenshot("04-tools-ar")
        compose.onNodeWithText("حاسبة الكميات").performClick()
        scrollTo("كتلة الدفعة").performTextInput("١٠")
        scrollTo("نسبة المكون 1 %").performTextInput("٢٥")
        scrollTo("نسبة المكون 2 %").performTextInput("٧٥")
        scrollTo("حساب الكميات").performClick()
        scrollTo("المكون 1: 2.5 كجم").assertIsDisplayed()
        screenshot("05-calculator-ar")
        back()
        compose.onNodeWithText("حاسبة تكلفة الإنتاج").performClick()
        compose.onNodeWithText("إجمالي تكلفة الخامات").performTextInput("100")
        scrollTo("كمية الإنتاج قبل الفاقد — وحدة أو كجم").performTextInput("100")
        scrollTo("هامش الربح من سعر البيع %").performTextReplacement("20")
        scrollTo("حساب التكلفة وسعر البيع").performClick()
        scrollTo("سعر البيع المحسوب للوحدة: 1.25").assertIsDisplayed()
        screenshot("06-cost-ar")
        back()
        scrollTo("الإعدادات وحول التطبيق").performClick()
        compose.onNodeWithText("English").performClick()
        compose.waitUntil(5000) { compose.onAllNodesWithText("Settings & about").fetchSemanticsNodes().isNotEmpty() }
        screenshot("07-settings-en")
        compose.onNodeWithText("العربية").performClick()
    }
}
