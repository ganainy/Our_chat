package com.example.ourchat.ui.fbLoginFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ourchat.Utils.ErrorMessage
import com.example.ourchat.Utils.LoadState
import com.example.ourchat.data.model.User
import com.facebook.AccessToken
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class FacebookLoginViewModel : ViewModel() {


    val userStored = MutableLiveData<Boolean>()
    val userExists = MutableLiveData<Boolean>()
    val loadState = MutableLiveData<LoadState>()
    val firebaseUser = MutableLiveData<FirebaseUser>()


    fun handleFacebookAccessToken(auth: FirebaseAuth, token: AccessToken) {


        loadState.value = LoadState.LOADING

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
                ErrorMessage.errorMessage = it.message
                loadState.value = LoadState.FAILURE
            }


    }


    fun storeFacebookUserInFirebase() {
        val usersRef = FirebaseFirestore.getInstance().collection("users")


        val user = User(
            firebaseUser.value?.uid,
            firebaseUser.value?.displayName,
            firebaseUser.value?.email,
            firebaseUser.value?.photoUrl?.toString()
        )

        firebaseUser.value?.uid?.let {
            usersRef.document(it).set(user).addOnSuccessListener {
                userStored.value = true
                loadState.value = LoadState.SUCCESS
            }.addOnFailureListener {
                userStored.value = false
                ErrorMessage.errorMessage = it.message
                loadState.value = LoadState.FAILURE
                throw it

            }
        }


    }

    fun isUserAlreadyStored(uid: String) {
        val usersRef = FirebaseFirestore.getInstance().collection("users")
        usersRef.document(uid).get().addOnSuccessListener {
            userExists.value = it.exists()
        }.addOnFailureListener {
            ErrorMessage.errorMessage = it.message
            loadState.value = LoadState.FAILURE
        }
    }

}
