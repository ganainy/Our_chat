package com.example.ourchat.data.model


data class LastMessageOwner(
    var ownerUser: User? = null,
    var text: String? = null,
    var date: Long? = null

)