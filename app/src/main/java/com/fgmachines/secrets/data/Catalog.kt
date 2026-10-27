package com.fgmachines.secrets.data

import org.json.JSONObject
import java.text.Normalizer

fun searchKey(text: String): String = Normalizer.normalize(text, Normalizer.Form.NFD)
    .replace(Regex("[\\p{M}ـ]"), "").replace('أ', 'ا').replace('إ', 'ا').replace('آ', 'ا').replace('ى', 'ي').lowercase(java.util.Locale.ROOT)

data class Topic(val id: String, val name: String, val group: String, val source: String, val sourceItem: Int, val status: String, val originalText: String, val originalNotes: String) {
    fun matches(query: String): Boolean = searchKey("$name $group $id $originalText").contains(searchKey(query.trim()))
}
fun parseCatalog(json: String): List<Topic> {
    val root = JSONObject(json)
    require(root.getInt("schemaVersion") == 2) { "Unsupported catalog schema" }
    val array = root.getJSONArray("topics")
    val topics = (0 until array.length()).map { i ->
        val entry = array.getJSONObject(i)
        Topic(entry.getString("id"), entry.getString("title"), entry.getString("category"), entry.getString("source"), entry.getInt("sourceItem"), entry.getString("status"), entry.getString("originalText"), entry.optString("originalNotes"))
    }
    require(topics.map { it.id }.distinct().size == topics.size) { "Duplicate topic ID" }
    require(topics.map { it.source to it.sourceItem }.distinct().size == topics.size) { "Duplicate source item" }
    require(topics.all { it.id.isNotBlank() && it.name.isNotBlank() && it.group.isNotBlank() && it.source.isNotBlank() && it.originalText.isNotBlank() && it.sourceItem > 0 }) { "Incomplete topic" }
    return topics
}
