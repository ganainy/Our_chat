package com.example.ourchat.ui.different_user_profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.ourchat.R
import com.example.ourchat.Utils.CLICKED_USER
import com.example.ourchat.data.model.User
import com.example.ourchat.databinding.DifferentUserProfileFragmentBinding
import com.example.ourchat.ui.main_activity.SharedViewModel
import com.google.gson.Gson

class DifferentUserProfileFragment : Fragment() {
    private lateinit var binding: DifferentUserProfileFragmentBinding

    companion object {
        fun newInstance() = DifferentUserProfileFragment()
    }

    private lateinit var viewModel: DifferentUserProfileFragmentViewModel
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.different_user_profile_fragment,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel =
            ViewModelProviders.of(this).get(DifferentUserProfileFragmentViewModel::class.java)
        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        //get data of clicked user from find user fragment
        val gson = Gson()
        val user = gson.fromJson(arguments?.getString(CLICKED_USER), User::class.java)


        activity?.title = user.username?.split("\\s".toRegex())?.get(0) + "'s profile"

        //check if alreadyFriends
        viewModel.checkIfFriends(user.uid).observe(this, Observer { friendRequestState ->
            when (friendRequestState) {//change button color and icon to show that a request is sent or not
                DifferentUserProfileFragmentViewModel.FriendRequestState.SENT -> {
                    showButtonAsSentRequest()
                }
                DifferentUserProfileFragmentViewModel.FriendRequestState.NOT_SENT -> {
                    showButtonAsRequestNotSent()
                }
            }
        })

        //set data to views and download image
        binding.bioTextView.text = user.bio ?: "No bio yet"
        binding.name.text = user.username
        viewModel.downloadProfilePicture(user.profile_picture_url)


        //show downloaded image in profile imageview
        viewModel.loadedImage.observe(this, Observer {
            it.into(binding.profileImage)
        })


        binding.sendFriendRequestButton.setOnClickListener {
            //add id to sentRequests document in user
            if (binding.sendFriendRequestButton.text == getString(R.string.friend_request_not_sent)) {
                viewModel.updateSentRequestsForSender(user.uid)
                showButtonAsSentRequest()
            } else if (binding.sendFriendRequestButton.text == getString(R.string.friend_request_sent)) {
                viewModel.cancelFriendRequest(user.uid)
                showButtonAsRequestNotSent()
            }
        }




        //load friends of that user
        sharedViewModel.loadFriends(user).observe(this, Observer {
            println("DifferentUserProfileFragment.onActivityCreated:${it?.size}")
        })

    }


    //change sent button to show that no request is sent
    private fun showButtonAsRequestNotSent() {
        binding.sendFriendRequestButton.text =
            getString(R.string.friend_request_not_sent)
        binding.sendFriendRequestButton.setIconResource(R.drawable.ic_person_add_black_24dp)
        binding.sendFriendRequestButton.backgroundTintList =
            context?.let { it1 -> ContextCompat.getColorStateList(it1, R.color.grey) }
    }


    //change sent button to show that  request is sent
    private fun showButtonAsSentRequest() {
        binding.sendFriendRequestButton.text = getString(R.string.friend_request_sent)
        binding.sendFriendRequestButton.setIconResource(R.drawable.ic_done_black_24dp)
        binding.sendFriendRequestButton.backgroundTintList =
            context?.let { it1 -> ContextCompat.getColorStateList(it1, R.color.green) }
    }


}
