package com.fgmachines.secrets.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "topics")
data class TopicEntity(@PrimaryKey val id: String, val name: String, val category: String, val source: String, val sourceItem: Int, val status: String, val originalText: String, val originalNotes: String) {
    fun topic() = Topic(id, name, category, source, sourceItem, status, originalText, originalNotes)
}
@Entity(tableName = "favorites")
data class FavoriteEntity(@PrimaryKey val key: String)
@Dao
interface CatalogDao {
    @Query("SELECT * FROM topics ORDER BY id") fun observeTopics(): Flow<List<TopicEntity>>
    @Query("SELECT * FROM topics") suspend fun allTopics(): List<TopicEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertTopics(topics: List<TopicEntity>)
    @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun seedTopics(topics: List<TopicEntity>)
    @Query("SELECT * FROM favorites ORDER BY `key`") fun observeFavorites(): Flow<List<FavoriteEntity>>
    @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun addFavorite(favorite: FavoriteEntity)
    @Query("DELETE FROM favorites WHERE `key` = :key") suspend fun removeFavorite(key: String)
}
@Database(entities = [TopicEntity::class, FavoriteEntity::class], version = 1, exportSchema = true)
abstract class CatalogDatabase : RoomDatabase() {
    abstract fun catalog(): CatalogDao
    companion object {
        @Volatile private var instance: CatalogDatabase? = null
        fun get(context: Context): CatalogDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, CatalogDatabase::class.java, "fg-secrets.db").build().also { instance = it }
        }
    }
}
class CatalogRepository(private val context: Context, private val db: CatalogDatabase) {
    val topics = db.catalog().observeTopics()
    val favorites = db.catalog().observeFavorites()
    suspend fun importCatalog() {
        val topics = context.assets.open("catalog.json").bufferedReader(Charsets.UTF_8).use { parseCatalog(it.readText()) }
        db.withTransaction {
            db.catalog().seedTopics(topics.map { it.entity() })
        }
    }
    suspend fun importPack(topics: List<Topic>) {
        db.withTransaction {
            val incoming = topics.map { it.id }.toSet()
            val combined = db.catalog().allTopics().filter { it.id !in incoming }.map { it.topic() } + topics
            require(combined.map { it.source to it.sourceItem }.distinct().size == combined.size) { "Source item already assigned to another topic" }
            db.catalog().upsertTopics(topics.map { it.entity() })
        }
    }
    private fun Topic.entity() = TopicEntity(id, name, group, source, sourceItem, status, originalText, originalNotes)
    suspend fun setFavorite(key: String, enabled: Boolean) {
        if (enabled) db.catalog().addFavorite(FavoriteEntity(key)) else db.catalog().removeFavorite(key)
    }
}
