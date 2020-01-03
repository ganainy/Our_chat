package com.example.ourchat.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ourchat.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore


class HomeViewModel : ViewModel() {

    private val incomingRequestSize = MutableLiveData<Int>()

    fun getIncomingRequestsCount(): MutableLiveData<Int> {
        FirebaseFirestore.getInstance().collection("users")
            .document(FirebaseAuth.getInstance().uid.toString()).addSnapshotListener(
            EventListener { documentSnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException == null) {
                    val user = documentSnapshot?.toObject(User::class.java)
                    val receivedRequestsArraySize = user?.receivedRequests?.size
                    incomingRequestSize.value = receivedRequestsArraySize
                }
            })
        return incomingRequestSize
    }


}
