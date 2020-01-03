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
import com.example.ourchat.databinding.DifferentUserProfileFragmentBinding

class DifferentUserProfileFragment : Fragment() {
    private lateinit var binding: DifferentUserProfileFragmentBinding

    companion object {
        fun newInstance() = DifferentUserProfileFragment()
    }

    private lateinit var viewModel: DifferentUserProfileFragmentViewModel

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

        //get data from profile fragment
        val uid = arguments?.getString("uid")
        val bio = arguments?.getString("bio")
        val profile_picture_url = arguments?.getString("profile_picture_url")
        val username = arguments?.getString("username")


        //check if alreadyFriends
        viewModel.checkIfFriends(uid)

        //set data to views and download image
        binding.bioTextView.text = bio ?: "No bio yet"
        binding.name.text = username
        viewModel.downloadProfilePicture(profile_picture_url)


        //show downloaded image in profile imageview
        viewModel.loadedImage.observe(this, Observer {
            it.into(binding.profileImage)
        })


        binding.sendFriendRequestButton.setOnClickListener {
            //add id to sentRequests document in user
            if (binding.sendFriendRequestButton.text == getString(R.string.friend_request_not_sent)) {
                viewModel.updateSentRequestsForSender(uid)
            } else if (binding.sendFriendRequestButton.text == getString(R.string.friend_request_sent)) {
                viewModel.cancelFriendRequest(uid)
            }
        }


        //change button depending on sent friend request or not
        viewModel.friendRequestState.observe(this, Observer {
            when (it) {
                DifferentUserProfileFragmentViewModel.FriendRequestState.SENT -> {
                    binding.sendFriendRequestButton.text = getString(R.string.friend_request_sent)
                    binding.sendFriendRequestButton.setIconResource(R.drawable.ic_done_black_24dp)
                    binding.sendFriendRequestButton.backgroundTintList =
                        context?.let { it1 -> ContextCompat.getColorStateList(it1, R.color.green) }
                }
                DifferentUserProfileFragmentViewModel.FriendRequestState.NOT_SENT -> {
                    binding.sendFriendRequestButton.text =
                        getString(R.string.friend_request_not_sent)
                    binding.sendFriendRequestButton.setIconResource(R.drawable.ic_person_add_black_24dp)
                    binding.sendFriendRequestButton.backgroundTintList =
                        context?.let { it1 -> ContextCompat.getColorStateList(it1, R.color.grey) }
                }
            }

        })
    }

}
