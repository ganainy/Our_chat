package com.example.ourchat.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ourchat.data.model.User
import com.example.ourchat.databinding.ReceivedRequestItemBinding


class ReceivedRequestsAdapter(private val buttonCallback: ButtonCallback) :
    RecyclerView.Adapter<ReceivedRequestsAdapter.UserHolder>() {


    private var mUsers = listOf<User>()


    fun setDataSource(users: List<User>?) {
        if (users != null) {
            mUsers = users
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {

        return UserHolder.from(parent)

    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val item = mUsers[position]

        holder.bind(item, buttonCallback)
    }


    class UserHolder private constructor(val binding: ReceivedRequestItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: User,
            buttonCallback: ButtonCallback
        ) {

            binding.user = item
            binding.executePendingBindings()

            //callback to parent fragment when button clicked
            binding.confirmButton.setOnClickListener {
                buttonCallback.onConfirmClicked(item)
            }
            binding.deleteButton.setOnClickListener {
                buttonCallback.onDeleteClicked(item)
            }
        }

        companion object {
            fun from(parent: ViewGroup): UserHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ReceivedRequestItemBinding.inflate(layoutInflater, parent, false)

                return UserHolder(binding)
            }
        }


    }


    interface ButtonCallback {
        fun onConfirmClicked(user: User)
        fun onDeleteClicked(user: User)
    }


}


