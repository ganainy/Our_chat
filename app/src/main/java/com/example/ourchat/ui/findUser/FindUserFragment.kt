package com.example.ourchat.ui.findUser

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.ourchat.R
import com.example.ourchat.Utils.LoadState
import com.example.ourchat.databinding.FindUserFragmentBinding

class FindUserFragment : Fragment() {
    private lateinit var adapter: UserAdapter
    private lateinit var binding: FindUserFragmentBinding

    companion object {
        fun newInstance() = FindUserFragment()
    }

    private lateinit var viewModel: FindUserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        activity?.title = "Search for friends"
        binding = DataBindingUtil.inflate(inflater, R.layout.find_user_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FindUserViewModel::class.java)
        // get list of users
        viewModel.loadUsers()


        //get list of all users
        //todo don't load user if already friend
        //todo add pagination to get users 20 by 20(coding in flow video)
        viewModel.userDocuments.observe(this, Observer {
            adapter.submitList(it)
            adapter.userList = it
            println("FindUserFragment.usercount:${it.size}")
        })


        //Show loading until list of all users is downloaded
        viewModel.usersLoadState.observe(this, Observer {
            when (it) {
                LoadState.LOADING -> {
                    binding.loadingLayout.visibility = View.VISIBLE
                }

                LoadState.SUCCESS -> {
                    //bio updated successfully
                    binding.loadingLayout.visibility = View.GONE
                }

                LoadState.FAILURE -> {
                    binding.loadingLayout.visibility = View.GONE
                }
            }
        })


        //setup recycler
        adapter = UserAdapter(UserClickListener {


            var bundle = bundleOf(
                "uid" to it.uid,
                "bio" to it.bio,
                "profile_picture_url" to it.profile_picture_url,
                "username" to it.username
            )

            findNavController().navigate(
                R.id.action_findUserFragment_to_differentUserProfile,
                bundle
            )
        })

        binding.recycler.adapter = adapter


        /*  //hide search view while scrolling
          binding.recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
              override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                  super.onScrollStateChanged(recyclerView, newState)
                  if (newState == SCROLL_STATE_DRAGGING) {
                      binding.searchView.visibility = View.GONE
                  }
                  if (newState == SCROLL_STATE_IDLE) {
                      binding.searchView.visibility = View.VISIBLE
                  }

              }
          })
  */

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)


        inflater.inflate(R.menu.search_menu, menu)

        //do filtering when i type in search or click search
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
        })


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
