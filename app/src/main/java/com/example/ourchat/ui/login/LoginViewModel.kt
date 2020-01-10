package com.example.ourchat.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ourchat.Utils.ErrorMessage
import com.example.ourchat.Utils.LoadState
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Matcher
import java.util.regex.Pattern


class LoginViewModel : ViewModel() {


    private val loadingState = MutableLiveData<LoadState>()

    val emailMatch = MutableLiveData<Boolean>()

    private val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)\$"

    fun isEmailFormatCorrect(it: String): LiveData<Boolean> {

        val pattern: Pattern = Pattern.compile(emailRegex)
        val matcher: Matcher = pattern.matcher(it)
        emailMatch.value = matcher.matches()

        return emailMatch
    }


    fun login(auth: FirebaseAuth, email: String, password: String): LiveData<LoadState> {
        loadingState.value = LoadState.LOADING

        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            loadingState.value = LoadState.SUCCESS
        }.addOnFailureListener {
            ErrorMessage.errorMessage = it.message
            loadingState.value = LoadState.FAILURE
        }
        return loadingState
    }

}
