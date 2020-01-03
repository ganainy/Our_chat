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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.ourchat.R
import com.example.ourchat.databinding.HomeFragmentBinding
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth


class HomeFragment : Fragment() {

    lateinit var binding: HomeFragmentBinding

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


        viewModel.getIncomingRequestsCount().observe(this, Observer {
            println("HomeFragment.onActivityCreated:$it")
            when (it) {
                /* 1-> binding.bottomAppBar.setNavigationIcon(R.drawable.ic_request_count_1)
                 2-> binding.bottomAppBar.setNavigationIcon(R.drawable.request_count_2)
                 3-> binding.bottomAppBar.setNavigationIcon(R.drawable.request_count_3)
                 4-> binding.bottomAppBar.setNavigationIcon(R.drawable.request_count_4)
                 5-> binding.bottomAppBar.setNavigationIcon(R.drawable.request_count_5)
                  else ->binding.bottomAppBar.setNavigationIcon(R.drawable.request_count_plus_5)*/

            }
        })


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
                R.id.item_add_friend -> {
                    findNavController().navigate(R.id.action_homeFragment_to_findUserFragment)
                }
            }
            true

        }

        //show incoming friend requests on click
        binding.bottomAppBar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_incomingRequestsFragment)
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


    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        LoginManager.getInstance().logOut()
        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
    }


}




