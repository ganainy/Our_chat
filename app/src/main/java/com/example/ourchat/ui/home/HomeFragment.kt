package com.example.ourchat.ui.home

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
import com.example.ourchat.Utils.FragmentDestination
import com.example.ourchat.databinding.HomeFragmentBinding
import com.example.ourchat.ui.main_activity.MainActivity
import com.example.ourchat.ui.main_activity.SharedViewModel
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth


class HomeFragment : Fragment() {

    lateinit var binding: HomeFragmentBinding

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel
    lateinit var sharedViewModel: SharedViewModel


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
        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)


        viewModel.getIncomingRequestsCount().observe(this, Observer {
            println("HomeFragment.onActivityCreated:$it")
            sharedViewModel.incomingRequestCount.value = it
        })


        //
        sharedViewModel.fragmentDestination.observe(this, Observer {
            if (it != null) {
                when (it) {
                    //  FragmentDestination.SETTINGS-> findNavController().navigate(R.id.ac)
                    FragmentDestination.ADD_FRIEND -> {
                        findNavController().navigate(R.id.action_homeFragment_to_findUserFragment)

                    }
                    FragmentDestination.PROFILE -> {
                        findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
                    }
                    FragmentDestination.LOGOUT -> {
                        logout()
                    }
                    FragmentDestination.INCOMING_REQUEST -> {
                        findNavController().navigate(R.id.action_homeFragment_to_incomingRequestsFragment)
                    }
                }
                sharedViewModel.doneNavigation()
            }
        })


        //handle fab click
        binding.fab.setOnClickListener {
            //todo
        }


    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        LoginManager.getInstance().logOut()
        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
    }


    override fun onResume() {
        super.onResume()
        val mainActivity = activity as MainActivity
        mainActivity.showToolbar()

    }

    override fun onPause() {
        super.onPause()
        val mainActivity = activity as MainActivity
        mainActivity.hideToolbar()

    }


}




