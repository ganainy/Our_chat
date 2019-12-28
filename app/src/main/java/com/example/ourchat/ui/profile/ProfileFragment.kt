package com.example.ourchat.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.ourchat.R
import com.example.ourchat.databinding.ProfileFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet_profile_picture.view.*

class ProfileFragment : Fragment() {

    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<NestedScrollView>
    lateinit var binding: ProfileFragmentBinding

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private lateinit var viewModel: ProfileViewModel

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
        // TODO: Use the ViewModel

        mBottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)


        binding.bottomSheet.cameraButton.setOnClickListener {
            Toast.makeText(
                context,
                "open camera",
                Toast.LENGTH_LONG
            ).show()
        }
        binding.bottomSheet.galleryButton.setOnClickListener {
            Toast.makeText(
                context,
                "open gallery",
                Toast.LENGTH_LONG
            ).show()
        }
        //show selection bottom sheet when those buttons clicked
        binding.profileImage.setOnClickListener { selectProfilePicture() }
        binding.cameraImageView.setOnClickListener { selectProfilePicture() }

    }


    private fun selectProfilePicture() {
        println("ProfileFragment.selectProfilePicture:")
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

}
