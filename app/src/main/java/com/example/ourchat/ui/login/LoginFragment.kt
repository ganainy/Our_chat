package com.example.ourchat.ui.login

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.ourchat.R
import com.example.ourchat.databinding.LoginFragmentBinding
import com.example.ourchat.ui.signup.SignupFragment
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.issue_layout.view.*

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

        //check if user has previously logged in
        if (FirebaseAuth.getInstance().currentUser != null) {
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
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
                println("facebook:onSuccess:$loginResult")
                viewModel.handleFacebookAccessToken(auth, loginResult.accessToken)
            }

            override fun onCancel() {
                viewModel.errorMessage.value = "Logging in with facebook cancelled"

            }

            override fun onError(error: FacebookException) {
                viewModel.errorMessage.value = error.message

            }
        })




        // Navigate to signup fragment
        binding.gotoSignUpFragmentTextView.setOnClickListener {
            it.findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        //Report text change to viewmodel
        binding.emailEditText.afterTextChanged {
            viewModel.emailChanged(it)
        }

        //Observe if email format is correct
        viewModel.emailMatch.observe(this, Observer {
            if (!it) {//email format is not correct
                binding.email.error = getString(R.string.wrong_email_format)
            } else {
                binding.email.isErrorEnabled = false
            }

        })

        //password length must be at least 6 characters
        binding.passwordEditText.afterTextChanged {
            if (it.length < 6) {
                binding.password.error = getString(R.string.password_size)
            } else {
                binding.password.isErrorEnabled = false
            }
        }


        //handle login click
        binding.loginButton.setOnClickListener {
            if (binding.email.error != null || binding.password.error != null || binding.email.editText!!.text.isEmpty() || binding.password.editText!!.text.isEmpty()) {
                //name or password doesn't match format
                Toast.makeText(context, "Check email and password then retry.", Toast.LENGTH_LONG)
                    .show()
            } else {

                //All fields are correct we can register


                viewModel.login(
                    auth,
                    binding.email.editText!!.text.toString(),
                    binding.password.editText!!.text.toString()
                )

            }
        }

        //triggered when login with email and password is successful
        viewModel.loginSuccess.observe(this, Observer {
            if (it) {
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                Toast.makeText(context, "Login successful", Toast.LENGTH_LONG).show()
            }
        })

        //show appropriate error message on layout
        viewModel.errorMessage.observe(this, Observer {
            binding.issueLayout.textViewIssue.text = it
            binding.issueLayout.visibility = View.VISIBLE
        })


        //if firebase user isn't null it means login with facebook successful
        viewModel.firebaseUser.observe(this, Observer {
            if (it != null) {
                viewModel.storeFacebookUserInFirebase(it)
            }
        })


        //One observable to show/hide loading layout
        viewModel.loadingState.observe(this, Observer {
            if (it) {
                binding.loadingLayout.visibility = View.VISIBLE
            } else {
                binding.loadingLayout.visibility = View.GONE
            }
        })


        //if true facebook user is stored in firebase
        viewModel.userStored.observe(this, Observer {
            if (it) {
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                Toast.makeText(context, "Sign up successful", Toast.LENGTH_LONG).show()
            }
        })

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as Activity
        mCallback = mActivity as SignupFragment.ReturnCallBackManager
    }


    /**
     * Extension function to simplify setting an afterTextChanged action to EditText components.
     */
    fun TextInputEditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

    }
}
