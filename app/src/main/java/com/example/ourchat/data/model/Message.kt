package com.example.ourchat.data.model


data class Message(
    val from: String?,
    val date: Long?,
    val text: String?,
    val uri: String?,
    val name: String?,
    val type: Long?
) {
    constructor() : this(null, null, null, null, null, null)
}