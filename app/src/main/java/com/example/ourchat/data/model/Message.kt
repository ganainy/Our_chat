package com.example.ourchat.data.model


data class Message(val from: String?, val date: Long?, val text: String?, val imagUri: String?) {
    constructor() : this(null, null, null, null)
}