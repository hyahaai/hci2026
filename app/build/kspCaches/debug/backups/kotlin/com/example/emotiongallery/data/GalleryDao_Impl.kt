package com.example.emotiongallery.`data`

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class GalleryDao_Impl(
  __db: RoomDatabase,
) : GalleryDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfGalleryPhotoEntity: EntityInsertAdapter<GalleryPhotoEntity>

  private val __insertAdapterOfPhotoRecordEntity: EntityInsertAdapter<PhotoRecordEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfGalleryPhotoEntity = object : EntityInsertAdapter<GalleryPhotoEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `photos` (`id`,`colorStart`,`colorEnd`,`imageRes`) VALUES (?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: GalleryPhotoEntity) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindLong(2, entity.colorStart.toLong())
        statement.bindLong(3, entity.colorEnd.toLong())
        val _tmpImageRes: Int? = entity.imageRes
        if (_tmpImageRes == null) {
          statement.bindNull(4)
        } else {
          statement.bindLong(4, _tmpImageRes.toLong())
        }
      }
    }
    this.__insertAdapterOfPhotoRecordEntity = object : EntityInsertAdapter<PhotoRecordEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `records` (`photoId`,`emotion`,`memo`) VALUES (?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: PhotoRecordEntity) {
        statement.bindLong(1, entity.photoId.toLong())
        statement.bindText(2, entity.emotion)
        statement.bindText(3, entity.memo)
      }
    }
  }

  public override suspend fun insertPhotos(photos: List<GalleryPhotoEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfGalleryPhotoEntity.insert(_connection, photos)
  }

  public override suspend fun insertRecord(record: PhotoRecordEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfPhotoRecordEntity.insert(_connection, record)
  }

  public override fun getAllPhotos(): Flow<List<GalleryPhotoEntity>> {
    val _sql: String = "SELECT * FROM photos"
    return createFlow(__db, false, arrayOf("photos")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfColorStart: Int = getColumnIndexOrThrow(_stmt, "colorStart")
        val _columnIndexOfColorEnd: Int = getColumnIndexOrThrow(_stmt, "colorEnd")
        val _columnIndexOfImageRes: Int = getColumnIndexOrThrow(_stmt, "imageRes")
        val _result: MutableList<GalleryPhotoEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: GalleryPhotoEntity
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpColorStart: Int
          _tmpColorStart = _stmt.getLong(_columnIndexOfColorStart).toInt()
          val _tmpColorEnd: Int
          _tmpColorEnd = _stmt.getLong(_columnIndexOfColorEnd).toInt()
          val _tmpImageRes: Int?
          if (_stmt.isNull(_columnIndexOfImageRes)) {
            _tmpImageRes = null
          } else {
            _tmpImageRes = _stmt.getLong(_columnIndexOfImageRes).toInt()
          }
          _item = GalleryPhotoEntity(_tmpId,_tmpColorStart,_tmpColorEnd,_tmpImageRes)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllRecords(): Flow<List<PhotoRecordEntity>> {
    val _sql: String = "SELECT * FROM records"
    return createFlow(__db, false, arrayOf("records")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfPhotoId: Int = getColumnIndexOrThrow(_stmt, "photoId")
        val _columnIndexOfEmotion: Int = getColumnIndexOrThrow(_stmt, "emotion")
        val _columnIndexOfMemo: Int = getColumnIndexOrThrow(_stmt, "memo")
        val _result: MutableList<PhotoRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: PhotoRecordEntity
          val _tmpPhotoId: Int
          _tmpPhotoId = _stmt.getLong(_columnIndexOfPhotoId).toInt()
          val _tmpEmotion: String
          _tmpEmotion = _stmt.getText(_columnIndexOfEmotion)
          val _tmpMemo: String
          _tmpMemo = _stmt.getText(_columnIndexOfMemo)
          _item = PhotoRecordEntity(_tmpPhotoId,_tmpEmotion,_tmpMemo)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAllRecords() {
    val _sql: String = "DELETE FROM records"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAllPhotos() {
    val _sql: String = "DELETE FROM photos"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
