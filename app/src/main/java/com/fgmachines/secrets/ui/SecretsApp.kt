package com.fgmachines.secrets.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.fgmachines.secrets.data.*
import com.fgmachines.secrets.domain.*
import java.math.BigDecimal

private val Black = Color(0xFF07090D)
private val Silver = Color(0xFFAFB8C7)
private val Blue = Color(0xFF43B7FF)
private val Green = Color(0xFF63F5A4)
private val Red = Color(0xFFFF737B)
private val Gold = Color(0xFFFFD47C)
internal val LocalArabic = staticCompositionLocalOf { true }
@Composable private fun tr(ar: String, en: String) = if (LocalArabic.current) ar else en

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun SecretsApp(vm: SecretsViewModel) {
    val language by vm.language.collectAsStateWithLifecycle()
    val topics by vm.topics.collectAsStateWithLifecycle()
    val favorites by vm.favorites.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    val loading by vm.loading.collectAsStateWithLifecycle()
    val pendingPack by vm.pendingPack.collectAsStateWithLifecycle()
    val importedCount by vm.importedCount.collectAsStateWithLifecycle()
    val packPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> if (uri != null) vm.previewPack(uri) }
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route ?: "home"
    val root = route in listOf("home", "industries", "favorites", "tools")
    CompositionLocalProvider(LocalArabic provides (language == "ar"), LocalLayoutDirection provides if (language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr) {
        MaterialTheme(colorScheme = darkColorScheme(primary = Blue, secondary = Gold, background = Black, surface = Color(0xFF141A24), onSurface = Color(0xFFF1F4FA), onBackground = Color.White, error = Red)) {
            pendingPack?.let { pack -> AlertDialog(onDismissRequest = vm::cancelImport,
                title = { Text(tr("استيراد حزمة عناوين", "Import topic pack")) },
                text = { Text(tr("${pack.size} بندًا غير محقق. سيتم تحديث الأرقام المتطابقة وإضافة الجديدة مع الاحتفاظ بالمفضلة. اختر الحزم من مصدر تعرفه؛ نجاح الاستيراد لا يثبت صحة المحتوى.", "${pack.size} unverified records. Matching IDs will be updated, new IDs added, and favorites retained. Use a known source; successful import does not validate content.")) },
                confirmButton = { TextButton(onClick = vm::confirmImport) { Text(tr("استيراد", "Import")) } },
                dismissButton = { TextButton(onClick = vm::cancelImport) { Text(tr("إلغاء", "Cancel")) } }) }
            Scaffold(
                topBar = { TopAppBar(title = { Column {
                    Text(tr("أسرار الصناعات", "FG Secrets"), style = MaterialTheme.typography.titleLarge)
                    Text("FG Machines · إدارة علاء محمد", style = MaterialTheme.typography.labelSmall, color = Silver)
                } }, navigationIcon = { if (!root) IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, tr("رجوع", "Back"), tint = Blue) } }) },
                bottomBar = { if (root) NavigationBar {
                    val destinations = listOf(Triple("home", tr("الرئيسية", "Home"), Icons.Default.Home), Triple("industries", tr("الصناعات", "Industries"), Icons.Default.Build), Triple("favorites", tr("المفضلة", "Favorites"), Icons.Default.Star), Triple("tools", tr("المزيد", "More"), Icons.Default.Settings))
                    destinations.forEach { (path, title, icon) -> NavigationBarItem(selected = route == path, onClick = { if (route != path) nav.navigate(path) { popUpTo("home") { saveState = true }; launchSingleTop = true; restoreState = true } }, icon = { Icon(icon, null) }, label = { Text(title) }) }
                } }
            ) { padding ->
                Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF152134), Black, Black))).padding(padding)) {
                    if (error) { Text(tr("تعذر تحميل أو حفظ البيانات. حاول مجددًا.", "Could not load or save data. Try again."), color = Red, modifier = Modifier.padding(12.dp)); TextButton(onClick = vm::reload) { Text(tr("إعادة المحاولة", "Retry")) } }
                    if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())
                    NavHost(navController = nav, startDestination = "home", modifier = Modifier.weight(1f)) {
                        composable("home") { CatalogScreen(topics, favorites, home = true, onTopic = { nav.navigate("topic/$it") }, onFavorite = vm::favorite, onTools = { nav.navigate("tools") }) }
                        composable("industries") { CatalogScreen(topics, favorites, home = false, onTopic = { nav.navigate("topic/$it") }, onFavorite = vm::favorite, onTools = { nav.navigate("tools") }) }
                        composable("favorites") { FavoritesScreen(topics, favorites, { nav.navigate("topic/$it") }, vm::favorite) }
                        composable("tools") { Page {
                            item { Heading(tr("أدوات الورشة والمكتبة", "Workshop tools & library")) }
                            item { MenuCard(tr("قاموس الخامات", "Raw materials dictionary"), tr("الأسماء العلمية والمصطلحات الملتبسة", "Scientific names and ambiguous terms"), Icons.Default.Search) { nav.navigate("dictionary") } }
                            item { MenuCard(tr("حاسبة الكميات", "Mass calculator"), tr("حساب رياضي لنسب تدخلها بنفسك", "Arithmetic scaling for your own percentages"), Icons.Default.Build) { nav.navigate("formulation") } }
                            item { MenuCard(tr("حاسبة تكلفة الإنتاج", "Production cost"), tr("الخامات والتشغيل والفاقد وهامش الربح", "Materials, overhead, loss and profit margin"), Icons.Default.Build) { nav.navigate("cost") } }
                            item { MenuCard(tr("المكتبة الصناعية", "Industrial library"), tr("المراجع وحالة المحتوى دون اتصال", "References and offline content status"), Icons.Default.Info) { nav.navigate("library") } }
                            item { MenuCard(tr("الإعدادات وحول التطبيق", "Settings & about"), "FG Machines", Icons.Default.Settings) { nav.navigate("settings") } }
                        } }
                        composable("topic/{id}", arguments = listOf(navArgument("id") { type = NavType.StringType })) { back ->
                            val topic = topics.find { it.id == back.arguments?.getString("id") }
                            if (topic == null) Page { item { Text(tr("الموضوع غير متاح حاليًا.", "Topic is currently unavailable.")) } } else TopicScreen(topic, favorites.contains(topic.id), { vm.favorite(topic.id, !favorites.contains(topic.id)) })
                        }
                        composable("dictionary") { DictionaryScreen(favorites, vm::favorite) }
                        composable("formulation") { FormulationScreen() }
                        composable("cost") { CostScreen() }
                        composable("library") { LibraryScreen(topics, importedCount) { packPicker.launch(arrayOf("application/json", "text/plain")) } }
                        composable("settings") { SettingsScreen(language, vm::language) }
                    }
                }
            }
        }
    }
}

@Composable private fun Page(content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit) {
    LazyColumn(Modifier.fillMaxSize().testTag("page").imePadding(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
}
@Composable private fun Heading(text: String) { Text(text, style = MaterialTheme.typography.headlineSmall, color = Gold) }
@Composable private fun Panel(title: String, text: String, color: Color = Silver) {
    OutlinedCard(Modifier.fillMaxWidth(), border = BorderStroke(1.dp, Silver.copy(alpha = .55f))) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { Text(title, style = MaterialTheme.typography.titleMedium, color = color); Text(text, style = MaterialTheme.typography.bodyLarge) } }
}
@Composable private fun MenuCard(title: String, subtitle: String, icon: ImageVector, click: () -> Unit) {
    OutlinedCard(onClick = click, modifier = Modifier.fillMaxWidth(), border = BorderStroke(1.dp, Silver.copy(alpha = .55f))) { Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) { Icon(icon, null, tint = Blue, modifier = Modifier.size(30.dp)); Column(Modifier.weight(1f)) { Text(title, style = MaterialTheme.typography.titleLarge); Text(subtitle, color = Silver) } } }
}
@Composable private fun TopicCard(topic: Topic, favorite: Boolean, onTopic: () -> Unit, onFavorite: () -> Unit) {
    OutlinedCard(onClick = onTopic, modifier = Modifier.fillMaxWidth(), border = BorderStroke(1.dp, Silver.copy(alpha = .55f))) { Row(Modifier.padding(12.dp)) { Column(Modifier.weight(1f)) { Text(topic.name, style = MaterialTheme.typography.titleMedium); Text(topic.id, color = Blue); Text(tr("عنوان أولي · غير محقق", "Source title · Unverified"), color = Gold, style = MaterialTheme.typography.labelLarge) }; IconButton(onClick = onFavorite) { Icon(Icons.Default.Star, tr(if (favorite) "إزالة من المفضلة" else "حفظ في المفضلة", if (favorite) "Remove favorite" else "Save favorite"), tint = if (favorite) Gold else Silver) } } }
}
@Composable private fun CatalogScreen(topics: List<Topic>, favorites: Set<String>, home: Boolean, onTopic: (String) -> Unit, onFavorite: (String, Boolean) -> Unit, onTools: () -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    val categories = topics.map { it.group }.distinct()
    val filtered = remember(topics, query, category) { topics.filter { (category.isEmpty() || it.group == category) && it.matches(query) } }
    Page {
        if (home) item { Panel(tr("معرفة صناعية بين يديك", "Industrial knowledge at hand"), tr("${topics.size} عنوانًا من قائمتين أصليتين، محفوظة على جهازك. التوثيق العلمي والتركيبات المعتمدة قيد الاستكمال.", "${topics.size} Arabic source titles stored offline. Scientific documentation and verified formulations are still being developed."), Gold) }
        item { OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text(tr("ابحث بالاسم أو المصطلح أو الرقم", "Search name, source term or ID")) }, leadingIcon = { Icon(Icons.Default.Search, null, tint = Blue) }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
        item { Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) { FilterChip(category.isEmpty(), { category = "" }, { Text(tr("الكل", "All")) }); categories.forEach { c -> FilterChip(category == c, { category = c }, { Text(c) }) } } }
        if (home) item { TextButton(onClick = onTools) { Text(tr("افتح الحاسبات والقاموس والمراجع", "Open calculators, dictionary & references")) } }
        item { Text(tr("النتائج: ${filtered.size}", "Results: ${filtered.size}"), color = Green) }
        if (filtered.isEmpty()) item { Text(tr("لا توجد نتائج مطابقة. جرّب مصطلحًا آخر أو قسم الكل.", "No matching topics. Try another term or All categories.")) }
        items(filtered, key = { it.id }) { t -> TopicCard(t, t.id in favorites, { onTopic(t.id) }, { onFavorite(t.id, t.id !in favorites) }) }
    }
}
@Composable private fun FavoritesScreen(topics: List<Topic>, favorites: Set<String>, onTopic: (String) -> Unit, onFavorite: (String, Boolean) -> Unit) {
    Page {
        item { Heading(tr("المفضلة المحفوظة محليًا", "Saved on this device")) }
        if (favorites.isEmpty()) item { Text(tr("اضغط النجمة بجوار صناعة أو خامة لحفظها هنا.", "Tap the star on a topic or material to save it here.")) }
        items(topics.filter { it.id in favorites }, key = { it.id }) { t -> TopicCard(t, true, { onTopic(t.id) }, { onFavorite(t.id, false) }) }
        items(dictionary.filter { it.id in favorites }, key = { it.id }) { m -> MaterialCard(m, true, { onFavorite(m.id, false) }) }
    }
}
@Composable private fun TopicScreen(topic: Topic, favorite: Boolean, toggle: () -> Unit) {
    Page {
        item { Heading(topic.name) }
        item { Button(onClick = toggle) { Icon(Icons.Default.Star, null); Spacer(Modifier.width(8.dp)); Text(tr(if (favorite) "إزالة من المفضلة" else "حفظ في المفضلة", if (favorite) "Remove favorite" else "Save favorite")) } }
        item { Panel(tr("حالة الموضوع", "Topic status"), tr("غير محقق — عنوان من قائمة مشروعات، وليس تركيبة مثبتة. الكلمات مثل «غير سام» أو «لجميع الأسطح» من المصدر ولا تؤكد صلاحية المنتج.", "Unverified source title, not a validated formulation. Claims in the source such as non-toxic or universal adhesion have not been verified."), Gold) }
        item { Panel(tr("النص الأصلي المنقول", "Transcribed source text"), topic.originalText + if (topic.originalNotes.isNotBlank()) "\n${topic.originalNotes}" else "") }
        item { Panel(tr("المصدر والتتبع", "Source provenance"), "${topic.source}\n${tr("البند", "Item")} ${topic.sourceItem}\n${tr("مراجعة النقل: 2026-10-27 — فواصل الأسطر وعلامات الاتجاه منسقة؛ المطابقة البصرية النهائية معلقة.", "Transcription reviewed: 2026-10-27. Line breaks and bidi markers normalized; final visual reconciliation is pending.")}") }
        if (topic.id == "ADH-019" || topic.id == "ADH-010") {
            item { Panel(tr("معلومة مرجعية عن الهوت ميلت", "Referenced hot melt overview"), tr("تُطبق اللواصق الحرارية في الحالة المنصهرة ويتكون الربط الأولي عند التبريد. بعض الأنواع التفاعلية تتصلب لاحقًا بتفاعل كيميائي. لا يحدد عنوان القائمة نوع البوليمر أو وصفة المنتج.", "Hot melt adhesives are applied molten and form an initial bond on cooling. Reactive types may subsequently cure chemically. The source title does not identify a polymer or a product formulation."), Green) }
            item { ReferenceCard(hotMeltReference) }
        }
        knowledgeNotes[topic.id]?.forEach { note ->
            item { Panel(tr(note.titleAr, note.titleEn), tr(note.textAr, note.textEn), Green) }
            item { Text(tr("ملاحظة موثقة بمصدر منشور — لا تعني اختبار المنتج الوارد بالقائمة.", "Literature-referenced note — the source-list product has not been tested."), color = Green) }
            item { ReferenceCard(note.reference) }
        }
        item { Panel(tr("المواد الخام والتركيبة", "Raw materials & formulation"), tr("لا توجد نسب موثقة لهذا البند بعد. لا تُستنتج الخامات أو النسب من العنوان.", "No documented percentages yet. Ingredients and ratios cannot be inferred from a title.")) }
        item { Panel(tr("المراحل والمعدات والجودة", "Process, equipment & quality"), tr("لم تُراجع خطوات التصنيع والمعدات والخصائص والاختبارات والأخطاء الشائعة لهذا المنتج. يلزم مرجع فني محدد قبل نشرها.", "Manufacturing steps, equipment, properties, tests and troubleshooting have not been reviewed for this product. A specific technical reference is required.")) }
        item { Panel(tr("السلامة الصناعية", "Industrial safety"), tr("القائمة ليست تعليمات تشغيل. تطبيقات الجهد العالي ومياه الشرب والطب والوقود والأحماض تتطلب مراجعة فنية ونشرات SDS وتحققًا من ملاءمة المنتج قبل العمل.", "This list is not operating guidance. High voltage, drinking water, medical, fuel and acid applications require technical review, SDS and product suitability validation."), Red) }
    }
}
@Composable private fun ReferenceCard(reference: Reference) {
    val uri = LocalUriHandler.current
    Panel(reference.title, tr("مراجعة: ${reference.reviewedOn}\nالنص التعريفي محفوظ؛ فتح المرجع الأصلي يحتاج إنترنت.", "Reviewed: ${reference.reviewedOn}\nDefinition saved offline; opening the source requires internet."), Green)
    TextButton(onClick = { uri.openUri(reference.url) }) { Text(tr("فتح المرجع الأصلي", "Open original reference")) }
}
@Composable private fun MaterialCard(material: Material, favorite: Boolean, toggle: () -> Unit) {
    Panel("${material.arabic}\n${material.english}", if (LocalArabic.current) "${material.descriptionAr}\n${material.aliases}" else material.descriptionEn)
    TextButton(onClick = toggle) { Icon(Icons.Default.Star, null, tint = if (favorite) Gold else Silver); Text(tr(if (favorite) "إزالة من المفضلة" else "حفظ الخامة", if (favorite) "Remove favorite" else "Save material")) }
    ReferenceCard(material.reference)
}
@Composable private fun DictionaryScreen(favorites: Set<String>, toggle: (String, Boolean) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    val materials = dictionary.filter { searchKey("${it.arabic} ${it.english} ${it.aliases}").contains(searchKey(query)) }
    Page {
        item { Heading(tr("قاموس الخامات", "Raw materials dictionary")) }
        item { Text(tr("دفعة موثقة أولى: خامتان. المصطلح التجاري وحده لا يحدد التركيب أو الدرجة.", "Initial referenced set: two materials. A trade name alone does not identify composition or grade.")) }
        item { OutlinedTextField(query, { query = it }, label = { Text(tr("اسم الخامة", "Material name")) }, modifier = Modifier.fillMaxWidth(), singleLine = true) }
        if (materials.isEmpty()) item { Text(tr("لا توجد خامة مطابقة.", "No matching material.")) }
        items(materials, key = { it.id }) { m -> MaterialCard(m, m.id in favorites, { toggle(m.id, m.id !in favorites) }) }
    }
}
@Composable private fun LibraryScreen(topics: List<Topic>, importedCount: Int?, importPack: () -> Unit) {
    Page {
        item { Heading(tr("المكتبة الصناعية", "Industrial library")) }
        item { Button(onClick = importPack) { Text(tr("استيراد حزمة عناوين JSON", "Import JSON topic pack")) } }
        item { Text(tr("أضف آلاف العناوين دون تحديث التطبيق. حزم العناوين لا تضيف وصفات أو اعتمادات فنية. الحد 8 ميجابايت؛ صيغة الحزمة موثقة بالمستودع.", "Add thousands of titles without updating the app. Title packs cannot add recipes or technical validations. Limit: 8 MiB; pack format is documented in the repository.")) }
        if (importedCount != null) item { Text(tr("تم استيراد $importedCount بندًا.", "Imported $importedCount records."), color = Green) }
        item { Panel(tr("متاح دون اتصال", "Available offline"), tr("${topics.size} عنوانًا ونصوصها المنقولة، ${dictionary.size} تعريف للخامات، المفضلة والحاسبات. المستندات الأصلية الكاملة وصور الصفحات غير مرفقة بالتطبيق.", "${topics.size} source titles and transcriptions, ${dictionary.size} material definitions, favorites and calculators. Full original documents and page images are not bundled."), Green) }
        items(topics.groupBy { it.source }.toList()) { (source, entries) -> Panel(source, tr("${entries.size} بندًا — مصدر قائمة عناوين غير محققة.", "${entries.size} entries — unverified source-title list.")) }
        item { ReferenceCard(hotMeltReference) }
        items(dictionary) { ReferenceCard(it.reference) }
    }
}
@Composable private fun SettingsScreen(language: String, setLanguage: (String) -> Unit) {
    Page {
        item { Heading(tr("الإعدادات وحول التطبيق", "Settings & about")) }
        item { Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { FilterChip(language == "ar", { setLanguage("ar") }, { Text("العربية") }); FilterChip(language == "en", { setLanguage("en") }, { Text("English") }) } }
        item { Panel(tr("المظهر", "Appearance"), tr("هوية FG Machines: أسود، فضي، أزرق كهربائي. تصميم داكن ثابت.", "FG Machines: black, silver and electric blue. Fixed dark appearance.")) }
        item { Panel("FG Secrets 0.2.0", "FG Machines\nإدارة علاء محمد\n" + tr("موسوعة صناعية قيد التطوير. نصوص المصادر عربية؛ واجهة الاستخدام تدعم العربية والإنجليزية.", "Industrial encyclopedia in development. Source records remain Arabic; the interface supports Arabic and English.")) }
        item { Panel(tr("الخصوصية", "Privacy"), tr("لا حسابات أو إعلانات أو تحليلات. البيانات والمفضلة والإعدادات محلية. لا يطلب التطبيق أذونات حساسة. فتح المراجع يتم عبر المتصفح الخارجي.", "No accounts, ads or analytics. Data, favorites and settings stay local. No sensitive permissions. References open in your external browser.")) }
        item { Panel(tr("حدود الاستخدام", "Use limitations"), tr("المحتوى تعليمي ولا يثبت صلاحية منتج للبيع أو ملاءمته الطبية أو الكهربائية. الحاسبات تنفذ مدخلاتك ولا تتحقق من صلاحية أي وصفة.", "Educational content does not certify a product for sale, medical use or electrical use. Calculators process your inputs and do not validate a recipe."), Gold) }
    }
}
