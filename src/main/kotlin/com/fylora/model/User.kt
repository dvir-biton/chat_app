package com.fylora.model

import io.ktor.websocket.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

typealias Username = String

@Serializable
data class User(
    val username: Username,
    val session: WebSocketSession,
) {
    companion object {
        suspend fun User.send(message: Message) {
            this.session.send(
                Json.encodeToString(message)
            )
        }
    }
}