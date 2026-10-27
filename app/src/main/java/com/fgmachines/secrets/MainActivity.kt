package com.fgmachines.secrets

import android.os.Bundle
import org.json.JSONObject
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.CompositionLocalProvider

data class Topic(
    val id: String,
    val name: String,
    val group: String,
    val source: String,
    val sourceItem: Int,
    val status: String
)
fun parseCatalog(json: String): List<Topic> {
    val array = JSONObject(json).getJSONArray("topics")
    return (0 until array.length()).map { index ->
        val entry = array.getJSONObject(index)
        Topic(
            entry.getString("id"),
            entry.getString("title"),
            entry.getString("category"),
            entry.getString("source"),
            entry.getInt("sourceItem"),
            entry.getString("status")
        )
    }
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val topics = assets.open("catalog.json").bufferedReader(Charsets.UTF_8).use {
            parseCatalog(it.readText())
        }
        setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                MaterialTheme {
                    var query by remember { mutableStateOf("") }
                    var selected by remember { mutableStateOf<Topic?>(null) }
                    Column(Modifier.fillMaxSize().padding(18.dp)) {
                        Text("أسرار الصناعات", style = MaterialTheme.typography.headlineMedium)
                        Text("FG Machines — إدارة علاء محمد")
                        Spacer(Modifier.height(16.dp))
                        if (selected == null) {
                            Text("فهرس أولي: ${topics.size} موضوعًا — قيد التوثيق")
                            OutlinedTextField(
                                value = query,
                                onValueChange = { query = it },
                                label = { Text("ابحث عن صناعة أو خامة") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(12.dp))
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(topics.filter {
                                    it.name.contains(query, ignoreCase = true) ||
                                    it.group.contains(query, ignoreCase = true) ||
                                    it.id.contains(query, ignoreCase = true)
                                }, key = { it.id }) { topic ->
                                    ElevatedCard(
                                        onClick = { selected = topic },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(Modifier.padding(16.dp)) {
                                            Text(topic.name, style = MaterialTheme.typography.titleMedium)
                                            Text("${topic.id} • ${topic.group}", style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                        } else {
                            val topic = selected!!
                            TextButton(onClick = { selected = null }) { Text("رجوع إلى الفهرس") }
                            Text(topic.name, style = MaterialTheme.typography.headlineSmall)
                            Spacer(Modifier.height(12.dp))
                            Text("القسم: ${topic.group}")
                            Text("المرجع: ${topic.source} — البند ${topic.sourceItem}")
                            Text("الحالة: ${topic.status}")
                            Spacer(Modifier.height(16.dp))
                            Text("لم تُنشر تركيبة أو خطوات تصنيع معتمدة لهذا الموضوع بعد.")
                            Text("تحتاج المواصفات والسلامة والتكلفة واختبارات الجودة إلى مراجعة مصادر متخصصة.")
                        }
                    }
                }
            }
        }
    }
}
