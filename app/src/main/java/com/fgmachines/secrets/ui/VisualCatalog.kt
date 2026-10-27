package com.fgmachines.secrets.ui

import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.compose.ui.platform.testTag
import com.fgmachines.secrets.data.Topic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

data class VisualIndustry(
    val id: String, val title: String, val category: String, val description: String,
    val sections: List<Pair<String, String>>, val sourceName: String, val sourceUrl: String
)

// Editorial summaries of primary references; no source-document claims or invented recipes.
val visualIndustries = listOf(
    VisualIndustry("VIS-GREASE", "الشحوم الصناعية", "الزيوت والشحوم",
        "كيف يتكوّن الشحم؟ تعرّف على دور زيت الأساس والمثخّن والإضافات، وما الذي يحدد اختيار الشحم للمحامل.",
        listOf(
            "زيت الأساس" to "الجزء الذي يقوم بالتزييت. قد يكون زيتًا معدنيًا أو صناعيًا بحسب ظروف الاستخدام.",
            "المثخّن" to "يحتفظ بالزيت ويعطي الشحم قوامه شبه الصلب. اختلاف نوعه يغيّر خصائص الشحم.",
            "الإضافات" to "قد تحسّن مقاومة الأكسدة والصدأ والتآكل أو تحمّل الضغط، بحسب المنتج.",
            "كيف تختار؟" to "راجع لزوجة زيت الأساس والقوام وحرارة التشغيل والحمل وظروف الماء والتلوث في نشرة المنتج.",
            "خطأ شائع" to "زيادة كمية الشحم قد ترفع حرارة المحمل. كمية التعبئة تعتمد على المحمل وسرعته وتصميم المبيت.",
            "حدود الدليل" to "هذه خريطة لفهم المكوّنات؛ ليست وصفة تصنيع. لا توجد درجة تسخين أو نسب موحدة لكل أنواع الشحم."
        ), "NSK · Grease Lubrication / SKF · Grease lubrication",
        "https://www.nsk.com/tools-resources/abc-bearings/grease-lubrication/"),
    VisualIndustry("ADH-019", "الغراء الحراري — هوت ميلت", "الغراء واللواصق",
        "دليل بصري لفكرة عمل الغراء الحراري، من التطبيق في الحالة المنصهرة إلى تكوين الرابطة عند التبريد، مع استخداماته في الورق وتجليد الكتب.",
        listOf(
            "فكرة العمل" to "يُطبّق اللاصق في الحالة المنصهرة، ثم تتكوّن رابطة أولية عند تبريده.",
            "١ · الصهر" to "تحدد النشرة الفنية للمنتج ظروف الصهر والتطبيق؛ لا توجد حرارة واحدة تصلح لكل المنتجات.",
            "٢ · التطبيق والتجميع" to "يُختار المنتج وفق السطح وزمن التشغيل المطلوب. تختلف اللزوجة والمرونة وزمن بقاء اللاصق مفتوحًا.",
            "٣ · التبريد" to "يُكوّن التبريد الرابطة الأولية. بعض الأنواع التفاعلية تتصلب لاحقًا بتفاعل كيميائي.",
            "أين يستخدم؟" to "التغليف، إغلاق الكرتون، تجليد الكتب، تجميع المنتجات وبعض تطبيقات الأقمشة.",
            "قبل التشغيل" to "راجع النشرة الفنية ونشرة السلامة للدرجة المختارة. هذا شرح للمبدأ وليس تركيبة تصنيع أو تعليمات تشغيل."
        ), "Henkel · Hot melt adhesives",
        "https://next.henkel-adhesives.com/us/en/products.html/producttype_2.3.5.4.1.4.html")
)

private val visualCategories = listOf("الغراء واللواصق", "الزيوت والشحوم", "تركيب العطور", "مزيلات الصدأ وتلميع المعادن", "العوازل والكابلات والحرارة")

@Composable
fun VisualCatalog(topics: List<Topic>, open: (String) -> Unit) {
    var category by rememberSaveable { mutableStateOf("") }
    var query by rememberSaveable { mutableStateOf("") }
    val key = com.fgmachines.secrets.data.searchKey(query)
    val ready = visualIndustries.filter { (category.isEmpty() || it.category == category) && com.fgmachines.secrets.data.searchKey(it.title + it.description).contains(key) }
    val index = topics.filter { (category.isEmpty() || it.group == category) && it.matches(query) && it.id !in visualIndustries.map { v -> v.id } }
    androidx.compose.foundation.lazy.LazyColumn(Modifier.fillMaxSize().testTag("page"), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Text("الصناعة في صورة", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.secondary)
            Text("تصفّح الأقسام، افتح الإنفوجرافيك، ثم احفظه أو شاركه.", style = MaterialTheme.typography.bodyLarge)
        }
        item { OutlinedTextField(query, { query = it }, label = { Text("ابحث عن صناعة") }, modifier = Modifier.fillMaxWidth(), singleLine = true) }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = { category = "" }) { Text("كل الأقسام") }
                visualCategories.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { c ->
                            OutlinedCard(onClick = { category = c }, modifier = Modifier.weight(1f)) {
                                Column(Modifier.padding(14.dp)) {
                                    Text(c, color = if (c == category) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleMedium)
                                    val count = visualIndustries.count { it.category == c }
                                    Text(if (count > 0) "$count دليل مصوّر" else "المحتوى قيد الإعداد", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
        item { Text(if (category.isEmpty()) "الأدلة المصوّرة" else category, style = MaterialTheme.typography.headlineSmall) }
        items(ready.size, key = { ready[it].id }) { i ->
            val industry = ready[i]
            OutlinedCard(onClick = { open(industry.id) }, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    var cover by remember(industry.id) { mutableStateOf<Bitmap?>(null) }
                    LaunchedEffect(industry.id) { cover = withContext(Dispatchers.Default) { renderIndustry(industry) } }
                    cover?.let { Image(it.asImageBitmap(), industry.title, Modifier.fillMaxWidth().height(220.dp), contentScale = androidx.compose.ui.layout.ContentScale.Crop, alignment = androidx.compose.ui.Alignment.TopCenter) }
                    Text(industry.category, color = MaterialTheme.colorScheme.primary)
                    Text(industry.title, style = MaterialTheme.typography.headlineSmall)
                    Text(industry.description)
                    Text("فتح الصورة والدليل ←", color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
        if (ready.isEmpty()) item { Text("لا يوجد دليل مصوّر منشور هنا بعد.") }
        if (index.isNotEmpty()) {
            item { Text("فهرس الصناعات — قيد إعداد الصور", style = MaterialTheme.typography.titleLarge) }
            items(index.size, key = { index[it].id }) { i -> Text(cleanIndustryName(index[i].name), modifier = Modifier.padding(vertical = 6.dp)) }
        }
    }
}

fun cleanIndustryName(name: String): String = name.removePrefix("مشروع ").removePrefix("تصنيع ").removePrefix("تركيبة ").replace(" غير سام", "")

/** Native Android text shaping produces readable Arabic in the exported PNG.
 * Height is measured from all layouts before allocation; no clipped footer.
 */
fun renderIndustry(industry: VisualIndustry): Bitmap {
    val width = 1080
    val margin = 56
    fun layout(value: String, size: Float, bold: Boolean = false): StaticLayout {
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AndroidColor.WHITE
            textSize = size
            typeface = Typeface.create("sans-serif", if (bold) Typeface.BOLD else Typeface.NORMAL)
        }
        return StaticLayout.Builder.obtain(value, 0, value.length, paint, width - margin * 2 - 48)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setTextDirection(android.text.TextDirectionHeuristics.FIRSTSTRONG_RTL)
            .setLineSpacing(8f, 1f).setIncludePad(true).build()
    }
    val rows = listOf(
        layout("FG Machines · أسرار الصناعات", 36f, true),
        layout(industry.title, 66f, true),
        layout(industry.description, 34f)
    ) + industry.sections.flatMap { listOf(layout(it.first, 42f, true), layout(it.second, 36f)) } +
        listOf(layout("المصادر: " + industry.sourceName + "\nمراجعة 2026-10-27 · شرح تعليمي بمراجع منشورة\nإدارة علاء محمد", 28f))
    val height = rows.sumOf { it.height + 40 } + 380
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawColor(AndroidColor.rgb(6, 10, 17))
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var y = 40f
    rows.forEachIndexed { i, text ->
        if (i == 3) {
            // A drawn schematic, not a simulated photograph of manufacturing.
            val centers = listOf(220f, 540f, 860f)
            centers.forEachIndexed { j, x ->
                paint.color = listOf(0xFFFFC857.toInt(), 0xFF54B9FF.toInt(), 0xFF60E0A0.toInt())[j]
                canvas.drawCircle(x, y + 95f, 76f, paint)
                paint.color = AndroidColor.rgb(8, 15, 24)
                paint.textAlign = Paint.Align.CENTER
                paint.textSize = 56f
                paint.typeface = Typeface.DEFAULT_BOLD
                if (industry.id == "VIS-GREASE" && j == 0) {
                    val drop = android.graphics.Path().apply {
                        moveTo(x, y + 42f); cubicTo(x - 65f, y + 110f, x - 25f, y + 148f, x, y + 148f)
                        cubicTo(x + 35f, y + 148f, x + 60f, y + 106f, x, y + 42f); close()
                    }
                    canvas.drawPath(drop, paint)
                } else if (industry.id == "VIS-GREASE" && j == 1) {
                    paint.style = Paint.Style.STROKE; paint.strokeWidth = 7f
                    for (n in -1..1) {
                        canvas.drawLine(x - 42f, y + 95f + n * 25f, x + 42f, y + 95f + n * 25f, paint)
                        canvas.drawLine(x + n * 25f, y + 53f, x + n * 25f, y + 137f, paint)
                    }
                    paint.style = Paint.Style.FILL
                } else if (industry.id == "VIS-GREASE") {
                    canvas.drawRoundRect(x - 12f, y + 48f, x + 12f, y + 142f, 5f, 5f, paint)
                    canvas.drawRoundRect(x - 47f, y + 83f, x + 47f, y + 107f, 5f, 5f, paint)
                } else {
                    canvas.drawText((j + 1).toString(), x, y + 114f, paint)
                }
            }
            y += 220f
        }
        val header = i == 1 || (i >= 3 && i < rows.lastIndex && (i - 3) % 2 == 0)
        paint.color = if (header) 0xFF123C58.toInt() else 0xFF151F2E.toInt()
        canvas.drawRoundRect(32f, y, width - 32f, y + text.height + 28f, 18f, 18f, paint)
        paint.color = 0xFF8C9AAE.toInt()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        canvas.drawRoundRect(32f, y, width - 32f, y + text.height + 28f, 18f, 18f, paint)
        paint.style = Paint.Style.FILL
        canvas.save()
        canvas.translate(margin.toFloat() + 24f, y + 12f)
        text.draw(canvas)
        canvas.restore()
        y += text.height + 40f
    }
    return bitmap
}

@Composable
fun VisualIndustryScreen(id: String, favorite: Boolean, toggle: () -> Unit) {
    val industry = visualIndustries.find { it.id == id }
    if (industry == null) {
        Text("الدليل المصوّر غير متاح حاليًا.", Modifier.padding(24.dp))
        return
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    var bitmap by remember(id) { mutableStateOf<Bitmap?>(null) }
    var message by remember(id) { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    LaunchedEffect(id) {
        try { bitmap = withContext(Dispatchers.Default) { renderIndustry(industry) } }
        catch (e: Exception) { message = "تعذر إنشاء الصورة. أعد فتح الدليل." }
    }
    val saver = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("image/png")) { uri ->
        val image = bitmap
        if (uri != null && image != null) scope.launch {
            busy = true
            message = try {
                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri)?.use { check(image.compress(Bitmap.CompressFormat.PNG, 100, it)) } ?: error("No output stream")
                }
                "تم حفظ الصورة"
            } catch (e: Exception) { "تعذر حفظ الصورة. جرّب مكانًا آخر." }
            busy = false
        }
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(industry.title, style = MaterialTheme.typography.headlineMedium)
        val image = bitmap
        if (image == null) LinearProgressIndicator(Modifier.fillMaxWidth())
        else {
            var zoom by remember(id) { mutableFloatStateOf(1f) }
            Text("كبّر الصورة بإصبعين · تصغير", color = MaterialTheme.colorScheme.primary)
            TextButton(onClick = { zoom = 1f }) { Text("إعادة حجم الصورة") }
            Box(Modifier.fillMaxWidth().clipToBounds().pointerInput(id) { detectTransformGestures { _, _, scale, _ -> zoom = (zoom * scale).coerceIn(1f, 3f) } }) {
                Image(image.asImageBitmap(), industry.title + ". " + industry.description, Modifier.fillMaxWidth().graphicsLayer(scaleX = zoom, scaleY = zoom))
            }
            Button(enabled = !busy, onClick = { saver.launch(industry.id + ".png") }, modifier = Modifier.fillMaxWidth()) { Text("حفظ الصورة") }
            OutlinedButton(enabled = !busy, onClick = {
                scope.launch {
                    busy = true
                    try {
                        val uri = withContext(Dispatchers.IO) {
                            val dir = File(context.cacheDir, "infographics").apply { check(isDirectory || mkdirs()) }
                            val file = File(dir, industry.id + ".png")
                            file.outputStream().use { check(image.compress(Bitmap.CompressFormat.PNG, 100, it)) }
                            FileProvider.getUriForFile(context, context.packageName + ".images", file)
                        }
                        val send = Intent(Intent.ACTION_SEND).apply {
                            type = "image/png"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            clipData = ClipData.newRawUri(industry.title, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(send, "مشاركة الإنفوجرافيك"))
                    } catch (e: Exception) { message = "تعذرت المشاركة. حاول حفظ الصورة أولًا." }
                    busy = false
                }
            }, modifier = Modifier.fillMaxWidth()) { Text("مشاركة الصورة") }
        }
        if (message.isNotBlank()) Text(message)
        TextButton(onClick = toggle) { Text(if (favorite) "إزالة من المفضلة" else "حفظ في المفضلة") }
        Text("وصف الصناعة", style = MaterialTheme.typography.titleLarge)
        Text(industry.description)
        industry.sections.forEach { (title, body) ->
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Text(body)
        }
        Text("المصادر · مراجعة 2026-10-27", style = MaterialTheme.typography.titleMedium)
        TextButton(onClick = { uriHandler.openUri(industry.sourceUrl) }) { Text(industry.sourceName) }
        if (id == "VIS-GREASE") TextButton(onClick = { uriHandler.openUri("https://cdn.skfmediahub.skf.com/api/public/0901d1968024f02a/pdf_preview_medium/0901d1968024f02a_pdf_preview_medium.pdf") }) { Text("SKF · Grease lubrication") }
    }
}
