package com.fgmachines.secrets

import android.graphics.Bitmap
import android.content.ContentValues
import android.provider.MediaStore
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test

@androidx.test.filters.SdkSuppress(minSdkVersion = 29)
class NavigationTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private fun screenshot(name: String) {
        compose.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val resolver = instrumentation.targetContext.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$name.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/FG-Secrets")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: error("Screenshot destination unavailable")
        val bitmap = instrumentation.uiAutomation.takeScreenshot() ?: error("Screenshot unavailable")
        resolver.openOutputStream(uri)!!.use { check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) }
        bitmap.recycle()
        resolver.update(uri, ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) }, null, null)
    }
    private fun scrollTo(text: String): SemanticsNodeInteraction {
        if (compose.onAllNodesWithText(text).fetchSemanticsNodes().isEmpty()) {
            compose.onNodeWithTag("page").performScrollToNode(hasText(text))
        }
        compose.waitForIdle()
        return compose.onNodeWithText(text).performScrollTo()
    }
    private fun back() { compose.onNodeWithContentDescription("رجوع").performClick() }
    @Test fun landscapeAndLargerTextRemainNavigable() {
        compose.waitUntil(15000) { compose.onAllNodesWithText("النتائج: 71").fetchSemanticsNodes().isNotEmpty() }
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        try {
            instrumentation.uiAutomation.executeShellCommand("settings put system font_scale 1.3").close()
            compose.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            compose.waitUntil(15000) { compose.activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE }
            compose.onNodeWithText("المزيد", useUnmergedTree = true).performClick()
            scrollTo("الإعدادات وحول التطبيق").performClick()
            compose.onNodeWithContentDescription("رجوع").assertIsDisplayed()
            screenshot("08-landscape-large-text")
        } finally {
            instrumentation.uiAutomation.executeShellCommand("settings put system font_scale 1.0").close()
            compose.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
    @Test fun searchFavoriteNavigationCalculatorsAndLanguage() {
        compose.waitUntil(15000) { compose.onAllNodesWithText("النتائج: 71").fetchSemanticsNodes().isNotEmpty() }
        screenshot("01-home-ar")
        compose.onNodeWithText("ابحث بالاسم أو المصطلح أو الرقم").performTextInput("ADH-001")
        scrollTo("النتائج: 1").assertIsDisplayed()
        scrollTo("تصنيع غراء المكبس").performClick()
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
        scrollTo("كتلة الدفعة").performTextReplacement("١٠")
        scrollTo("نسبة المكون 1 %").performTextReplacement("٢٥")
        scrollTo("نسبة المكون 2 %").performTextReplacement("٧٥")
        scrollTo("حساب الكميات").performClick()
        scrollTo("المكون 1: 2.5 كجم").assertIsDisplayed()
        screenshot("05-calculator-ar")
        back()
        compose.onNodeWithText("حاسبة تكلفة الإنتاج").performClick()
        compose.onNodeWithText("إجمالي تكلفة الخامات").performTextReplacement("100")
        scrollTo("كمية الإنتاج قبل الفاقد — وحدة أو كجم").performTextReplacement("100")
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
        compose.waitUntil(5000) { compose.onAllNodesWithText("الإعدادات وحول التطبيق").fetchSemanticsNodes().isNotEmpty() }
    }
}
