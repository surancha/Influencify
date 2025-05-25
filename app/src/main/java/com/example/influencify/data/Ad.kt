package com.example.influencify.data

data class Ad(
    val key: String = "",
    val title: String = "",
    val description: String = "",
    val price: String = "",
    val urLink: String = "",
    val platform: String = "",
    val imageUrl: String = "",
    val isFavorite: Boolean = false,
    val creatorUid: String = "",
    val category: String = "Fashion",
    val currency: String = "$",
    val averageRating: Float = 0f // Новое поле для среднего рейтинга
)
//package com.example.influencify.data
//
//data class Ad(
//    val key: String = "",
//    val title: String = "",
//    val description: String = "",
//    val price: String = "",
//    val platform: String = "",
//    val urLink: String = "",
//    val imageUrl: String = "",
//    val isFavorite: Boolean = false,
//    val creatorUid: String = ""
//)