package com.example.ourchat.ui.chat

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
import com.example.ourchat.Utils.AuthUtil

import com.example.ourchat.databinding.ChatFragmentBinding
import com.example.ourchat.ui.contacts.PROFILE_PICTURE
import com.example.ourchat.ui.contacts.UID
import com.example.ourchat.ui.contacts.USERNAME



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

        //get receiver data from contacts fragment
        val uid = arguments?.getString(UID)
        val profile_picture_url = arguments?.getString(PROFILE_PICTURE)
        val username = arguments?.getString(USERNAME) ?: "user"
        activity?.title = "Chatting with $username"

        //user viewmodel factory to pass ids on creation of view model
        if (uid != null) {
            viewModeldFactory = ChatViewModelFactory(AuthUtil.authUid, uid)
            viewModel =
                ViewModelProviders.of(this, viewModeldFactory).get(ChatViewModel::class.java)
        }

        //Move layouts up when soft keyboard is shown
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)


        binding.sendFab.setOnClickListener {
            if (binding.messageEditText.text.isEmpty()) {
                Toast.makeText(context, getString(R.string.empty_message), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.sendMessage(binding.messageEditText.text.toString())
            binding.messageEditText.setText("")
        }


        //pass messages list for recycler to show
        viewModel.messagesMutableLiveData.observe(this, Observer {

            adapter.setDataSource(it)
            binding.recycler.adapter = adapter
            //scroll to last items in recycler (recent messages)
            binding.recycler.scrollToPosition(it.size - 1)

        })

    }


    //todo override menu and add block option


}
