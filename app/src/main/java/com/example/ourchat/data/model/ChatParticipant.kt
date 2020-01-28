package com.example.ourchat.data.model


data class ChatParticipant(
    var particpant: User? = null,
    var lastMessage: String? = null,
    var lastMessageDate: Map<String, Double>? = null,
    var isLoggedUser: Boolean? = null,
    var lastMessageType: Double? = null
)
