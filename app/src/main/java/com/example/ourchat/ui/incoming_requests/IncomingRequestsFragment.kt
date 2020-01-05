package com.example.ourchat.ui.incoming_requests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.ourchat.R
import com.example.ourchat.data.model.User
import com.example.ourchat.databinding.IncomingRequestsFragmentBinding

class IncomingRequestsFragment : Fragment() {


    private lateinit var adapter: IncomingRequestsAdapter
    private lateinit var binding: IncomingRequestsFragmentBinding
    var sendersList: MutableList<User>? = null


    companion object {
        fun newInstance() = IncomingRequestsFragment()
    }

    private lateinit var viewModel: IncomingRequestsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = "Received friend requests"
        binding =
            DataBindingUtil.inflate(inflater, R.layout.incoming_requests_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(IncomingRequestsViewModel::class.java)


        //check if there is any incoming friend requests
        viewModel.checkIncomingFriendRequests()

        //handle click on item of friend request recycler
        adapter =
            IncomingRequestsAdapter(
                object : IncomingRequestsAdapter.ButtonCallback {
                    override fun onConfirmClicked(user: User, position: Int) {
                        viewModel.addToFriends(user)
                        Toast.makeText(
                            context,
                            "${user.username} added to your friends",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        DeleteFromRecycler(position)
                    }

                    override fun onDeleteClicked(user: User, position: Int) {
                        viewModel.deleteRequest(user)
                        Toast.makeText(context, "Request deleted", Toast.LENGTH_LONG).show()
                        DeleteFromRecycler(position)
                    }

                    private fun DeleteFromRecycler(position: Int) {
                        sendersList?.removeAt(position)
                        adapter.setDataSource(sendersList)
                        adapter.notifyItemRemoved(position)
                        //if no requests left (after user accept or delete)show the empty layout
                        if (sendersList?.size == 0) {
                            binding.noIncomingRequestsLayout.visibility = View.VISIBLE
                        }
                    }

                })

        viewModel.senders.observe(this, Observer {
            println("HomeFrgment.onActivityCreagment.onActivityCreated:${it?.size}")
            if (it == null) {
                binding.noIncomingRequestsLayout.visibility = View.VISIBLE
            } else {
                binding.noIncomingRequestsLayout.visibility = View.GONE
                adapter.setDataSource(it)
                sendersList = it
                binding.receivedRequestsRecycler.adapter = adapter
            }
        })
    }


}
