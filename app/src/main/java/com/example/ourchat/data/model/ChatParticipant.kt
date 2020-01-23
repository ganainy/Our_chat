package com.example.ourchat.data.model

import com.google.firebase.Timestamp


data class ChatParticipant(
    var particpant: User? = null,
    var lastMessage: String? = null,
    var lastMessageDate: Timestamp? = null,
    var isLoggedUser: Boolean? = null,
    var lastMessageType: Double? = null
)