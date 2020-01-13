/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.ourchat.ui.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ourchat.Utils.AuthUtil


import com.example.ourchat.data.model.Message
import com.example.ourchat.databinding.IncomingMessageItemBinding
import com.example.ourchat.databinding.SentMessageItemBinding

class ChatAdapter(private val context: Context?, private val clickListener: MessageClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var messageList: List<Message>

    companion object {
        private const val TYPE_SENT_MESSAGE = 0
        private const val TYPE_RECEIVED_MESSAGE = 1
    }

    fun setDataSource(mMessageList: List<Message>) {
        messageList = mMessageList

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_SENT_MESSAGE -> {
                SentMessageViewHolder.from(parent)
            }
            TYPE_RECEIVED_MESSAGE -> {
                ReceivedMessageViewHolder.from(parent)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount() = messageList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SentMessageViewHolder -> {
                holder.bind(clickListener, messageList[position])
            }
            is ReceivedMessageViewHolder -> {
                holder.bind(clickListener, messageList[position])
            }
            else -> throw IllegalArgumentException("Invalid ViewHolder type")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (messageList[position].from) {
            AuthUtil.getAuthId() -> {
                TYPE_SENT_MESSAGE
            }
            else -> {
                TYPE_RECEIVED_MESSAGE
            }
        }
    }


    //----------------SentMessageViewHolder------------
    class SentMessageViewHolder private constructor(val binding: SentMessageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: MessageClickListener, item: Message) {
            binding.message = item
            binding.clickListener = clickListener
            binding.position = adapterPosition
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): SentMessageViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SentMessageItemBinding.inflate(layoutInflater, parent, false)

                return SentMessageViewHolder(binding)
            }
        }


    }

    //----------------ReceivedMessageViewHolder------------
    class ReceivedMessageViewHolder private constructor(val binding: IncomingMessageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: MessageClickListener, item: Message) {
            binding.message = item
            binding.clickListener = clickListener
            binding.position = adapterPosition
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ReceivedMessageViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = IncomingMessageItemBinding.inflate(layoutInflater, parent, false)

                return ReceivedMessageViewHolder(binding)
            }
        }
    }


}

interface MessageClickListener {
    fun onMessageClick(position: Int)
}









