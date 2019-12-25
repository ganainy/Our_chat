package com.example.ourchat.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.ourchat.R
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.FacebookSdk.getApplicationContext
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.android.synthetic.main.signup_fragment.*
import java.util.*


class SignupFragment : Fragment() {

    companion object {
        fun newInstance() = SignupFragment()
    }

    private lateinit var callbackManager: CallbackManager
    private lateinit var viewModel: SignupViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.signup_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SignupViewModel::class.java)
        // TODO: Use the ViewModel
        FacebookSdk.sdkInitialize(getApplicationContext())
        AppEventsLogger.activateApp(activity)


        callbackManager = CallbackManager.Factory.create()

//
        val EMAIL = "email"

        login_button.setReadPermissions(Arrays.asList(EMAIL))
        login_button.fragment = this


// Callback registration
        login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) { // App code
                println("SignupFragment.onSuccess:${loginResult.toString()}")

            }

            override fun onCancel() { // App code
                println("SignupFragment.onCancel:")
            }

            override fun onError(exception: FacebookException) { // App code
                println("SignupFragment.onError:${exception.message}")
            }
        })
        //

        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {  println("SignupFragment.onSuccess:${loginResult.toString()}")
                }

                override fun onCancel() {  println("SignupFragment.onCancel:")
                }

                override fun onError(exception: FacebookException) {  println("SignupFragment.onError:${exception.message}")
                }
            })


    }

}
