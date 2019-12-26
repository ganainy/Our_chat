package com.example.ourchat.ui.login

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.ourchat.R
import com.example.ourchat.databinding.LoginFragmentBinding
import com.example.ourchat.ui.signup.SignupFragment
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginFragment : Fragment() {

    private lateinit var binding: LoginFragmentBinding
    private lateinit var callbackManager: CallbackManager
    private lateinit var auth: FirebaseAuth

    private lateinit var mCallback: SignupFragment.ReturnCallBackManager
    private lateinit var mActivity: Activity

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.login_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        // TODO: Use the ViewModel
        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create()
        mCallback.bringBackCallbackManager(callbackManager)

        binding.FBloginButton.setReadPermissions("email", "public_profile")
        binding.FBloginButton.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                println("facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                println("facebook:onCancel")
                // ...
            }

            override fun onError(error: FacebookException) {
                println("facebook:onError${error.message}")
                // ...
            }
        })


        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()


        binding.registerLayout.setOnClickListener { }
        // Navigate to signup fragment
        binding.gotoSignUpFragmentTextView.setOnClickListener {
            it.findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as Activity
        mCallback = mActivity as SignupFragment.ReturnCallBackManager
    }

    private fun handleFacebookAccessToken(token: AccessToken) {

        val credential = FacebookAuthProvider.getCredential(token.token)
        activity?.let {
            auth.signInWithCredential(credential)
                .addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        println("signInWithCredential:success")
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        println("signInWithCredential:failure$ task.exception")
                        Toast.makeText(
                            context, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateUI(null)
                    }

                    // ...
                }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        //todo move to home if user is not null and show error message if null
        println("--------------------------------------------------")
        println(user?.displayName)
        println(user?.photoUrl)
        println(user?.email)
    }

}
