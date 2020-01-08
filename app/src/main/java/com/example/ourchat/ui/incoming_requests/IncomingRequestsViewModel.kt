package com.example.ourchat.ui.incoming_requests

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ourchat.Utils.ConstantsUtil
import com.example.ourchat.data.model.User
import com.example.ourchat.ui.different_user_profile.RECEIVED_REQUEST_ARRAY
import com.example.ourchat.ui.different_user_profile.SENT_REQUEST_ARRAY
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

const val FRIENDS = "friends"


class IncomingRequestsViewModel : ViewModel() {
    val usersRef = FirebaseFirestore.getInstance().collection("users")
    val senders = MutableLiveData<MutableList<User>?>()

    fun checkIncomingFriendRequests() {
        ConstantsUtil.AUTH_UID?.let {
            usersRef.document(it).get().addOnSuccessListener {
                val user = it?.toObject(User::class.java)
                val receivedRequestListSize = user?.receivedRequests?.size ?: -1
                if (receivedRequestListSize > 0) {
                    downloadSenders(user?.receivedRequests)
                } else {
                    senders.value = null
                }
            }.addOnFailureListener {
                senders.value = null
                //error
            }
        }

    }


    //get info of the users that sent friend requests
    private fun downloadSenders(receivedRequests: List<String>?) {

        val users = mutableListOf<User>()

        if (receivedRequests != null) {
            for (receivedRequest in receivedRequests) {
                usersRef.document(receivedRequest).get().addOnSuccessListener {
                    val user = it?.toObject(User::class.java)
                    user?.let { it1 -> users.add(it1) }
                    senders.value = users

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
        if (uid != null) {
            ConstantsUtil.AUTH_UID?.let {
                db.collection("users").document(it)
                    .update(FRIENDS, FieldValue.arrayUnion(uid)).addOnSuccessListener {
                        //add loggedInUserId in receivedRequest array for other user
                        db.collection("users").document(uid)
                            .update(FRIENDS, FieldValue.arrayUnion(ConstantsUtil.AUTH_UID))
                            .addOnSuccessListener {
                            }.addOnFailureListener {
                            }
                    }.addOnFailureListener {
                    }
            }
        }


    }


    fun deleteRequest(user: User) {
        val uid = user.uid
        //remove id from sentRequest array for logged in user
        val db = FirebaseFirestore.getInstance()
        if (uid != null) {
            ConstantsUtil.AUTH_UID?.let {
                db.collection("users").document(it)
                    .update(RECEIVED_REQUEST_ARRAY, FieldValue.arrayRemove(uid))
                    .addOnSuccessListener {
                        //remove loggedInUserId from receivedRequest array for other user
                        db.collection("users").document(uid)
                            .update(
                                SENT_REQUEST_ARRAY,
                                FieldValue.arrayRemove(ConstantsUtil.AUTH_UID)
                            )
                            .addOnSuccessListener {
                            }.addOnFailureListener {
                            }
                    }.addOnFailureListener {
                    }
            }
        }
    }
}
