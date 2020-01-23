package com.example.ourchat.Utils.eventbus_events

import com.example.ourchat.data.model.RecordMessage

class UpdateRecordEvent(
    val item: RecordMessage,
    val adapterPosition: Int
)