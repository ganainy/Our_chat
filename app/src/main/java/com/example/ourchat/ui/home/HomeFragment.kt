package com.example.ourchat.ui.home

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.ourchat.R
import com.example.ourchat.Utils.CLICKED_USER
import com.example.ourchat.databinding.HomeFragmentBinding
import com.example.ourchat.ui.main_activity.SharedViewModel
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import java.lang.String


class HomeFragment : Fragment() {

    var receivedRequestsCount: Int? = null

    lateinit var binding: HomeFragmentBinding
    val gson = Gson()
    private lateinit var countBadgeTextView: TextView
    private val adapter: ChatPreviewAdapter by lazy {
        ChatPreviewAdapter(ClickListener { chatParticipant ->
            //navigate to chat with selected user on chat outer item click
            val clickedUser = gson.toJson(chatParticipant.particpant)
            findNavController().navigate(
                R.id.action_homeFragment_to_chatFragment, bundleOf(
                    CLICKED_USER to clickedUser
                )
            )
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


        //get user data
        viewModel.getUserData().observe(this, Observer { loggedUser ->
            //save logged user data in shared pref to use in other fragments
            val mPrefs: SharedPreferences = activity!!.getPreferences(MODE_PRIVATE)
            val prefsEditor: SharedPreferences.Editor = mPrefs.edit()
            val json = gson.toJson(loggedUser)
            prefsEditor.putString("loggedUser", json)
            prefsEditor.apply()

            activity?.title = loggedUser.username

            //show notification badge if there is incoming requests
            receivedRequestsCount = loggedUser.receivedRequests?.size
            setupBadge(receivedRequestsCount)



            //get user chat history
            viewModel.getChats(loggedUser!!)?.observe(this, Observer { chatParticipantsList ->

                //Hide loading image
                binding.loadingChatImageView.visibility = View.GONE
                if (chatParticipantsList.isNullOrEmpty()) {
                    //show no chat layout
                    binding.noChatLayout.visibility = View.VISIBLE
                } else {

                    binding.noChatLayout.visibility = View.GONE
                    binding.recycler.adapter = adapter
                    adapter.submitList(chatParticipantsList.distinct())
                    adapter.chatList = chatParticipantsList.distinct()
                }

            })

        })




        //handle startChatFab click
        binding.startChatFab.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_contactsFragment)
        }


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
        //if fragment is coming from back stack setupBadge will be called before onCreateOptionsMenu so we have to call setupbadge again
        setupBadge(receivedRequestsCount)



        actionView.setOnClickListener { onOptionsItemSelected(menuItem) }

        //do filtering when i type in search or click search
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        //todo search messages

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
        })

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

