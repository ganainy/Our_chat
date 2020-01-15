package com.example.ourchat.ui.chat

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.ourchat.R
import com.example.ourchat.Utils.AuthUtil
import com.example.ourchat.Utils.CLICKED_USER
import com.example.ourchat.Utils.LOGGED_USER
import com.example.ourchat.Utils.eventbus_events.SelectFileEvent
import com.example.ourchat.Utils.eventbus_events.SelectGalleryImageEvent
import com.example.ourchat.data.model.Message
import com.example.ourchat.data.model.User
import com.example.ourchat.databinding.ChatFragmentBinding
import com.example.ourchat.ui.main_activity.SharedViewModel
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import java.util.*


const val SELECT_CHAT_IMAGE_REQUEST = 3

class ChatFragment : Fragment() {

    private var messageList = mutableListOf<Message>()
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
    private lateinit var sharedViewModel: SharedViewModel
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
        val clickedUser = gson.fromJson(arguments?.getString(CLICKED_USER), User::class.java)


        activity?.title = "Chatting with ${clickedUser.username}"

        //user viewmodel factory to pass ids on creation of view model
        if (clickedUser.uid != null) {
            viewModeldFactory = ChatViewModelFactory(loggedUser.uid, clickedUser.uid)
            viewModel =
                ViewModelProviders.of(this, viewModeldFactory).get(ChatViewModel::class.java)
        }
        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        //Move layouts up when soft keyboard is shown
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)


        //handle send button
        binding.sendFab.setOnClickListener {
            sendMessage()
        }


        //send message on keyboard done click
        binding.messageEditText.setOnEditorActionListener { _, actionId, _ ->
            sendMessage()
            true
        }

        //pass messages list for recycler to show
        viewModel.loadMessages().observe(this, Observer { mMessagesList ->
            messageList = mMessagesList as MutableList<Message>
            adapter.submitList(mMessagesList)
            binding.recycler.adapter = adapter
            //scroll to last items in recycler (recent messages)
            binding.recycler.scrollToPosition(mMessagesList.size - 1)

        })


        //open alert dialog with option on attachmentImageView click
        binding.attachmentImageView.setOnClickListener {

            val dialogBuilder = context?.let { it1 -> AlertDialog.Builder(it1) }
            val inflater = this.layoutInflater
            val dialogView: View = inflater.inflate(R.layout.attachment_layout, null)
            dialogBuilder?.setView(dialogView)
            val alertDialog = dialogBuilder?.create()
            alertDialog?.show()

            //handle select image button click
            val sendPictureButton = dialogView.findViewById<View>(R.id.sendPictureButton) as Button
            sendPictureButton.setOnClickListener {
                EventBus.getDefault().post(SelectGalleryImageEvent(SELECT_CHAT_IMAGE_REQUEST))
                alertDialog?.dismiss()
            }
            //handle select image button click
            val sendFileButton = dialogView.findViewById<View>(R.id.sendFileButton) as Button
            sendFileButton.setOnClickListener {
                EventBus.getDefault().post(SelectFileEvent())
                alertDialog?.dismiss()
            }


        }

        //chat image was uploaded now store the uri with the message
        sharedViewModel.chatImageDownloadUriMutableLiveData.observe(this, Observer { chatImageUri ->
            viewModel.sendMessage(null, chatImageUri.toString(), null, 1)
        })


        //result on gallery select image , show in recycler until image uploaded
        sharedViewModel.chatImageMutableLiveData.observe(this, Observer {
            messageList.add(
                Message(
                    AuthUtil.getAuthId(),
                    Date().time,
                    null,
                    null,
                    null,
                    it.toString(),
                    1
                )
            )
            adapter.submitList(messageList)
            adapter.notifyItemInserted(messageList.size - 1)
            binding.recycler.scrollToPosition(messageList.size - 1)
        })


        //chat file was uploaded now store the uri with the message
        sharedViewModel.chatFileMapMutableLiveData.observe(this, Observer { chatFileMap ->
            viewModel.sendMessage(
                null, chatFileMap["downloadUri"].toString(),
                chatFileMap["fileName"].toString(), 3
            )
        })

    }

    private fun sendMessage() {
        if (binding.messageEditText.text.isEmpty()) {
            Toast.makeText(context, getString(R.string.empty_message), Toast.LENGTH_LONG).show()
            return
        }
        viewModel.sendMessage(binding.messageEditText.text.toString(), null, null, 0)
        binding.messageEditText.setText("")
    }


}
