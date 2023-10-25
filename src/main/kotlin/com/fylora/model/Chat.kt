package com.fylora.model

import com.fylora.model.User.Companion.send
import io.ktor.websocket.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

@OptIn(DelicateCoroutinesApi::class)
class Chat {

    private val state = MutableStateFlow(ChatState())

    init {
        GlobalScope.launch {
            broadcast(
                Message(
                    author = "System",
                    body = "Chat Room opened!",
                    timeStamp = System.currentTimeMillis()
                )
            )
            log(Logging.ServerOpened)
        }
    }

    fun connectUser(
        username: Username,
        session: WebSocketSession
    ): User? {
        val isUsernameAlreadyExists = state.value.connectedUsers.any { it.username == username }

        if(isUsernameAlreadyExists || username == "System") {
            return null
        }

        val user = User(
            username = username,
            session = session
        )

        state.update {
            it.copy(
                connectedUsers = it.connectedUsers + user
            )
        }

        GlobalScope.launch {
            state.value.messages.forEach {
                user.send(it)
            }
            user.send(
                Message(
                    author = "System",
                    body = "Welcome to the chat!",
                    timeStamp = System.currentTimeMillis()
                )
            )
        }
        log(Logging.UserJoined(username))

        return user
    }

    fun disconnectUser(username: Username) {
        val user = state.value.connectedUsers.find { it.username == username } ?: return

        state.update {
            it.copy(
                connectedUsers = state.value.connectedUsers - user
            )
        }

        GlobalScope.launch {
            broadcast(
                message = Message(
                    author = "System",
                    body = "$username disconnected :(",
                    timeStamp = System.currentTimeMillis()
                )
            )
        }

        log(Logging.UserLeft(user.username))
    }

    private suspend fun broadcast(message: Message) {
        state.update {
            it.copy(
                messages = state.value.messages + message
            )
        }

        state.value.connectedUsers.forEach { user ->
            user.session.send(
                Json.encodeToString(value = message)
            )
        }

        log(Logging.MessageSent(message.body, message.author))
    }

    suspend fun broadcastExceptForOneUser(message: Message, user: User) {
        state.update {
            it.copy(
                messages = state.value.messages + message
            )
        }

        state.value.connectedUsers.forEach {
            if(it.username != user.username) {
                it.session.send(
                    Json.encodeToString(value = message)
                )
            }
        }
        log(Logging.MessageSent(message.body, message.author))
    }

    fun isUsernameAlreadyTaken(username: Username): Boolean =
        state.value.connectedUsers.any { it.username == username }
}
