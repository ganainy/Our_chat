package com.example.ourchat.ui.chat

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.ourchat.R
import com.example.ourchat.Utils.LOGGED_USER
import com.example.ourchat.data.model.User
import com.example.ourchat.databinding.ChatFragmentBinding
import com.example.ourchat.ui.contacts.PROFILE_PICTURE
import com.example.ourchat.ui.contacts.UID
import com.example.ourchat.ui.contacts.USERNAME
import com.google.gson.Gson


class ChatFragment : Fragment() {

    lateinit var binding: ChatFragmentBinding
    val adapter: ChatAdapter by lazy {
        ChatAdapter(context, object : MessageClickListener {
            override fun onMessageClick(position: Int) {
                println("ChatFragment.onMessageClick:$position")
            }

        })
    }

    companion object {
        fun newInstance() = ChatFragment()
    }

    private lateinit var viewModel: ChatViewModel
    private lateinit var viewModeldFactory: ChatViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        binding = DataBindingUtil.inflate(inflater, R.layout.chat_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        //get logged user from shared preferences
        val mPrefs: SharedPreferences = activity!!.getPreferences(Context.MODE_PRIVATE)
        val gson = Gson()
        val json: String? = mPrefs.getString(LOGGED_USER, null)
        val loggedUser: User = gson.fromJson(json, User::class.java)

        //get receiver data from contacts fragment
        val receiverId = arguments?.getString(UID)
        val receiverProfilePictureUrl = arguments?.getString(PROFILE_PICTURE)
        val receiverUsername = arguments?.getString(USERNAME) ?: "user"

        activity?.title = "Chatting with $receiverUsername"

        //user viewmodel factory to pass ids on creation of view model
        if (receiverId != null) {
            viewModeldFactory = ChatViewModelFactory(loggedUser.uid, receiverId)
            viewModel =
                ViewModelProviders.of(this, viewModeldFactory).get(ChatViewModel::class.java)
        }

        //Move layouts up when soft keyboard is shown
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)


        //handle send button
        binding.sendFab.setOnClickListener {
            if (binding.messageEditText.text.isEmpty()) {
                Toast.makeText(context, getString(R.string.empty_message), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.sendMessage(binding.messageEditText.text.toString())
            binding.messageEditText.setText("")
        }


        //pass messages list for recycler to show
        viewModel.loadMessages().observe(this, Observer { messagesList ->

            adapter.setDataSource(messagesList)
            binding.recycler.adapter = adapter
            //scroll to last items in recycler (recent messages)
            binding.recycler.scrollToPosition(messagesList.size - 1)

        })

    }


    //todo override menu and add block option


}
