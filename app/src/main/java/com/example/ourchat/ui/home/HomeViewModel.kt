package com.example.ourchat.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ourchat.data.model.User
import com.example.ourchat.ui.different_user_profile.RECEIVED_REQUEST_ARRAY
import com.example.ourchat.ui.different_user_profile.SENT_REQUEST_ARRAY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


const val FRIENDS = "friends"

class HomeViewModel : ViewModel() {

    val usersRef = FirebaseFirestore.getInstance().collection("users")
    val receivedRequestsProfiles = MutableLiveData<List<User>?>()

    fun checkIncomingFriendRequests() {
        usersRef.document(FirebaseAuth.getInstance().uid.toString())
            .addSnapshotListener { t, firebaseFirestoreException ->
                if (firebaseFirestoreException == null) {
                    val user = t?.toObject(User::class.java)
                    val receivedRequestListSize = user?.receivedRequests?.size ?: -1
                    if (receivedRequestListSize > 0) {
                        downloadUsers(user?.receivedRequests)
                    } else {
                        receivedRequestsProfiles.value = null
                    }
                } else {
                    receivedRequestsProfiles.value = null
                    //error
                }
            }
    }


    //get info of the users that sent friend requests
    private fun downloadUsers(receivedRequests: List<String>?) {

        val users = mutableListOf<User>()

        if (receivedRequests != null) {
            for (receivedRequest in receivedRequests) {
                usersRef.document(receivedRequest).get().addOnSuccessListener {
                    val user = it?.toObject(User::class.java)
                    user?.let { it1 -> users.add(it1) }
                    receivedRequestsProfiles.value = users

                }.addOnFailureListener {

                }
            }
        }


    }

    fun addToFriends(user: User) {
        deleteRequest(user)


        val uid = user.uid
        //add id in sentRequest array for logged in user
        val db = FirebaseFirestore.getInstance()
        val loggedInUserId = FirebaseAuth.getInstance().uid.toString()
        if (uid != null) {
            db.collection("users").document(loggedInUserId)
                .update(FRIENDS, FieldValue.arrayUnion(uid)).addOnSuccessListener {
                    //add loggedInUserId in receivedRequest array for other user
                    db.collection("users").document(uid)
                        .update(FRIENDS, FieldValue.arrayUnion(loggedInUserId))
                        .addOnSuccessListener {
                        }.addOnFailureListener {
                        }
                }.addOnFailureListener {
                }
        }


    }


    fun deleteRequest(user: User) {
        val uid = user.uid
        //remove id from sentRequest array for logged in user
        val db = FirebaseFirestore.getInstance()
        val loggedInUserId = FirebaseAuth.getInstance().uid.toString()
        if (uid != null) {
            db.collection("users").document(loggedInUserId)
                .update(RECEIVED_REQUEST_ARRAY, FieldValue.arrayRemove(uid)).addOnSuccessListener {
                    //remove loggedInUserId from receivedRequest array for other user
                    db.collection("users").document(uid)
                        .update(SENT_REQUEST_ARRAY, FieldValue.arrayRemove(loggedInUserId))
                        .addOnSuccessListener {
                        }.addOnFailureListener {
                        }
                }.addOnFailureListener {
                }
        }
    }

}
