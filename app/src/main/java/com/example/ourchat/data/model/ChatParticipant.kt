package com.example.ourchat.data.model


data class ChatParticipant(
    var particpant: User? = null,
    var lastMessage: String? = null,
    var lastMessageDate: Map<String, Double>? = null,
    var isLoggedUser: Boolean? = null,
    var lastMessageType: Double? = null
)

class LastMessageDateMap(val map: Map<String, Any?>) {
    val name: String by map
    val age: Int by map
}