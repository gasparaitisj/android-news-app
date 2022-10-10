package com.telesoftas.justasonboardingapp.utils.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Article(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "published_at") val publishedAt: String,
    @ColumnInfo(name = "source") val source: String?,
    @ColumnInfo(name = "category") val category: Int,
    @ColumnInfo(name = "author") val author: String?,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "image_url") val imageUrl: String?
)
