package com.example.ourchat.ui.contacts

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.ourchat.R
import com.example.ourchat.data.model.User
import com.example.ourchat.databinding.ContactsFragmentBinding
import com.example.ourchat.ui.main_activity.SharedViewModel


class ContactsFragment : Fragment() {

    lateinit var binding: ContactsFragmentBinding

    companion object {
        fun newInstance() = ContactsFragment()
    }

    private lateinit var viewModel: ContactsViewModel
    private lateinit var adapter: ContactsAdapter
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        activity?.title = "Contacts"
        binding = DataBindingUtil.inflate(inflater, R.layout.contacts_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ContactsViewModel::class.java)
        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)



        adapter = ContactsAdapter(object : ContactsAdapter.ItemClickCallback {
            override fun onItemClicked(user: User) {
                //todo start chat with user
                println("ContactsFragment.onItemClicked:${user.username}")
            }

        })

        sharedViewModel.loadFriends().observe(this, Observer {
            if (it != null) {
                //user has friends
                showFriends(it)
            } else {
                //user has no friends
                showEmptyLayout()
            }
        })


    }


    private fun showFriends(it: List<User>) {
        binding.noFriendsLayout.visibility = View.GONE
        adapter.setDataSource(it)
        binding.contactsRecycler.adapter = adapter
    }

    private fun showEmptyLayout() {
        binding.noFriendsLayout.visibility = View.VISIBLE
        binding.addFriendsButton.setOnClickListener { findNavController().navigate(R.id.action_contactsFragment_to_findUserFragment) }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)


        inflater.inflate(R.menu.search_menu, menu)

        //do filtering when i type in search or click search
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        //todo connect search view with recycler
        /*   searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
               override fun onQueryTextSubmit(queryString: String?): Boolean {
                   adapter.filter.filter(queryString)
                   return false
               }

               override fun onQueryTextChange(queryString: String?): Boolean {
                   adapter.filter.filter(queryString)
                   if (queryString != null) {
                       adapter.onChange(queryString)
                   }

                   return false
               }
           })*/


    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.action_search -> {
            println("MainActivity.onOptionsItemSelected:${item.title}")
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }

    }

}
