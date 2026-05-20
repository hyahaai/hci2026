package com.example.emotiongallery.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class GalleryPhotoEntity(
    @PrimaryKey val id: Int,
    val colorStart: Int,
    val colorEnd: Int,
    val imageRes: Int? = null
)

@Entity(tableName = "records")
data class PhotoRecordEntity(
    @PrimaryKey val photoId: Int,
    val emotion: String,
    val memo: String
)
