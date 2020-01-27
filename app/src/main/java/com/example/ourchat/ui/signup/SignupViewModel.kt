package com.example.ourchat.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ourchat.Utils.ErrorMessage
import com.example.ourchat.Utils.FirestoreUtil
import com.example.ourchat.Utils.LoadState
import com.example.ourchat.data.model.User
import com.google.firebase.auth.FirebaseAuth


class SignupViewModel : ViewModel() {

    private val userStored = MutableLiveData<Boolean>()
    val loadingState = MutableLiveData<LoadState>()
    private val user = MutableLiveData<User>()


    fun registerEmail(
        auth: FirebaseAuth,
        email: String,
        password: String,
        username: String
    ): LiveData<User> {

        loadingState.value = LoadState.LOADING

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                user.value = User(it.user?.uid, username, email)
            }.addOnFailureListener {
                ErrorMessage.errorMessage = it.message
                loadingState.value = LoadState.FAILURE
            }

        return user
    }


    fun storeUserInFirestore(user: User): LiveData<Boolean> {
        val db = FirestoreUtil.firestoreInstance
        user.uid?.let { uid ->
            db.collection("users").document(uid).set(user).addOnSuccessListener {
                userStored.value = true
            }.addOnFailureListener {
                loadingState.value = LoadState.FAILURE
                ErrorMessage.errorMessage = it.message
            }
        }

        return userStored
    }


}