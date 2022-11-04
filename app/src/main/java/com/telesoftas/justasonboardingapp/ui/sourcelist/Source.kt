package com.telesoftas.justasonboardingapp.ui.sourcelist

import com.telesoftas.justasonboardingapp.utils.data.SourceEntity

data class Source(
    val id: String,
    val title: String,
    val description: String
) {
    fun toEntity(): SourceEntity {
        return SourceEntity(
            id = id.toIntOrNull() ?: 0,
            title = title,
            description = description
        )
    }
}
