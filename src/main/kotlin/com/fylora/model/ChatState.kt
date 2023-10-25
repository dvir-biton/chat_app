package com.fylora.model

data class ChatState(
    val messages: List<Message> = emptyList<Message>(),
    val connectedUsers: List<User> = emptyList<User>()
)
