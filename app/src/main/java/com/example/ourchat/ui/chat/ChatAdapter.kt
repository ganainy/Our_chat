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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ourchat.Utils.AuthUtil
import com.example.ourchat.data.model.Message
import com.example.ourchat.databinding.*

class ChatAdapter(private val context: Context?, private val clickListener: MessageClickListener) :
    ListAdapter<Message, RecyclerView.ViewHolder>(DiffCallbackMessages()) {


    companion object {
        private const val TYPE_SENT_MESSAGE = 0
        private const val TYPE_RECEIVED_MESSAGE = 1
        private const val TYPE_SENT_IMAGE_MESSAGE = 2
        private const val TYPE_RECEIVED_IMAGE_MESSAGE = 3
        private const val TYPE_SENT_FILE_MESSAGE = 4
        private const val TYPE_RECEIVED_FILE_MESSAGE = 5
    }


    //todo show view holder while image is uploading after selection , fix dialog layout , send audio record and files ,fix network callback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_SENT_MESSAGE -> {
                SentMessageViewHolder.from(parent)
            }
            TYPE_RECEIVED_MESSAGE -> {
                ReceivedMessageViewHolder.from(parent)
            }
            TYPE_SENT_IMAGE_MESSAGE -> {
                SentImageMessageViewHolder.from(parent)
            }
            TYPE_RECEIVED_IMAGE_MESSAGE -> {
                ReceivedImageMessageViewHolder.from(parent)
            }
            TYPE_SENT_FILE_MESSAGE -> {
                SentFileMessageViewHolder.from(parent)
            }
            TYPE_RECEIVED_FILE_MESSAGE -> {
                ReceivedFileMessageViewHolder.from(parent)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SentMessageViewHolder -> {
                holder.bind(clickListener, getItem(position))
            }
            is ReceivedMessageViewHolder -> {
                holder.bind(clickListener, getItem(position))
            }
            is SentImageMessageViewHolder -> {
                holder.bind(clickListener, getItem(position))
            }
            is ReceivedImageMessageViewHolder -> {
                holder.bind(clickListener, getItem(position))
            }
            is ReceivedFileMessageViewHolder -> {
                holder.bind(clickListener, getItem(position))
            }
            is SentFileMessageViewHolder -> {
                holder.bind(clickListener, getItem(position))
            }
            else -> throw IllegalArgumentException("Invalid ViewHolder type")
        }
    }

    override fun getItemViewType(position: Int): Int {

        val currentMessage = getItem(position)

        if (currentMessage.from == AuthUtil.getAuthId() && currentMessage.type == 0L) {
            return TYPE_SENT_MESSAGE
        } else if (currentMessage.from != AuthUtil.getAuthId() && currentMessage.type == 0L) {
            return TYPE_RECEIVED_MESSAGE
        } else if (currentMessage.from == AuthUtil.getAuthId() && currentMessage.type == 1L) {
            return TYPE_SENT_IMAGE_MESSAGE
        } else if (currentMessage.from != AuthUtil.getAuthId() && currentMessage.type == 1L) {
            return TYPE_RECEIVED_IMAGE_MESSAGE
        } else if (currentMessage.from == AuthUtil.getAuthId() && currentMessage.type == 3L) {
            return TYPE_SENT_FILE_MESSAGE
        } else if (currentMessage.from != AuthUtil.getAuthId() && currentMessage.type == 3L) {
            return TYPE_RECEIVED_FILE_MESSAGE
        } else {
            throw IllegalArgumentException("Invalid ItemViewType")
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

    //----------------SentImageMessageViewHolder------------
    class SentImageMessageViewHolder private constructor(val binding: SentChatImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: MessageClickListener, item: Message) {
            binding.message = item
            binding.clickListener = clickListener
            binding.position = adapterPosition
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): SentImageMessageViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SentChatImageItemBinding.inflate(layoutInflater, parent, false)

                return SentImageMessageViewHolder(binding)
            }
        }
    }


    //----------------ReceivedImageMessageViewHolder------------
    class ReceivedImageMessageViewHolder private constructor(val binding: IncomingChatImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: MessageClickListener, item: Message) {
            binding.message = item
            binding.clickListener = clickListener
            binding.position = adapterPosition
            binding.executePendingBindings()//
        }

        companion object {
            fun from(parent: ViewGroup): ReceivedImageMessageViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = IncomingChatImageItemBinding.inflate(layoutInflater, parent, false)

                return ReceivedImageMessageViewHolder(binding)
            }
        }
    }


    //----------------SentFileMessageViewHolder------------
    class SentFileMessageViewHolder private constructor(val binding: SentChatFileItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: MessageClickListener, item: Message) {
            binding.message = item
            binding.clickListener = clickListener
            binding.position = adapterPosition
            binding.executePendingBindings()//
        }

        companion object {
            fun from(parent: ViewGroup): SentFileMessageViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SentChatFileItemBinding.inflate(layoutInflater, parent, false)

                return SentFileMessageViewHolder(binding)
            }
        }
    }


    //----------------ReceivedFileMessageViewHolder------------
    class ReceivedFileMessageViewHolder private constructor(val binding: IncomingChatFileItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: MessageClickListener, item: Message) {
            binding.message = item
            binding.clickListener = clickListener
            binding.position = adapterPosition
            binding.executePendingBindings()//
        }

        companion object {
            fun from(parent: ViewGroup): ReceivedFileMessageViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = IncomingChatFileItemBinding.inflate(layoutInflater, parent, false)

                return ReceivedFileMessageViewHolder(binding)
            }
        }
    }




}

interface MessageClickListener {
    fun onMessageClick(position: Int, message: Message)
}


class DiffCallbackMessages : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}







