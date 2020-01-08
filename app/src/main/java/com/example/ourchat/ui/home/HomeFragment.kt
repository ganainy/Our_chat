package com.example.ourchat.ui.home

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.ourchat.R
import com.example.ourchat.Utils.ConstantsUtil
import com.example.ourchat.databinding.HomeFragmentBinding
import com.example.ourchat.ui.main_activity.SharedViewModel
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import java.lang.String


class HomeFragment : Fragment() {


    lateinit var binding: HomeFragmentBinding
    private lateinit var countBadgeTextView: TextView
    private val adapter: ChatPreviewAdapter by lazy {
        ChatPreviewAdapter(ClickListener {
            println("HomeFragment.:${it.ownerUser?.uid}")
            println("HomeFragment.:${it.ownerUser?.username}")
        })
    }


    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel
    lateinit var sharedViewModel: SharedViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = "Home"
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        //save auth uid in constants class to use it through the app
        ConstantsUtil.AUTH_UID = FirebaseAuth.getInstance().uid.toString()


        //show badge over menu item with incoming friends count
        viewModel.getIncomingRequestsCount().observe(this, Observer {
            if (::countBadgeTextView.isInitialized) {
                setupBadge(it)
            }
        })


        //handle startChatFab click
        binding.startChatFab.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_contactsFragment)
        }


        viewModel.getChats()?.observe(this, Observer {
            if (it == null || it.size == 0) {

            } else {
                //todo make methods in binding adapter for last message user , download user image in shared view model like
                //in profile fragment to use when last message is from logged in user

                // binding.recycler.adapter=adapter
                //adapter.submitList(it)
            }

        })





    }


    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        LoginManager.getInstance().logOut()
        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.main_menu, menu)
        val menuItem = menu.findItem(R.id.action_incoming_requests)
        val actionView = menuItem?.actionView
        countBadgeTextView = actionView?.findViewById<View>(R.id.count_badge) as TextView


        actionView.setOnClickListener { onOptionsItemSelected(menuItem) }

        //do filtering when i type in search or click search
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        //todo search messages

        /*    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(queryString: kotlin.String?): Boolean {
                    adapter.filter.filter(queryString)
                    return false
                }

                override fun onQueryTextChange(queryString: kotlin.String?): Boolean {
                    adapter.filter.filter(queryString)
                    if (queryString != null) {
                        adapter.onChange(queryString)
                    }

                    return false
                }
            })*/

    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            //findNavController().navigate(R.id.home)
            true
        }
        R.id.action_add_friend -> {
            findNavController().navigate(R.id.action_homeFragment_to_findUserFragment)
            true
        }
        R.id.action_edit_profile -> {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
            true
        }
        R.id.action_logout -> {
            logout()
            true
        }
        R.id.action_incoming_requests -> {
            findNavController().navigate(R.id.action_homeFragment_to_incomingRequestsFragment)
            println("MainActivity.onOptionsItemSelected:${item.title}")

            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }

    }


    private fun setupBadge(count: Int?) {
        if (::countBadgeTextView.isInitialized) {
            if (null == count || count == 0) {
                countBadgeTextView.visibility = View.GONE
            } else {
                countBadgeTextView.visibility = View.VISIBLE
                countBadgeTextView.text = String.valueOf(
                    count.let { Math.min(it, 99) }
                )
            }
        }
    }

}

