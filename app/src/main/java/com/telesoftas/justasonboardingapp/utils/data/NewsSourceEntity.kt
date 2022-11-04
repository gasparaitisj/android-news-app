package com.telesoftas.justasonboardingapp.utils.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.telesoftas.justasonboardingapp.ui.sourcelist.Source

@Entity(tableName = "news_source")
data class NewsSourceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String
) {
    fun toNewsSource(): Source {
        return Source(
            id = id.toString(),
            title = title,
            description = description
        )
    }
}
