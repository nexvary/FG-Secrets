package com.fgmachines.secrets.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.settings by preferencesDataStore("settings")
class SettingsRepository(private val context: Context) {
    private val languageKey = stringPreferencesKey("language")
    val language = context.settings.data.catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }.map { it[languageKey] ?: "ar" }
    suspend fun setLanguage(language: String) { require(language in listOf("ar", "en")); context.settings.edit { it[languageKey] = language } }
}
