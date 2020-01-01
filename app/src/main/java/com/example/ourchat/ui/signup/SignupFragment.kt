package com.example.ourchat.ui.signup

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.ourchat.R
import com.example.ourchat.databinding.SignupFragmentBinding
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.issue_layout.view.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class SignupFragment : Fragment() {

    private lateinit var binding: SignupFragmentBinding

    companion object {
        fun newInstance() = SignupFragment()
    }

    private lateinit var viewModel: SignupViewModel

    private lateinit var callbackManager: CallbackManager
    private lateinit var auth: FirebaseAuth

    private lateinit var mCallback: ReturnCallBackManager
    private lateinit var mActivity: Activity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.signup_fragment, container, false)

        //check if user has previously logged in
        if (FirebaseAuth.getInstance().currentUser != null) {
            findNavController().navigate(R.id.action_signupFragment_to_homeFragment)
        }
        return binding.root
    }


    interface ReturnCallBackManager {
        fun bringBackCallbackManager(callbackManager: CallbackManager)
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SignupViewModel::class.java)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        //cancel error layout on image click
        binding.issueLayout.cancelImage.setOnClickListener {
            binding.issueLayout.visibility = View.GONE
        }


        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create()
        mCallback.bringBackCallbackManager(callbackManager)

        binding.FBloginButton.setReadPermissions("email", "public_profile")
        binding.FBloginButton.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                viewModel.handleFacebookAccessToken(auth, loginResult.accessToken)
            }

            override fun onCancel() {
                viewModel.errorMessage.value = "Logging in with facebook cancelled"

            }

            override fun onError(error: FacebookException) {
                viewModel.errorMessage.value = error.message

            }
        })


        val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)\$"
        val pattern: Pattern = Pattern.compile(emailRegex)


        //handle register click
        binding.registerButton.setOnClickListener {

            binding.userName.isErrorEnabled = false
            binding.email.isErrorEnabled = false
            binding.password.isErrorEnabled = false


            if (binding.userName.editText!!.text.length < 4) {
                binding.userName.error = "User name should be at least 4 characters"
                return@setOnClickListener
            }


            //check if email is empty or wrong format
            if (!binding.email.editText!!.text.isEmpty()) {
                val matcher: Matcher = pattern.matcher(binding.email.editText!!.text)
                if (!matcher.matches()) {
                    binding.email.error = "Email format isn't correct."
                    return@setOnClickListener
                }
            } else if (binding.email.editText!!.text.isEmpty()) {
                binding.email.error = "Email field can't be empty."
                return@setOnClickListener
            }


            if (binding.password.editText!!.text.length < 6) {
                binding.password.error = "Password should be at least 6 characters"
                return@setOnClickListener
            }



            viewModel.registerToFirebase(
                auth,
                binding.email.editText!!.text.toString(),
                binding.password.editText!!.text.toString(),
                binding.userName.editText!!.text.toString()
            )


        }

        //if returned user isn't null it means authentication success and we should save user in firebase
        viewModel.user.observe(this, Observer {
            if (it != null) {
                viewModel.storeUserInFirebase(it)
            }
        })

        //if true user is stored in firebase(wether he is normal or facebook user)
        viewModel.userStored.observe(this, Observer {
            if (it) {
                findNavController().navigate(R.id.action_signupFragment_to_homeFragment)
                Toast.makeText(context, "Sign up successful", Toast.LENGTH_LONG).show()
            }
        })


        //if firebase user isn't null it means registeration with facebook successful
        viewModel.firebaseUser.observe(this, Observer {
            if (it != null) {
                //save user info in firebase
                viewModel.storeFacebookUserInFirebase(it)
            }
        })





        viewModel.errorMessage.observe(this, Observer {
            binding.issueLayout.textViewIssue.text = it
            binding.issueLayout.visibility = View.VISIBLE
        })


        //One observable to show/hide loading layout
        viewModel.loadingState.observe(this, Observer {
            if (it) {
                binding.loadingLayout.visibility = View.VISIBLE
            } else {
                binding.loadingLayout.visibility = View.GONE
            }
        })
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as Activity
        mCallback = mActivity as ReturnCallBackManager
    }





}



