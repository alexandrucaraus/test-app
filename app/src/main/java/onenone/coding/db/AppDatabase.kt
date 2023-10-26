package onenone.coding.db

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single


@Entity
data class Content(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "content") val content: String?,
)

@Dao
interface ContentDao {
    @Query("SELECT * FROM Content WHERE uid IN (:contentId)")
    fun loadAllByIds(contentId: Array<String>): List<Content>

    @Insert
    fun insertAll(vararg contents: Content)

    @Delete
    fun delete(content: Content)

}

@Database(entities = [Content::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao
}

@Module
class DatabaseModule {
    @Single
    fun databaseModule(appContext: Context): AppDatabase =
        Room.databaseBuilder(appContext, AppDatabase::class.java, "content").build()

    @Single
    fun contentDao(appDatabase: AppDatabase): ContentDao = appDatabase.contentDao()
}