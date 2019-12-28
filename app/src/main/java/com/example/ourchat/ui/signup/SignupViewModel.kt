package com.example.ourchat.ui.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ourchat.data.model.User
import com.facebook.AccessToken
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore


class SignupViewModel : ViewModel() {

    val userStored = MutableLiveData<Boolean>()
    val loadingState = MutableLiveData<Boolean>()
    val user = MutableLiveData<User>()
    val firebaseUser = MutableLiveData<FirebaseUser>()
    val errorMessage = MutableLiveData<String>()

    fun registerToFirebase(auth: FirebaseAuth, email: String, password: String, username: String) {

        loadingState.value = true

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid: String? = auth.currentUser?.uid
                user.value = User(uid, username, email)

            }.addOnFailureListener {
                user.value = null
                println("SignupViewModel.registerToFirebase:${it.message}")
                errorMessage.value = it.message
                loadingState.value = false
            }

    }


    fun storeUserInFirebase(user: User) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(user.uid!!).set(user).addOnSuccessListener {
            userStored.value = true
        }.addOnFailureListener {
            println("<top>.storeUserInDatabase:${it.message}")
            userStored.value = false
            loadingState.value = false
            errorMessage.value = it.message
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
        db.collection("users").document(firebaseUser.uid).set(user).addOnSuccessListener {
            userStored.value = true
            loadingState.value = true
        }.addOnFailureListener {
            println("<top>.storeUserInDatabase:${it.message}")
            userStored.value = false
            loadingState.value = false
            errorMessage.value = it.message
        }


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
                loadingState.value = false
                errorMessage.value = it.message
            }


    }
}