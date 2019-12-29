package com.example.ourchat.ui.findUser

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ourchat.databinding.UserItemBinding
import com.example.ourchat.ui.findUser.FindUserFragment.Companion.mQueryString
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

class UserAdapter(private val clickListener: UserClickListener) :
    ListAdapter<DocumentSnapshot, UserAdapter.ViewHolder>(DiffCallbackUsers()), Filterable {


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
            binding.user = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
            println("ViewHolder.bindcalled:$mQueryString")
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

            }
        }
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