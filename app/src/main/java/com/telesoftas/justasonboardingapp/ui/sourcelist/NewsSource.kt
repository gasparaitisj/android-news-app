package com.telesoftas.justasonboardingapp.ui.sourcelist

import com.telesoftas.justasonboardingapp.utils.data.NewsSourceEntity

data class NewsSource(
    val id: String,
    val title: String,
    val description: String
) {
    fun toEntity(): NewsSourceEntity {
        return NewsSourceEntity(
            id = id.toIntOrNull() ?: 0,
            title = title,
            description = description
        )
    }
}
