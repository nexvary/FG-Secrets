package com.fgmachines.secrets.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fgmachines.secrets.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SecretsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CatalogRepository(application, CatalogDatabase.get(application))
    private val settings = SettingsRepository(application)
    val topics = repository.topics.map { rows -> rows.map { it.topic() } }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val favorites = repository.favorites.map { rows -> rows.map { it.key }.toSet() }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())
    val language = settings.language.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "ar")
    private val _error = MutableStateFlow(false)
    val error = _error.asStateFlow()
    private val _loading = MutableStateFlow(true)
    val loading = _loading.asStateFlow()
    init { reload() }
    fun reload() { viewModelScope.launch(Dispatchers.IO) {
        _loading.value = true; _error.value = false
        try { repository.importCatalog() } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            _error.value = true
        } finally { _loading.value = false }
    } }
    fun favorite(key: String, enabled: Boolean) { viewModelScope.launch { try { repository.setFavorite(key, enabled) } catch (e: Exception) { if (e is kotlinx.coroutines.CancellationException) throw e; _error.value = true } } }
    fun language(language: String) { viewModelScope.launch { try { settings.setLanguage(language) } catch (e: Exception) { if (e is kotlinx.coroutines.CancellationException) throw e; _error.value = true } } }
}
