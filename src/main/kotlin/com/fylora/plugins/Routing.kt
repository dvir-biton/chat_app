package com.fylora.plugins

import com.fylora.model.Chat
import com.fylora.socket
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(chat: Chat) {
    routing {
        get("/") {
            call.respondText("dayum")
        }
        socket(chat)
    }
}
