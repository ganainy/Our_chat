package com.example.ourchat.ui.fbLoginFragment

import android.app.Activity
import android.content.Context
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
import com.example.ourchat.Utils.ErrorMessage
import com.example.ourchat.Utils.LoadState
import com.example.ourchat.databinding.FacebookLoginFragmentBinding
import com.example.ourchat.ui.main.SharedViewModel
import com.example.ourchat.ui.signup.SignupFragment
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.auth.FirebaseAuth


class FacebookLoginFragment : Fragment() {


    private lateinit var callbackManager: CallbackManager
    private lateinit var mCallback: SignupFragment.ReturnCallBackManager
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FacebookLoginFragmentBinding


    companion object {
        fun newInstance() = FacebookLoginFragment()
    }

    private lateinit var viewModel: FacebookLoginViewModel
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.facebook_login_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FacebookLoginViewModel::class.java)
        sharedViewModel = ViewModelProviders.of(this.activity!!).get(SharedViewModel::class.java)


        auth = FirebaseAuth.getInstance()
        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create()
        mCallback.bringBackCallbackManager(callbackManager)

        binding.FBloginButton.setReadPermissions("email", "public_profile")
        binding.FBloginButton.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                println("facebook:onSuccess:$loginResult")
                viewModel.handleFacebookAccessToken(auth, loginResult.accessToken)

            }

            override fun onCancel() {
                ErrorMessage.errorMessage = "Logging in with facebook cancelled"
                sharedViewModel.loadState.value = LoadState.FAILURE

            }

            override fun onError(error: FacebookException) {
                ErrorMessage.errorMessage = error.message
                sharedViewModel.loadState.value = LoadState.FAILURE
            }
        })


        //if firebase user isn't null it means login with facebook successful
        viewModel.firebaseUser.observe(this, Observer {
            if (it != null) {
                viewModel.isUserAlreadyStored(it.uid)
            }
        })


        //if user doesn't exist in database store him
        viewModel.userExists.observe(this, Observer {
            if (!it) {
                viewModel.storeFacebookUserInFirebase()
            } else {
                viewModel.userStored.value = true
                viewModel.loadState.value = LoadState.SUCCESS
            }
        })


        //if true facebook user is stored in firebase
        viewModel.userStored.observe(this, Observer {
            if (it) {
                try {
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                } catch (e: Exception) {
                    findNavController().navigate(R.id.action_signupFragment_to_homeFragment)
                }
            }
        })


        //pass loading state to shared fragment to show proper layout
        viewModel.loadState.observe(this, Observer {
            sharedViewModel.showLoadState(it)
        })

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        var mActivity = context as Activity
        mCallback = mActivity as SignupFragment.ReturnCallBackManager
    }


}
