package com.example.ourchat.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ourchat.data.model.User
import com.facebook.AccessToken
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Matcher
import java.util.regex.Pattern


class LoginViewModel : ViewModel() {

    val firebaseUser = MutableLiveData<FirebaseUser>()
    val errorMessage = MutableLiveData<String>()
    val loginSuccess = MutableLiveData<Boolean>()
    val loadingState = MutableLiveData<Boolean>()
    val userStored = MutableLiveData<Boolean>()


    val emailMatch = MutableLiveData<Boolean>()

    val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)\$"

    fun emailChanged(it: String) {

        val pattern: Pattern = Pattern.compile(emailRegex)
        val matcher: Matcher = pattern.matcher(it)
        emailMatch.value = matcher.matches()

    }


    fun handleFacebookAccessToken(auth: FirebaseAuth, token: AccessToken) {

        loadingState.value = true


        val credential = FacebookAuthProvider.getCredential(token.token)

        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                // Sign in success, update UI with the signed-in user's information
                println("signInWithCredential:success")
                val user = auth.currentUser
                firebaseUser.value = user
            }
            .addOnFailureListener {
                // If sign in fails, display a message to the user.
                println("signInWithCredential:failure$ task.exception")
                firebaseUser.value = null
                errorMessage.value = it.message
                loadingState.value = false
            }


    }


    fun storeFacebookUserInFirebase(firebaseUser: FirebaseUser) {
        val user = User(
            firebaseUser.uid,
            firebaseUser.displayName,
            firebaseUser.email,
            firebaseUser.photoUrl?.toString()
        )
        val db = FirebaseFirestore.getInstance()
        db.collection("users").add(user).addOnSuccessListener {
            userStored.value = true
            loadingState.value = true
        }.addOnFailureListener {
            println("<top>.storeUserInDatabase:${it.message}")
            userStored.value = false
            loadingState.value = false
            errorMessage.value = it.message
        }


    }


    fun login(auth: FirebaseAuth, email: String, password: String) {
        loadingState.value = true

        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            loginSuccess.value = true
        }.addOnFailureListener {
            errorMessage.value = it.message
            loginSuccess.value = false
            loadingState.value = false
        }
    }

}
