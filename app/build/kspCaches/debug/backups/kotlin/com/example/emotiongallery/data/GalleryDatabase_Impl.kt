package com.example.emotiongallery.`data`

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class GalleryDatabase_Impl : GalleryDatabase() {
  private val _galleryDao: Lazy<GalleryDao> = lazy {
    GalleryDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(4, "88de81299c87c917d5bdfdf408603575", "351a79944db3cf9161739bb96436af8f") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `photos` (`id` INTEGER NOT NULL, `colorStart` INTEGER NOT NULL, `colorEnd` INTEGER NOT NULL, `imageRes` INTEGER, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `records` (`photoId` INTEGER NOT NULL, `emotion` TEXT NOT NULL, `memo` TEXT NOT NULL, PRIMARY KEY(`photoId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '88de81299c87c917d5bdfdf408603575')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `photos`")
        connection.execSQL("DROP TABLE IF EXISTS `records`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsPhotos: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsPhotos.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPhotos.put("colorStart", TableInfo.Column("colorStart", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPhotos.put("colorEnd", TableInfo.Column("colorEnd", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPhotos.put("imageRes", TableInfo.Column("imageRes", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPhotos: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesPhotos: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoPhotos: TableInfo = TableInfo("photos", _columnsPhotos, _foreignKeysPhotos, _indicesPhotos)
        val _existingPhotos: TableInfo = read(connection, "photos")
        if (!_infoPhotos.equals(_existingPhotos)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |photos(com.example.emotiongallery.data.GalleryPhotoEntity).
              | Expected:
              |""".trimMargin() + _infoPhotos + """
              |
              | Found:
              |""".trimMargin() + _existingPhotos)
        }
        val _columnsRecords: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsRecords.put("photoId", TableInfo.Column("photoId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRecords.put("emotion", TableInfo.Column("emotion", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRecords.put("memo", TableInfo.Column("memo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysRecords: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesRecords: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoRecords: TableInfo = TableInfo("records", _columnsRecords, _foreignKeysRecords, _indicesRecords)
        val _existingRecords: TableInfo = read(connection, "records")
        if (!_infoRecords.equals(_existingRecords)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |records(com.example.emotiongallery.data.PhotoRecordEntity).
              | Expected:
              |""".trimMargin() + _infoRecords + """
              |
              | Found:
              |""".trimMargin() + _existingRecords)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "photos", "records")
  }

  public override fun clearAllTables() {
    super.performClear(false, "photos", "records")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(GalleryDao::class, GalleryDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun galleryDao(): GalleryDao = _galleryDao.value
}
