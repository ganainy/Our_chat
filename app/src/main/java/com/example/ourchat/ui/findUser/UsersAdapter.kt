package com.example.ourchat.ui.findUser

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ourchat.databinding.UserItemBinding
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*


var mQuery = ""


class UserAdapter(private val clickListener: UserClickListener) :
    ListAdapter<DocumentSnapshot, UserAdapter.ViewHolder>(DiffCallbackUsers()), Filterable,
    OnQueryTextChange {


    var userList = mutableListOf<DocumentSnapshot>()
    lateinit var filteredUserList: MutableList<DocumentSnapshot>


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(clickListener, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: UserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: UserClickListener, item: DocumentSnapshot) {
            println("ViewHolder.bind:")

            val userName: String = item.get("username").toString()

            //if query text isn't empty set the selected text with sky blue+bold
            if (mQuery.isEmpty()) {
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
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = UserItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                filteredUserList = mutableListOf()
                if (charString.isEmpty()) {
                    filteredUserList = userList


                } else {
                    for (user in userList) {
                        if (user.get("username")?.toString()?.toLowerCase(Locale.ENGLISH)?.contains(
                                charString.toLowerCase(Locale.ENGLISH)
                            )!!
                        ) {
                            filteredUserList.add(user)
                        }
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredUserList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {

                submitList(filterResults.values as MutableList<DocumentSnapshot>?)
                notifyDataSetChanged()//todo find other way to force call on bind after filtering
                println("UserAdapter.publishResults:")

            }
        }
    }

    //get search text from fragment using callback
    override fun onChange(query: String) {
        mQuery = query
        println("UserAdapter.onChange:$query")
    }


}

/**
 * Callback for calculating the diff between two non-null items in a list.
 *
 * Used by ListAdapter to calculate the minumum number of changes between and old list and a new
 * list that's been passed to `submitList`.
 */
class DiffCallbackUsers : DiffUtil.ItemCallback<DocumentSnapshot>() {
    override fun areItemsTheSame(oldItem: DocumentSnapshot, newItem: DocumentSnapshot): Boolean {
        return oldItem.get("uid") == newItem.get("uid")
    }

    override fun areContentsTheSame(oldItem: DocumentSnapshot, newItem: DocumentSnapshot): Boolean {
        return oldItem == newItem
    }
}

class UserClickListener(val clickListener: (user: DocumentSnapshot) -> Unit) {
    fun onClick(user: DocumentSnapshot) {
        return clickListener(user)
    }
}


interface OnQueryTextChange {
    fun onChange(query: String)
}