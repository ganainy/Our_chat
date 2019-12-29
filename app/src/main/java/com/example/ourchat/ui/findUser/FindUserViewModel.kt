package com.example.ourchat.ui.findUser

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ourchat.Utils.LoadState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class FindUserViewModel : ViewModel() {


    val usersDownloadState = MutableLiveData<LoadState>()

    val userDocuments = MutableLiveData<MutableList<DocumentSnapshot>>()

    lateinit var db: FirebaseFirestore

    fun loadUsers() {
        usersDownloadState.value = LoadState.DOWNLOADING
        db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users")
        docRef.get()
            .addOnSuccessListener { querySnapshot ->
                usersDownloadState.value = LoadState.SUCCESS
                //check if logged in user is in result list and remove him
                val result = mutableListOf<DocumentSnapshot>()
                for (document in querySnapshot.documents) {
                    if (!document.get("uid").toString().equals(FirebaseAuth.getInstance().uid)) {
                        result.add(document)
                    }
                }
                userDocuments.value = result

            }
            .addOnFailureListener { exception ->
                usersDownloadState.value = LoadState.FAILURE
            }
    }


}
