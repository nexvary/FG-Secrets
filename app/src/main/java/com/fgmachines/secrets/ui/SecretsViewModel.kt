package com.fgmachines.secrets.ui

import android.app.Application
import android.net.Uri
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
    private val _pendingPack = MutableStateFlow<List<Topic>?>(null)
    val pendingPack = _pendingPack.asStateFlow()
    private val _importedCount = MutableStateFlow<Int?>(null)
    val importedCount = _importedCount.asStateFlow()
    fun previewPack(uri: Uri) { viewModelScope.launch(Dispatchers.IO) {
        try {
            val bytes = getApplication<Application>().contentResolver.openInputStream(uri)?.use { stream ->
                val output = java.io.ByteArrayOutputStream()
                val buffer = ByteArray(8192)
                while (true) {
                    val count = stream.read(buffer)
                    if (count == -1) break
                    require(output.size() + count <= 8 * 1024 * 1024) { "Pack too large" }
                    output.write(buffer, 0, count)
                }
                output.toByteArray() } ?: error("Cannot read pack")
            require(bytes.size <= 8 * 1024 * 1024) { "Pack too large" }
            val pack = parseCatalog(bytes.toString(Charsets.UTF_8))
            require(pack.isNotEmpty())
            _pendingPack.value = pack
        } catch (e: Exception) { if (e is kotlinx.coroutines.CancellationException) throw e; _error.value = true }
    } }
    fun cancelImport() { _pendingPack.value = null }
    fun confirmImport() {
        val pack = _pendingPack.value ?: return
        _pendingPack.value = null
        viewModelScope.launch(Dispatchers.IO) {
            try { repository.importPack(pack); _importedCount.value = pack.size }
            catch (e: Exception) { if (e is kotlinx.coroutines.CancellationException) throw e; _error.value = true }
        }
    }
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
