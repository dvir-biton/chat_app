package com.fylora

import com.fylora.model.Chat
import com.fylora.model.Message
import com.fylora.model.User.Companion.send
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlin.random.Random

fun Route.socket(chat: Chat) {
    route("/chat") {
        webSocket {
            val username = call.request.queryParameters["username"]

            if(username == null) {
                close(
                    CloseReason(
                        CloseReason.Codes.CANNOT_ACCEPT,
                        message = "Username is required"
                    )
                )
                return@webSocket
            }

            if(chat.isUsernameAlreadyTaken(username)) {
                close(
                    CloseReason(
                        CloseReason.Codes.CANNOT_ACCEPT,
                        message = "Username is already taken"
                    )
                )
                return@webSocket
            }

            val user = chat.connectUser(
                username = username,
                session = this
            ) ?: return@webSocket

            chat.broadcastExceptForOneUser(
                message = Message(
                    author = "System",
                    body = "User $username joined the chat!",
                    timeStamp = System.currentTimeMillis()
                ),
                user = user
            )

            try {
                incoming.consumeEach { frame ->
                    if(frame is Frame.Text) {
                        val messageBody = frame.readText()
                        val message = Message(
                            author = username,
                            body = messageBody,
                            timeStamp = System.currentTimeMillis()
                        )

                        chat.broadcastExceptForOneUser(message, user)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                chat.disconnectUser(username)
            }
        }
    }
}