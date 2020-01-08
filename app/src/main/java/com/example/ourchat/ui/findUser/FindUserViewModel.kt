package com.example.ourchat.ui.findUser

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ourchat.Utils.ConstantsUtil
import com.example.ourchat.Utils.ErrorMessage
import com.example.ourchat.Utils.LoadState
import com.example.ourchat.data.model.User
import com.example.ourchat.ui.incoming_requests.FRIENDS
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore

class FindUserViewModel : ViewModel() {


    val usersLoadState = MutableLiveData<LoadState>()

    val userDocuments = MutableLiveData<MutableList<User?>>()

    lateinit var db: FirebaseFirestore

    fun loadUsers() {

        if (ConstantsUtil.AUTH_UID == null) return

        usersLoadState.value = LoadState.LOADING
        db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users")
        docRef.get()
            .addOnSuccessListener { querySnapshot ->
                //add any user that isn't logged in user to result
                val result = mutableListOf<User?>()
                for (document in querySnapshot.documents) {
                    if (!document.get("uid").toString().equals(ConstantsUtil.AUTH_UID)) {
                        val user = document.toObject(User::class.java)
                        result.add(user)
                    }

                }


                // remove friends of logged in user from result list
                docRef.whereArrayContains(FRIENDS, ConstantsUtil.AUTH_UID!!)
                    .addSnapshotListener(
                        EventListener { querySnapshot, firebaseFirestoreException ->
                            if (firebaseFirestoreException == null) {
                                val documents = querySnapshot?.documents
                                if (documents != null) {
                                    for (document in documents) {
                                        val user = document.toObject(User::class.java)
                                        result.remove(user)

                                    }

                                    userDocuments.value = result
                                    usersLoadState.value = LoadState.SUCCESS

                                }
                            } else {
                                ErrorMessage.errorMessage = firebaseFirestoreException.message
                                usersLoadState.value = LoadState.FAILURE
                            }
                        })





            }
            .addOnFailureListener { exception ->
                ErrorMessage.errorMessage = exception.message
                usersLoadState.value = LoadState.FAILURE
            }
    }


}
