package com.fgmachines.secrets

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.runtime.CompositionLocalProvider

data class Topic(val name: String, val group: String)
val initialTopics = listOf(
    Topic("غراء المكبس", "الغراء واللواصق"),
    Topic("لاصق السيليكون للأنابيب", "الغراء واللواصق"),
    Topic("الغراء الحيواني السائل", "الغراء واللواصق"),
    Topic("لاصق سيليكات الصوديوم", "الغراء واللواصق"),
    Topic("لاصق الخشب بالمعادن", "الغراء واللواصق"),
    Topic("لاصق الهوت ميلت", "الغراء واللواصق"),
    Topic("ورنيش عازل للكابلات الأرضية", "العوازل والكابلات"),
    Topic("عازل للمحولات عالي الحرارة", "العوازل والكابلات"),
    Topic("عازل كابلات 11 كيلوفولت", "العوازل والكابلات"),
    Topic("فوم عازل للحرارة والماء", "العوازل والكابلات"),
    Topic("أسمنت الأفران الحرارية", "العوازل والكابلات")
)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                MaterialTheme {
                    var query by remember { mutableStateOf("") }
                    var selected by remember { mutableStateOf<Topic?>(null) }
                    Column(Modifier.fillMaxSize().padding(18.dp)) {
                        Text("أسرار الصناعات", style = MaterialTheme.typography.headlineMedium)
                        Text("FG Machines — إدارة علاء محمد")
                        Spacer(Modifier.height(20.dp))
                        if (selected == null) {
                            OutlinedTextField(query, { query = it }, label = { Text("ابحث عن صناعة أو خامة") }, modifier = Modifier.fillMaxWidth())
                            Spacer(Modifier.height(12.dp))
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(initialTopics.filter { it.name.contains(query, ignoreCase = true) || it.group.contains(query, ignoreCase = true) }) { topic ->
                                    ElevatedCard(onClick = { selected = topic }, modifier = Modifier.fillMaxWidth()) {
                                        Column(Modifier.padding(16.dp)) {
                                            Text(topic.name, style = MaterialTheme.typography.titleMedium)
                                            Text(topic.group, style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                        } else {
                            TextButton(onClick = { selected = null }) { Text("العودة للأقسام") }
                            Text(selected!!.name, style = MaterialTheme.typography.headlineSmall)
                            Text("القسم: ${selected!!.group}")
                            Spacer(Modifier.height(12.dp))
                            Text("حالة المحتوى: عنوان مستخرج من القائمة المصدرية، وليس تركيبة تصنيع معتمدة.")
                            Text("ستضاف الخامات والمصادر والمعدات والاختبارات بعد المراجعة الفنية.")
                        }
                    }
                }
            }
        }
    }
}
