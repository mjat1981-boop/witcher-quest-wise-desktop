package com.example.data

data class ChatMessage(
    val id: String,
    val sender: String, // "USER" or "GERALT"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
