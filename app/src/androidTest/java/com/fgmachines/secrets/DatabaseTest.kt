package com.fgmachines.secrets

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fgmachines.secrets.data.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    @Test fun importIsRepeatableAndPreservesFavorites() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val db = Room.inMemoryDatabaseBuilder(context, CatalogDatabase::class.java).build()
        try {
            val repository = CatalogRepository(context, db)
            repository.importCatalog(); repository.setFavorite("ADH-001", true); repository.importCatalog()
            assertEquals(71, repository.topics.first().size)
            assertEquals(listOf(FavoriteEntity("ADH-001")), repository.favorites.first())
            val first = repository.topics.first().first().topic()
            repository.importPack(listOf(first.copy(name = "Updated display title")))
            repository.importCatalog()
            assertEquals("Updated display title", repository.topics.first().first().name)
            try {
                repository.importPack(listOf(first.copy(id = "OTHER-001")))
                fail("Duplicate source item accepted")
            } catch (_: IllegalArgumentException) {}
            assertEquals(71, repository.topics.first().size)
            repository.setFavorite("ADH-001", false)
            assertTrue(repository.favorites.first().isEmpty())
        } finally { db.close() }
    }
}
