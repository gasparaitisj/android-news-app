package com.telesoftas.justasonboardingapp.utils.network.data

import com.squareup.moshi.Json

data class Article (
    val id: String?,
    val votes: Long?,
    val publishedAt: String?,
    val source: String?,
    val category: ArticleCategory?,
    val author: String?,
    val title: String?,
    val description: String?,
    val imageURL: String?
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
    OTHER("Other")
}
