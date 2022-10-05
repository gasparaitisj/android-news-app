package com.telesoftas.justasonboardingapp.utils.network.data

import com.squareup.moshi.Json

data class ArticlePreviewResponse (
    @Json(name="id")
    val id: String,

    @Json(name="votes")
    val votes: Long,

    @Json(name="publishedAt")
    val publishedAt: String,

    @Json(name="source")
    val source: String? = null,

    @Json(name="category")
    val category: ArticleCategory,

    @Json(name="author")
    val author: String? = null,

    @Json(name="title")
    val title: String? = null,

    @Json(name="description")
    val description: String? = null,

    @Json(name="imageUrl")
    val imageUrl: String? = null
)

enum class ArticleCategory(val value: String) {
    @Json(name="Politics")
    POLITICS("Politics"),

    @Json(name="Gaming")
    GAMING("Gaming"),

    @Json(name="Sports")
    SPORTS("Sports"),

    @Json(name="Culture")
    CULTURE("Culture"),

    @Json(name="Food")
    FOOD("Food"),

    @Json(name="Business")
    BUSINESS("Business"),

    @Json(name="Health")
    HEALTH("Health"),

    @Json(name="Other")
    OTHER("Other"),

    NONE("")
}

enum class SortBy {
    ASCENDING,
    DESCENDING,
    NONE
}
