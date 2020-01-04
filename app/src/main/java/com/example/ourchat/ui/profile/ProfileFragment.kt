package com.example.ourchat.ui.profile

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.ourchat.R
import com.example.ourchat.Utils.LoadState
import com.example.ourchat.data.model.User
import com.example.ourchat.databinding.ProfileFragmentBinding
import com.example.ourchat.ui.main.MainActivity
import com.example.ourchat.ui.main.SharedViewModel
import com.example.ourchat.ui.main.hideKeyboard
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet_profile_picture.view.*
import java.io.ByteArrayOutputStream


class ProfileFragment : Fragment() {

    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<NestedScrollView>
    lateinit var binding: ProfileFragmentBinding
    lateinit var adapter: FriendsAdapter
    lateinit var mainActivity: MainActivity

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private lateinit var viewModel: ProfileViewModel
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.profile_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)


        //download user bio and image on fragment start
        viewModel.downloadBio()
        viewModel.downloadProfileImage()



        adapter = FriendsAdapter(object : FriendsAdapter.ItemClickCallback {
            override fun onItemClicked(user: User) {
                //todo open profile of clicked user
            }
        })


        //load friends of logged in user and show in recycler
        viewModel.loadFriends().observe(this, Observer {
            if (it != null) {
                //user has friends
                binding.noFriendsLayout.visibility = View.GONE
                binding.friendsLayout.visibility = View.VISIBLE
                adapter.setDataSource(it)
                binding.friendsRecycler.adapter = adapter
                binding.friendsCountTextView.text = it.size.toString()
            } else {
                //user has no friends
                binding.friendsLayout.visibility = View.GONE
                binding.noFriendsLayout.visibility = View.VISIBLE
                binding.addFriendsButton.setOnClickListener { findNavController().navigate(R.id.action_profileFragment_to_findUserFragment) }
            }

        })


        mBottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)


        binding.bottomSheet.cameraButton.setOnClickListener {
            mainActivity = activity as MainActivity
            mainActivity.dispatchTakePictureIntent()
        }
        binding.bottomSheet.galleryButton.setOnClickListener {
            mainActivity = activity as MainActivity
            mainActivity.selectFromGallery()
        }
        //show selection bottom sheet when those buttons clicked
        binding.profileImage.setOnClickListener { selectProfilePicture() }
        binding.cameraImageView.setOnClickListener { selectProfilePicture() }


        //Observe camera image change from parent activity
        sharedViewModel.imageBitmap.observe(this, androidx.lifecycle.Observer {
            binding.profileImage.setImageBitmap(it)


            // Get the data from an ImageView as bytes
            binding.profileImage.isDrawingCacheEnabled = true
            binding.profileImage.buildDrawingCache()
            val bitmap = (binding.profileImage.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            sharedViewModel.uploadImageAsBytearray(data)


            mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        })

        //Observe gallery image change from parent activity
        sharedViewModel.galleryImageUri.observe(this, androidx.lifecycle.Observer {
            binding.profileImage.setImageURI(it)
            mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        })


        //Observe upload image state and show appropriate ui
        sharedViewModel.uploadState.observe(this, androidx.lifecycle.Observer {
            setProfileImageLoadUi(it)
        })


        //edit bio handle click
        binding.editTextview.setOnClickListener {
            if (binding.editTextview.text.equals(getString(R.string.edit))) {
                //show edit text to allow user to edit bio and change text view text to submit
                binding.editTextview.text = getString(R.string.submit)
                binding.editTextview.setTextColor(Color.GREEN)
                binding.bioTextView.visibility = View.GONE
                binding.newBioEditText.visibility = View.VISIBLE


            } else if (binding.editTextview.text.equals(getString(R.string.submit))) {
                //hide edit text and upload changes to user document
                binding.editTextview.text = getString(R.string.edit)
                binding.editTextview.setTextColor(Color.CYAN)
                binding.bioTextView.visibility = View.VISIBLE
                binding.newBioEditText.visibility = View.GONE

                //upload bio to user document
                viewModel.updateBio(binding.newBioEditText.text.toString())

                //hide keyboard
                mainActivity = activity as MainActivity
                hideKeyboard(mainActivity)
            }
        }


        //Show loading until bio is loaded
        viewModel.bioLoadState.observe(this, Observer {
            when (it) {
                LoadState.LOADING -> {
                    binding.bioProgressBar.visibility = View.VISIBLE
                }

                LoadState.SUCCESS -> {
                    //bio updated successfully
                    binding.bioProgressBar.visibility = View.GONE
                }

                LoadState.FAILURE -> {
                    binding.bioProgressBar.visibility = View.GONE
                }
            }
        })


        //show downloaded bio in textview
        viewModel.bio.observe(this, Observer {
            binding.bioTextView.text = it
        })


        //show downloaded image in profile imageview
        viewModel.loadedImage.observe(this, Observer {
            it.into(binding.profileImage)
        })


        //show loading state while profile image loading
        viewModel.profileImageLoadState.observe(this, Observer {
            setProfileImageLoadUi(it)
        })



    }

    private fun setProfileImageLoadUi(it: LoadState?) {
        when (it) {

            LoadState.SUCCESS -> {
                binding.uploadProgressBar.visibility = View.GONE
                binding.uploadText.visibility = View.GONE
                binding.profileImage.alpha = 1f
            }
            LoadState.FAILURE -> {
                binding.uploadProgressBar.visibility = View.GONE
                binding.uploadText.visibility = View.GONE
                binding.profileImage.alpha = 1f
            }
            LoadState.LOADING -> {
                binding.uploadProgressBar.visibility = View.VISIBLE
                binding.uploadText.visibility = View.GONE
                binding.profileImage.alpha = .5f

            }
        }
    }


    private fun selectProfilePicture() {
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }


}

