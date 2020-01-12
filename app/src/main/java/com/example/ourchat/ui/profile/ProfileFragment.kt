package com.example.ourchat.ui.profile

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ourchat.R
import com.example.ourchat.Utils.LOGGED_USER
import com.example.ourchat.Utils.LoadState
import com.example.ourchat.Utils.eventbus_events.KeyboardEvent
import com.example.ourchat.data.model.User
import com.example.ourchat.databinding.ProfileFragmentBinding
import com.example.ourchat.ui.main.MainActivity
import com.example.ourchat.ui.main_activity.SharedViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import kotlinx.android.synthetic.main.bottom_sheet_profile_picture.view.*
import org.greenrobot.eventbus.EventBus
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
        activity?.title = "My profile"
        binding = DataBindingUtil.inflate(inflater, R.layout.profile_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        //setup bottomsheet
        mBottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)


        //get user from shared preferences
        val mPrefs: SharedPreferences = activity!!.getPreferences(MODE_PRIVATE)
        val gson = Gson()
        val json: String? = mPrefs.getString(LOGGED_USER, null)
        val loggedUser: User = gson.fromJson(json, User::class.java)
        //show user name & email & bio
        binding.bioTextView.text = loggedUser.bio ?: "No bio yet"
        binding.email.text = loggedUser.email
        binding.name.text = loggedUser.username
        //download profile photo
        Glide.with(this).load(loggedUser.profile_picture_url)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.anonymous_profile)
                    .circleCrop()
            )
            .into(binding.profileImage)


        //create adapter and handle recycle item click callback
        adapter = FriendsAdapter(object : FriendsAdapter.ItemClickCallback {
            override fun onItemClicked(user: User) {
                //todo open profile of clicked user
            }
        })


        //load friends of logged in user and show in recycler
        sharedViewModel.loadFriends(loggedUser).observe(this, Observer { friendsList ->
            //hide loading
            binding.loadingFriendsImageView.visibility = View.GONE
            if (friendsList != null) {
                binding.friendsLayout.visibility = View.VISIBLE
                binding.noFriendsLayout.visibility = View.GONE
                showFriendsInRecycler(friendsList)
            } else {
                binding.friendsLayout.visibility = View.GONE
                binding.noFriendsLayout.visibility = View.VISIBLE
                binding.addFriendsButton.setOnClickListener {
                    this@ProfileFragment.findNavController()
                        .navigate(R.id.action_profileFragment_to_findUserFragment)
                }
            }

        })


        //todo can be replaced with event bus to remove dependency on certain activity
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
        sharedViewModel.uploadImageLoadStateMutableLiveData.observe(
            this,
            androidx.lifecycle.Observer {
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
                binding.editTextview.setTextColor(Color.parseColor("#b39ddb"))
                binding.bioTextView.visibility = View.VISIBLE
                binding.bioTextView.text = binding.newBioEditText.text
                binding.newBioEditText.visibility = View.GONE
                EventBus.getDefault().post(KeyboardEvent())
                //upload bio to user document
                viewModel.updateBio(binding.newBioEditText.text.toString())

                //hide keyboard
                EventBus.getDefault().post(KeyboardEvent())
            }
        }




    }


    private fun showFriendsInRecycler(it: List<User>) {
        adapter.setDataSource(it)
        binding.friendsRecycler.adapter = adapter
        binding.friendsCountTextView.text = it.size.toString()
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

