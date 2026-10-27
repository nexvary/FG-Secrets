package com.fgmachines.secrets.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fgmachines.secrets.domain.*

@Composable private fun label(ar: String, en: String) = if (LocalArabic.current) ar else en
@Composable private fun NumberField(value: String, change: (String) -> Unit, title: String) {
    OutlinedTextField(value, change, label = { Text(title) }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
}
@Composable fun FormulationScreen() {
    val keyboard = LocalSoftwareKeyboardController.current
    var batch by rememberSaveable { mutableStateOf("") }
    var percentages by rememberSaveable { mutableStateOf(listOf("", "")) }
    var unitName by rememberSaveable { mutableStateOf(MassUnit.KILOGRAM.name) }
    var outputName by rememberSaveable { mutableStateOf(MassUnit.KILOGRAM.name) }
    var result by remember(batch, percentages, unitName, outputName) { mutableStateOf<List<String>?>(null) }
    var invalid by remember(batch, percentages, unitName, outputName) { mutableStateOf(false) }
    LazyColumn(Modifier.fillMaxSize().testTag("page").imePadding(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text(label("حاسبة الكميات", "Mass calculator"), style = MaterialTheme.typography.headlineSmall) }
        item { Text(label("أداة رياضية لمدخلاتك فقط؛ لا تمثل وصفة موثقة ولا تمنح اعتمادًا للتصنيع. النسب كتلية وليست حجمية. يجب أن يكون مجموعها 100% بالضبط.", "Arithmetic for your inputs, not a referenced recipe or manufacturing approval. Mass percentages only; total must equal exactly 100%.")) }
        item { NumberField(batch, { batch = it }, label("كتلة الدفعة", "Batch mass")) }
        item { Text(label("وحدة الدفعة", "Batch unit")); UnitSelector(unitName) { unitName = it } }
        item { Text(label("وحدة النتائج", "Result unit")); UnitSelector(outputName) { outputName = it } }
        itemsIndexed(percentages) { index, value ->
            Column {
                NumberField(value, { v -> percentages = percentages.toMutableList().also { it[index] = v } }, label("نسبة المكون ${index + 1} %", "Component ${index + 1} %"))
                if (percentages.size > 2) TextButton(onClick = { percentages = percentages.filterIndexed { i, _ -> i != index } }) { Text(label("حذف المكون", "Remove component")) }
            }
        }
        item { TextButton(onClick = { percentages = percentages + "" }, enabled = percentages.size < 50) { Text(label("إضافة مكون", "Add component")) } }
        item { Button(onClick = {
            keyboard?.hide()
            try {
                result = FormulationCalculator.scale(percentages.map(Numbers::parse), Numbers.parse(batch), MassUnit.valueOf(unitName), MassUnit.valueOf(outputName)).map { it.display() }
            } catch (_: IllegalArgumentException) { invalid = true }
        }, modifier = Modifier.fillMaxWidth()) { Text(label("حساب الكميات", "Calculate quantities")) } }
        if (invalid) item { Text(label("راجع الأرقام: كتلة أكبر من صفر، ونسب بين صفر و100 مجموعها 100. استخدم النقطة أو ٫ للفاصل العشري دون فواصل آلاف.", "Check inputs: positive batch mass and percentages in 0–100 totaling 100. Use . or ٫ as decimal separator, with no grouping."), color = MaterialTheme.colorScheme.error) }
        result?.let { quantities ->
            itemsIndexed(quantities) { index, quantity -> Text(label("المكون ${index + 1}: $quantity ${unitLabel(MassUnit.valueOf(outputName))}", "Component ${index + 1}: $quantity ${unitLabel(MassUnit.valueOf(outputName))}")) }
            item { Text(label("العرض مقرب إلى 4 منازل عشرية؛ قد يظهر فرق تقريبي في المجموع. اختَر الجرام للكميات الصغيرة.", "Display rounded to 4 decimal places; displayed totals may differ by rounding. Use grams for small amounts.")) }
        }
    }
}
@Composable private fun unitLabel(unit: MassUnit) = when (unit) {
    MassUnit.GRAM -> label("جرام", "g")
    MassUnit.KILOGRAM -> label("كجم", "kg")
    MassUnit.TONNE -> label("طن", "t")
}
@Composable private fun UnitSelector(selected: String, change: (String) -> Unit) {
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) { MassUnit.entries.forEach { unit -> FilterChip(selected == unit.name, { change(unit.name) }, { Text(unitLabel(unit)) }) } }
}
@Composable fun CostScreen() {
    val keyboard = LocalSoftwareKeyboardController.current
    var values by rememberSaveable { mutableStateOf(listOf("", "0", "0", "0", "0", "", "0", "0")) }
    var result by remember(values) { mutableStateOf<CostResult?>(null) }
    var invalid by remember(values) { mutableStateOf(false) }
    val titles = listOf(label("إجمالي تكلفة الخامات", "Total material cost"), label("أجور التشغيل", "Labor"), label("الطاقة", "Energy"), label("تكلفة العبوات للدفعة", "Batch packaging cost"), label("تكاليف أخرى", "Other costs"), label("كمية الإنتاج قبل الفاقد — وحدة أو كجم", "Output before loss — units or kg"), label("فاقد الإنتاج %", "Output loss %"), label("هامش الربح من سعر البيع %", "Profit margin on selling price %"))
    LazyColumn(Modifier.fillMaxSize().testTag("page").imePadding(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text(label("حاسبة تكلفة الإنتاج", "Production cost calculator"), style = MaterialTheme.typography.headlineSmall) }
        item { Text(label("أدخل جميع التكاليف بنفس العملة. الفاقد يقلل الكمية القابلة للبيع؛ هامش الربح يحسب من سعر البيع وليس زيادة على التكلفة. الضرائب والشحن تدخل ضمن تكاليف أخرى عند الحاجة.", "Use one currency throughout. Loss reduces saleable output; profit margin is a share of selling price, not a markup. Include taxes and shipping in other costs where needed.")) }
        itemsIndexed(values) { index, value -> NumberField(value, { v -> values = values.toMutableList().also { it[index] = v } }, titles[index]) }
        item { Button(onClick = { keyboard?.hide(); try {
            val n = values.map(Numbers::parse)
            result = CostCalculator.calculate(CostInput(n[0], n[1], n[2], n[3], n[4], n[5], n[6], n[7]))
        } catch (_: IllegalArgumentException) { invalid = true } }, modifier = Modifier.fillMaxWidth()) { Text(label("حساب التكلفة وسعر البيع", "Calculate cost & selling price")) } }
        if (invalid) item { Text(label("راجع المدخلات: تكاليف غير سالبة، كمية أكبر من صفر، فاقد وهامش ربح من صفر إلى أقل من 100%.", "Check inputs: nonnegative costs, positive output, loss and margin from zero to below 100%."), color = MaterialTheme.colorScheme.error) }
        result?.let { r ->
            item { Text(label("تكلفة الدفعة: ${r.batchCost.display()}", "Batch cost: ${r.batchCost.display()}")) }
            item { Text(label("كمية قابلة للبيع: ${r.saleableUnits.display()}", "Saleable output: ${r.saleableUnits.display()}")) }
            item { Text(label("تكلفة الوحدة بعد الفاقد: ${r.unitCost.display()}", "Unit cost after loss: ${r.unitCost.display()}")) }
            item { Text(label("سعر البيع المحسوب للوحدة: ${r.sellingPrice.display()}", "Calculated unit selling price: ${r.sellingPrice.display()}"), color = MaterialTheme.colorScheme.secondary) }
            item { Text(label("النتائج بنفس عملة التكاليف، مقربة إلى 4 منازل. ليست أسعار سوق أو عرض سعر.", "Results use the input currency, rounded to 4 decimals. These are not market prices or quotations.")) }
        }
    }
}
