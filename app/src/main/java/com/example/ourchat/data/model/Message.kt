package com.example.ourchat.data.model

import com.google.firebase.Timestamp


interface Message {
    val from: String?
    val created_at: Timestamp?
    val type: Double?
}


/**0*/
data class TextMessage(
    override val from: String?,
    override val created_at: Timestamp?,
    override val type: Double?,
    val text: String?

) : Message


/**1*/
data class ImageMessage(

    override val from: String?,
    override val created_at: Timestamp?,
    override val type: Double?,
    val uri: String?

) : Message


/**2*/
data class FileMessage(
    override val from: String?,
    override val created_at: Timestamp?,
    override val type: Double?,
    val name: String?,
    val uri: String?
) : Message

/**3*/
data class RecordMessage(

    override val from: String?,
    override val created_at: Timestamp?,
    override val type: Double?,
    var duration: String?,
    val uri: String?,
    var currentProgress: String?,
    var isPlaying: Boolean?

) : Message



