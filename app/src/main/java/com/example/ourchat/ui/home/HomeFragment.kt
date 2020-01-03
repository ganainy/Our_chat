package com.example.ourchat.ui.home

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.ourchat.R
import com.example.ourchat.data.model.User
import com.example.ourchat.databinding.HomeFragmentBinding
import com.example.ourchat.ui.home.ReceivedRequestsAdapter.ButtonCallback
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth


class HomeFragment : Fragment() {

    private lateinit var adapter: ReceivedRequestsAdapter
    lateinit var binding: HomeFragmentBinding
    var sendersList: MutableList<User>? = null

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)


        //handle click on item of friend request recycler
        adapter = ReceivedRequestsAdapter(object : ButtonCallback {
            override fun onConfirmClicked(user: User, position: Int) {
                viewModel.addToFriends(user)
                Toast.makeText(context, "${user.username} added to your friends", Toast.LENGTH_LONG)
                    .show()
                sendersList?.removeAt(position)
                adapter.setDataSource(sendersList)
                adapter.notifyItemRemoved(position)
            }

            override fun onDeleteClicked(user: User, position: Int) {
                viewModel.deleteRequest(user)
                Toast.makeText(context, "Request deleted", Toast.LENGTH_LONG).show()
                sendersList?.removeAt(position)
                adapter.setDataSource(sendersList)
                adapter.notifyItemRemoved(position)
            }

        })

        //check if there is any incoming friend requests
        viewModel.checkIncomingFriendRequests()

        //show main_menu items on bottom appbar
        binding.bottomAppBar.replaceMenu(R.menu.main_menu)


        //handle click of menu items
        binding.bottomAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_settings -> {
                    //todo open settings preference fragment
                }
                R.id.item_logout -> {
                    logout()
                }
                R.id.item_edit_profile -> {
                    findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
                }
            }
            true

        }

        //handle add friend click
        binding.bottomAppBar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_findUserFragment)
        }

        //handle fab click
        binding.fab.setOnClickListener {
            //todo
        }


        //change overflow icon to white
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.bottomAppBar.overflowIcon?.colorFilter =
                BlendModeColorFilter(Color.WHITE, BlendMode.SRC_ATOP)
        } else {
            @Suppress("DEPRECATION")
            binding.bottomAppBar.overflowIcon?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        }


        viewModel.senders.observe(this, Observer {
            println("HomeFragment.onActivityCreated:${it?.size}")
            adapter.setDataSource(it)
            sendersList = it
            binding.receivedRequestsRecycler.adapter = adapter
        })
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        LoginManager.getInstance().logOut()
        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
    }


}




