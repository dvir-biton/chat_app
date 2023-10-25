package com.fylora.model

sealed class Logging(val message: String) {
    data class UserJoined(val username: Username):
        Logging("The user $username joined the chat")
    data class UserLeft(val username: Username):
        Logging("The user $username left the chat")
    data class MessageSent(val messageBody: String, val user: Username):
        Logging("$user sent: $messageBody")
    data object ServerOpened: Logging("Room chat opened")
}

fun log(log: Logging) {
    println(log.message)
}