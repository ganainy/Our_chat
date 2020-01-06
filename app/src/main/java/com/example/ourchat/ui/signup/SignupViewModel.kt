package com.example.ourchat.ui.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ourchat.Utils.ErrorMessage
import com.example.ourchat.Utils.LoadState
import com.example.ourchat.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SignupViewModel : ViewModel() {

    val userStored = MutableLiveData<Boolean>()
    val loadingState = MutableLiveData<LoadState>()
    val user = MutableLiveData<User>()


    fun registerEmail(auth: FirebaseAuth, email: String, password: String, username: String) {

        loadingState.value = LoadState.LOADING

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid: String? = auth.currentUser?.uid
                user.value = User(uid, username, email)


            }.addOnFailureListener {
                user.value = null
                println("SignupViewModel.registerToFirebase:${it.message}")
                ErrorMessage.errorMessage = it.message
                loadingState.value = LoadState.FAILURE
            }

    }


    fun storeUserInFirebase(user: User) {
        val db = FirebaseFirestore.getInstance()
        user.uid?.let {
            db.collection("users").document(it).set(user).addOnSuccessListener {
                userStored.value = true
            }.addOnFailureListener {
                println("<top>.storeUserInDatabase:${it.message}")
                loadingState.value = LoadState.FAILURE
                ErrorMessage.errorMessage = it.message
            }
        }


    }


}