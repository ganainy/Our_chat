package com.example.ourchat.ui.login

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
import com.example.ourchat.Utils.ErrorMessage
import com.example.ourchat.Utils.LoadState
import com.example.ourchat.databinding.LoginFragmentBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.issue_layout.view.*


class LoginFragment : Fragment() {

    private lateinit var binding: LoginFragmentBinding

    private lateinit var auth: FirebaseAuth


    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = "Login"
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



        binding.issueLayout.cancelImage.setOnClickListener {
            binding.issueLayout.visibility = View.GONE
        }
        //show loading ui
        viewModel.loadingState.observe(this, Observer {
            when (it) {
                LoadState.LOADING -> {
                    binding.loadingLayout.visibility = View.VISIBLE
                    binding.issueLayout.visibility = View.GONE
                }
                LoadState.SUCCESS -> {
                    binding.loadingLayout.visibility = View.GONE
                    binding.issueLayout.visibility = View.GONE
                }
                LoadState.FAILURE -> {
                    binding.loadingLayout.visibility = View.GONE
                    binding.issueLayout.visibility = View.VISIBLE
                    binding.issueLayout.textViewIssue.text = ErrorMessage.errorMessage
                }

            }
        })



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
