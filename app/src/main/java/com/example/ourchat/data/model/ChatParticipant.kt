package com.example.ourchat.data.model


data class ChatParticipant(
    var particpant: User? = null,
    var lastMessage: String? = null,
    var lastMessageDate: Long? = null,
    var isLoggedUser: Boolean? = null,
    var uri: String? = null,
    var lastMessageType: Long? = null,
    var name: String? = null


)