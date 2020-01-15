package com.example.ourchat.data.model


data class Message(
    val from: String?,
    val date: Long?,
    val text: String?,
    val imageUri: String?,
    val fileUri: String?,
    val fileName: String?,
    val type: Long?
) {
    constructor() : this(null, null, null, null, null, null, null)
}