package com.example.emotiongallery.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GalleryDao {
    @Query("SELECT * FROM photos")
    fun getAllPhotos(): Flow<List<GalleryPhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<GalleryPhotoEntity>)

    @Query("SELECT * FROM records")
    fun getAllRecords(): Flow<List<PhotoRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: PhotoRecordEntity)

    @Query("DELETE FROM records")
    suspend fun deleteAllRecords()

    @Query("DELETE FROM photos")
    suspend fun deleteAllPhotos()
}
