package com.example.ourchat.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.ourchat.R
import com.example.ourchat.databinding.ChatFragmentBinding
import com.example.ourchat.ui.contacts.PROFILE_PICTURE
import com.example.ourchat.ui.contacts.UID
import com.example.ourchat.ui.contacts.USERNAME

class ChatFragment : Fragment() {

    lateinit var binding: ChatFragmentBinding

    companion object {
        fun newInstance() = ChatFragment()
    }

    private lateinit var viewModel: ChatViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        activity?.title = ""

        binding = DataBindingUtil.inflate(inflater, R.layout.chat_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ChatViewModel::class.java)

        //Move layouts up when soft keyboard is shown
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        val uid = arguments?.getString(UID)
        val profile_picture_url = arguments?.getString(PROFILE_PICTURE)
        val username = arguments?.getString(USERNAME)


    }

}
