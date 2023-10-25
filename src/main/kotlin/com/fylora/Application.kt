package com.fylora

import com.fylora.model.Chat
import com.fylora.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val chat = Chat()

    configureSockets()
    configureMonitoring()
    configureSerialization()
    configureRouting(chat)
}
