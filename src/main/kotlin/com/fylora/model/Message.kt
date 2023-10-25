package com.fylora.model

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val author: Username,
    val body: String,
    val timeStamp: Long,
)
