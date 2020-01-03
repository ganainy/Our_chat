package com.example.ourchat.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ourchat.Utils.ErrorMessage
import com.example.ourchat.Utils.LoadState
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Matcher
import java.util.regex.Pattern


class LoginViewModel : ViewModel() {


    val loadingState = MutableLiveData<LoadState>()

    val loginSuccess = MutableLiveData<Boolean>()


    val emailMatch = MutableLiveData<Boolean>()

    val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)\$"

    fun emailChanged(it: String) {

        val pattern: Pattern = Pattern.compile(emailRegex)
        val matcher: Matcher = pattern.matcher(it)
        emailMatch.value = matcher.matches()

    }



    fun login(auth: FirebaseAuth, email: String, password: String) {
        loadingState.value = LoadState.LOADING

        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            loginSuccess.value = true
            loadingState.value = LoadState.SUCCESS
        }.addOnFailureListener {
            ErrorMessage.errorMessage = it.message
            loadingState.value = LoadState.FAILURE
        }
    }

}
