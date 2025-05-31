package com.example.influencify.data

data class Message(
    val senderUid: String = "",
    val receiverUid: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
