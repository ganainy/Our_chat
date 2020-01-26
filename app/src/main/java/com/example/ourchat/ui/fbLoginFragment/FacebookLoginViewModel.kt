package com.example.ourchat.ui.fbLoginFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ourchat.Utils.ErrorMessage
import com.example.ourchat.Utils.FirestoreUtil
import com.example.ourchat.Utils.LoadState
import com.example.ourchat.data.model.User
import com.facebook.AccessToken
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class FacebookLoginViewModel : ViewModel() {

    val loadState = MutableLiveData<LoadState>()
    private val isStoredSuccessfully = MutableLiveData<Boolean>()
    private val userExists = MutableLiveData<Boolean>()
    private val firebaseUser = MutableLiveData<FirebaseUser>()


    fun handleFacebookAccessToken(auth: FirebaseAuth, token: AccessToken): LiveData<FirebaseUser> {


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
                ErrorMessage.errorMessage = it.message
                loadState.value = LoadState.FAILURE
            }

        return firebaseUser
    }


    fun isUserAlreadyStoredInFirestore(uid: String): LiveData<Boolean> {
        val usersRef = FirestoreUtil.firestoreInstance.collection("users")
        usersRef.document(uid).get().addOnSuccessListener {
            userExists.value = it.exists()
        }.addOnFailureListener {
            println("FacebookLoginViewModel.isUserAlreadyStoredInFirestore:${it.message}")
        }
        return userExists
    }

    fun storeFacebookUserInFirebase(): LiveData<Boolean> {
        val usersRef = FirebaseFirestore.getInstance().collection("users")


        val user = User(
            firebaseUser.value?.uid,
            firebaseUser.value?.displayName,
            firebaseUser.value?.email,
            firebaseUser.value?.photoUrl?.toString()
        )

        firebaseUser.value?.uid?.let {
            usersRef.document(it).set(user).addOnSuccessListener {
                isStoredSuccessfully.value = true
                loadState.value = LoadState.SUCCESS
            }.addOnFailureListener {
                isStoredSuccessfully.value = false
                ErrorMessage.errorMessage = it.message
                loadState.value = LoadState.FAILURE

            }
        }

        return isStoredSuccessfully
    }


}
