package com.example.ourchat.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ourchat.data.model.LastMessageOwner
import com.example.ourchat.databinding.ChatOuterItemBinding

var mQuery = ""


class ChatPreviewAdapter(private val clickListener: ClickListener) :
    ListAdapter<LastMessageOwner, ChatPreviewAdapter.ViewHolder>(DiffCallbackUsers())
/*, Filterable,OnQueryTextChange*/ {


    var chatList = mutableListOf<LastMessageOwner?>()
    var filteredChatList = mutableListOf<LastMessageOwner?>()


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(clickListener, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ChatOuterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: ClickListener, lastMessageOwner: LastMessageOwner) {
            println("ViewHolder.bind:")

            binding.lastMessageOwner = lastMessageOwner
            binding.clickListener = clickListener
            //if query text isn't empty set the selected text with sky blue+bold
            /* if (mQuery.isEmpty()) {
                 binding.usernameTextView.text = userName
             } else {
                 var index = userName.indexOf(mQuery, 0, true)
                 val sb = SpannableStringBuilder(userName)
                 while (index >= 0) {
                     val fcs = ForegroundColorSpan(Color.rgb(135, 206, 235))
                     sb.setSpan(
                         fcs,
                         index,
                         index + mQuery.length,
                         Spannable.SPAN_INCLUSIVE_INCLUSIVE
                     )
                     sb.setSpan(
                         StyleSpan(Typeface.BOLD),
                         index,
                         index + mQuery.length,
                         Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                     )
                     index = userName.indexOf(mQuery, index + 1)
                 }
                 binding.usernameTextView.text = sb
             }
             binding.clickListener = clickListener
             binding.user = item
             binding.executePendingBindings()*/
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ChatOuterItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }


    /*   override fun getFilter(): Filter {
           return object : Filter() {
               override fun performFiltering(charSequence: CharSequence): FilterResults {
                   val charString = charSequence.toString()
                   filteredChatList = mutableListOf()
                   if (charString.isEmpty()) {
                       filteredChatList = chatList


                   } else {
                       for (user in chatList) {
                           if (user?.username?.toLowerCase(Locale.ENGLISH)?.contains(
                                   charString.toLowerCase(Locale.ENGLISH)
                               )!!
                           ) {
                               filteredChatList.add(user)
                           }
                       }
                   }
                   val filterResults = FilterResults()
                   filterResults.values = filteredChatList
                   return filterResults
               }

               override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {

                   submitList(filterResults.values as MutableList<LastMessageOwner?>)
                   notifyDataSetChanged()//todo find other way to force call on bind after filtering

               }
           }
       }
       //get search text from fragment using callback
       override fun onChange(query: String) {
           mQuery = query
       }
   */


}

/**
 * Callback for calculating the diff between two non-null items in a list.
 *
 * Used by ListAdapter to calculate the minumum number of changes between and old list and a new
 * list that's been passed to `submitList`.
 */
class DiffCallbackUsers : DiffUtil.ItemCallback<LastMessageOwner>() {
    override fun areItemsTheSame(oldItem: LastMessageOwner, newItem: LastMessageOwner): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: LastMessageOwner, newItem: LastMessageOwner): Boolean {
        return oldItem == newItem
    }
}

class ClickListener(val clickListener: (lastMessageOwner: LastMessageOwner) -> Unit) {
    fun onClick(lastMessageOwner: LastMessageOwner) {
        return clickListener(lastMessageOwner)
    }
}


interface OnQueryTextChange {
    fun onChange(query: String)
}